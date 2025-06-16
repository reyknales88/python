package com.dentalclinic.patientmanagement.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dentalclinic.patientmanagement.databinding.ItemInstallmentBinding
import com.dentalclinic.patientmanagement.model.PaymentInstallment
import java.text.NumberFormat
import java.util.Locale

class InstallmentListAdapter(
    private val onInstallmentCheckedChanged: (PaymentInstallment, Boolean) -> Unit
) : ListAdapter<PaymentInstallment, InstallmentListAdapter.InstallmentViewHolder>(InstallmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstallmentViewHolder {
        val binding = ItemInstallmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InstallmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InstallmentViewHolder, position: Int) {
        val installment = getItem(position)
        holder.bind(installment, onInstallmentCheckedChanged)
    }

    class InstallmentViewHolder(private val binding: ItemInstallmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            installment: PaymentInstallment,
            onInstallmentCheckedChanged: (PaymentInstallment, Boolean) -> Unit
        ) {
            binding.installmentAmountTextView.text = "Amount: ${formatCurrency(installment.amount)}"
            binding.installmentDueDateTextView.text = "Due: ${installment.dueDate}" // Consider formatting date

            binding.isPaidCheckBox.setOnCheckedChangeListener(null) // Avoid triggering listener during bind
            binding.isPaidCheckBox.isChecked = installment.isPaid
            binding.isPaidCheckBox.setOnCheckedChangeListener { _, isChecked ->
                onInstallmentCheckedChanged(installment, isChecked)
            }
        }

        private fun formatCurrency(amount: Double): String {
            return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(amount)
        }
    }

    class InstallmentDiffCallback : DiffUtil.ItemCallback<PaymentInstallment>() {
        override fun areItemsTheSame(oldItem: PaymentInstallment, newItem: PaymentInstallment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PaymentInstallment, newItem: PaymentInstallment): Boolean {
            return oldItem == newItem
        }
    }
}
