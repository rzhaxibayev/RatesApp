package kz.rauanzk.ratesapp.utils

import android.widget.EditText

fun EditText.putCursorAtEnd() {
    val textValue = text
    if (textValue.isNotBlank()) {
        setSelection(textValue.length, textValue.length)
    }
}