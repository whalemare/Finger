package ru.whalemare.finger.options

import android.os.Build

object Options {

    val ALGORITHM_AES = "AES"
    val ALGORITHM_RSA = "RSA"
    val ALGORITHM_SHA256_WITH_RSA_ENCRYPTION = "SHA256WithRSAEncryption"

    /** Electronic Codebook (ECB) block mode.  */
    val BLOCK_MODE_ECB = "ECB"

    /** Cipher Block Chaining (CBC) block mode.  */
    val BLOCK_MODE_CBC = "CBC"

    /** Counter (CTR) block mode.  */
    val BLOCK_MODE_CTR = "CTR"

    /** Galois/Counter Mode (GCM) block mode.  */
    val BLOCK_MODE_GCM = "GCM"

    val PADDING_PKCS_1 = "PKCS1Padding"
    val PADDING_PKCS_7 = "PKCS7Padding"

    val RSA_ECB_PKCS1PADDING = "RSA/ECB/PKCS1Padding"
    val AES_CBC_PKCS7PADDING = "AES/CBC/PKCS7Padding"

    val RSA_ECB_PKCS1PADDING_ENCRYPTION_BLOCK_SIZE_FOR_JELLY_BEAN = 245
    val RSA_ECB_PKCS1PADDING_DECRYPTION_BLOCK_SIZE_FOR_JELLY_BEAN = 256

    val RSA_ECB_PKCS1PADDING_1024_ENCRYPTION_BLOCK_SIZE = 117
    val RSA_ECB_PKCS1PADDING_1024_DECRYPTION_BLOCK_SIZE = 128

    /**
     * For default created asymmetric keys
     */
    var TRANSFORMATION_ASYMMETRIC = RSA_ECB_PKCS1PADDING

    /**
     * For default created symmetric keys
     */
    var TRANSFORMATION_SYMMETRIC = AES_CBC_PKCS7PADDING

    /**
     * For default created asymmetric keys
     */
    var ENCRYPTION_BLOCK_SIZE: Int = 0

    /**
     * For default created asymmetric keys
     */
    var DECRYPTION_BLOCK_SIZE: Int = 0

    init {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ENCRYPTION_BLOCK_SIZE = RSA_ECB_PKCS1PADDING_ENCRYPTION_BLOCK_SIZE_FOR_JELLY_BEAN
            DECRYPTION_BLOCK_SIZE = RSA_ECB_PKCS1PADDING_DECRYPTION_BLOCK_SIZE_FOR_JELLY_BEAN
        } else {
            ENCRYPTION_BLOCK_SIZE = RSA_ECB_PKCS1PADDING_1024_ENCRYPTION_BLOCK_SIZE
            DECRYPTION_BLOCK_SIZE = RSA_ECB_PKCS1PADDING_1024_DECRYPTION_BLOCK_SIZE
        }
    }
}
