package ru.whalemare.finger.sample

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * @since 2018
 * @author Anton Vlasov - whalemare
 */
fun EditText.onTextChanges(onEvent: (CharSequence?) -> Unit){
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onEvent.invoke(s)
        }
    })
}