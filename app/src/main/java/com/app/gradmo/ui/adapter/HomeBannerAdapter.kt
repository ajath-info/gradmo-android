package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.databinding.AdapterHomeBannerBinding
import com.app.gradmo.model.banner.response.BannerResponse
import com.bumptech.glide.Glide

class HomeBannerAdapter(val banners: List<BannerResponse.Data.Banner>) :
    RecyclerView.Adapter<HomeBannerAdapter.HomeBannerViewHolder>() {

    // Infinite scroll trick: multiply items
    private val MULTIPLIER = 1000
    private val fakeCount get() = if (banners.isEmpty()) 0 else banners.size * MULTIPLIER

    inner class HomeBannerViewHolder(val binding: AdapterHomeBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBannerViewHolder {
        val binding = AdapterHomeBannerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HomeBannerViewHolder(binding)
    }

    override fun getItemCount(): Int = fakeCount

    // Real index via modulo → infinite loop effect
    override fun onBindViewHolder(holder: HomeBannerViewHolder, position: Int) {
        val realPosition = position % banners.size
        holder.binding.apply {
            Glide.with(root.context)
                .load(banners[realPosition].image_url)
                .placeholder(R.drawable.banner_placeholder_new)
                .error(R.drawable.banner_placeholder_new)
                .into(imageView)
        }
    }

    // Returns the real index for dot indicator
    fun getRealPosition(position: Int): Int = if (banners.isEmpty()) 0 else position % banners.size

    // Start position at middle to allow scrolling both ways
    fun getStartPosition(): Int = (fakeCount / 2) - ((fakeCount / 2) % banners.size)
}