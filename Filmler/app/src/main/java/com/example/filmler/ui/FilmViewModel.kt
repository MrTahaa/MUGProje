package com.example.filmler.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.filmler.data.local.FilmDetay
import com.example.filmler.data.local.FilmEntity
import com.example.filmler.data.local.TurEntity
import com.example.filmler.data.repository.FilmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FilmViewModel(
    private val repository: FilmRepository,
    private val searchPrefs: SearchPreferences


) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val isSearchActive = MutableStateFlow(false)

    val searchHistory = MutableStateFlow(searchPrefs.getSearchHistory())

    val uiState: StateFlow<HomeScreenState> = combine(
        repository.tumFilmler,
        searchQuery
    ) { filmler, query ->
        if (query.isBlank()) {
            val kategoriler = filmler
                .flatMap { film ->
                    if (film.turler.isEmpty()) listOf("Diğer" to film)
                    else film.turler.map { (it.turAdi ?: "Genel") to film }
                }
                .groupBy({ it.first }, { it.second })
            HomeScreenState.KategoriliGorumum(kategoriler)
        } else {
            val filtered = filmler.filter { detay ->
                detay.film.filmAdi?.contains(query, ignoreCase = true) == true ||
                        detay.oyuncular.any { it.adSoyad?.contains(query, ignoreCase = true) == true } ||
                        detay.turler.any { it.turAdi?.contains(query, ignoreCase = true) == true }
            }
            HomeScreenState.AramaSonucu(filtered)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeScreenState.Yukleniyor
    )

    fun onSearchTriggered(query: String) {
        if (query.isNotBlank()) {
            searchPrefs.addSearchTerm(query)
            searchHistory.value = searchPrefs.getSearchHistory()
            searchQuery.value = query
            isSearchActive.value = false
        }
    }

    fun onSearchTextChange(newText: String) {
        searchQuery.value = newText
    }

    fun onToggleSearch(active: Boolean) {
        isSearchActive.value = active
        if (active) {
            searchHistory.value = searchPrefs.getSearchHistory()
        }
    }

    fun clearHistory() {
        searchPrefs.clearHistory()
        searchHistory.value = emptyList()
    }

    fun getFilmDetay(id: Int) = repository.getFilmById(id)
    fun addFilm(ad: String, yil: String, ozet: String, resimUrl: String) {
        viewModelScope.launch {
            val yeniId = (1000..99999).random()

            val yeniFilm = FilmEntity(
                id = yeniId,
                filmAdi = ad,
                vizyonYili = yil,
                ozet = ozet,
                filmresim = resimUrl,
                imdbPuani = 0.0,
                yonetmenId = null
            )
            repository.insertFilm(yeniFilm)
        }
    }

    fun deleteFilm(film: FilmEntity) {
        viewModelScope.launch {
            repository.deleteFilm(film)
        }
    }

    // FilmViewModel.kt
    val mevcutTurler: StateFlow<List<TurEntity>> = repository.tumTurler
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun filmEkle(
        ad: String,
        yil: String,
        ozet: String,
        imdbStr: String,
        resim: String,
        yonetmen: String,
        oyuncular: List<String>,
        secilenTurler: List<String>
    ) {
        viewModelScope.launch {
            val imdbDouble = imdbStr.toDoubleOrNull()
            repository.filmEkleDetayli(
                ad = ad,
                yil = yil,
                ozet = ozet,
                imdb = imdbDouble,
                resim = resim,
                yonetmen = yonetmen,
                oyuncular = oyuncular,
                turler = secilenTurler
            )
        }
    }
}

sealed class HomeScreenState {
    object Yukleniyor : HomeScreenState()
    data class KategoriliGorumum(val categories: Map<String, List<FilmDetay>>) : HomeScreenState()
    data class AramaSonucu(val sonuclar: List<FilmDetay>) : HomeScreenState()
}

class FilmViewModelFactory(
    private val repository: FilmRepository,
    private val searchPrefs: SearchPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FilmViewModel(repository, searchPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}