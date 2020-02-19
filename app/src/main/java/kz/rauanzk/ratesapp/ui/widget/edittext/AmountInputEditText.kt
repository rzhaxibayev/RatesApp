package kz.rauanzk.ratesapp.ui.widget.edittext

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import kz.rauanzk.ratesapp.R
import timber.log.Timber
import java.math.BigDecimal
import kotlin.math.max
import kotlin.math.min

class AmountInputEditText : AppCompatEditText {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.editTextStyle)

    constructor(context: Context) : this(context, null)

    private var lastValue: String = ""

    init {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Timber.d("beforeTextChanged ${s?.toString()}")
            }

            override fun onTextChanged(x: CharSequence?, start: Int, before: Int, count: Int) {

                val finalString =
                    pretty(x.toString(), truncZeros = lastValue.isEmpty())

                if (x.toString() != finalString) {
                    lastValue = x.toString()
                    this@AmountInputEditText.setText(finalString, BufferType.EDITABLE)
                    this@AmountInputEditText.setSelection(max(0, finalString.length - 2))
                }
            }

        })
    }

    val sum: Double
        get() {
            val digits =
                this.text?.filter { TextUtils.isDigitsOnly(it.toString()) || it.toString() == "," || it.toString() == "." }
            val digitAsString = digits.toString().replace(" ", "").replace(",", ".")
            return try {
                digitAsString.toDouble()
            } catch (e: Exception) {
                0.0
            }
        }

    val sumAsLong: Long
        get() {
            val digits =
                this.text?.filter { TextUtils.isDigitsOnly(it.toString()) || it.toString() == "," || it.toString() == "." }
            val digitAsString = digits.toString().replace(" ", "").replace(",", ".")
            return try {
                (digitAsString.toDouble() * 100).toLong()
            } catch (e: Exception) {
                0L
            }
        }


    companion object {

        private fun raw(s: String?): String {
            if (s != null) {
                var result = ""
                var hasDot = false
                for (c in s) {
                    if (c.isDigit()) {
                        result += c
                    }
                    if (!hasDot && (c == ',' || c == '.')) {
                        hasDot = true
                        result += "."
                    }
                }
                if (result.isNotEmpty())
                    return result
                return ""
            } else {
                return ""
            }
        }

        fun sum(s: String?): BigDecimal? {
            if (s == null) {
                return null
            }
            val digits =
                s.filter { TextUtils.isDigitsOnly(it.toString()) || it.toString() == "," || it.toString() == "." }
            val digitAsString = digits.replace(" ", "").replace(",", ".")
            return try {
                digitAsString.toBigDecimal()
            } catch (e: Exception) {
                null
            }
        }

        fun sumAsLong(x: String?): Long {
            val digitAsString = x?.replace(" ", "")?.replace(",", ".")
            return try {
                ((Regex("[^0-9.]").replace(digitAsString!!, "").toDouble() * 100)).toLong()
            } catch (e: Exception) {
                0L
            }
        }

        fun pretty(d: String, truncZeros: Boolean = false): String {
            val rawValue = raw(d)
            if (rawValue.isEmpty()) {
                return ""
            }
            val parts = rawValue.split(".")
            var finalString = ""
            var firstPart = parts[0]
            while (firstPart.isNotEmpty() && firstPart[0] == '0') {
                firstPart = firstPart.substring(1, firstPart.length)
            }

            if (firstPart.isEmpty()) {
                firstPart = "0"
            }

            var right = firstPart.length
            while (right > 0) {
                val left = max(right - 3, 0)
                finalString = if (finalString.isNotEmpty()) {
                    firstPart.substring(left, right) + finalString
                } else {
                    firstPart.substring(left, right)
                }

                right -= 3
            }

            if (truncZeros) {
                if (parts.size > 1 && !(parts[1] == "0" || parts[1] == "00")) {
                    finalString += "," + parts[1].substring(0, min(2, parts[1].length))
                }
            } else {
                if (parts.size > 1) {
                    finalString += "," + parts[1].substring(0, min(2, parts[1].length))
                }
            }

            return finalString
        }
    }
}