package ru.whalemare.finger.options

import javax.crypto.Cipher

enum class Mode(val cipherMode: Int) {
    AUTHENTICATION(Cipher.ENCRYPT_MODE),
    DECRYPTION(Cipher.DECRYPT_MODE),
    ENCRYPTION(Cipher.ENCRYPT_MODE);
}