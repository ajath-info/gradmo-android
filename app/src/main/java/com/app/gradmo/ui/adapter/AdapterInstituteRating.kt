package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.databinding.AdapterInstituteRatingReviewBinding
import com.app.gradmo.model.institute_detail.response.InstituteDetailResponse

class AdapterInstituteRating(
    private val rating: InstituteDetailResponse.Rating,
    private val onRatingSelected: () -> Unit
) : RecyclerView.Adapter<AdapterInstituteRating.SearchInstituteViewHolder>() {
    inner class SearchInstituteViewHolder(val binding: AdapterInstituteRatingReviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchInstituteViewHolder {
        val binding = AdapterInstituteRatingReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchInstituteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun onBindViewHolder(holder: SearchInstituteViewHolder, position: Int) {
        holder.binding.root.setOnClickListener {
            onRatingSelected()
        }
    }
}