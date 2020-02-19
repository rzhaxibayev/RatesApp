package kz.rauanzk.ratesapp.ui.module.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kz.rauanzk.ratesapp.data.api.base.Result
import kz.rauanzk.ratesapp.utils.Event

open class BaseViewModel : ViewModel() {

    val loading = MutableLiveData<Boolean>()
    val showToastEvent = MutableLiveData<Event<String>>()

    fun <T : Any> apiCall(
        call: suspend () -> Result<Any>,
        onSuccess: ((T) -> Unit)? = null,
        onError: ((String?) -> Unit)? = null,
        loading: (() -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ) {
        loading?.invoke()

        viewModelScope.launch {

            when (val result = call.invoke()) {
                is Result.Success<*> -> {
                    onSuccess?.invoke(result.data as T)
                }
                is Result.Error -> {
                    onError?.invoke(result.errorMessage)
                }
            }

            onComplete?.invoke()
        }
    }
}