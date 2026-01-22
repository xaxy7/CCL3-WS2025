package com.example.ccl_3.ui.notebook

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl_3.data.db.BookmarkEntity
import com.example.ccl_3.data.db.DatabaseProvider
import com.example.ccl_3.data.repository.BookmarkRepository
import com.example.ccl_3.model.BookmarkType
import com.example.ccl_3.ui.components.AppTopBar
import com.example.ccl_3.ui.components.NavigationIcon
import com.example.ccl_3.ui.navigation.LocalAppNavigator
import coil.compose.AsyncImage
import com.example.ccl_3.R
import com.example.ccl_3.ui.region.regionToImage
import com.example.ccl_3.ui.theme.AppColors

@Composable
fun NotebookScreen(
    onStartQuiz: (BookmarkType) -> Unit
) {
    val appNavigator = LocalAppNavigator.current
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
            AppTopBar(
                title = "Bookmark NoteBook",
                navigationIcon = NavigationIcon.Back,
                onNavigationClick = { appNavigator.popBackStack() },

            )
        },
        containerColor = AppColors.Primary
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()

        ) {
            TabRow(selectedTabIndex = selectedTab, ) {
                tabs.forEachIndexed { index, type ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        modifier = Modifier.background(AppColors.NavBg),
                        text = { Text(if (type == BookmarkType.SHAPE) "Shapes" else "Flags", color = AppColors.TextWhite)
                        }
                    )
                }
            }

            val activeList = if (selectedTab == 0) shapeBookmarks else flagBookmarks
            val activeType = if (selectedTab == 0) BookmarkType.SHAPE else BookmarkType.FLAG

            if (activeList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "No bookmarks yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.TextWhite
                    )
                    Text(
                        text = "Play a round to see your bookmarks here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextWhite
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(activeList, key = { "${it.countryCode}_${it.contentType}" }) { bookmark ->
                        BookmarkCard(
                            bookmark = bookmark,
                            onDelete = { viewModel.remove(bookmark) }
                        )
                    }
                }
                Button(
                    onClick = { onStartQuiz(activeType) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Start quiz with these bookmarks")
                }
            }
        }
    }
}

@Composable
private fun BookmarkCard(
    bookmark: BookmarkEntity,
    onDelete: () -> Unit
) {
    BookmarkRow(
        name = bookmark.countryName,
        flagUrl = bookmark.flagUrl,
        shapeUrl = bookmark.shapeUrl,
        contentType = bookmark.contentType,
        onRemove = onDelete
    )
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

        // 🔹 HEADER ROW
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.TextWhite,
                modifier = Modifier.weight(1f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = { showConfirmDelete = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = AppColors.TextWhite
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = AppColors.TextWhite
                    )
                }
            }
        }

        // 🔹 EXPANDED CONTENT (BELOW ROW)
        if (expanded) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                color = AppColors.NavBg,
                border = BorderStroke(1.dp, AppColors.Stroke),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
            }
        }

        // 🔹 DELETE CONFIRM
        if (showConfirmDelete) {
            AlertDialog(
                onDismissRequest = { showConfirmDelete = false },
                title = { Text("Confirm delete") },
                text = { Text("Are you sure you want to delete the bookmark?") },
                confirmButton = {
                    TextButton(onClick = {
                        onRemove()
                        showConfirmDelete = false
                    }) { Text("Yes") }
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
