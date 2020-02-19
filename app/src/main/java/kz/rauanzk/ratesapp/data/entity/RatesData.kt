package kz.rauanzk.ratesapp.data.entity

data class RatesData(
    val baseCurrency: String,
    val rates: HashMap<String, Float>
)
