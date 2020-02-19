package kz.rauanzk.ratesapp.utils

import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StyleableRes

fun View.parseAttributes(
    attrs: AttributeSet,
    @StyleableRes styleableId: IntArray,
    parse: (TypedArray) -> Unit
) {
    val a = context.theme.obtainStyledAttributes(attrs, styleableId, 0, 0)
    try {
        parse(a)
    } finally {
        a.recycle()
    }
}