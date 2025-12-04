package com.example.playlistmaker.feature_settings.presentation

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
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val settings = getThemeUseCase.execute()
                _state.value = SettingsState(
                    isDarkTheme = settings.isDarkTheme
                )
            } catch (e: Exception) {
                _state.value = SettingsState(error = e.message)
            }
        }
    }

    fun toggleTheme() {
        val currentState = _state.value ?: SettingsState()
        val newDarkTheme = !currentState.isDarkTheme

        viewModelScope.launch {
            try {
                // Сохраняем новую тему
                setThemeUseCase.execute(Settings(isDarkTheme = newDarkTheme))

                // Обновляем состояние
                _state.value = currentState.copy(isDarkTheme = newDarkTheme)

                // Сигнализируем о необходимости пересоздать Activity
                _shouldRecreate.value = true
            } catch (e: Exception) {
                _state.value = currentState.copy(error = e.message)
            }
        }
    }

    // Метод для сброса флага пересоздания
    fun onRecreated() {
        _shouldRecreate.value = false
    }
}