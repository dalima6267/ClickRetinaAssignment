package com.dalima.clickretinaassignment.viewmodel

import androidx.lifecycle.*
import com.dalima.clickretinaassignment.network.ProfileRepository
import com.dalima.clickretinaassignment.data.User
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val user: User) : UiState()
    data class Error(val message: String) : UiState()
}

class ProfileViewModel(private val repo: ProfileRepository) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>(UiState.Loading)
    val uiState: LiveData<UiState> = _uiState

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val res = repo.getProfile()
                if (res.isSuccessful) {
                    val body = res.body()
                    if (body != null) {
                        _uiState.value = UiState.Success(body.user)
                    } else {
                        _uiState.value = UiState.Error("Empty response")
                    }
                } else {
                    _uiState.value = UiState.Error("Network error: ${res.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Exception: ${e.localizedMessage ?: e.message}")
            }
        }
    }
}

class ProfileViewModelFactory(private val repo: ProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
