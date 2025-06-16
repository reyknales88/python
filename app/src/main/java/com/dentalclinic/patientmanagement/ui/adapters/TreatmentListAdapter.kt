package com.dentalclinic.patientmanagement.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dentalclinic.patientmanagement.databinding.ItemTreatmentBinding
import com.dentalclinic.patientmanagement.model.Treatment
import java.text.NumberFormat
import java.util.Locale

class TreatmentListAdapter(private val onItemClicked: (Treatment) -> Unit) :
    ListAdapter<Treatment, TreatmentListAdapter.TreatmentViewHolder>(TreatmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreatmentViewHolder {
        val binding = ItemTreatmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TreatmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TreatmentViewHolder, position: Int) {
        val treatment = getItem(position)
        holder.bind(treatment)
        holder.itemView.setOnClickListener {
            onItemClicked(treatment)
        }
    }

    class TreatmentViewHolder(private val binding: ItemTreatmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(treatment: Treatment) {
            binding.treatmentDescriptionTextView.text = treatment.description
            binding.treatmentDateTextView.text = "Date: ${treatment.date}" // Consider formatting date
            binding.treatmentCostTextView.text = "Cost: ${formatCurrency(treatment.cost)}"
        }

        private fun formatCurrency(amount: Double): String {
            return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(amount)
        }
    }

    class TreatmentDiffCallback : DiffUtil.ItemCallback<Treatment>() {
        override fun areItemsTheSame(oldItem: Treatment, newItem: Treatment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Treatment, newItem: Treatment): Boolean {
            return oldItem == newItem
        }
    }
}
