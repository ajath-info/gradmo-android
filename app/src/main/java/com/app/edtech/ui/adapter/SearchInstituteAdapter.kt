package com.app.edtech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.databinding.AdapterSearchInstituteBinding

class SearchInstituteAdapter : RecyclerView.Adapter<SearchInstituteAdapter.SearchInstituteViewHolder>() {
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
        return 5
    }

    override fun onBindViewHolder(holder: SearchInstituteViewHolder, position: Int) {

    }
}