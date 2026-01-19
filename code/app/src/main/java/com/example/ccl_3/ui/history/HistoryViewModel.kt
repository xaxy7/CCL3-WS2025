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

    private fun refresh() {
        viewModelScope.launch {
            _history.value = repository.getAllResults().sortedByDescending { it.id }
        }
    }
}

class HistoryViewModelFactory(
    private val repository: RoundResultRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
