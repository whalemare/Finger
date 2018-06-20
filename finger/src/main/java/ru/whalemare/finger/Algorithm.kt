package ru.whalemare.finger

import javax.crypto.Cipher

/**
 * Interface for implementation your own crypto operations around verifyed cipher
 * @since 2018
 * @author Anton Vlasov - whalemare
 */
interface Algorithm<T> {
    fun encrypt(cipher: Cipher, value: T): T

    fun decrypt(cipher: Cipher, value: T): T
}