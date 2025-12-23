package com.example.filmler.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.filmler.data.local.FilmDetay
import com.example.filmler.data.local.FilmEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FilmViewModel,
    onFilmClick: (Int) -> Unit,
    onAddFilmClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val historyList by viewModel.searchHistory.collectAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    androidx.activity.compose.BackHandler(enabled = isSearchActive || searchQuery.isNotEmpty()) {
        viewModel.onSearchTextChange("")
        viewModel.onToggleSearch(false)
        focusManager.clearFocus()
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var filmToDelete by remember { mutableStateOf<FilmEntity?>(null) }

    if (showDeleteDialog && filmToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Filmi Sil") },
            text = { Text("${filmToDelete?.filmAdi} silinsin mi?") },
            confirmButton = {
                TextButton(onClick = {
                    filmToDelete?.let { viewModel.deleteFilm(it) }
                    showDeleteDialog = false
                }) { Text("Sil", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("İptal") }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFilmClick,
                containerColor = Color(0xFFFFC107),
                contentColor = Color.Black,
                shape = CircleShape
            ) { Icon(Icons.Default.Add, contentDescription = null) }
        },
        topBar = {
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchTextChange(it) },
                onSearch = { query ->
                    if (query.isNotBlank()) {
                        viewModel.onSearchTriggered(query)
                        viewModel.onToggleSearch(false)
                        focusManager.clearFocus()
                    }
                },
                active = isSearchActive,
                onActiveChange = { viewModel.onToggleSearch(it) },
                placeholder = {
                    Text("Film, oyuncu veya tür ara...", color = Color.White.copy(alpha = 0.5f))
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFFFC107))
                },
                trailingIcon = {
                    if (isSearchActive || searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            if (searchQuery.isNotEmpty()) {
                                viewModel.onSearchTextChange("")
                            } else {
                                viewModel.onToggleSearch(false)
                                focusManager.clearFocus()
                            }
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
                        }
                    }
                },
                colors = SearchBarDefaults.colors(
                    containerColor = if (isSearchActive) Color(0xFF141E30) else Color.White.copy(alpha = 0.1f),
                    inputFieldColors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFFFFC107),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isSearchActive) 0.dp else 16.dp)
                    .animateContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF141E30))
                ) {
                    if (historyList.isNotEmpty()) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Son Aramalar", color = Color.White.copy(alpha = 0.6f))
                                Text(
                                    "Temizle",
                                    color = Color(0xFFFF5252),
                                    modifier = Modifier.clickable { viewModel.clearHistory() }
                                )
                            }

                            LazyColumn {
                                items(historyList) { item ->
                                    ListItem(
                                        headlineContent = { Text(item, color = Color.White) },
                                        leadingContent = { Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFFFFC107)) },
                                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                        modifier = Modifier.clickable {
                                            viewModel.onSearchTriggered(item)
                                            viewModel.onToggleSearch(false)
                                            focusManager.clearFocus()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF141E30), Color(0xFF243B55))))
                .padding(padding)
        ) {
            when (val state = uiState) {
                is HomeScreenState.Yukleniyor -> { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFFFFC107)) }
                is HomeScreenState.KategoriliGorumum -> {
                    LazyColumn(contentPadding = PaddingValues(bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                        state.categories.forEach { (kategori, liste) ->
                            item {
                                KategoriRow(kategori, liste, onFilmClick) { filmToDelete = it; showDeleteDialog = true }
                            }
                        }
                    }
                }
                is HomeScreenState.AramaSonucu -> {
                    if (state.sonuclar.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Sonuç bulunamadı.", color = Color.White.copy(alpha = 0.6f))
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(150.dp),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.sonuclar) { filmDetay ->
                                FilmCardDikey(
                                    filmDetay = filmDetay,
                                    onClick = onFilmClick,
                                    onLongClick = { selectedFilmEntity ->
                                        filmToDelete = selectedFilmEntity
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun KategoriRow(baslik: String, filmler: List<FilmDetay>, onClick: (Int) -> Unit, onLongClick: (FilmEntity) -> Unit) {
    Column {
        Text(baslik, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = Color(0xFFFFC107))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filmler) { film -> FilmCardYatay(film, onClick, onLongClick) }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilmCardYatay(filmDetay: FilmDetay, onClick: (Int) -> Unit, onLongClick: (FilmEntity) -> Unit) {
    Surface(
        modifier = Modifier
            .width(150.dp)
            .padding(4.dp)
            .combinedClickable(
                onClick = { onClick(filmDetay.film.id) },
                onLongClick = { onLongClick(filmDetay.film) }
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E1E1E),
        shadowElevation = 8.dp
    ) {
        Column {
            Box(modifier = Modifier.height(210.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(filmDetay.film.filmresim)
                        .addHeader(
                            "User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                        )
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(bottomStart = 12.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = filmDetay.film.imdbPuani?.toString() ?: "0.0",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = filmDetay.film.filmAdi ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun FilmCardDikey(filmDetay: FilmDetay, onClick: (Int) -> Unit, onLongClick: (FilmEntity) -> Unit) {
    FilmCardYatay(filmDetay, onClick, onLongClick)
}