package kz.rauanzk.ratesapp.data.api.base

sealed class Result<out T : Any> {
    data class Success<T : Any>(val data: T) : Result<T>()
    data class Error(val errorMessage: String?) : Result<Nothing>()
}