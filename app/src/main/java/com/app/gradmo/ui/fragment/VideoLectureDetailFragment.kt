package com.app.gradmo.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentVideoLectureDetailBinding
import com.app.gradmo.model.homework.HomeworkListResponse
import com.app.gradmo.model.video_lecture.VideoLectureListResponse
import com.app.gradmo.utils.CommonUtils
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoLectureDetailFragment : BaseFragment<FragmentVideoLectureDetailBinding>() {
    var videoData = VideoLectureListResponse.Data.VideoLecture()

    override fun initView(savedInstanceState: Bundle?) {
        videoData = arguments?.getParcelable<VideoLectureListResponse.Data.VideoLecture>("video") ?: VideoLectureListResponse.Data.VideoLecture()
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            Glide.with(requireContext()).load(videoData.url).placeholder(R.color.theme_blue).error(R.color.theme_blue).into(imageView)
            name.text = videoData.title
            description.text = videoData.description
            tvDate.text = CommonUtils.convertTimeFormat(videoData.addedAt ?: "", "yyyy-MM-dd HH:mm:ss", "MMM dd, yyyy")?.ifBlank { "N/A" } ?: ""
        }
    }


    private fun clickEvent() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.imageViewLayout.setOnClickListener {
            val bundle = Bundle().apply { putString("video_url", videoData.url) }
            findNavController().navigate(R.id.videoPlayerFragment, bundle)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickEvent()
    }
    override fun getLayoutId(): Int = R.layout.fragment_video_lecture_detail
    override fun restoreView() {
        setupUI()
    }

}