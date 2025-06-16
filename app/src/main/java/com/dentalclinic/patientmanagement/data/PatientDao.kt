package com.dentalclinic.patientmanagement.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dentalclinic.patientmanagement.model.Patient
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Insert
    suspend fun insert(patient: Patient): Long

    @Update
    suspend fun update(patient: Patient)

    @Delete
    suspend fun delete(patient: Patient)

    @Query("SELECT * FROM patients WHERE id = :id")
    fun getPatientById(id: Long): Flow<Patient?>

    @Query("SELECT * FROM patients ORDER BY name ASC")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("DELETE FROM patients WHERE id = :patientId")
    suspend fun deletePatientById(patientId: Long)
}
