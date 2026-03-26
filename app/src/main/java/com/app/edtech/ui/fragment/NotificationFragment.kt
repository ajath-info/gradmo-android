package com.app.edtech.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentNotificationBinding
import com.app.edtech.databinding.FragmentSearchBinding
import com.app.edtech.ui.view_model.NotificationViewModel
import com.app.edtech.ui.view_model.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {
    private val viewModel: NotificationViewModel by viewModels()
    override fun initView(savedInstanceState: Bundle?) {

    }
    override fun getLayoutId(): Int {
        return R.layout.fragment_notification
    }
}