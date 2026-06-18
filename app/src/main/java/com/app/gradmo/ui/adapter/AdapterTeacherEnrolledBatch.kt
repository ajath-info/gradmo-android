package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.databinding.AdapterTeacherEnrolledBatchBinding
import com.app.gradmo.model.batch_list.BatchListResponse
import com.app.gradmo.model.institute_detail.response.InstituteDetailResponse
import com.app.gradmo.utils.CommonUtils.convertTimeFormat
import com.bumptech.glide.Glide

class AdapterTeacherEnrolledBatch(
    private val batches: List<BatchListResponse.Data.EnrolledBatche>,
    private val onBatchSelected: (BatchListResponse.Data.EnrolledBatche) -> Unit
) : RecyclerView.Adapter<AdapterTeacherEnrolledBatch.SearchInstituteViewHolder>() {

    private var filteredBatches: List<BatchListResponse.Data.EnrolledBatche> = batches.toList()

    inner class SearchInstituteViewHolder(val binding: AdapterTeacherEnrolledBatchBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchInstituteViewHolder {
        val binding = AdapterTeacherEnrolledBatchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SearchInstituteViewHolder(binding)
    }

    override fun getItemCount() = filteredBatches.size

    override fun onBindViewHolder(holder: SearchInstituteViewHolder, position: Int) {
        val batch = filteredBatches[position]
        holder.binding.apply {
            Glide.with(root.context).load(batch.batchImage)
                .placeholder(R.drawable.batch_placeholder)
                .error(R.drawable.batch_placeholder)
                .into(imageView)
            instituteName.text = batch.batchName
            tvTeacherName.text = batch.instructor
            tvTime.text = "${convertTimeFormat(batch.start_time.toString(), "HH:mm:ss", "h:mm a")} - ${convertTimeFormat(batch.end_time.toString(), "HH:mm:ss", "h:mm a")}"
        }
        holder.binding.root.setOnClickListener {
            onBatchSelected(batch)
        }
    }

    fun filter(query: String) {
        filteredBatches = if (query.isBlank()) {
            batches.toList()
        } else {
            batches.filter { it.batchName?.contains(query.trim(), ignoreCase = true) == true }
        }
        notifyDataSetChanged()
    }
}