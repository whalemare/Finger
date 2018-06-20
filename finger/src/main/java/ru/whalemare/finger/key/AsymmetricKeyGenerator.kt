package ru.whalemare.finger.key

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.*
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.security.auth.x500.X500Principal

/**
 * @since 2018
 * @author Anton Vlasov - whalemare
 */
sealed class AsymmetricKeyGenerator {

    /**
     * Api 23 and higher
     */
    @RequiresApi(Build.VERSION_CODES.M)
    class API23 : AsymmetricKeyGenerator() {

        private val PROVIDER_ANDROID_KEY_STORE = "AndroidKeyStore"

        override fun generate(params: KeyParams): KeyPair {
            val keySpec = map(params)
            return generateKey(params, keySpec)
        }

        private fun map(params: KeyParams): KeyGenParameterSpec {
            val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            return KeyGenParameterSpec.Builder(params.alias, purposes)
                .setKeySize(params.keySize)
                .setCertificateSerialNumber(params.serialNumber)
                .setCertificateSubject(params.subject)
                .setCertificateNotBefore(params.startDate)
                .setCertificateNotAfter(params.endDate)
                .setBlockModes(params.blockModes)
                .setEncryptionPaddings(params.encryptionPaddings)
                .build()
        }

        private fun generateKey(params: KeyParams, keySpec: AlgorithmParameterSpec): KeyPair {
            val generator = KeyPairGenerator.getInstance(params.algorithm, PROVIDER_ANDROID_KEY_STORE)
            generator.initialize(keySpec)
            return generator.generateKeyPair()
        }
    }

    /**
     * [Build.VERSION_CODES.KITKAT] = API 19
     * API 19 and higher
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @Suppress("DEPRECATION")
    class API19(context: Context) : API18(context) {
        override fun map(context: Context, keyProps: KeyParams): KeyPairGeneratorSpec.Builder {
            return super.map(context, keyProps)
                .setKeySize(keyProps.keySize)
        }
    }

    /**
     * [Build.VERSION_CODES.JELLY_BEAN_MR2] = API 18
     * API 18 and higher
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Suppress("DEPRECATION")
    open class API18(private val context: Context) : AsymmetricKeyGenerator() {

        private val PROVIDER_ANDROID_KEY_STORE = "AndroidKeyStore"

        override fun generate(params: KeyParams): KeyPair {
            val keySpec = map(context, params).build()
            return createKey(params, keySpec)
        }

        protected open fun createKey(params: KeyParams, keySpec: KeyPairGeneratorSpec): KeyPair {
            val provider = PROVIDER_ANDROID_KEY_STORE
            val generator = KeyPairGenerator.getInstance(params.algorithm, provider)
            generator.initialize(keySpec)
            return generator.generateKeyPair()
        }

        protected open fun map(context: Context, keyProps: KeyParams): KeyPairGeneratorSpec.Builder {
            return KeyPairGeneratorSpec.Builder(context)
                .setAlias(keyProps.alias)
                .setSerialNumber(keyProps.serialNumber)
                .setSubject(keyProps.subject)
                .setStartDate(keyProps.startDate)
                .setEndDate(keyProps.endDate)
        }
    }

    class API8(context: Context,
               keystoreName: String = "keystore",
               private val keystorePassword: String) : AsymmetricKeyGenerator() {
        private val PROVIDER_BC = "BC"
        private val keystoreFile = File(context.filesDir, keystoreName)

        override fun generate(params: KeyParams): KeyPair {
            val keyPair = createKey(params)
            val keyPrivate = keyPair.private
            val certificate = getCertificate(keyPair, params)
            val keyStore = createKeyStore()

            keyStore.setKeyEntry(params.alias, keyPrivate, params.password.toString().toCharArray(), arrayOf<Certificate>(certificate))
            keyStore.store(FileOutputStream(keystoreFile), keystorePassword.toCharArray())
            return keyPair
        }

        private fun createKeyStore(): KeyStore {
            val defaultType = KeyStore.getDefaultType()
            val defaultKeyStore = KeyStore.getInstance(defaultType)
            if (!keystoreFile.exists()) {
                defaultKeyStore.load(null)
            } else {
                defaultKeyStore.load(FileInputStream(keystoreFile), keystorePassword.toCharArray())
            }
            return defaultKeyStore
        }

        private fun createKey(params: KeyParams): KeyPair {
            val generator = KeyPairGenerator.getInstance(params.algorithm)
            generator.initialize(params.keySize)
            return generator.generateKeyPair()
        }

        private fun getCertificate(keyPair: KeyPair, params: KeyParams): X509Certificate {
            val generatorClass: Class<*>? = try {
                Class.forName("com.android.org.bouncycastle.x509.X509V3CertificateGenerator")
            } catch (e: ClassNotFoundException) {
                // if there is no android default implementation of X509V3CertificateGenerator try to find it from library
                try {
                    Class.forName("org.bouncycastle.x509.X509V3CertificateGenerator")
                } catch (e1: ClassNotFoundException) {
                    val message = "You need to include  http://www.bouncycastle.org/ library to generate KeyPair on ${Build.VERSION.SDK_INT} API version. " +
                            "You can do this via gradle using command 'compile 'org.bouncycastle:bcprov-jdk15on:1.54'"
                    throw NotImplementedError(message)
                }
            }

            return getCertificate(generatorClass!!, keyPair, params)
        }

        /**
         * Generating X509Certificate using private com.android.org.bouncycastle.x509.X509V3CertificateGenerator class.
         * Google did copied http://www.bouncycastle.org/ but made it private. To not include additional library Im
         * using reflection here. Tested on API level 16, 17
         */
        private fun getCertificate(generatorClass: Class<*>, keyPair: KeyPair, params: KeyParams): X509Certificate {
            val generator = generatorClass.newInstance()

            var method = generator.javaClass.getMethod("setPublicKey", PublicKey::class.java)
            method.invoke(generator, keyPair.public)

            method = generator.javaClass.getMethod("setSerialNumber", BigInteger::class.java)
            method.invoke(generator, params.serialNumber)

            method = generator.javaClass.getMethod("setSubjectDN", X500Principal::class.java)
            method.invoke(generator, params.subject)

            method = generator.javaClass.getMethod("setIssuerDN", X500Principal::class.java)
            method.invoke(generator, params.subject)

            method = generator.javaClass.getMethod("setNotBefore", Date::class.java)
            method.invoke(generator, params.startDate)

            method = generator.javaClass.getMethod("setNotAfter", Date::class.java)
            method.invoke(generator, params.endDate)

            method = generator.javaClass.getMethod("setSignatureAlgorithm", String::class.java)
            method.invoke(generator, params.signatureAlgorithm)

            method = generator.javaClass.getMethod("generate", PrivateKey::class.java, String::class.java)
            return method.invoke(generator, keyPair.private, PROVIDER_BC) as X509Certificate
        }

    }

    abstract fun generate(params: KeyParams): KeyPair

    fun generate(alias: String, password: String): KeyPair {
        return generate(KeyParams(alias, password))
    }

    companion object {
        /**
         * Factory method for get properly instance of AsymmetricKeyGenerator
         * @param context = properly for API <= [Build.VERSION_CODES.M]
         * @param keystoreName = properly for API <= [Build.VERSION_CODES.JELLY_BEAN_MR2]
         * @param keystorePassword = properly for API <= [Build.VERSION_CODES.JELLY_BEAN_MR2]
         */
        @SuppressLint("NewApi")
        fun getInstance(context: Context? = null,
                        version: Int = Build.VERSION.SDK_INT,
                        keystoreName: String? = null,
                        keystorePassword: String? = null): AsymmetricKeyGenerator {
            return when {
                version >= Build.VERSION_CODES.M -> API23()
                version >= Build.VERSION_CODES.KITKAT -> API19(context!!)
                version >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> API18(context!!)
                else -> API8(context!!, keystoreName!!, keystorePassword!!)
            }
        }

//        @RequiresApi(Build.VERSION_CODES.M)
//        fun getInstance(): AsymmetricKeyGenerator {
//            return API23()
//        }
//
//        @RequiresApi(Build.VERSION_CODES.KITKAT)
//        fun getInstance(context: Context,
//                        version: Int = Build.VERSION.SDK_INT): AsymmetricKeyGenerator {
//            return when {
//                version >= Build.VERSION_CODES.KITKAT -> API19(context!!)
//                version >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> API18(context!!)
//                else -> API8(context!!, keystoreName!!, keystorePassword!!)
//            }
//        }
    }
}