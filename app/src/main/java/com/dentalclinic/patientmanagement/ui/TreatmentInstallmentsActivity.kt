package com.dentalclinic.patientmanagement.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dentalclinic.patientmanagement.data.AppDatabase
import com.dentalclinic.patientmanagement.databinding.ActivityTreatmentInstallmentsBinding
import com.dentalclinic.patientmanagement.model.PaymentInstallment
import com.dentalclinic.patientmanagement.model.Treatment
import com.dentalclinic.patientmanagement.ui.adapters.InstallmentListAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class TreatmentInstallmentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTreatmentInstallmentsBinding
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val treatmentDao by lazy { database.treatmentDao() }
    private val paymentInstallmentDao by lazy { database.paymentInstallmentDao() }
    private lateinit var installmentListAdapter: InstallmentListAdapter

    private var currentTreatmentId: Long = -1L
    private var currentTreatment: Treatment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTreatmentInstallmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarInstallments)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currentTreatmentId = intent.getLongExtra(EXTRA_TREATMENT_ID, -1L)
        if (currentTreatmentId == -1L) {
            Toast.makeText(this, "Treatment ID not found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupInstallmentRecyclerView()
        observeTreatmentDetails()
        observeInstallments()

        binding.addInstallmentFab.setOnClickListener {
            val intent = Intent(this, AddInstallmentActivity::class.java).apply {
                putExtra(AddInstallmentActivity.EXTRA_TREATMENT_ID, currentTreatmentId)
            }
            startActivity(intent)
        }
    }

    private fun observeTreatmentDetails() {
        lifecycleScope.launch {
            treatmentDao.getTreatmentById(currentTreatmentId).collectLatest { treatment ->
                if (treatment == null) {
                    Toast.makeText(this@TreatmentInstallmentsActivity, "Treatment not found.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    currentTreatment = treatment
                    binding.toolbarInstallments.title = "Installments for ${treatment.description}"
                    binding.selectedTreatmentDescriptionTextView.text = "Treatment: ${treatment.description}"
                    binding.selectedTreatmentCostTextView.text = "Total Cost: ${formatCurrency(treatment.cost)}"
                    updateBalanceDisplay()
                }
            }
        }
    }

    private fun setupInstallmentRecyclerView() {
        installmentListAdapter = InstallmentListAdapter { installment, isChecked ->
            // Handle checkbox change: update the installment in the database
            val updatedInstallment = installment.copy(isPaid = isChecked)
            lifecycleScope.launch {
                paymentInstallmentDao.update(updatedInstallment)
                // Data will be re-observed by observeInstallments() and list updated
                // Optionally, show a toast or confirmation
            }
        }
        binding.installmentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TreatmentInstallmentsActivity)
            adapter = installmentListAdapter
        }
    }

    private fun observeInstallments() {
        lifecycleScope.launch {
            paymentInstallmentDao.getInstallmentsForTreatment(currentTreatmentId).collectLatest { installments ->
                installmentListAdapter.submitList(installments)
                if (installments.isEmpty()) {
                    binding.emptyViewInstallments.visibility = View.VISIBLE
                    binding.installmentsRecyclerView.visibility = View.GONE
                } else {
                    binding.emptyViewInstallments.visibility = View.GONE
                    binding.installmentsRecyclerView.visibility = View.VISIBLE
                }
                updateBalanceDisplay(installments)
            }
        }
    }

    private fun updateBalanceDisplay(installments: List<PaymentInstallment>? = null) {
        val actualInstallments = installments ?: installmentListAdapter.currentList
        val totalPaid = actualInstallments.filter { it.isPaid }.sumOf { it.amount }
        binding.totalPaidTextView.text = "Total Paid: ${formatCurrency(totalPaid)}"

        currentTreatment?.let {
            val remaining = it.cost - totalPaid
            binding.remainingBalanceTextView.text = "Remaining: ${formatCurrency(remaining)}"
        }
    }


    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(amount)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_TREATMENT_ID = "com.dentalclinic.patientmanagement.ui.EXTRA_TREATMENT_ID"
    }
}
