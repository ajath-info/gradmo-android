package com.app.gradmo.ui.adapter

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.databinding.ItemQuestionPageBinding
import com.app.gradmo.model.questions.QuestionData

/**
 * ViewPager2 adapter — one [QuestionData] per swipeable page.
 *
 * All edits (text / image / correct-answer) write directly into the shared
 * [questions] list that the ViewModel owns, so state is never lost on swipe.
 *
 * @param questions      Mutable list from the ViewModel.
 * @param onBrowseClick  Called with page position → fragment launches image picker.
 * @param onDeleteClick  Called with page position → fragment asks ViewModel to delete.
 */
class QuestionPagerAdapter(
    private val questions: MutableList<QuestionData>,
    private val onBrowseClick: (position: Int) -> Unit,
    private val onDeleteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<QuestionPagerAdapter.QuestionViewHolder>() {

    inner class QuestionViewHolder(private val binding: ItemQuestionPageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Hold watcher refs so we can remove them before re-binding
        private var questionWatcher: TextWatcher? = null
        private var opt1Watcher: TextWatcher? = null
        private var opt2Watcher: TextWatcher? = null
        private var opt3Watcher: TextWatcher? = null
        private var opt4Watcher: TextWatcher? = null

        fun bind(data: QuestionData, position: Int, totalCount: Int) = with(binding) {

            // ── Header ───────────────────────────────────────────────────
            tvQuestionNumber.text = "Question ${position + 1}"

            // Show delete button only when there is more than one question
            btnDeleteQuestion.visibility = if (totalCount > 1) View.VISIBLE else View.GONE
            btnDeleteQuestion.setOnClickListener { onDeleteClick(position) }

            // ── Strip stale watchers before setText ──────────────────────
            etQuestion.removeTextChangedListener(questionWatcher)
            etOption1.removeTextChangedListener(opt1Watcher)
            etOption2.removeTextChangedListener(opt2Watcher)
            etOption3.removeTextChangedListener(opt3Watcher)
            etOption4.removeTextChangedListener(opt4Watcher)

            // ── Restore text ─────────────────────────────────────────────
            etQuestion.setText(data.questionText)
            etOption1.setText(data.option1)
            etOption2.setText(data.option2)
            etOption3.setText(data.option3)
            etOption4.setText(data.option4)

            etQuestion.setSelection(data.questionText.length)

            // ── Attach fresh watchers ────────────────────────────────────
            questionWatcher = watcher { data.questionText = it }
            opt1Watcher     = watcher { data.option1 = it }
            opt2Watcher     = watcher { data.option2 = it }
            opt3Watcher     = watcher { data.option3 = it }
            opt4Watcher     = watcher { data.option4 = it }

            etQuestion.addTextChangedListener(questionWatcher)
            etOption1.addTextChangedListener(opt1Watcher)
            etOption2.addTextChangedListener(opt2Watcher)
            etOption3.addTextChangedListener(opt3Watcher)
            etOption4.addTextChangedListener(opt4Watcher)

            // ── Image preview ─────────────────────────────────────────────
            if (!data.imagePath.isNullOrBlank()) {
                imagePreviewCard.visibility = View.VISIBLE
                ivPreview.setImageURI(Uri.parse(data.imagePath))
            } else {
                imagePreviewCard.visibility = View.GONE
            }
            btnBrowse.setOnClickListener { onBrowseClick(position) }
            btnRemoveImage.setOnClickListener {
                data.imagePath = null
                imagePreviewCard.visibility = View.GONE
            }

            // ── Correct-answer selector ───────────────────────────────────
            refreshAnswerButtons(data.correctAnswer)
            btnAnswer1.setOnClickListener { selectAnswer(data, 1) }
            btnAnswer2.setOnClickListener { selectAnswer(data, 2) }
            btnAnswer3.setOnClickListener { selectAnswer(data, 3) }
            btnAnswer4.setOnClickListener { selectAnswer(data, 4) }
        }

        private fun selectAnswer(data: QuestionData, answer: Int) {
            data.correctAnswer = answer
            refreshAnswerButtons(answer)
        }

        private fun refreshAnswerButtons(selected: Int) = with(binding) {
            btnAnswer1.isSelected = selected == 1
            btnAnswer2.isSelected = selected == 2
            btnAnswer3.isSelected = selected == 3
            btnAnswer4.isSelected = selected == 4
        }

        private fun watcher(onChanged: (String) -> Unit) = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) = Unit
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int)     = Unit
            override fun afterTextChanged(s: Editable?) { onChanged(s?.toString() ?: "") }
        }
    }

    // ── RecyclerView.Adapter ──────────────────────────────────────────────

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        QuestionViewHolder(
            ItemQuestionPageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) =
        holder.bind(questions[position], position, questions.size)

    override fun getItemCount() = questions.size

    // ── Public helpers ────────────────────────────────────────────────────

    /** Attach a picked image URI to a specific question page. */
    fun setImageForQuestion(position: Int, uri: Uri) {
        if (position in questions.indices) {
            questions[position].imagePath = uri.toString()
            notifyItemChanged(position)
        }
    }

    /**
     * Full dataset refresh — call after the ViewModel mutates the list
     * (add OR delete) so page numbers and delete-button visibility all repaint.
     */
    fun refreshAll() = notifyDataSetChanged()
}