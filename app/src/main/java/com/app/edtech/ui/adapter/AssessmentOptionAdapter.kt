package com.app.edtech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.R
import com.app.edtech.databinding.ItemAssessmentOptionBinding
import com.app.edtech.model.static.AssessmentOption

class AssessmentOptionAdapter(
    private val options: List<AssessmentOption>,
    private val onOptionSelected: (Int) -> Unit
) : RecyclerView.Adapter<AssessmentOptionAdapter.OptionViewHolder>() {

    private var selectedOptionId: Int = -1
    private val optionLabels = listOf("A", "B", "C", "D", "E", "F")

    fun setSelectedOption(optionId: Int) {
        val prev = selectedOptionId
        selectedOptionId = optionId
        if (prev != -1) notifyItemChanged(prev)
        if (optionId != -1) notifyItemChanged(optionId)
    }

    fun clearSelection() {
        val prev = selectedOptionId
        selectedOptionId = -1
        if (prev != -1) notifyItemChanged(prev)
    }

    inner class OptionViewHolder(
        private val binding: ItemAssessmentOptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(option: AssessmentOption, position: Int) {
            val isSelected = option.id == selectedOptionId
            val ctx = binding.root.context

            binding.tvOptionLabel.text = optionLabels.getOrElse(position) { (position + 1).toString() }
            binding.tvOptionText.text = option.optionText

            if (isSelected) {
                // Blue card + white text
                binding.optionCard.setCardBackgroundColor(
                    ContextCompat.getColor(ctx, R.color.colorPrimary)
                )
                binding.tvOptionText.setTextColor(
                    ContextCompat.getColor(ctx, R.color.white)
                )
            } else {
                // White card + dark text
                binding.optionCard.setCardBackgroundColor(
                    ContextCompat.getColor(ctx, R.color.white)
                )
                binding.tvOptionText.setTextColor(
                    ContextCompat.getColor(ctx, R.color.black)
                )
            }

            binding.root.setOnClickListener {
                onOptionSelected(option.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding = ItemAssessmentOptionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(options[position], position)
    }

    override fun getItemCount() = options.size
}