package com.example.ccl_3.ui.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ccl_3.data.repository.RoundResultRepository

class DebugViewModelFactory(
    private val repository: RoundResultRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DebugViewModel::class.java)) {
            return DebugViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}