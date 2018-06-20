package ru.whalemare.finger.cipher

import android.security.keystore.KeyProperties
import ru.whalemare.finger.options.Mode
import ru.whalemare.finger.Provider
import ru.whalemare.finger.Storage
import ru.whalemare.finger.iv.IV
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

/**
 * @since 2018
 * @author Anton Vlasov - whalemare
 */
class CipherProvider(private val alias: String,
                     private val mode: Mode,
                     private val key: Key,
                     private val ivStorage: Storage<IV>) : Provider<Cipher> {

    override fun provide(): Cipher {
        val algorithm = KeyProperties.KEY_ALGORITHM_AES
        val blockMode = KeyProperties.BLOCK_MODE_CBC
        val padding = KeyProperties.ENCRYPTION_PADDING_PKCS7
        val transformation = "$algorithm/$blockMode/$padding"

        val cipher = Cipher.getInstance(transformation)

        if (mode == Mode.DECRYPTION) {
            val iv: IV = ivStorage.retrieve(alias)!!
            cipher.init(mode.cipherMode, key, IvParameterSpec(iv))
        } else {
            cipher.init(mode.cipherMode, key)
            ivStorage.store(alias, cipher.iv)
        }
        return cipher
    }
}