package com.giza.gizaamrdata.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.giza.gizaamrdata.GizaApp
import com.giza.gizaamrdata.data.C
import com.giza.gizaamrdata.data.local.database.converters.UrlsConverter
import com.giza.gizaamrdata.data.local.database.daos.MetersDao
import com.giza.gizaamrdata.data.local.database.migrations.Migrations
import com.giza.gizaamrdata.models.Address
import com.giza.gizaamrdata.models.Location
import com.giza.gizaamrdata.models.Meter
import com.giza.gizaamrdata.models.Owner

/**
 * @author hossam.
 */
@Database(
    entities = [
        Meter::class,
        Address::class,
        Location::class,
        Owner::class], version = 4, exportSchema = false
)
@TypeConverters(
    UrlsConverter::class
)
abstract class MetersDatabase : RoomDatabase() {
    abstract fun metersDao(): MetersDao

    companion object {
        private var INSTANCE: MetersDatabase? = null

        fun getInstance(): MetersDatabase {
            if (INSTANCE == null) {
                synchronized(MetersDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        GizaApp.instance.applicationContext,
                        MetersDatabase::class.java,
                        C.Database.NAME)
                        .fallbackToDestructiveMigration()
                        .addMigrations(Migrations.M1_2)
                        .addMigrations(Migrations.M2_3)
                        .build()
                    return INSTANCE as MetersDatabase
                }
            } else {
                return INSTANCE as MetersDatabase
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}