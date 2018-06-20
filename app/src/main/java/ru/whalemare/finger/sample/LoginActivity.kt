package ru.whalemare.finger.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.CoroutineExceptionHandler
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import ru.whalemare.finger.Finger
import ru.whalemare.finger.FingerResult

/**
 * @since 2018
 * @author Anton Vlasov - whalemare
 */
class LoginActivity : AppCompatActivity() {

    val errorHandler = CoroutineExceptionHandler { _, throwable ->
        // handle error here
        Log.e("ERROR", "coroutine error\n", throwable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val finger = Finger(this, "alias-for-crypt-password")
        if (finger.isAvailable()) {
            showFingerprint(finger)
        } else {
            showEnterCode()
        }
    }

    fun showFingerprint(finger: Finger) {
        buttonEncrypt.setOnClickListener {
            encrypt(finger, "my password")
        }
    }


    fun encrypt(finger: Finger, text: String) = launch(UI + errorHandler) {
        // show on ui that user need touch sensor
        val result = finger.encrypt(text)

        val status = when (result) {
            is FingerResult.Success -> "Result: [${result.output}]"
            is FingerResult.Error -> "Error: type: ${result.type}\nmessage: [${result.message}]"
            is FingerResult.Help -> "Help: type: ${result.type}\nmessage: [${result.message}]"
        }
        textStatus.text = status
    }

    fun decrypt(finger: Finger, text: String) {
        launch(UI + errorHandler) {
            // show on ui that user need touch sensor
            val result = finger.decrypt(text)

            val status = when (result) {
                is FingerResult.Success -> "Result: [${result.output}]"
                is FingerResult.Error -> "Error: type: ${result.type}\nmessage: [${result.message}]"
                is FingerResult.Help -> "Help: type: ${result.type}\nmessage: [${result.message}]"
            }
            textStatus.text = status
        }
    }

    private fun showEnterCode() {

    }
}