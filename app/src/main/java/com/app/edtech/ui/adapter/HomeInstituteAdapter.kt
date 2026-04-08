package com.app.edtech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.databinding.AdapterHomeInstituteBinding

class HomeInstituteAdapter : RecyclerView.Adapter<HomeInstituteAdapter.HomeInstituteViewHolder>() {
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
        return 10
    }

    override fun onBindViewHolder(holder: HomeInstituteViewHolder, position: Int) {

    }
}