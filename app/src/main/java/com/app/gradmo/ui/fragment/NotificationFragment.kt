package com.app.gradmo.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentNotificationBinding
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.notification_list.NotificationListRequest
import com.app.gradmo.model.notification_list.NotificationListResponse.Notification
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.activity.HomeActivity
import com.app.gradmo.ui.adapter.AdapterNotification
import com.app.gradmo.ui.view_model.NotificationViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class NotificationFragment() : BaseFragment<FragmentNotificationBinding>() {
    private var totalRecords: Int = 0
    private var currentPage = 1
    private val pageSize = 10   // adjust as per API
    private var isLoading = false
    private var isLastPage = false

    private val BooksList = mutableListOf<Notification>()
    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var libraryBooksAdapter: AdapterNotification  // replace with your adapter

    private var orderType = "DESC"   // default Z-A
    private var selectedMode = ""    // "online" / "offline" / ""
    private var flow = ""    // "online" / "offline" / ""
    private var batch_id = ""
    var accessToken: String = ""

    override fun initView(savedInstanceState: Bundle?) {
        flow = arguments?.getString("flow", "") ?: ""
        batch_id = arguments?.getString("batch_id", "") ?: ""
        Log.i("TAG", "batch_id: "+batch_id)
        Log.i("TAG", "flow: "+flow)
        setupUI()
        currentPage = 1
        isLastPage = false
        BooksList.clear()

//        replace with library api, and set adapter from inside of api response
        setupBooksRecycler(listOf(), false, totalRecords)
        accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken ?: ""
        viewModel.hitNotificationListApi("Bearer $accessToken", NotificationListRequest())
    }

    private fun setupUI() {
        when(flow){
            "seeAll"->{
                binding.title.text = "Institutes"
                (activity as HomeActivity).hideNavigationView()
            }
        }
    }

    private fun setupBooksRecycler(institutes: List<Notification>, isRestore:Boolean, totalRecords:Int) {
        libraryBooksAdapter = AdapterNotification(BooksList, ::onBookSelected)
        binding.instituteRecycler.apply {
            adapter = libraryBooksAdapter
        }
        addPaginationScroll()
//        binding.tvShowingCount.text = "Showing ${BooksList.size} of ${totalRecords} results"
        if (!isRestore){
            val start = BooksList.size
            BooksList.addAll(institutes)
            libraryBooksAdapter.notifyItemRangeInserted(start, institutes.size)
            isLoading = false

            if (institutes.size < pageSize) {
                isLastPage = true
            }
//            binding.tvShowingCount.text = "Showing ${BooksList.size} of ${totalRecords} results"
        }
    }

    private fun addPaginationScroll() {
        val layoutManager = binding.instituteRecycler.layoutManager as LinearLayoutManager

        binding.instituteRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        loadNextPage()
                    }
                }
            }
        })
    }
    private fun loadNextPage() {
        isLoading = true
        currentPage++
        viewModel.hitNotificationListApi("Bearer $accessToken", NotificationListRequest())

    }
    private fun onBookSelected(item:Notification) {
//        var bundle = Bundle()
//        bundle.putParcelable("video", item)
//        findNavController().navigate(R.id.videoLectureDetailFragment, bundle)
    }

    private fun clickEvent() {
        binding.backButton.setOnClickListener{
            findNavController().popBackStack()
        }
        binding.sideOptions.setOnClickListener { anchor ->
            val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_side_options, null)

            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true // focusable = dismisses on outside tap
            )

            // Rounded background + shadow
            popupWindow.elevation = 16f
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.WHITE))

            // Show just below the anchor button
            popupWindow.showAsDropDown(anchor, 0, 8)

            popupView.findViewById<TextView>(R.id.option_read_all).setOnClickListener {
                popupWindow.dismiss()
                // TODO: handle read all
            }

            popupView.findViewById<TextView>(R.id.option_delete_all).setOnClickListener {
                popupWindow.dismiss()
                // TODO: handle delete all
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }
    override fun getLayoutId(): Int = R.layout.fragment_notification
    private fun setObserver() {
        viewModel.getNotificationListLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        totalRecords = it.data.pagination.totalRecords
                        setupBooksRecycler(it.data.notifications, false, totalRecords)
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Login Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

    }
    override fun restoreView() {
        viewModel.getNotificationListLiveData().value?.data?.notifications?.let {
            setupBooksRecycler(it, true, totalRecords)
        }
    }

}