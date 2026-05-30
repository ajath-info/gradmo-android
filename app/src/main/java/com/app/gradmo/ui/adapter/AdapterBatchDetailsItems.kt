package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.databinding.AdapterBatchDetailItemsBinding
import com.app.gradmo.model.staticdata.BatchDetailItem

class AdapterBatchDetailsItems(
    val list: List<BatchDetailItem>,
    private val onItemSelected: (BatchDetailItem, Int) -> Unit
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
            onItemSelected(list[position], position)
        }
    }
}