package ru.whalemare.finger.key

import android.security.keystore.KeyProperties
import ru.whalemare.finger.options.Options
import java.math.BigInteger
import java.util.*
import javax.security.auth.x500.X500Principal

/**
 * @since 2018
 * @author Anton Vlasov - whalemare
 */
data class KeyParams(
    val alias: String,
    val password: CharSequence? = null,
    val algorithm: String = Options.ALGORITHM_AES,
    val keySize: Int = 256,
    val blockModes: String = KeyProperties.BLOCK_MODE_CBC,
    val encryptionPaddings: String = KeyProperties.ENCRYPTION_PADDING_PKCS7,
    val signatureAlgorithm: String = Options.ALGORITHM_SHA256_WITH_RSA_ENCRYPTION,
    val serialNumber: BigInteger = BigInteger.ONE,
    val subject: X500Principal = X500Principal("CN=$alias CA Certificate"),
    val startDate: Date = Calendar.getInstance().time,
    val endDate: Date = Calendar.getInstance().apply {
        add(Calendar.YEAR, 20)
    }.time
)

