package com.dentalclinic.patientmanagement.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog // Single import
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dentalclinic.patientmanagement.R
import com.dentalclinic.patientmanagement.data.AppDatabase
import com.dentalclinic.patientmanagement.databinding.ActivityPatientDetailBinding
import com.dentalclinic.patientmanagement.model.Patient
import com.dentalclinic.patientmanagement.model.Treatment
import com.dentalclinic.patientmanagement.ui.adapters.TreatmentListAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class PatientDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientDetailBinding
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val patientDao by lazy { database.patientDao() }
    private val treatmentDao by lazy { database.treatmentDao() }
    private lateinit var treatmentListAdapter: TreatmentListAdapter
    private var currentPatientId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currentPatientId = intent.getLongExtra(EXTRA_PATIENT_ID, -1L)

        if (currentPatientId == -1L) {
            Toast.makeText(this, "Patient ID not found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupTreatmentRecyclerView()
        observePatientDetails()
        observeTreatments()

        binding.addTreatmentFab.setOnClickListener {
            val intent = Intent(this, AddEditTreatmentActivity::class.java).apply {
                putExtra(AddEditTreatmentActivity.EXTRA_PATIENT_ID, currentPatientId)
            }
            startActivity(intent)
        }

        binding.totalSpendingTextView.text = getString(R.string.total_spent_placeholder, formatCurrency(0.0))
    }

    private fun observePatientDetails() {
        lifecycleScope.launch {
            patientDao.getPatientById(currentPatientId).collectLatest { patient ->
                patient?.let {
                    displayPatientData(it)
                } ?: run {
                    if(!isFinishing) {
                         Toast.makeText(this@PatientDetailActivity, "Patient not found.", Toast.LENGTH_LONG).show()
                         finish()
                    }
                }
            }
        }
    }

    private fun displayPatientData(patient: Patient) {
        binding.toolbarLayout.title = patient.name
        binding.detailPatientPhoneTextView.text = patient.phoneNumber
        binding.detailPatientAddressTextView.text = patient.address
        binding.detailPatientDobTextView.text = patient.dateOfBirth
        binding.detailPatientMedicalHistoryTextView.text = patient.medicalHistoryNotes ?: "No medical history notes."
    }

    private fun setupTreatmentRecyclerView() {
        treatmentListAdapter = TreatmentListAdapter { treatment ->
            if (treatment.isPaidInInstallments) {
                val intent = Intent(this, TreatmentInstallmentsActivity::class.java).apply {
                    putExtra(TreatmentInstallmentsActivity.EXTRA_TREATMENT_ID, treatment.id)
                }
                startActivity(intent)
            } else {
                val intent = Intent(this, AddEditTreatmentActivity::class.java).apply {
                    putExtra(AddEditTreatmentActivity.EXTRA_PATIENT_ID, currentPatientId)
                    putExtra(AddEditTreatmentActivity.EXTRA_TREATMENT_ID, treatment.id)
                }
                startActivity(intent)
            }
        }
        binding.treatmentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PatientDetailActivity)
            adapter = treatmentListAdapter
        }
    }

    private fun observeTreatments() {
        lifecycleScope.launch {
            treatmentDao.getTreatmentsForPatient(currentPatientId).collectLatest { treatments ->
                treatmentListAdapter.submitList(treatments)
                val totalCost = treatments.sumOf { it.cost }
                binding.totalSpendingTextView.text = getString(R.string.total_spent_placeholder, formatCurrency(totalCost))
                if (treatments.isEmpty()) {
                    binding.treatmentsRecyclerView.visibility = View.GONE
                } else {
                    binding.treatmentsRecyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(amount)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_patient_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_edit_patient -> {
                val intent = Intent(this, AddEditPatientActivity::class.java).apply {
                    putExtra(AddEditPatientActivity.EXTRA_PATIENT_ID, currentPatientId)
                }
                startActivity(intent)
                true
            }
            R.id.action_delete_patient -> {
                confirmDeletePatient()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun confirmDeletePatient() {
        AlertDialog.Builder(this)
            .setTitle("Delete Patient")
            .setMessage("Are you sure you want to delete this patient and all associated data? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deletePatient()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deletePatient() {
        lifecycleScope.launch {
            patientDao.deletePatientById(currentPatientId)
            Toast.makeText(this@PatientDetailActivity, "Patient deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {
        const val EXTRA_PATIENT_ID = "com.dentalclinic.patientmanagement.ui.EXTRA_PATIENT_ID"
    }
}
