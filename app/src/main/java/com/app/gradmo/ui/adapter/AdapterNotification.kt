package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.databinding.AdapterNotificationBinding
import com.app.gradmo.databinding.AdapterPaymentHistoryBinding
import com.app.gradmo.model.notification_list.NotificationListResponse.Notification
import com.app.gradmo.model.payment_history.PaymentHistoryResponse.PaymentData
import com.app.gradmo.utils.CommonUtils.convertTimeFormat

class AdapterNotification(
    private val batches: List<Notification>,
    private val onBatchSelected: (Notification) -> Unit
) : RecyclerView.Adapter<AdapterNotification.SearchInstituteViewHolder>() {
    inner class SearchInstituteViewHolder(val binding: AdapterNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchInstituteViewHolder {
        val binding = AdapterNotificationBinding.inflate(
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
            title.text = batch.notificationType
            description.text = batch.msg
            tvDateTime.text = convertTimeFormat(
                batch.time.toString(),
                "yyyy-MM-dd HH:mm:ss",
                "h:mm a, MMM yyyy"
            )
        }
        holder.binding.root.setOnClickListener {
            onBatchSelected(batch)
        }
    }
}