package kz.rauanzk.ratesapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import kz.rauanzk.ratesapp.data.repository.RatesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { provideSharedPreferences(androidContext()) }

    single { RatesRepository(get(), get()) }
}

private fun provideSharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}