package com.app.gradmo.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentNotificationBinding
import com.app.gradmo.ui.view_model.NotificationViewModel
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