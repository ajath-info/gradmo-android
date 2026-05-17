package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.databinding.AdapterVideoLectureBinding
import com.app.gradmo.model.homework.HomeworkListResponse
import com.app.gradmo.utils.CommonUtils
import com.bumptech.glide.Glide

class AdapterHomework(
    val books: List<HomeworkListResponse.HomeWork>,
    private val onInstituteSelected: (HomeworkListResponse.HomeWork) -> Unit
) : RecyclerView.Adapter<AdapterHomework.LibraryBookViewHolder>() {
    inner class LibraryBookViewHolder(val binding: AdapterVideoLectureBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryBookViewHolder {
        val binding = AdapterVideoLectureBinding.inflate(
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
            Glide.with(root.context).load(book.attachmentUrl).placeholder(R.drawable.banner_placeholder).error(R.drawable.banner_placeholder).into(image)
            name.text = book.subjectName
            tvDes.text = book.description
            tvDate.text = CommonUtils.convertTimeFormat(book.addedAt ?: "", "yyyy-MM-dd HH:mm:ss", "MMM dd, yyyy")?.ifBlank { "N/A" } ?: ""        }
        holder.binding.root.setOnClickListener {
            onInstituteSelected(book)
        }
    }
}