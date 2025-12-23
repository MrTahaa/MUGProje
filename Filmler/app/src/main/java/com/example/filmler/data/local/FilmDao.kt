package com.example.filmler.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmDao {
    @Query("SELECT * FROM filmler")
    fun getTumFilmler(): Flow<List<FilmEntity>>

    @Transaction
    @Query("SELECT * FROM filmler WHERE id = :filmId")
    fun getFilmDetay(filmId: Int): Flow<FilmDetay>

    @Transaction
    @Query("SELECT * FROM filmler")
    fun getTumFilmlerDetayli(): Flow<List<FilmDetay>>

    @Query("SELECT * FROM turler ORDER BY tur_adi ASC")
    fun getTumTurler(): Flow<List<TurEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilm(film: FilmEntity): Long

    @Query("SELECT * FROM yonetmenler WHERE ad_soyad = :adSoyad LIMIT 1")
    suspend fun getYonetmenByName(adSoyad: String): YonetmenEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertYonetmen(yonetmen: YonetmenEntity): Long

    @Query("SELECT * FROM oyuncular WHERE ad_soyad = :adSoyad LIMIT 1")
    suspend fun getOyuncuByName(adSoyad: String): OyuncuEntity?
    @Query("SELECT * FROM turler WHERE tur_adi = :turAdi LIMIT 1")
    suspend fun getTurByName(turAdi: String): TurEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTur(tur: TurEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFilmTurRelation(crossRef: FilmTurCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOyuncu(oyuncu: OyuncuEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFilmOyuncuRelation(crossRef: FilmOyuncuCrossRef)
    @Delete
    suspend fun deleteFilm(film: FilmEntity)



    @Transaction
    suspend fun filmEkleDetayli(
        filmAdi: String,
        ozet: String,
        vizyonYili: String,
        imdbPuani: Double?,
        resimUrl: String?,
        yonetmenAdi: String?,
        oyuncuIsimleri: List<String>,
        turIsimleri: List<String>
    ) {
        var yonetmenId: Int? = null
        if (!yonetmenAdi.isNullOrBlank()) {
            val mevcutYonetmen = getYonetmenByName(yonetmenAdi.trim())
            yonetmenId = mevcutYonetmen?.id ?: insertYonetmen(YonetmenEntity(adSoyad = yonetmenAdi.trim())).toInt()
        }

        val yeniFilm = FilmEntity(
            filmAdi = filmAdi,
            ozet = ozet,
            vizyonYili = vizyonYili,
            imdbPuani = imdbPuani,
            yonetmenId = yonetmenId,
            filmresim = resimUrl
        )
        val yeniFilmId = insertFilm(yeniFilm).toInt()

        for (isim in oyuncuIsimleri) {
            val temizIsim = isim.trim()
            if (temizIsim.isNotEmpty()) {
                val mevcutOyuncu = getOyuncuByName(temizIsim)
                val oyuncuId = mevcutOyuncu?.id ?: insertOyuncu(OyuncuEntity(adSoyad = temizIsim)).toInt()
                insertFilmOyuncuRelation(FilmOyuncuCrossRef(yeniFilmId, oyuncuId))
            }
        }

        for (turAdi in turIsimleri) {
            val temizTur = turAdi.trim()
            if (temizTur.isNotEmpty()) {
                val mevcutTur = getTurByName(temizTur)
                val turId = mevcutTur?.id ?: insertTur(TurEntity(turAdi = temizTur)).toInt()
                insertFilmTurRelation(FilmTurCrossRef(yeniFilmId, turId))
            }
        }
    }
}

