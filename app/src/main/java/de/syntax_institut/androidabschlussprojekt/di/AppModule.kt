package de.syntax_institut.androidabschlussprojekt.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.syntax_institut.androidabschlussprojekt.data.remote.ProductApiService
import de.syntax_institut.androidabschlussprojekt.repository.ProductRepository
import de.syntax_institut.androidabschlussprojekt.viewmodel.HomeViewModel
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val appModule = module {


    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }


    single {
        Retrofit.Builder()
            .baseUrl("https://api.escuelajs.co/api/v1/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
            .create(ProductApiService::class.java)
    }


    single { ProductRepository(get()) }
    viewModel { HomeViewModel(get()) }
}