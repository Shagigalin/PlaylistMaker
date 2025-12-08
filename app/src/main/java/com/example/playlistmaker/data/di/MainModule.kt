package com.example.playlistmaker.data.di

import com.example.playlistmaker.feature_main.presentation.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {

    // ViewModel
    viewModel { MainViewModel() }
}