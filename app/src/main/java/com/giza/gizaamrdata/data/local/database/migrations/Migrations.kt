package com.giza.gizaamrdata.data.local.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val M1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `Owner`  ADD COLUMN `old_meter_number` TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE `Owner`  ADD COLUMN `old_meter_reading` TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE `Owner`  ADD COLUMN `electricity_meter` TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE `Owner`  ADD COLUMN `custom1` TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE `Owner`  ADD COLUMN `custom2` TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE `Owner`  ADD COLUMN `custom3` TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE `Owner`  ADD COLUMN `custom4` TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE `Owner`  ADD COLUMN `custom5` TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE `Owner`  ADD COLUMN `custom6` TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE `Owner`  ADD COLUMN `custom7` TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE `Owner`  ADD COLUMN `custom8` TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE `Owner`  ADD COLUMN `custom9` TEXT NOT NULL DEFAULT ''")
            database.execSQL("ALTER TABLE `Owner`  ADD COLUMN `custom10` TEXT NOT NULL DEFAULT ''")
        }
    }

    val M2_3: Migration = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `Meters`  ADD COLUMN `actions` TEXT NOT NULL DEFAULT ''")
        }
    }
}