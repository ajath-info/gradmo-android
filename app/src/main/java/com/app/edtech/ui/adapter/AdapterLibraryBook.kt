package com.app.edtech.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.R
import com.app.edtech.databinding.AdapterLibraryBookListBinding
import com.app.edtech.model.institute_list.response.InstituteListResponse
import com.app.edtech.model.library_list.LibraryListResponse
import com.app.edtech.utils.CommonUtils
import com.bumptech.glide.Glide

class AdapterLibraryBook(
    val books: List<LibraryListResponse.Data.Library>,
    private val onInstituteSelected: (LibraryListResponse.Data.Library) -> Unit
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
        return books.size
    }

    override fun onBindViewHolder(holder: LibraryBookViewHolder, position: Int) {
        val book = books[position]
        holder.binding.apply {
//            Glide.with(root.context).load(book.downloadUrl).placeholder(R.drawable.banner_placeholder).error(R.drawable.banner_placeholder).into()
            name.text = book.title
//            tvDate.text = CommonUtils.convertTimeFormat(book.addedAt, "")?.ifBlank { "N/A" } ?: ""
        }
        holder.binding.root.setOnClickListener {
            onInstituteSelected(book)
        }
    }
}