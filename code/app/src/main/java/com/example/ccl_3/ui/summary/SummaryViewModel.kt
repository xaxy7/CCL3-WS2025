package com.example.ccl_3.ui.summary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl_3.data.repository.RoundResultRepository
import com.example.ccl_3.model.RoundResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SummaryViewModel(
    private val repository: RoundResultRepository
) : ViewModel() {

    private val _result = MutableStateFlow<RoundResult?>(null)
    val result: StateFlow<RoundResult?> = _result

    init {
        viewModelScope.launch {
            _result.value = repository.getLastResult()
        }
    }
}
