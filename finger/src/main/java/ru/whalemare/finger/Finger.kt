package ru.whalemare.finger

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v4.os.CancellationSignal
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import ru.whalemare.finger.algorithm.AlgorithmString
import ru.whalemare.finger.crypto.CryptoFactory
import ru.whalemare.finger.crypto.DefaultCryptoFactory
import ru.whalemare.finger.options.Mode

/**
 * Maintain class that you should use for handle data transformation (encrypt/decrypt)
 * @since 2018
 * @author Anton Vlasov - whalemare
 * @param context for create instance of [FingerprintManagerCompat] and native authentication with hardware sensor
 * @param alias unique key identifier, [java.security.Key] is stored to [java.security.KeyStore] under this value
 * @param algorithm used for implementation your own crypto operations around verified cipher
 * @param cryptoFactory used for creating instance of [FingerprintManagerCompat.CryptoObject]
 */
open class Finger(
    context: Context,
    private val alias: String,
    private val algorithm: Algorithm<String> = AlgorithmString(),
    private val cryptoFactory: CryptoFactory = DefaultCryptoFactory(alias)
) {

    protected val manager = FingerprintManagerCompat.from(context)

    /**
     * @return true if device support fingerprinting
     */
    fun isAvailable(): Boolean {
        return manager.isHardwareDetected && manager.hasEnrolledFingerprints()
    }

    /**
     * Authenticate user via Fingerprint. If user is successfully authenticated,
     * [AlgorithmString] implementation is used to automatically encrypt given value.<br>
     * Use it when saving some data that should not be saved as plain text (e.g. password).
     * To decrypt the value use [Finger.decrypt] method.
     *
     * @param value String value which will be encrypted if user successfully authenticates
     */
    suspend fun encrypt(value: CharSequence): FingerResult {
        val cryptoObject = createCryptoObject(Mode.ENCRYPTION).await()
        return nativeAuthentication(cryptoObject, value, Mode.ENCRYPTION)
    }

    suspend fun decrypt(value: CharSequence): FingerResult {
        val cryptoObject = createCryptoObject(Mode.DECRYPTION).await()
        return nativeAuthentication(cryptoObject, value, Mode.DECRYPTION)
    }

    private suspend fun nativeAuthentication(cryptoObject: FingerprintManagerCompat.CryptoObject,
                                             value: CharSequence,
                                             mode: Mode): FingerResult =
        suspendCancellableCoroutine { continutation ->
            val cancelationSignal = CancellationSignal()
            cancelationSignal.setOnCancelListener {
                continutation.cancel()
            }

            manager.authenticate(cryptoObject, 0, cancelationSignal, object : FingerprintManagerCompat.AuthenticationCallback() {
                override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errMsgId, errString)

                    continutation.resume(FingerResult.Error(errMsgId, errString))
                }

                override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val transformed = when (mode) {
                        Mode.AUTHENTICATION -> ""
                        Mode.ENCRYPTION -> algorithm.encrypt(result.cryptoObject.cipher!!, value.toString())
                        Mode.DECRYPTION -> algorithm.decrypt(result.cryptoObject.cipher!!, value.toString())
                    }

                    continutation.resume(FingerResult.Success(transformed))
                }

                override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
                    super.onAuthenticationHelp(helpMsgId, helpString)

                    continutation.resume(FingerResult.Help(helpMsgId, helpString))
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    continutation.resume(FingerResult.Help(-1, "Not recognized"))
                }
            }, Handler(Looper.getMainLooper()))
        }

    private fun createCryptoObject(mode: Mode) = async {
        return@async cryptoFactory.create(mode)
    }

}