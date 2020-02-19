package kz.rauanzk.ratesapp.di

import kz.rauanzk.ratesapp.ui.module.main.MainActivityViewModel
import kz.rauanzk.ratesapp.ui.module.rates.RatesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainActivityViewModel() }
    viewModel { RatesViewModel(get()) }
}