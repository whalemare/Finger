package ru.whalemare.finger.key

import android.annotation.SuppressLint
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.support.annotation.RequiresApi
import ru.whalemare.finger.Provider
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * @since 2018
 * @author Anton Vlasov - whalemare
 */
sealed class SymmetricKeyProvider(val params: KeyParams) : Provider<SecretKey> {

    @RequiresApi(Build.VERSION_CODES.N)
    class API24(params: KeyParams) : SymmetricKeyProvider(params) {
        override fun provide(): SecretKey {
            val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val nativeParams = KeyGenParameterSpec.Builder(params.alias, purposes)
                .setKeySize(params.keySize)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setCertificateSerialNumber(params.serialNumber)
                .setCertificateSubject(params.subject)
                .setCertificateNotBefore(params.startDate)
                .setCertificateNotAfter(params.endDate)
                .setBlockModes(params.blockModes)
                .setEncryptionPaddings(params.encryptionPaddings)
                .setInvalidatedByBiometricEnrollment(true)

            val keyGenerator = KeyGenerator.getInstance(params.algorithm, "AndroidKeyStore")
            keyGenerator.init(nativeParams.build())
            return keyGenerator.generateKey()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    class API23(params: KeyParams) : SymmetricKeyProvider(params) {
        override fun provide(): SecretKey {
            val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val nativeParams = KeyGenParameterSpec.Builder(params.alias, purposes)
                .setKeySize(params.keySize)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setCertificateSerialNumber(params.serialNumber)
                .setCertificateSubject(params.subject)
                .setCertificateNotBefore(params.startDate)
                .setCertificateNotAfter(params.endDate)
                .setBlockModes(params.blockModes)
                .setEncryptionPaddings(params.encryptionPaddings)

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                keyGenParamsBuilder.setInvalidatedByBiometricEnrollment(true)
//            }

            val keyGenerator = KeyGenerator.getInstance(params.algorithm, "AndroidKeyStore")
            keyGenerator.init(nativeParams.build())
            return keyGenerator.generateKey()
        }
    }

    @RequiresApi(Build.VERSION_CODES.FROYO)
    class API8(params: KeyParams) : SymmetricKeyProvider(params) {

        override fun provide(): SecretKey {
            val keyGenerator = KeyGenerator.getInstance(params.algorithm)
            keyGenerator.init(params.keySize)
            return keyGenerator.generateKey()
        }
    }

    companion object {
        @SuppressLint("NewApi")
        fun getInstance(params: KeyParams,
                        version: Int = Build.VERSION.SDK_INT): Provider<SecretKey> {
            return when {
                version >= Build.VERSION_CODES.N -> API24(params)
                version >= Build.VERSION_CODES.M -> API23(params)
                else -> API8(params)
            }
        }
    }
}