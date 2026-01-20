package com.example.ccl_3.ui.notebook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.ccl_3.data.db.DatabaseProvider
import com.example.ccl_3.data.repository.BookmarkRepository
import com.example.ccl_3.model.BookmarkType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(navController: NavHostController) {
    val context = LocalContext.current
    val bookmarkRepository = remember {
        BookmarkRepository(DatabaseProvider.getDatabase(context).bookmarkDao())
    }
    val viewModel: NotebookViewModel = viewModel(
        factory = NotebookViewModelFactory(bookmarkRepository)
    )

    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf(BookmarkType.SHAPE, BookmarkType.FLAG)

    val shapeBookmarks = remember(bookmarks) { bookmarks.filter { it.contentType == BookmarkType.SHAPE } }
    val flagBookmarks = remember(bookmarks) { bookmarks.filter { it.contentType == BookmarkType.FLAG } }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bookmark NoteBook") },
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, type ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(if (type == BookmarkType.SHAPE) "Shapes" else "Flags") }
                    )
                }
            }

            val activeList = if (selectedTab == 0) shapeBookmarks else flagBookmarks

            Button(
                onClick = {
                    val type = if (selectedTab == 0) BookmarkType.SHAPE else BookmarkType.FLAG
                    navController.navigate("bookmarkQuiz/${type.name}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Play bookmarked ${if (selectedTab == 0) "shapes" else "flags"} quiz")
            }

            if (activeList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "No bookmarks yet",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = if (selectedTab == 0) {
                            "Bookmark shapes from quiz feedback to see them here."
                        } else {
                            "Bookmark flags from quiz feedback to see them here."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeList, key = { it.countryCode + it.contentType.name }) { bookmark ->
                        BookmarkRow(
                            name = bookmark.countryName,
                            flagUrl = bookmark.flagUrl,
                            shapeUrl = bookmark.shapeUrl,
                            contentType = bookmark.contentType,
                            onRemove = { viewModel.remove(bookmark) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarkRow(
    name: String,
    flagUrl: String?,
    shapeUrl: String?,
    contentType: BookmarkType,
    onRemove: () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { showConfirmDelete = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }
        }

        if (expanded) {
            if (contentType == BookmarkType.SHAPE) {
                shapeUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "$name shape",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (contentType == BookmarkType.FLAG) {
                flagUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "$name flag",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        if (showConfirmDelete) {
            AlertDialog(
                onDismissRequest = { showConfirmDelete = false },
                title = { Text("Confirm delete") },
                text = { Text("Are you sure you want to delete the bookmark?") },
                confirmButton = {
                    TextButton(onClick = {
                        onRemove()
                        showConfirmDelete = false
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDelete = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}
