package com.app.edtech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.R
import com.app.edtech.databinding.AdapterHomeInstituteBinding
import com.app.edtech.model.institute_list.response.InstituteListResponse
import com.bumptech.glide.Glide

class HomeInstituteAdapter(val institutes: List<InstituteListResponse.Institute>) : RecyclerView.Adapter<HomeInstituteAdapter.HomeInstituteViewHolder>() {
    inner class HomeInstituteViewHolder(val binding: AdapterHomeInstituteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeInstituteViewHolder {
        val binding = AdapterHomeInstituteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HomeInstituteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return institutes.size
    }

    override fun onBindViewHolder(holder: HomeInstituteViewHolder, position: Int) {
        val institute = institutes[position]
        holder.binding.apply {
            Glide.with(root.context).load(institute.imageUrl).placeholder(R.drawable.banner_placeholder).error(R.drawable.banner_placeholder).into(imageView)
            instituteName.text = institute.name
            instituteAddress.text = institute.address?.ifBlank { "N/A" } ?: ""
        }
    }
}