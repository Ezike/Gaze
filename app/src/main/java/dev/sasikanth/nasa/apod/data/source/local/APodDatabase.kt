package dev.sasikanth.nasa.apod.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.sasikanth.nasa.apod.data.APod

@Database(entities = [APod::class], version = 1, exportSchema = false)
abstract class APodDatabase : RoomDatabase() {

    abstract val aPodDao: APodDao
}