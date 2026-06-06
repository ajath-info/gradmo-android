package com.app.gradmo.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.databinding.ItemStudentAttendanceBinding
import com.app.gradmo.model.mark_attendance.AttendanceStatus
import com.app.gradmo.model.mark_attendance.Student

/**
 * @param onMark   Called when a P/A button is tapped. ViewModel handles toggle logic.
 * @param getStatus Called to retrieve the current live status for each student.
 *                  This indirection lets the adapter reflect ViewModel state without
 *                  carrying a copy of the full map.
 */
class StudentAttendanceAdapter(
    private val onMark: (studentId: String, status: AttendanceStatus) -> Unit,
    private val getStatus: (studentId: String) -> AttendanceStatus?
) : ListAdapter<Student, StudentAttendanceAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(
        private val binding: ItemStudentAttendanceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            binding.tvRollNumber.text = student.rollNumber.toString()
            binding.tvName.text       = student.name

            // Avatar: load with Glide/Coil if url available, else show initials
            if (student.avatarUrl != null) {
                // TODO: replace with Glide.with(binding.root).load(student.avatarUrl).into(binding.ivAvatar)
                binding.ivAvatar.setImageResource(android.R.color.transparent)
                binding.tvAvatarInitials.text = ""
            } else {
                binding.ivAvatar.setImageResource(android.R.color.transparent)
                binding.tvAvatarInitials.text = student.name
                    .split(" ")
                    .take(2)
                    .joinToString("") { it.first().uppercase() }
            }

            applyStatus(student.id)

            val presentColor = if (student.status == AttendanceStatus.PRESENT)
                ContextCompat.getColor(binding.root.context, R.color.color_present_icon_selected)
            else
                ContextCompat.getColor(binding.root.context, R.color.color_text_secondary)

            val absentColor = if (student.status == AttendanceStatus.ABSENT)
                ContextCompat.getColor(binding.root.context, R.color.color_absent_icon_selected)
            else
                ContextCompat.getColor(binding.root.context, R.color.color_text_secondary)

            binding.btnPresent.imageTintList = ColorStateList.valueOf(presentColor)
            binding.btnAbsent.imageTintList  = ColorStateList.valueOf(absentColor)

//            binding.btnPresent.setOnClickListener {
//                onMark(student.id, AttendanceStatus.PRESENT)
//            }
//            binding.btnAbsent.setOnClickListener {
//                onMark(student.id, AttendanceStatus.ABSENT)
//            }
        }

        private fun applyStatus(studentId: String) {
            val status = getStatus(studentId)
            binding.btnPresent.isSelected = (status == AttendanceStatus.PRESENT)
            binding.btnAbsent.isSelected  = (status == AttendanceStatus.ABSENT)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStudentAttendanceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /** Re-bind only the buttons for the changed student — avoids full list flicker. */
    fun refreshItem(studentId: String) {
        val idx = currentList.indexOfFirst { it.id == studentId }
        if (idx != -1) notifyItemChanged(idx, PAYLOAD_STATUS)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.contains(PAYLOAD_STATUS)) {
            // Lightweight re-bind: only update button states
            val student = getItem(position)
            holder.itemView.let {
                val binding = ItemStudentAttendanceBinding.bind(it)
                binding.btnPresent.isSelected = (getStatus(student.id) == AttendanceStatus.PRESENT)
                binding.btnAbsent.isSelected  = (getStatus(student.id) == AttendanceStatus.ABSENT)
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    companion object {
        private const val PAYLOAD_STATUS = "payload_status"

        private val DIFF = object : DiffUtil.ItemCallback<Student>() {
            override fun areItemsTheSame(a: Student, b: Student) = a.id == b.id
            override fun areContentsTheSame(a: Student, b: Student) = a == b
        }
    }
}