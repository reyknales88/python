package com.dentalclinic.patientmanagement.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dentalclinic.patientmanagement.model.PaymentInstallment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentInstallmentDao {
    @Insert
    suspend fun insert(installment: PaymentInstallment): Long

    @Update
    suspend fun update(installment: PaymentInstallment)

    @Delete
    suspend fun delete(installment: PaymentInstallment)

    @Query("SELECT * FROM payment_installments WHERE treatmentId = :treatmentId ORDER BY dueDate ASC")
    fun getInstallmentsForTreatment(treatmentId: Long): Flow<List<PaymentInstallment>>

    @Query("SELECT * FROM payment_installments WHERE id = :id")
    fun getInstallmentById(id: Long): Flow<PaymentInstallment?>

    @Query("DELETE FROM payment_installments WHERE treatmentId = :treatmentId")
    suspend fun deleteInstallmentsForTreatment(treatmentId: Long)
}
