package com.dentalclinic.patientmanagement.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dentalclinic.patientmanagement.data.AppDatabase
import com.dentalclinic.patientmanagement.databinding.ActivityPatientListBinding
import com.dentalclinic.patientmanagement.model.Patient // Required for dummy data insertion if uncommented
import kotlinx.coroutines.launch

class PatientListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientListBinding
    private lateinit var patientListAdapter: PatientListAdapter
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val patientDao by lazy { database.patientDao() }

import android.content.Intent // Added for Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dentalclinic.patientmanagement.data.AppDatabase
import com.dentalclinic.patientmanagement.databinding.ActivityPatientListBinding
import com.dentalclinic.patientmanagement.model.Patient // Required for dummy data insertion if uncommented
import kotlinx.coroutines.launch

class PatientListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientListBinding
    private lateinit var patientListAdapter: PatientListAdapter
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val patientDao by lazy { database.patientDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observePatients()

        binding.addPatientFab.setOnClickListener {
            val intent = Intent(this, AddEditPatientActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        patientListAdapter = PatientListAdapter { patient ->
            // Handle patient item click - navigate to PatientDetailActivity
            val intent = Intent(this, PatientDetailActivity::class.java).apply {
                putExtra(PatientDetailActivity.EXTRA_PATIENT_ID, patient.id)
            }
            startActivity(intent)
        }
        binding.patientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PatientListActivity)
            adapter = patientListAdapter
        }
    }

    private fun observePatients() {
        lifecycleScope.launch {
            patientDao.getAllPatients().collect { patients ->
                patientListAdapter.submitList(patients)
            }
        }
    }
}
