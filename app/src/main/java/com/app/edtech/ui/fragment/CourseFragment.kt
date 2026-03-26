package com.app.edtech.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentCourseBinding
import com.app.edtech.ui.view_model.CourseViewModel
import com.app.edtech.ui.view_model.HomeViewModel
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