package com.example.ccl_3.ui.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl_3.data.repository.RoundResultRepository
import kotlinx.coroutines.launch

class DebugViewModel(
    private val repository: RoundResultRepository
) : ViewModel() {

    fun insertDebugRound() {
        viewModelScope.launch {
            repository.insertDebugRound()
        }
    }
}
