package ru.whalemare.finger.crypto

import android.security.keystore.KeyProperties
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import ru.whalemare.finger.Provider
import ru.whalemare.finger.Storage
import ru.whalemare.finger.cipher.CipherProvider
import ru.whalemare.finger.iv.IV
import ru.whalemare.finger.iv.IvMemoryStorage
import ru.whalemare.finger.key.KeyParams
import ru.whalemare.finger.key.SymmetricKeyProvider
import ru.whalemare.finger.keystore.KeystoreProvider
import ru.whalemare.finger.options.Mode
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.SecretKey

/**
 * @since 2018
 * @author Anton Vlasov - whalemare
 */
class DefaultCryptoFactory(
    private val alias: String,
    private val ivStorage: Storage<IV> = IvMemoryStorage(),
    private val keystoreProvider: Provider<KeyStore> = KeystoreProvider.getInstance()
) : CryptoFactory {

    var cipherProvider: CipherProvider? = null

    override fun create(mode: Mode): FingerprintManagerCompat.CryptoObject {
        val cipher = createCipher(alias, mode)
        return FingerprintManagerCompat.CryptoObject(cipher)
    }

    private fun createCipher(alias: String, mode: Mode): Cipher {
        val keystore = keystoreProvider.provide()
        val key = getKey(alias, keystore)
        return getCipher(alias, mode, key, ivStorage)
    }

    private fun getCipher(alias: String,
                          mode: Mode,
                          key: Key,
                          ivStorage: Storage<IV>): Cipher {
        if (cipherProvider == null) {
            cipherProvider = CipherProvider(alias, mode, key, ivStorage)
        }
        return cipherProvider!!.provide()
    }

    private fun getKey(alias: String, keystore: KeyStore): Key {
        return if (keystore.containsAlias(alias)) {
            loadKey(alias, keystore)
        } else {
            createKey(alias, keystore)
        }
    }

    private fun createKey(alias: String, keystore: KeyStore): Key {
        val params = getKeyParams(alias)
        val key = getKeyGenerator(params).provide()
        return loadKey(alias, keystore)
    }

    private fun loadKey(alias: String, keystore: KeyStore): Key {
        keystore.load(null)
        return keystore.getKey(alias, null)
    }

    private fun getKeyParams(alias: String): KeyParams {
        return KeyParams(
            alias = alias,
            password = null,
            blockModes = KeyProperties.BLOCK_MODE_CBC,
            encryptionPaddings = KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }

    private fun getKeyGenerator(params: KeyParams): Provider<SecretKey> {
        return SymmetricKeyProvider.getInstance(params)
    }
}