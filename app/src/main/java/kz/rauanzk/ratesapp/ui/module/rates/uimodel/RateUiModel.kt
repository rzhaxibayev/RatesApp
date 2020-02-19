package kz.rauanzk.ratesapp.ui.module.rates.uimodel

import kz.rauanzk.ratesapp.R
import kz.rauanzk.ratesapp.utils.CURRENCY_DESCRIPTIONS
import kz.rauanzk.ratesapp.utils.CURRENCY_ICONS

data class RateUiModel(
    val name: String,
    var value: Float,
    var type: Int = TYPE_NORMAL
) {

    val isBase: Boolean
        get() = type == TYPE_BASE

    val iconRes: Int
        get() = CURRENCY_ICONS[name] ?: R.drawable.ic_default

    val description: String
        get() = CURRENCY_DESCRIPTIONS[name] ?: ""

    companion object {
        const val TYPE_BASE = 1
        const val TYPE_NORMAL = 2
    }
}