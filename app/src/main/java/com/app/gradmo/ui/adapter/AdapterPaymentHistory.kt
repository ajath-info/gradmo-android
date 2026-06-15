package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.databinding.AdapterInstituteBatchBinding
import com.app.gradmo.databinding.AdapterPaymentHistoryBinding
import com.app.gradmo.model.batch_list.BatchListResponse.Data.EnrolledBatche
import com.app.gradmo.model.payment_history.PaymentHistoryResponse.PaymentData
import com.app.gradmo.utils.CommonUtils.convertTimeFormat
import com.bumptech.glide.Glide

class AdapterPaymentHistory(
    private val batches: List<PaymentData>,
    private val onBatchSelected: (PaymentData) -> Unit
) : RecyclerView.Adapter<AdapterPaymentHistory.SearchInstituteViewHolder>() {
    inner class SearchInstituteViewHolder(val binding: AdapterPaymentHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchInstituteViewHolder {
        val binding = AdapterPaymentHistoryBinding.inflate(
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
            batchName.text = batch.batchName
            amount.text = batch.currencyCode+" "+batch.amount
            tvTransactionId.text = "Transaction ID: "+batch.transactionId
            tvDateTime.text = convertTimeFormat(
                batch.createAt.toString(),
                "yyyy-MM-dd HH:mm:ss",
                "h:mm a, MMM yyyy"
            )        }
        holder.binding.root.setOnClickListener {
            onBatchSelected(batch)
        }
    }
}