package com.app.gradmo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.databinding.AdapterUpcomingExamsBinding
import com.app.gradmo.model.teacher_created_exams.TeacherCreatedExamsResponse.Data.Exam
import com.app.gradmo.utils.CommonUtils.convertTimeFormat
import com.bumptech.glide.Glide

class AdapterTeacherCreatedExamList(
    val books: List<Exam>,
    private val onInstituteSelected: (Exam) -> Unit
) : RecyclerView.Adapter<AdapterTeacherCreatedExamList.LibraryBookViewHolder>() {
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
            tvCompletedBy.text = "${convertTimeFormat(book.scheduledTime.toString(), "HH:mm:ss", "h:mm a")}, ${convertTimeFormat(book.scheduledDate.toString(), "YYYY-mm-dd", "dd MMM YYY")}"
            if (book.hasStarted==true){
                startAssessmentButton.isVisible=false
                completedTag.isVisible = false
            }else{
                startAssessmentButton.isVisible=false
                completedTag.isVisible = false
            }
        }
        holder.binding.startAssessmentButton.setOnClickListener {
            onInstituteSelected(book)
        }
    }
}