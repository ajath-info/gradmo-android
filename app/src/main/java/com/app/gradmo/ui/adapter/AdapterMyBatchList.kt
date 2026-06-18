package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.databinding.AdapterInstituteBatchBinding
import com.app.gradmo.model.batch_list.BatchListResponse.Data.EnrolledBatche
import com.app.gradmo.model.institute_detail.response.InstituteDetailResponse
import com.app.gradmo.utils.CommonUtils.convertTimeFormat
import com.bumptech.glide.Glide

class AdapterMyBatchList(
    private val batches: List<EnrolledBatche>,
    private val onBatchSelected: (EnrolledBatche) -> Unit
) : RecyclerView.Adapter<AdapterMyBatchList.SearchInstituteViewHolder>() {
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
        return batches.size
    }

    override fun onBindViewHolder(holder: SearchInstituteViewHolder, position: Int) {
        val batch = batches[position]
        holder.binding.apply {
            Glide.with(root.context).load(batch.batchName).placeholder(R.drawable.batch_placeholder).error(R.drawable.batch_placeholder).into(imageView)
            instituteName.text = batch.batchName
//            tvTeacherName.text = "N/A"
            tvTeacherName.text = batch.instructor
            tvTime.text = "${convertTimeFormat(batch.start_time.toString(), "HH:mm:ss", "h:mm a")} - ${convertTimeFormat(batch.end_time.toString(), "HH:mm:ss", "h:mm a")}"
        }
        holder.binding.root.setOnClickListener {
            onBatchSelected(batch)
        }
    }
}