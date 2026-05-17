package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.databinding.AdapterPromocodeListBinding
import com.app.gradmo.model.promocode.response.PromocodeListResponse

class AdapterPromocodeList(
    private val fullList: List<PromocodeListResponse.Data.PromoCode>,
    private val onBatchSelected: (PromocodeListResponse.Data.PromoCode) -> Unit
) : RecyclerView.Adapter<AdapterPromocodeList.PromoCodeViewHolder>() {

    // Separate filtered list for search
    private var filteredList: List<PromocodeListResponse.Data.PromoCode> = fullList.toList()
    private var selectedCode: String? = null

    inner class PromoCodeViewHolder(val binding: AdapterPromocodeListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoCodeViewHolder {
        val binding = AdapterPromocodeListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PromoCodeViewHolder(binding)
    }

    override fun getItemCount(): Int = filteredList.size

    override fun onBindViewHolder(holder: PromoCodeViewHolder, position: Int) {
        val item = filteredList[position]
        val isSelected = item.code == selectedCode

        holder.binding.apply {
            name.text = item.code

            // ✅ Toggle border based on selection
            root.background = ContextCompat.getDrawable(
                root.context,
                if (isSelected) R.drawable.bg_blue_border   // selected state
                else R.drawable.bg_black_border                      // default state
            )

            root.setOnClickListener {
                val previousSelected = selectedCode
                selectedCode = item.code

                // Refresh only changed items for performance
                val previousIndex = filteredList.indexOfFirst { it.code == previousSelected }
                if (previousIndex != -1) notifyItemChanged(previousIndex)
                notifyItemChanged(position)

                onBatchSelected(item)
            }
        }
    }

    // ✅ Filter list based on search query
    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            fullList.toList()
        } else {
            fullList.filter {
                it.code.contains(query, ignoreCase = true)
            }
        }
        // Clear selection if selected item not in filtered list
        if (filteredList.none { it.code == selectedCode }) {
            selectedCode = null
        }
        notifyDataSetChanged()
    }

    // ✅ Returns selected promo code object or null
    fun getSelectedPromoCode(): PromocodeListResponse.Data.PromoCode? {
        return filteredList.find { it.code == selectedCode }
    }

    // ✅ Manually select a code (used for apply from EditText)
    fun selectByCode(code: String): Boolean {
        val found = filteredList.find { it.code.equals(code, ignoreCase = true) }
        return if (found != null) {
            selectedCode = found.code
            notifyDataSetChanged()
            true
        } else {
            false
        }
    }

    fun getFilteredList(): List<PromocodeListResponse.Data.PromoCode> = filteredList
}