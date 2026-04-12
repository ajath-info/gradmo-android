package com.app.edtech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.databinding.AdapterInstituteBatchBinding

class AdapterInstituteBatch(private val onBatchSelected : ()-> Unit) : RecyclerView.Adapter<AdapterInstituteBatch.SearchInstituteViewHolder>() {
    inner class SearchInstituteViewHolder(val binding: AdapterInstituteBatchBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchInstituteViewHolder {
        val binding = AdapterInstituteBatchBinding.inflate(
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
            onBatchSelected()
        }
    }
}