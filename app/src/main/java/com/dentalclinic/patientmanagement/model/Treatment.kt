package com.dentalclinic.patientmanagement.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "treatments",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["id"],
        childColumns = ["patientId"],
        onDelete = ForeignKey.CASCADE // Defines action on deletion of parent Patient
    )]
)
data class Treatment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: Long, // Foreign key
    val description: String,
    val date: String,
    val cost: Double,
    val isPaidInInstallments: Boolean
) : Parcelable
