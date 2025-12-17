package com.example.playlistmaker.feature_settings.presentation

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.feature_settings.domain.model.Settings
import com.example.playlistmaker.feature_settings.domain.usecase.GetThemeUseCaseInterface
import com.example.playlistmaker.feature_settings.domain.usecase.SetThemeUseCaseInterface
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
        applySavedTheme()
    }

    private fun loadSettings() = viewModelScope.launch {
        try {
            _state.value = SettingsState(isDarkTheme = getThemeUseCase.execute().isDarkTheme)
        } catch (e: Exception) {
            _state.value = SettingsState(error = e.message)
        }
    }

    private fun applySavedTheme() = viewModelScope.launch {
        try {
            applyTheme(getThemeUseCase.execute().isDarkTheme)
        } catch (_: Exception) {}
    }

    fun toggleTheme() = viewModelScope.launch {
        val currentState = _state.value ?: SettingsState()
        val newDarkTheme = !currentState.isDarkTheme

        try {
            setThemeUseCase.execute(Settings(isDarkTheme = newDarkTheme))
            applyTheme(newDarkTheme)
            _state.value = currentState.copy(isDarkTheme = newDarkTheme)
            _shouldRecreate.value = true
        } catch (e: Exception) {
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