package com.dentalclinic.patientmanagement.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val address: String,
    val dateOfBirth: String,
    val medicalHistoryNotes: String? = null
) : Parcelable
