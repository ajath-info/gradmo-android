package com.app.edtech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.databinding.AdapterBatchDetailItemsBinding
import com.app.edtech.model.static.BatchDetailItem

class AdapterBatchDetailsItems(
    val list: List<BatchDetailItem>,
    private val onItemSelected: () -> Unit
) : RecyclerView.Adapter<AdapterBatchDetailsItems.SearchInstituteViewHolder>() {
    inner class SearchInstituteViewHolder(val binding: AdapterBatchDetailItemsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchInstituteViewHolder {
        val binding = AdapterBatchDetailItemsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchInstituteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SearchInstituteViewHolder, position: Int) {
        holder.binding.apply {
            name.text = list[position].name
            image.setImageResource(list[position].image)
        }
        holder.binding.root.setOnClickListener {
            onItemSelected()
        }
    }
}