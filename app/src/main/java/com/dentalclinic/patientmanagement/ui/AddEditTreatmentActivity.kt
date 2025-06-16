package com.dentalclinic.patientmanagement.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dentalclinic.patientmanagement.data.AppDatabase
import com.dentalclinic.patientmanagement.databinding.ActivityAddEditTreatmentBinding
import com.dentalclinic.patientmanagement.model.Treatment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddEditTreatmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTreatmentBinding
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val treatmentDao by lazy { database.treatmentDao() }
    private val paymentInstallmentDao by lazy { database.paymentInstallmentDao() } // Added for deleting installments

    private var currentPatientId: Long = -1L
    private var currentTreatmentId: Long? = null
    private var originalTreatment: Treatment? = null // To store the state before editing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTreatmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentPatientId = intent.getLongExtra(EXTRA_PATIENT_ID, -1L)
        if (currentPatientId == -1L) {
            Toast.makeText(this, "Patient ID is required.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        currentTreatmentId = intent.getLongExtra(EXTRA_TREATMENT_ID, 0L).takeIf { it != 0L }

        if (currentTreatmentId != null) {
            title = "Edit Treatment"
            loadTreatmentData(currentTreatmentId!!)
        } else {
            title = "Add New Treatment"
            // Set current date as default for new treatments
            binding.treatmentDateEditText.setText(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
        }

        binding.saveTreatmentButton.setOnClickListener {
            saveTreatment()
        }
    }

    private fun loadTreatmentData(treatmentId: Long) {
        lifecycleScope.launch {
            treatmentDao.getTreatmentById(treatmentId).collect { treatment ->
                treatment?.let {
                    originalTreatment = it // Store original treatment
                    binding.treatmentDescriptionEditText.setText(it.description)
                    binding.treatmentDateEditText.setText(it.date)
                    binding.treatmentCostEditText.setText(it.cost.toString())
                    binding.isPaidInInstallmentsCheckBox.isChecked = it.isPaidInInstallments
                }
            }
        }
    }

    private fun saveTreatment() {
        val description = binding.treatmentDescriptionEditText.text.toString().trim()
        val date = binding.treatmentDateEditText.text.toString().trim()
        val costString = binding.treatmentCostEditText.text.toString().trim()
        val isPaidInInstallments = binding.isPaidInInstallmentsCheckBox.isChecked

        if (description.isEmpty()) {
            binding.treatmentDescriptionInputLayout.error = "Description cannot be empty"
            return
        } else {
            binding.treatmentDescriptionInputLayout.error = null
        }

        if (date.isEmpty()) {
            // TODO: Add proper date validation (e.g., format YYYY-MM-DD)
            binding.treatmentDateInputLayout.error = "Date cannot be empty"
            return
        } else {
            binding.treatmentDateInputLayout.error = null
        }

        val cost = costString.toDoubleOrNull()
        if (cost == null || cost < 0) {
            binding.treatmentCostInputLayout.error = "Invalid cost"
            return
        } else {
            binding.treatmentCostInputLayout.error = null
        }

        val treatment = Treatment(
            id = currentTreatmentId ?: 0,
            patientId = currentPatientId,
            description = description,
            date = date,
            cost = cost,
            isPaidInInstallments = isPaidInInstallments
        )

        lifecycleScope.launch {
            if (currentTreatmentId == null) { // New treatment
                treatmentDao.insert(treatment)
                Toast.makeText(this@AddEditTreatmentActivity, "Treatment saved", Toast.LENGTH_SHORT).show()
            } else { // Editing existing treatment
                treatmentDao.update(treatment)
                Toast.makeText(this@AddEditTreatmentActivity, "Treatment updated", Toast.LENGTH_SHORT).show()

                // Check if isPaidInInstallments changed from true to false
                if (originalTreatment?.isPaidInInstallments == true && !treatment.isPaidInInstallments) {
                    paymentInstallmentDao.deleteInstallmentsForTreatment(treatment.id)
                    Toast.makeText(this@AddEditTreatmentActivity, "Existing installments deleted.", Toast.LENGTH_LONG).show()
                }
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_PATIENT_ID = "com.dentalclinic.patientmanagement.ui.EXTRA_PATIENT_ID"
        const val EXTRA_TREATMENT_ID = "com.dentalclinic.patientmanagement.ui.EXTRA_TREATMENT_ID"
    }
}
