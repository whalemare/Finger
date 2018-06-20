package ru.whalemare.finger.algorithm

import android.util.Base64
import ru.whalemare.finger.Algorithm
import javax.crypto.Cipher

/**
 * @since 2018
 * @author Anton Vlasov - whalemare
 */
class AlgorithmString: Algorithm<String> {

    override fun encrypt(cipher: Cipher, value: String): String {
        val encrypted = cipher.doFinal(value.toByteArray())
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    override fun decrypt(cipher: Cipher, value: String): String {
        val decrypted = Base64.decode(value, Base64.DEFAULT)
        return String(cipher.doFinal(decrypted))
    }
}