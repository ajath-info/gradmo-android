package com.app.gradmo.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentCourseBinding
import com.app.gradmo.ui.view_model.CourseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class CourseFragment : BaseFragment<FragmentCourseBinding>() {

    private val viewModel: CourseViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {

    }
    override fun getLayoutId(): Int {
        return R.layout.fragment_course
    }
}