package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.databinding.AdapterInstituteBatchBinding
import com.app.gradmo.databinding.AdapterInstituteTabBinding
import com.app.gradmo.model.user_detail.UserDetailResponse
import com.app.gradmo.utils.CommonUtils.convertTimeFormat
import com.bumptech.glide.Glide

class InstitueTabAdapter(
    private val institutes: List<UserDetailResponse.Data.Institute>,
    private val batchMap: Map<Int, List<UserDetailResponse.Data.Batch>>,
    private val onInstituteSelected: (UserDetailResponse.Data.Institute) -> Unit,
    private val onBatchSelected: (UserDetailResponse.Data.Batch) -> Unit
) : RecyclerView.Adapter<InstitueTabAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: AdapterInstituteTabBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterInstituteTabBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = institutes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val institute = institutes[position]
        holder.binding.apply {

            // Banner image = institute image
            Glide.with(root.context)
                .load(institute.image)
                .placeholder(R.drawable.onboarding3)
                .error(R.drawable.onboarding3)
                .into(bannerImage)

            // Institute card image = static placeholder
            Glide.with(root.context)
                .load(R.drawable.batch_placeholder)
                .into(instituteImage)

            // Text fields
            instituteName.text = institute.name
            emailAddress.text = institute.email
            phoneNumber.visibility = View.GONE

            // Batch nested recycler
            val batches = batchMap[institute.instituteId] ?: emptyList()
            val batchAdapter = AdapterInstituteBatchForInstitute(batches, onBatchSelected)
            categoryRecyclerView.apply {
                layoutManager = LinearLayoutManager(root.context)
                adapter = batchAdapter
                isNestedScrollingEnabled = false
            }
        }

        holder.binding.root.setOnClickListener { onInstituteSelected(institute) }
    }
}