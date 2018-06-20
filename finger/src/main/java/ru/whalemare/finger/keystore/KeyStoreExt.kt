package ru.whalemare.finger.keystore

import java.io.File
import java.io.FileInputStream
import java.security.KeyStore

/**
 * @since 2018
 * @author Anton Vlasov - whalemare
 */
object KeyStoreExt {
    fun createDefaultKeyStore(keystoreFile: File, keystorePassword: String): KeyStore {
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