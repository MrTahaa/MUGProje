package com.example.filmler.data.repository

import com.example.filmler.data.local.FilmDao
import com.example.filmler.data.local.FilmDetay
import com.example.filmler.data.local.FilmEntity
import com.example.filmler.data.local.TurEntity
import kotlinx.coroutines.flow.Flow

class FilmRepository(private val dao: FilmDao) {
    val tumFilmler: Flow<List<FilmDetay>> = dao.getTumFilmlerDetayli()

    val tumTurler: Flow<List<TurEntity>> = dao.getTumTurler()

    fun getFilmById(id: Int): Flow<FilmDetay> = dao.getFilmDetay(id)
    suspend fun insertFilm(film: FilmEntity) = dao.insertFilm(film)
    suspend fun deleteFilm(film: FilmEntity) = dao.deleteFilm(film)

    suspend fun filmEkleDetayli(
        ad: String,
        yil: String,
        ozet: String,
        imdb: Double?,
        resim: String?,
        yonetmen: String,
        oyuncular: List<String>,
        turler: List<String>
    ) {
        dao.filmEkleDetayli(
            filmAdi = ad,
            ozet = ozet,
            vizyonYili = yil,
            imdbPuani = imdb,
            resimUrl = resim,
            yonetmenAdi = yonetmen,
            oyuncuIsimleri = oyuncular,
            turIsimleri = turler
        )
    }
}