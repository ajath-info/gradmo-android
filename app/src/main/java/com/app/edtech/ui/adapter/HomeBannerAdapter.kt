package com.app.edtech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.databinding.AdapterHomeBannerBinding

class HomeBannerAdapter : RecyclerView.Adapter<HomeBannerAdapter.HomeBannerViewHolder>() {
    inner class HomeBannerViewHolder(val binding: AdapterHomeBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBannerViewHolder {
        val binding = AdapterHomeBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HomeBannerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: HomeBannerViewHolder, position: Int) {

    }
}