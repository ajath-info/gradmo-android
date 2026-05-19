package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.databinding.AdapterUpcomingExamsBinding
import com.app.gradmo.model.exam_list.UpcomingExamListResponse
import com.app.gradmo.model.institute_list.response.InstituteListResponse
import com.app.gradmo.utils.CommonUtils.convertTimeFormat
import com.bumptech.glide.Glide

class AdapterUpcomingExams(
    val books: List<UpcomingExamListResponse.Data.UpcomingExam>,
    private val onInstituteSelected: (UpcomingExamListResponse.Data.UpcomingExam) -> Unit
) : RecyclerView.Adapter<AdapterUpcomingExams.LibraryBookViewHolder>() {
    inner class LibraryBookViewHolder(val binding: AdapterUpcomingExamsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryBookViewHolder {
        val binding = AdapterUpcomingExamsBinding.inflate(
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
            Glide.with(root.context).load("").into(imageView)
            name.text = book.name
            tvNoOfQuestions.text = book.totalQuestion
            tvDuration.text = book.timeDuration
            name.text = book.name
            tvCompletedBy.text = book.completeBy
//            tvCompletedBy.text = "${convertTimeFormat(batch.start_time.toString(), "HH:mm:ss", "h:mm a")} - ${convertTimeFormat(book.end_time.toString(), "HH:mm:ss", "h:mm a")}"
            if (book.statusLabel=="Upcoming"){
                startAssessmentButton.isVisible=true
                completedTag.isVisible = false
            }else{
                startAssessmentButton.isVisible=false
                completedTag.isVisible = true
            }
        }
        holder.binding.startAssessmentButton.setOnClickListener {
            onInstituteSelected(book)
        }
    }
}