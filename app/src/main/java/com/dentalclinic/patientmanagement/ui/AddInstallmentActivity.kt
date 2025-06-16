package com.dentalclinic.patientmanagement.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dentalclinic.patientmanagement.data.AppDatabase
import com.dentalclinic.patientmanagement.databinding.ActivityAddInstallmentBinding
import com.dentalclinic.patientmanagement.model.PaymentInstallment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddInstallmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddInstallmentBinding
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val paymentInstallmentDao by lazy { database.paymentInstallmentDao() }

    private var currentTreatmentId: Long = -1L
    // private var currentInstallmentId: Long? = null // For editing later if needed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddInstallmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentTreatmentId = intent.getLongExtra(EXTRA_TREATMENT_ID, -1L)
        if (currentTreatmentId == -1L) {
            Toast.makeText(this, "Treatment ID is required.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        title = "Add New Installment"
        // Set current date as default for new installments
        binding.installmentDueDateEditText.setText(
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )

        binding.saveInstallmentButton.setOnClickListener {
            saveInstallment()
        }
    }

    private fun saveInstallment() {
        val amountString = binding.installmentAmountEditText.text.toString().trim()
        val dueDate = binding.installmentDueDateEditText.text.toString().trim()
        val isPaid = binding.installmentIsPaidCheckBox.isChecked


        if (dueDate.isEmpty()) {
            // TODO: Add proper date validation
            binding.installmentDueDateInputLayout.error = "Due date cannot be empty"
            return
        } else {
            binding.installmentDueDateInputLayout.error = null
        }

        val amount = amountString.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.installmentAmountInputLayout.error = "Invalid amount"
            return
        } else {
            binding.installmentAmountInputLayout.error = null
        }

        val installment = PaymentInstallment(
            // id = currentInstallmentId ?: 0, // For editing
            treatmentId = currentTreatmentId,
            amount = amount,
            dueDate = dueDate,
            isPaid = isPaid
        )

        lifecycleScope.launch {
            // if (currentInstallmentId == null) {
            paymentInstallmentDao.insert(installment)
            Toast.makeText(this@AddInstallmentActivity, "Installment saved", Toast.LENGTH_SHORT).show()
            // } else {
            //     paymentInstallmentDao.update(installment)
            //     Toast.makeText(this@AddInstallmentActivity, "Installment updated", Toast.LENGTH_SHORT).show()
            // }
            finish()
        }
    }

    companion object {
        const val EXTRA_TREATMENT_ID = "com.dentalclinic.patientmanagement.ui.EXTRA_TREATMENT_ID"
        // const val EXTRA_INSTALLMENT_ID = "com.dentalclinic.patientmanagement.ui.EXTRA_INSTALLMENT_ID" // For editing
    }
}
