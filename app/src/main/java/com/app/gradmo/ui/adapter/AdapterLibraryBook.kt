package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.databinding.AdapterLibraryBookListBinding
import com.app.gradmo.model.library_list.LibraryListResponse
import com.app.gradmo.utils.CommonUtils

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
            name.text = book.fileName
            tvSize.text = book.fileSize
            tvDate.text = CommonUtils.convertTimeFormat(book.addedAt, "yyyy-MM-dd HH:mm:ss", "MMM dd, yyyy")?.ifBlank { "N/A" } ?: ""        }
        holder.binding.root.setOnClickListener {
            onInstituteSelected(book)
        }
    }
}