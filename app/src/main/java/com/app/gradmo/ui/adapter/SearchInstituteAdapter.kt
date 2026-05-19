package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.databinding.AdapterSearchInstituteBinding
import com.app.gradmo.model.institute_list.response.InstituteListResponse
import com.bumptech.glide.Glide

class SearchInstituteAdapter(
    val institutes: List<InstituteListResponse.Institute>,
    private val onInstituteSelected: (InstituteListResponse.Institute) -> Unit
) : RecyclerView.Adapter<SearchInstituteAdapter.SearchInstituteViewHolder>() {
    inner class SearchInstituteViewHolder(val binding: AdapterSearchInstituteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchInstituteViewHolder {
        val binding = AdapterSearchInstituteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchInstituteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return institutes.size
    }

    override fun onBindViewHolder(holder: SearchInstituteViewHolder, position: Int) {
        val institute = institutes[position]
        holder.binding.apply {
            Glide.with(root.context).load(institute.imageUrl).placeholder(R.drawable.batch_placeholder).error(R.drawable.batch_placeholder).into(image)
            instituteName.text = institute.name
            instituteAddress.text = institute.address?.ifBlank { "N/A" } ?: ""
        }
        holder.binding.root.setOnClickListener {
            onInstituteSelected(institute)
        }
    }
}