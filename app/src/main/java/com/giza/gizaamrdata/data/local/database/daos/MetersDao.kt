package com.giza.gizaamrdata.data.local.database.daos

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.giza.gizaamrdata.models.Meter
import io.reactivex.Completable
import io.reactivex.Single

/**
 * @author hossam.
 */
@Dao
interface MetersDao {
    @Query("SELECT * FROM `Meters`")
    fun getMeters(): List<Meter>

    @Query("SELECT * FROM `Meters` WHERE id IN (:metersIds)")
    fun getById(metersIds: IntArray): Single<List<Meter>>

    @Insert(onConflict = REPLACE)
    fun insert(vararg meters: Meter): Completable

    @Update
    fun update(vararg meters: Meter)

    @Delete
    fun delete(vararg meters: Meter)

}