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
import com.app.gradmo.model.attendance_students_list.StudentUiModel
import com.app.gradmo.model.attendance_students_list.AttendanceStatus

/**
 * @param onMark     Called when P or A button is tapped. studentId is Int (matches API).
 * @param getStatus  Reads live status from ViewModel's attendanceMap.
 */
class StudentAttendanceAdapter(
    private val onMark: (studentId: Int, status: AttendanceStatus) -> Unit,
    private val getStatus: (studentId: Int) -> AttendanceStatus?
) : ListAdapter<StudentUiModel, StudentAttendanceAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(
        val binding: ItemStudentAttendanceBinding  // ← make it internal, not private
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentUiModel) {
            binding.tvName.text = student.name
            binding.tvAvatarInitials.text = student.name
                .trim()
                .split(" ")
                .filter { it.isNotEmpty() }
                .take(2)
                .joinToString("") { it.first().uppercaseChar().toString() }

            applyButtonState(student.studentId)

            binding.btnPresent.setOnClickListener {
                onMark(student.studentId, AttendanceStatus.PRESENT)
                applyButtonState(student.studentId)
            }
            binding.btnAbsent.setOnClickListener {
                onMark(student.studentId, AttendanceStatus.ABSENT)
                applyButtonState(student.studentId)
            }
        }

        fun applyButtonState(studentId: Int) {
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

    /**
     * Partial re-bind — only refreshes the P/A button tints without
     * touching the name/avatar, preventing flicker.
     */
    fun refreshItem(studentId: Int) {
        val idx = currentList.indexOfFirst { it.studentId == studentId }
        if (idx != -1) notifyItemChanged(idx, PAYLOAD_STATUS)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.contains(PAYLOAD_STATUS)) {
            holder.applyButtonState(getItem(position).studentId)  // reuses stored binding
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    companion object {
        private const val PAYLOAD_STATUS = "payload_status"

        private val DIFF = object : DiffUtil.ItemCallback<StudentUiModel>() {
            override fun areItemsTheSame(a: StudentUiModel, b: StudentUiModel) =
                a.studentId == b.studentId
            override fun areContentsTheSame(a: StudentUiModel, b: StudentUiModel) =
                a == b
        }
    }
}