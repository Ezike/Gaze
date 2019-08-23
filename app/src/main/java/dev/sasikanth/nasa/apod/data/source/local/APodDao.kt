package dev.sasikanth.nasa.apod.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sasikanth.nasa.apod.data.APod

@Dao
interface APodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAPod(apod: APod)

    @Query("SELECT * FROM apods ORDER BY date DESC")
    fun getAPods(): LiveData<List<APod>>

    @Query("SELECT * FROM apods WHERE date == :date")
    suspend fun getAPod(date: String): APod?
}