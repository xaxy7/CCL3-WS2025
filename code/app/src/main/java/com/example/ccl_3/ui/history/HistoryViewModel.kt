package com.example.ccl_3.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ccl_3.data.repository.RoundResultRepository
import com.example.ccl_3.model.RoundResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: RoundResultRepository) : ViewModel() {
    private val _history = MutableStateFlow<List<RoundResult>>(emptyList())
    val history: StateFlow<List<RoundResult>> = _history

    init {
        refresh()
    }

    fun deleteResult(id: Long) {
        viewModelScope.launch {
            repository.deleteResult(id)
            refresh()
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
            refresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _history.value = repository.getAllResults().sortedByDescending { it.id }
        }
    }
}


