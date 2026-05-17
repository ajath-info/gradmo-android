package com.app.gradmo.ui.signup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.databinding.ItemOnboardingBinding
import com.app.gradmo.model.onboarding_item.OnboardingItem

class OnboardingAdapter(
    private val list: List<OnboardingItem>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(val binding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val binding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OnboardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val item = list[position]

        holder.binding.apply {
            imageView.setImageResource(item.image)
            titleText.text = item.title
            descriptionText.text = item.description
        }
    }

    override fun getItemCount(): Int = list.size
}