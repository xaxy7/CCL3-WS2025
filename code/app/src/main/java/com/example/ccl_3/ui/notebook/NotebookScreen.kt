package com.example.ccl_3.ui.notebook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.ccl_3.data.db.DatabaseProvider
import com.example.ccl_3.data.repository.BookmarkRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(navController: NavHostController) {
    val context = navController.context
    val bookmarkRepository = remember {
        BookmarkRepository(DatabaseProvider.getDatabase(context).bookmarkDao())
    }
    val viewModel: NotebookViewModel = viewModel(
        factory = NotebookViewModelFactory(bookmarkRepository)
    )

    val bookmarks by viewModel.bookmarks.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notebook") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (bookmarks.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "No bookmarks yet",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Bookmark flags or shapes from quiz feedback to see them here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookmarks, key = { it.countryCode }) { bookmark ->
                    BookmarkRow(
                        name = bookmark.countryName,
                        flagUrl = bookmark.flagUrl,
                        shapeUrl = bookmark.shapeUrl,
                        onRemove = { viewModel.remove(bookmark) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarkRow(
    name: String,
    flagUrl: String,
    shapeUrl: String?,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = name, style = MaterialTheme.typography.titleMedium)
        shapeUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = "$name shape",
                modifier = Modifier.fillMaxWidth()
            )
        }
        AsyncImage(
            model = flagUrl,
            contentDescription = "$name flag",
            modifier = Modifier
                .fillMaxWidth()
        )
        OutlinedButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove")
            Text("Remove", modifier = Modifier.padding(start = 8.dp))
        }
    }
}
