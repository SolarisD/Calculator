package com.dmitryluzev.calculator.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface Dao {
    @Query("SELECT * FROM history_records ORDER BY ID DESC")
    fun getHistoryRecords(): LiveData<List<Record>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistoryRecord(history: Record)

    @Query("DELETE FROM history_records")
    fun clearHistoryRecords()

    @Query("SELECT * FROM states")
    fun getStates(): List<State>

    @Query("SELECT * FROM states WHERE id = :id")
    fun getState(id: Int): State?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertState(state: State)

    @Query("DELETE FROM states")
    fun clearStates()

}
