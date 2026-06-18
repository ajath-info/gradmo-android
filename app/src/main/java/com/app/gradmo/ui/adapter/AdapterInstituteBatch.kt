package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.databinding.AdapterInstituteBatchBinding
import com.app.gradmo.model.institute_detail.response.InstituteDetailResponse
import com.app.gradmo.utils.CommonUtils.convertTimeFormat
import com.bumptech.glide.Glide

class AdapterInstituteBatch(
    private val batches: List<InstituteDetailResponse.Batche>,
    private val onBatchSelected: (InstituteDetailResponse.Batche) -> Unit
) : RecyclerView.Adapter<AdapterInstituteBatch.SearchInstituteViewHolder>() {
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
        return if (batches.size>2) 2 else batches.size
    }

    override fun onBindViewHolder(holder: SearchInstituteViewHolder, position: Int) {
        val batch = batches[position]
        holder.binding.apply {
            Glide.with(root.context).load(batch).placeholder(R.drawable.batch_placeholder).error(R.drawable.batch_placeholder).into(imageView)
            instituteName.text = batch.batch_name
            tvTeacherName.isVisible = false
            tvTeacherName.text = "Offline"
            tvTime.text = "${convertTimeFormat(batch.start_time.toString(), "HH:mm:ss", "h:mm a")} - ${convertTimeFormat(batch.end_time.toString(), "HH:mm:ss", "h:mm a")}"
        }
        holder.binding.root.setOnClickListener {
            onBatchSelected(batch)
        }
    }
}