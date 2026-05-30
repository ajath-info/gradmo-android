package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.databinding.ItemAssessmentOptionBinding

// ─────────────────────────────────────────────────────────────
// AssessmentOptionAdapter.kt
// Options are plain strings parsed from API: ["A1","A2","A","A4"]
// ─────────────────────────────────────────────────────────────

import android.content.res.ColorStateList

class AssessmentOptionAdapter(
    private val options: List<String>,
    preSelectedIndex: Int = -1,
    private val onOptionSelected: (index: Int) -> Unit
) : RecyclerView.Adapter<AssessmentOptionAdapter.OptionViewHolder>() {

    private var selectedIndex: Int = preSelectedIndex

    // Labels A, B, C, D ... for up to 6 options
    private val optionLabels = listOf("A", "B", "C", "D", "E", "F")

    inner class OptionViewHolder(
        private val binding: ItemAssessmentOptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(optionText: String, position: Int) {
            val isSelected = position == selectedIndex
            val ctx = binding.root.context

            binding.tvOptionLabel.text = optionLabels.getOrElse(position) { "${position + 1}" }
            binding.tvOptionText.text = optionText

            if (isSelected) {
                // ── Selected state: entire card turns blue, text turns white
                binding.optionCard.setCardBackgroundColor(
                    ContextCompat.getColor(ctx, R.color.colorPrimary)
                )
                binding.tvOptionText.setTextColor(
                    ContextCompat.getColor(ctx, R.color.white)
                )
                // Keep label badge dark grey regardless of selection
                binding.tvOptionLabel.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(ctx, R.color.option_label_gray) // dark grey
                )
            } else {
                // ── Unselected state: white card, dark text
                binding.optionCard.setCardBackgroundColor(
                    ContextCompat.getColor(ctx, R.color.white)
                )
                binding.tvOptionText.setTextColor(
                    ContextCompat.getColor(ctx, R.color.black)
                )
                binding.tvOptionLabel.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(ctx, R.color.option_label_gray)
                )
            }

            binding.root.setOnClickListener {
                val previousIndex = selectedIndex
                selectedIndex = position
                // Refresh only the two affected items (efficient)
                notifyItemChanged(previousIndex)
                notifyItemChanged(selectedIndex)
                onOptionSelected(position)
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

    override fun getItemCount(): Int = options.size
}