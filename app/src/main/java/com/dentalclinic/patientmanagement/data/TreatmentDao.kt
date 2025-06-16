package com.dentalclinic.patientmanagement.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dentalclinic.patientmanagement.model.Treatment
import kotlinx.coroutines.flow.Flow

@Dao
interface TreatmentDao {
    @Insert
    suspend fun insert(treatment: Treatment): Long

    @Update
    suspend fun update(treatment: Treatment)

    @Delete
    suspend fun delete(treatment: Treatment)

    @Query("SELECT * FROM treatments WHERE patientId = :patientId ORDER BY date DESC")
    fun getTreatmentsForPatient(patientId: Long): Flow<List<Treatment>>

    @Query("SELECT * FROM treatments WHERE id = :id")
    fun getTreatmentById(id: Long): Flow<Treatment?>
}
