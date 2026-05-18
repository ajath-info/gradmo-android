package com.app.gradmo.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentHomeworkDetailBinding
import com.app.gradmo.model.homework.HomeworkListResponse
import com.app.gradmo.utils.CommonUtils
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeworkDetailFragment : BaseFragment<FragmentHomeworkDetailBinding>() {
    var homeworkData = HomeworkListResponse.HomeWork()

    override fun initView(savedInstanceState: Bundle?) {
        homeworkData = arguments?.getParcelable<HomeworkListResponse.HomeWork>("homework") ?: HomeworkListResponse.HomeWork()
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            Glide.with(requireContext()).load(homeworkData.attachmentUrl).placeholder(R.drawable.banner_placeholder).error(R.drawable.banner_placeholder).into(image)
            name.text = homeworkData.subjectName
            description.text = homeworkData.description
            tvDate.text = CommonUtils.convertTimeFormat(homeworkData.addedAt ?: "", "yyyy-MM-dd HH:mm:ss", "MMM dd, yyyy")?.ifBlank { "N/A" } ?: ""
        }
    }


    private fun clickEvent() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickEvent()
    }
    override fun getLayoutId(): Int = R.layout.fragment_homework_detail
    override fun restoreView() {
        setupUI()
    }

}