package com.dentalclinic.patientmanagement.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "payment_installments",
    foreignKeys = [ForeignKey(
        entity = Treatment::class,
        parentColumns = ["id"],
        childColumns = ["treatmentId"],
        onDelete = ForeignKey.CASCADE // Defines action on deletion of parent Treatment
    )]
)
data class PaymentInstallment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val treatmentId: Long, // Foreign key
    val amount: Double,
    val dueDate: String,
    val isPaid: Boolean = false
) : Parcelable
