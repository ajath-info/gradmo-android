package com.app.gradmo.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentVideoLectureDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoLectureDetailFragment : BaseFragment<FragmentVideoLectureDetailBinding>() {

    override fun initView(savedInstanceState: Bundle?) {

    }

    private fun setupUI() {
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
    override fun getLayoutId(): Int = R.layout.fragment_video_lecture_detail
    override fun restoreView() {

    }

}