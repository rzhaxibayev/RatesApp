package kz.rauanzk.ratesapp.ui.module.rates

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kz.rauanzk.ratesapp.data.entity.RatesData
import kz.rauanzk.ratesapp.data.repository.RatesRepository
import kz.rauanzk.ratesapp.ui.module.base.BaseViewModel
import kz.rauanzk.ratesapp.ui.module.rates.uimodel.RateUiModel
import kz.rauanzk.ratesapp.utils.EUR
import kz.rauanzk.ratesapp.utils.Event
import timber.log.Timber

class RatesViewModel(
    private val ratesRepository: RatesRepository
) : BaseViewModel() {

    private var isAlreadyFetched = false

    var rateUiModels = arrayListOf<RateUiModel>()

    val updateList = MutableLiveData<Event<Unit>>()
    val showErrorLayout = MutableLiveData<Boolean>()

    var updateListJob: Job? = null

    init {
        Timber.d("RatesViewModel")
    }

    fun fetchLatestRates() {
        apiCall<RatesData>(
            { ratesRepository.fetchLatestRates(ratesRepository.currentBase ?: EUR) },
            onSuccess = { ratesData ->
                Timber.d("onSuccess = ${ratesData.baseCurrency}")

                showErrorLayout.value = false

                val ratesMap = ratesData.rates

                if (!isAlreadyFetched) {
                    isAlreadyFetched = true
                    updateListJob = viewModelScope.launch {
                        rateUiModels.apply {
                            clear()
                            add(
                                RateUiModel(
                                    name = ratesRepository.currentBase ?: EUR,
                                    value = 1F,
                                    type = RateUiModel.TYPE_BASE
                                )
                            )
                            addAll(ratesMap.map { RateUiModel(it.key, it.value) })
                        }

                        updateList.value = Event(Unit)
                    }
                } else {
                    updateListJob = viewModelScope.launch {

                        rateUiModels.forEachIndexed { index, item ->
                            if (index == 0) {
                                return@forEachIndexed
                            }
                            val value = ratesMap[item.name]
                            if (value != null) {
                                item.value = value
                            } else {
                                // TODO: It is possible that some currency rates may not be received from API, so we should REMOVE such items from the list
                                Timber.d("This item should be removed, position=$index")
                            }
                        }

                        updateList.value = Event(Unit)
                        Timber.d("Update job")

                        // TODO: It is possible that new currency rate may be received from API, if so we should ADD them to the list
                    }
                }
            },
            onError = { message ->
                Timber.d("onError = $message")
                if (!isAlreadyFetched) {
                    showErrorLayout.value = true
                }
            },
            loading = {
                if (!isAlreadyFetched) {
                    loading.value = true
                }
            },
            onComplete = {
                loading.value = false
            }
        )
    }

    fun updateCurrentBase(base: String) {
        ratesRepository.updateCurrentBase(base)
    }
}