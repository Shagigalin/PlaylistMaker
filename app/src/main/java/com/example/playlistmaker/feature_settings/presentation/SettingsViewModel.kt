package com.example.playlistmaker.feature_settings.presentation

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.feature_settings.domain.model.ThemeSettings
import com.example.playlistmaker.feature_settings.domain.usecase.GetThemeUseCaseInterface
import com.example.playlistmaker.feature_settings.domain.usecase.SetThemeUseCaseInterface
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getThemeUseCase: GetThemeUseCaseInterface,
    private val setThemeUseCase: SetThemeUseCaseInterface
) : ViewModel() {

    private val _state = MutableLiveData<SettingsState>()
    val state: LiveData<SettingsState> = _state

    private val _shouldRecreate = MutableLiveData<Boolean>()
    val shouldRecreate: LiveData<Boolean> = _shouldRecreate

    init {
        loadSettings()

    }

    private fun loadSettings() = viewModelScope.launch {
        try {
            val theme = getThemeUseCase.execute()
            val isDarkTheme = theme == ThemeSettings.DARK
            _state.value = SettingsState(isDarkTheme = isDarkTheme)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _state.value = SettingsState(error = e.message)
        }
    }

    fun toggleTheme() = viewModelScope.launch {
        val currentState = _state.value ?: SettingsState()
        val newIsDarkTheme = !currentState.isDarkTheme
        val newTheme = if (newIsDarkTheme) ThemeSettings.DARK else ThemeSettings.LIGHT

        try {

            setThemeUseCase.execute(newTheme)


            _state.value = currentState.copy(isDarkTheme = newIsDarkTheme)


            applyTheme(newIsDarkTheme)


            _shouldRecreate.value = true

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            _state.value = currentState.copy(error = e.message)
        }
    }

    private fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun onRecreated() {
        _shouldRecreate.value = false
    }
}