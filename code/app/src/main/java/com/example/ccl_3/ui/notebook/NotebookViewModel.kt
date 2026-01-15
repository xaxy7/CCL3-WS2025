package com.example.ccl_3.ui.notebook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl_3.data.db.BookmarkEntity
import com.example.ccl_3.data.repository.BookmarkRepository
import com.example.ccl_3.model.BookmarkType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotebookViewModel(
    private val repository: BookmarkRepository
) : ViewModel() {

    private val _bookmarks = MutableStateFlow<List<BookmarkEntity>>(emptyList())
    val bookmarks: StateFlow<List<BookmarkEntity>> = _bookmarks.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeBookmarks().collectLatest { list ->
                _bookmarks.value = list
            }
        }
    }

    fun remove(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            repository.removeBookmark(
                code = bookmark.countryCode,
                type = bookmark.contentType
            )
        }
    }
}
