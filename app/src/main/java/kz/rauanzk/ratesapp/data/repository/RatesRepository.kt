package kz.rauanzk.ratesapp.data.repository

import android.content.SharedPreferences
import kz.rauanzk.ratesapp.data.api.ApiDataSource
import kz.rauanzk.ratesapp.data.api.base.Result
import kz.rauanzk.ratesapp.data.entity.RatesData
import kz.rauanzk.ratesapp.data.repository.base.BaseRepository
import kz.rauanzk.ratesapp.utils.EUR

private const val PREF_CURRENT_BASE = "PREF_CURRENT_BASE"

class RatesRepository(
    private val apiDataSource: ApiDataSource,
    private val prefs: SharedPreferences
) : BaseRepository() {

    val currentBase: String?
        get() = prefs.getString(PREF_CURRENT_BASE, EUR)

    fun updateCurrentBase(base: String) {
        prefs.edit().putString(PREF_CURRENT_BASE, base).apply()
    }

    suspend fun fetchLatestRates(base: String? = null): Result<RatesData> {
        return safeApiCall {
            apiDataSource.fetchLatestRates(base)
        }
    }
}