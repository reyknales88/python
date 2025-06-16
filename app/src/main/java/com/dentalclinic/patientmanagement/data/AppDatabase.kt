package com.dentalclinic.patientmanagement.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dentalclinic.patientmanagement.model.Patient
import com.dentalclinic.patientmanagement.model.Treatment
import com.dentalclinic.patientmanagement.model.PaymentInstallment

@Database(
    entities = [Patient::class, Treatment::class, PaymentInstallment::class],
    version = 1,
    exportSchema = false // Recommended to disable for simplicity in this case
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun treatmentDao(): TreatmentDao
    abstract fun paymentInstallmentDao(): PaymentInstallmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dental_clinic_database"
                )
                // Wipes and rebuilds instead of migrating if no Migration object.
                // Migration is not part of this subtask.
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
