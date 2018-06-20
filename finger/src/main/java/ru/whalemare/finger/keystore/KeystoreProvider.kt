package ru.whalemare.finger.keystore

import android.os.Build
import android.support.annotation.RequiresApi
import ru.whalemare.finger.Provider
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore

/**
 * @since 2018
 * @author Anton Vlasov - whalemare
 */
sealed class KeystoreProvider: Provider<KeyStore> {

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    class API18 : KeystoreProvider() {
        private val PROVIDER_ANDROID_KEY_STORE = "AndroidKeyStore"

        override fun provide(): KeyStore {
            val keystore = KeyStore.getInstance(PROVIDER_ANDROID_KEY_STORE)
            keystore.load(null)
            return keystore
        }
    }

    @RequiresApi(Build.VERSION_CODES.FROYO)
    class API8(private val keystoreFile: File, private val keystorePassword: String) : KeystoreProvider() {

        override fun provide(): KeyStore {
            return fileKeystore(keystoreFile, keystorePassword)
        }

        private fun fileKeystore(keystoreFile: File, keystorePassword: String): KeyStore {
            val defaultType = KeyStore.getDefaultType()
            val defaultKeyStore = KeyStore.getInstance(defaultType)
            if (!keystoreFile.exists()) {
                defaultKeyStore.load(null)
            } else {
                defaultKeyStore.load(FileInputStream(keystoreFile), keystorePassword.toCharArray())
            }
            return defaultKeyStore
        }
    }

    companion object {
        fun getInstance(keystoreFile: File? = null,
                        keystorePassword: String? = null): KeystoreProvider {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                API18()
            } else {
                API8(keystoreFile!!, keystorePassword!!)
            }
        }
    }
}