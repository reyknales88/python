package com.dentalclinic.patientmanagement.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dentalclinic.patientmanagement.data.AppDatabase
import com.dentalclinic.patientmanagement.databinding.ActivityAddEditPatientBinding
import com.dentalclinic.patientmanagement.model.Patient
import kotlinx.coroutines.launch

class AddEditPatientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditPatientBinding
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val patientDao by lazy { database.patientDao() }

    // For editing, will be set if an ID is passed via Intent
    private var currentPatientId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if we are editing an existing patient
        currentPatientId = intent.getLongExtra(EXTRA_PATIENT_ID, 0L).takeIf { it != 0L }

        if (currentPatientId != null) {
            title = "Edit Patient"
            loadPatientData(currentPatientId!!)
        } else {
            title = "Add New Patient"
        }

        binding.savePatientButton.setOnClickListener {
            savePatient()
        }
    }

    private fun loadPatientData(patientId: Long) {
        lifecycleScope.launch {
            patientDao.getPatientById(patientId).collect { patient ->
                patient?.let {
                    binding.patientNameEditText.setText(it.name)
                    binding.patientPhoneEditText.setText(it.phoneNumber)
                    binding.patientAddressEditText.setText(it.address)
                    binding.patientDobEditText.setText(it.dateOfBirth)
                    binding.patientMedicalHistoryEditText.setText(it.medicalHistoryNotes)
                }
            }
        }
    }

    private fun savePatient() {
        val name = binding.patientNameEditText.text.toString().trim()
        val phone = binding.patientPhoneEditText.text.toString().trim()
        val address = binding.patientAddressEditText.text.toString().trim()
        val dob = binding.patientDobEditText.text.toString().trim()
        val medicalHistory = binding.patientMedicalHistoryEditText.text.toString().trim()

        if (name.isEmpty()) {
            binding.patientNameInputLayout.error = "Name cannot be empty"
            return
        } else {
            binding.patientNameInputLayout.error = null
        }

        if (phone.isEmpty()) {
            binding.patientPhoneInputLayout.error = "Phone number cannot be empty"
            return
        } else {
            binding.patientPhoneInputLayout.error = null
        }

        if (address.isEmpty()) {
            binding.patientAddressInputLayout.error = "Address cannot be empty"
            return
        } else {
            binding.patientAddressInputLayout.error = null
        }

        if (dob.isEmpty()) {
            binding.patientDobInputLayout.error = "Date of birth cannot be empty"
            return
        } else {
            binding.patientDobInputLayout.error = null
        }


        val patient = Patient(
            id = currentPatientId ?: 0, // If currentPatientId is null, it's a new patient (id 0 for autoGenerate)
            name = name,
            phoneNumber = phone,
            address = address,
            dateOfBirth = dob,
            medicalHistoryNotes = medicalHistory.takeIf { it.isNotEmpty() }
        )

        lifecycleScope.launch {
            if (currentPatientId == null) {
                patientDao.insert(patient)
                Toast.makeText(this@AddEditPatientActivity, "Patient saved", Toast.LENGTH_SHORT).show()
            } else {
                patientDao.update(patient)
                Toast.makeText(this@AddEditPatientActivity, "Patient updated", Toast.LENGTH_SHORT).show()
            }
            finish() // Return to the previous activity
        }
    }

    companion object {
        const val EXTRA_PATIENT_ID = "com.dentalclinic.patientmanagement.ui.EXTRA_PATIENT_ID"
    }
}
