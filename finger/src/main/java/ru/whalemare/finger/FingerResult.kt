package ru.whalemare.finger

import android.hardware.fingerprint.FingerprintManager

/**
 * Result of operations with fingerprinting.
 * @since 2018
 * @author Anton Vlasov - whalemare
 */
sealed class FingerResult {

    /**
     * Called when a fingerprint is recognized.
     */
    class Success(val output: String) : FingerResult()

    /**
     * Called when an unrecoverable error has been encountered and the operation is complete.
     * Called when a fingerprint is valid but not recognized.
     */
    class Error(messageId: Int, val message: CharSequence?) : FingerResult() {

        /**
         * Todo: add more error types
         */
        enum class Type {
            /**
             * The hardware is unavailable.
             */
            UNAVAILABLE,

            /**
             * Error state returned when the sensor was unable to process the current image.
             */
            UNABLE_TO_PROCESS,

            /**
             * Error state returned when the current request has been running too long.
             */
            TIMEOUT,

            /**
             * Error state returned for operations like enrollment; the operation cannot be completed because there's not
             * enough storage remaining to complete the operation.
             */
            NOT_ENOUGH_SPACE,

            /**
             * The operation was canceled because the fingerprint sensor is unavailable.
             */
            CANCELED,

            /**
             * The operation was canceled because the API is locked out due to too many attempts.
             */
            LOCKOUT,

            /**
             * Fingerprint did not start due to initialization failure, probably because of
             * [android.security.keystore.KeyPermanentlyInvalidatedException]
             */
            INITIALIZATION_FAILED,

            /**
             * Crypto failed to decrypt the value.
             */
            DECRYPTION_FAILED,

            /**
             * Crypto failed to encrypt the value.
             */
            ENCRYPTION_FAILED,

            /**
             * Unknown error happened.
             */
            UNKNOWN;
        }

        val type = when (messageId) {
            FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE -> Type.UNAVAILABLE
            FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS -> Type.UNABLE_TO_PROCESS
            FingerprintManager.FINGERPRINT_ERROR_TIMEOUT -> Type.TIMEOUT
            FingerprintManager.FINGERPRINT_ERROR_NO_SPACE -> Type.NOT_ENOUGH_SPACE
            FingerprintManager.FINGERPRINT_ERROR_CANCELED -> Type.CANCELED
            FingerprintManager.FINGERPRINT_ERROR_LOCKOUT -> Type.LOCKOUT
            else -> Type.UNKNOWN
        }
    }

    class Help(messageId: Int, val message: CharSequence?) : FingerResult() {
        enum class Type {
            /**
             * The image acquired was good.
             */
            GOOD,

            /**
             * Only a partial fingerprint image was detected.
             */
            PARTIAL,

            /**
             * The fingerprint image was too noisy to process due to a detected condition.
             */
            INSUFFICIENT,

            /**
             * The fingerprint image was too noisy due to suspected or detected dirt on the sensor.
             */
            DIRTY,

            /**
             * The fingerprint image was unreadable due to lack of motion.
             */
            TOO_SLOW,

            /**
             * The fingerprint image was incomplete due to quick motion.
             */
            TOO_FAST,

            /**
             * Fingerprint valid but not recognized.
             */
            FAILURE,
        }

        val type = when (messageId) {
            FingerprintManager.FINGERPRINT_ACQUIRED_GOOD -> Type.GOOD
            FingerprintManager.FINGERPRINT_ACQUIRED_PARTIAL -> Type.PARTIAL
            FingerprintManager.FINGERPRINT_ACQUIRED_INSUFFICIENT -> Type.INSUFFICIENT
            FingerprintManager.FINGERPRINT_ACQUIRED_IMAGER_DIRTY -> Type.DIRTY
            FingerprintManager.FINGERPRINT_ACQUIRED_TOO_SLOW -> Type.TOO_SLOW
            FingerprintManager.FINGERPRINT_ACQUIRED_TOO_FAST -> Type.TOO_FAST
            else -> Type.FAILURE
        }
    }
}