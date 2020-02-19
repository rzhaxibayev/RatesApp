package kz.rauanzk.ratesapp.data.api

import kz.rauanzk.ratesapp.data.entity.RatesData
import kz.rauanzk.ratesapp.utils.EUR
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiDataSource {

    @GET("/api/android/latest")
    suspend fun fetchLatestRates(@Query("base") base: String? = EUR): Response<RatesData>
}