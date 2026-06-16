package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.databinding.AdapterInstituteBatchBinding
import com.app.gradmo.model.user_detail.UserDetailResponse
import com.app.gradmo.utils.CommonUtils.convertTimeFormat
import com.bumptech.glide.Glide

class AdapterInstituteBatchForInstitute(
    private val batches: List<UserDetailResponse.Data.Batch>,
    private val onBatchSelected: (UserDetailResponse.Data.Batch) -> Unit
) : RecyclerView.Adapter<AdapterInstituteBatchForInstitute.ViewHolder>() {

    inner class ViewHolder(val binding: AdapterInstituteBatchBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterInstituteBatchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = if (batches.size > 2) 2 else batches.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val batch = batches[position]
        holder.binding.apply {
            Glide.with(root.context)
                .load(batch.logo)
                .placeholder(R.drawable.batch_placeholder)
                .error(R.drawable.batch_placeholder)
                .into(imageView)

            instituteName.text = batch.title
            tvTeacherName.text = batch.institute_name
            tvTime.text = "${convertTimeFormat(batch.start_time, "HH:mm:ss", "h:mm a")} - " +
                    "${convertTimeFormat(batch.end_time, "HH:mm:ss", "h:mm a")}"
        }
        holder.binding.root.setOnClickListener { onBatchSelected(batch) }
    }
}