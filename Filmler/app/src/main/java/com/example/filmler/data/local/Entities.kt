package com.example.filmler.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "yonetmenler")
data class YonetmenEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "ad_soyad") val adSoyad: String?
)

@Entity(tableName = "oyuncular")
data class OyuncuEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "ad_soyad") val adSoyad: String?
)

@Entity(tableName = "turler")
data class TurEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "tur_adi") val turAdi: String?
)

@Entity(
    tableName = "filmler",
    foreignKeys = [
        ForeignKey(
            entity = YonetmenEntity::class,
            parentColumns = ["id"],
            childColumns = ["yonetmen_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class FilmEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = "film_adi") val filmAdi: String?,
    val ozet: String?,
    @ColumnInfo(name = "vizyon_yili") val vizyonYili: String?,
    @ColumnInfo(name = "imdb_puani") val imdbPuani: Double?,
    @ColumnInfo(name = "yonetmen_id") val yonetmenId: Int?,
    val filmresim: String?
)

@Entity(
    tableName = "filmoyuncular",
    primaryKeys = ["film_id", "oyuncu_id"],
    foreignKeys = [
        ForeignKey(entity = FilmEntity::class, parentColumns = ["id"], childColumns = ["film_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = OyuncuEntity::class, parentColumns = ["id"], childColumns = ["oyuncu_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class FilmOyuncuCrossRef(
    @ColumnInfo(name = "film_id") val filmId: Int,
    @ColumnInfo(name = "oyuncu_id") val oyuncuId: Int
)

@Entity(
    tableName = "filmturleri",
    primaryKeys = ["film_id", "tur_id"],
    foreignKeys = [
        ForeignKey(entity = FilmEntity::class, parentColumns = ["id"], childColumns = ["film_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = TurEntity::class, parentColumns = ["id"], childColumns = ["tur_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class FilmTurCrossRef(
    @ColumnInfo(name = "film_id") val filmId: Int,
    @ColumnInfo(name = "tur_id") val turId: Int
)

data class FilmDetay(
    @Embedded val film: FilmEntity,

    @Relation(parentColumn = "yonetmen_id", entityColumn = "id")
    val yonetmen: YonetmenEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(value = FilmOyuncuCrossRef::class, parentColumn = "film_id", entityColumn = "oyuncu_id")
    )
    val oyuncular: List<OyuncuEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(value = FilmTurCrossRef::class, parentColumn = "film_id", entityColumn = "tur_id")
    )
    val turler: List<TurEntity>
)