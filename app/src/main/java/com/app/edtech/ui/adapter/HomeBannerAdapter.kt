package com.app.edtech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.R
import com.app.edtech.databinding.AdapterHomeBannerBinding
import com.app.edtech.model.banner.response.BannerResponse
import com.bumptech.glide.Glide

class HomeBannerAdapter(val banners: List<BannerResponse.Data.Banner>) : RecyclerView.Adapter<HomeBannerAdapter.HomeBannerViewHolder>() {
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
        return banners.size
    }

    override fun onBindViewHolder(holder: HomeBannerViewHolder, position: Int) {
        holder.binding.apply {
            Glide.with(root.context).load(banners[position].image_url).placeholder(R.drawable.banner_placeholder).error(R.drawable.banner_placeholder).into(imageView)
        }
    }
}