package dev.sasikanth.nasa.apod.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apods")
data class APod(
    @PrimaryKey
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "explanation")
    val explanation: String,
    @ColumnInfo(name = "copyright")
    val copyright: String?,
    @ColumnInfo(name = "media_type")
    val mediaType: String,
    @ColumnInfo(name = "service_version")
    val serviceVersion: String,
    @ColumnInfo(name = "url")
    val thumbnailUrl: String,
    @ColumnInfo(name = "hd_url")
    val hdUrl: String
)