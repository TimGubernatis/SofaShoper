package de.syntax_institut.androidabschlussprojekt.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.syntax_institut.androidabschlussprojekt.data.firebase.repositories.UserRepository
import de.syntax_institut.androidabschlussprojekt.data.firebase.services.AuthenticationService
import de.syntax_institut.androidabschlussprojekt.data.remote.ProductApiService
import de.syntax_institut.androidabschlussprojekt.data.repository.ProductRepository
import de.syntax_institut.androidabschlussprojekt.data.repository.CartRepository
import de.syntax_institut.androidabschlussprojekt.viewmodel.AuthViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.HomeViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.SettingsViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.UserProfileViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.CartViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.CheckoutViewModel
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases.ObserveCurrentUserUseCase
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases.SignOutUseCase
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases.SignInWithGoogleUseCase
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
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
        OkHttpClient.Builder()
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.escuelajs.co/api/v1/")
            .client(get<OkHttpClient>())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
            .create(ProductApiService::class.java)
    }

    single { ProductRepository(get()) }
    single { CartRepository() }

    singleOf(::AuthenticationService)

    singleOf(::UserRepository)

    singleOf(::SignInWithGoogleUseCase)
    singleOf(::SignOutUseCase)
    singleOf(::ObserveCurrentUserUseCase)

    viewModel { AuthViewModel(get(), get(), get(), get()) }
    viewModel { UserProfileViewModel(get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { CartViewModel(get()) }
    viewModel { CheckoutViewModel(get()) }
}