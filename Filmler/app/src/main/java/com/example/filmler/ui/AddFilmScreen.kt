package com.example.filmler.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFilmScreen(
    viewModel: FilmViewModel,
    onBackClick: () -> Unit
) {
    var ad by remember { mutableStateOf("") }
    var yil by remember { mutableStateOf("") }
    var imdb by remember { mutableStateOf("") }
    var resimUrl by remember { mutableStateOf("") }
    var ozet by remember { mutableStateOf("") }
    var yonetmenAdi by remember { mutableStateOf("") }
    var oyuncuListesi by remember { mutableStateOf(listOf<String>()) }

    val dbTurler by viewModel.mevcutTurler.collectAsState()
    var secilenTurler by remember { mutableStateOf(setOf<String>()) }

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yeni Film Ekle", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF141E30), Color(0xFF243B55))
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp, 200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .border(1.dp, Color(0xFFFFC107), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (resimUrl.isNotEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(resimUrl)
                                .addHeader(
                                    "User-Agent",
                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
                                )
                                .crossfade(true)
                                .build(),
                            contentDescription = "Film Afişi",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                            Text("Afiş Önizleme", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                ModernTextField(
                    value = resimUrl,
                    onValueChange = { resimUrl = it },
                    label = "Resim Linki (URL)",
                    icon = Icons.Default.Link
                )
                ModernTextField(
                    value = ad,
                    onValueChange = { ad = it },
                    label = "Film Adı",
                    icon = Icons.Default.Movie
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        ModernTextField(
                            value = yil,
                            onValueChange = { if (it.length <= 4) yil = it },
                            label = "Yıl",
                            icon = Icons.Default.CalendarToday,
                            keyboardType = KeyboardType.Number
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ModernTextField(
                            value = imdb,
                            onValueChange = { imdb = it },
                            label = "IMDb",
                            icon = Icons.Default.Star,
                            keyboardType = KeyboardType.Number
                        )
                    }
                }
                ModernTextField(
                    value = yonetmenAdi,
                    onValueChange = { yonetmenAdi = it },
                    label = "Yönetmen",
                    icon = Icons.Default.Person
                )
                ModernTextField(
                    value = ozet,
                    onValueChange = { ozet = it },
                    label = "Özet",
                    icon = Icons.Default.Description,
                    singleLine = false,
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(4.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Tür Seçimi",
                        color = Color(0xFFFFC107),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (dbTurler.isEmpty()) {
                        Text("Veritabanında kayıtlı tür bulunamadı.", color = Color.Gray, fontSize = 12.sp)
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            dbTurler.forEach { tur ->
                                val turAdi = tur.turAdi ?: ""
                                val isSelected = secilenTurler.contains(turAdi)

                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        secilenTurler = if (isSelected) {
                                            secilenTurler - turAdi
                                        } else {
                                            secilenTurler + turAdi
                                        }
                                    },
                                    label = { Text(turAdi) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFFFC107),
                                        selectedLabelColor = Color.Black,
                                        containerColor = Color(0xFF37474F),
                                        labelColor = Color.White
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (isSelected) Color.Transparent else Color.Gray
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Divider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(4.dp))
                ListeEklemeBileseni(
                    baslik = "Oyuncu",
                    eklenenlerListesi = oyuncuListesi,
                    onListeGuncellendi = { oyuncuListesi = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.filmEkle(
                            ad = ad,
                            yil = yil,
                            ozet = ozet,
                            imdbStr = imdb,
                            resim = resimUrl,
                            yonetmen = yonetmenAdi,
                            oyuncular = oyuncuListesi,
                            secilenTurler = secilenTurler.toList()
                        )
                        focusManager.clearFocus()
                        onBackClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                    enabled = ad.isNotEmpty()
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Filmi Kaydet", color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color(0xFFFFC107)) },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color(0xFFFFC107),
            focusedBorderColor = Color(0xFFFFC107),
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
            focusedLabelColor = Color(0xFFFFC107),
            unfocusedLabelColor = Color.LightGray
        )
    )
}

@Composable
fun ListeEklemeBileseni(
    baslik: String,
    eklenenlerListesi: List<String>,
    onListeGuncellendi: (List<String>) -> Unit
) {
    var girilenMetin by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = girilenMetin,
                onValueChange = { girilenMetin = it },
                label = { Text("$baslik Ekle") },
                leadingIcon = { Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFFFFC107)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardActions = KeyboardActions(onDone = {
                    if (girilenMetin.isNotBlank()) {
                        onListeGuncellendi(eklenenlerListesi + girilenMetin.trim())
                        girilenMetin = ""
                    }
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFFC107),
                    focusedBorderColor = Color(0xFFFFC107),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    focusedLabelColor = Color(0xFFFFC107),
                    unfocusedLabelColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (girilenMetin.isNotBlank()) {
                        onListeGuncellendi(eklenenlerListesi + girilenMetin.trim())
                        girilenMetin = ""
                    }
                },
                modifier = Modifier
                    .background(Color(0xFFFFC107), CircleShape)
                    .size(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Ekle", tint = Color.Black)
            }
        }

        if (eklenenlerListesi.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(eklenenlerListesi) { isim ->
                    InputChip(
                        selected = true,
                        onClick = {},
                        label = { Text(isim, color = Color.White) },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = Color(0xFF37474F),
                            selectedLabelColor = Color.White
                        ),
                        border = InputChipDefaults.inputChipBorder(
                            enabled = true,
                            selected = true,
                            selectedBorderColor = Color(0xFFFFC107)
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = { onListeGuncellendi(eklenenlerListesi - isim) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Sil",
                                    tint = Color(0xFFFFC107)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

