package com.app.edtech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.databinding.AdapterLibraryBookListBinding
import com.app.edtech.model.institute_list.response.InstituteListResponse

class AdapterLibraryBook(
    val books: List<InstituteListResponse.Institute>,
    private val onInstituteSelected: (InstituteListResponse.Institute) -> Unit
) : RecyclerView.Adapter<AdapterLibraryBook.LibraryBookViewHolder>() {
    inner class LibraryBookViewHolder(val binding: AdapterLibraryBookListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryBookViewHolder {
        val binding = AdapterLibraryBookListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LibraryBookViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return /*books.size*/15
    }

    override fun onBindViewHolder(holder: LibraryBookViewHolder, position: Int) {
        val book = books[position]
        holder.binding.apply {
//            Glide.with(root.context).load(institute.imageUrl).placeholder(R.drawable.banner_placeholder).error(R.drawable.banner_placeholder).into(image)
//            instituteName.text = institute.name
//            instituteAddress.text = institute.address?.ifBlank { "N/A" } ?: ""
        }
        holder.binding.root.setOnClickListener {
            onInstituteSelected(book)
        }
    }
}