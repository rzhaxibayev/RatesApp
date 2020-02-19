package kz.rauanzk.ratesapp.data.repository.base

import com.google.gson.Gson
import kz.rauanzk.ratesapp.data.api.base.ErrorData
import kz.rauanzk.ratesapp.data.api.base.Result
import retrofit2.Response
import java.lang.Exception

open class BaseRepository {

    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()

            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                val errorBodyString = response.errorBody()!!.string()
                val errorData = Gson().fromJson(errorBodyString, ErrorData::class.java)
                Result.Error(errorData.error)
            }
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    }
}