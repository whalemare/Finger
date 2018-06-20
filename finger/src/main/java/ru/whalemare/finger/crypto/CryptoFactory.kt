package ru.whalemare.finger.crypto

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import ru.whalemare.finger.options.Mode

/**
 * @since 2018
 * @author Anton Vlasov - whalemare
 */
interface CryptoFactory {
    fun create(mode: Mode): FingerprintManagerCompat.CryptoObject
}