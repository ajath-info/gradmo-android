package com.app.gradmo.ui.fragment

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentBatchDetailBinding
import com.app.gradmo.model.batch_detail.BatchDetails
import com.app.gradmo.model.institute_detail.response.InstituteDetailResponse
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.static.BatchDetailItem
import com.app.gradmo.model.static.StaticLists
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.activity.ZoomVideoActivity
import com.app.gradmo.ui.adapter.AdapterBatchDetailsItems
import com.app.gradmo.ui.view_model.BatchDetailsViewModel
import com.app.gradmo.utils.CommonUtils.convertTimeFormat
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlin.getValue

class BatchDetailFragment : BaseFragment<FragmentBatchDetailBinding>() {
    private lateinit var adapterBatchDetailsItems: AdapterBatchDetailsItems  // replace with your adapter
    lateinit var batch: BatchDetails
    private val viewModel: BatchDetailsViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        var batchItem = arguments?.getParcelable("batch") ?: InstituteDetailResponse.Batche()
        setupRecyclerView()
        observeViewModel()
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
        viewModel.hitBatchDetailsApi("Bearer $accessToken", batchItem.id.toString())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }
    private fun setObserver() {
        viewModel.getBatchDetailsLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "getBatchDetailsLiveData success: ${Gson().toJson(it)}")
                    if (it.data?.status == true) {
                        Log.i("TAG", "getBatchDetailsLiveData: "+ Gson().toJson(it.data))
                        batch = it.data.batch_details
                        setupUI()
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "getBatchDetailsLiveData Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }

    private fun setupUI() {
        binding.apply {
            if (batch.enrollment.status==0){
                blurView.isVisible=true
                recyclerBatchDetailItem.isEnabled=false
                recyclerBatchDetailItem.isClickable=false
                enrollButton.isVisible=true
            }else{
                blurView.isVisible=false
                recyclerBatchDetailItem.isEnabled=true
                recyclerBatchDetailItem.isClickable=true
                enrollButton.isVisible=false
            }
            Glide.with(root.context).load(batch.batchImage).placeholder(R.drawable.banner_placeholder).error(R.drawable.banner_placeholder).into(imageView)
            instituteName.text = batch.batchName
            tvTeacherName.text = "N/A"
            tvTiming.text = "${convertTimeFormat(batch.start_time.toString(), "HH:mm:ss", "h:mm a")} - ${convertTimeFormat(batch.end_time.toString(), "HH:mm:ss", "h:mm a")}"
        }
    }

    private fun setupRecyclerView() {
        adapterBatchDetailsItems = AdapterBatchDetailsItems(StaticLists.batchDetailList, ::onBatchItemSelected)
        binding.recyclerBatchDetailItem.apply {
            adapter = adapterBatchDetailsItems
        }
//        if (batch)
    }
    fun onBatchItemSelected(item:BatchDetailItem, position: Int){
        when(position){
            0->{
                openZoomClass()
            }
            1->{
                openVideoLecture()
            }
            2->{
                openLibrary()
            }
            3->{
                findNavController().navigate(R.id.seeAttendenceFragment)
            }
            4->{
                openUpcomingExams()
            }
            5->{
                openHomework()
            }
        }
    }
    private fun openUpcomingExams() {
        var bundle = Bundle()
        bundle.putString("batch_id", batch.batch_id.toString())
        findNavController().navigate(R.id.upcomingExamListFragment, bundle)
    }
    private fun openVideoLecture() {
        var bundle = Bundle()
        bundle.putString("batch_id", batch.batch_id.toString())
        findNavController().navigate(R.id.videoLecturesFragment, bundle)
    }
    private fun openLibrary() {
        var bundle = Bundle()
        bundle.putString("batch_id", batch.batch_id.toString())
        findNavController().navigate(R.id.libraryFragment, bundle)
    }
    private fun openHomework() {
        var bundle = Bundle()
        bundle.putString("batch_id", batch.batch_id.toString())
        findNavController().navigate(R.id.homeworkFragment, bundle)
    }

    companion object {
        // For testing — later this will come from your API (batch.zoom_link or batch.meeting_url)
        private const val TEST_ZOOM_LINK = "https://zoom.us/test"
    }

    private fun openZoomClass() {
//        val zoomLink = batch.zoom_link
//            .ifEmpty { TEST_ZOOM_LINK }
        val zoomLink = TEST_ZOOM_LINK
        // Choose ONE of the two options below:
        openInBrowser(zoomLink)       // Option A
        // openInWebView(zoomLink)    // Option B
    }

    // Option A — External Browser
    private fun openInBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No browser found to open link", Toast.LENGTH_SHORT).show()
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) openZoomSession()
        else Toast.makeText(requireContext(), "Camera & Mic permissions required", Toast.LENGTH_SHORT).show()
    }

    // Call this instead of openZoomSession() directly
    private fun requestPermissionsAndJoin() {
        permissionLauncher.launch(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ))
    }
    private fun openZoomSession() {
        val intent = Intent(requireContext(), ZoomVideoActivity::class.java).apply {
            putExtra(ZoomVideoActivity.EXTRA_SESSION_NAME, "batch-${batch.batchName}-live")
            putExtra(ZoomVideoActivity.EXTRA_SESSION_TOKEN, getSessionToken()) // see below
            putExtra(ZoomVideoActivity.EXTRA_USER_NAME, "Student")
        }
        startActivity(intent)
    }

    // For TESTING only — generate a token from your backend normally
    private fun getSessionToken(): String {
        // Replace with a real JWT from your backend
        // For testing, generate one at: https://videosdk.zoom.us/test
        return "YOUR_TEST_JWT_TOKEN"
    }
    private fun clickEvent() {
        binding.backButton.setOnClickListener{
            findNavController().popBackStack()
        }
        binding.enrollButton.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("batch_price", batch.batch_price.toString())
            bundle.putString("batch_offer_price", batch.batch_offer_price.toString())
            bundle.putString("batch_id", batch.batch_id.toString())
            findNavController().navigate(R.id.selectPlanFragment, bundle)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.categories.collect { list ->
//                categoryAdapter.submitList(list)
//            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_batch_detail

    override fun restoreView() {
        if (::batch.isInitialized) setupUI()
        setupRecyclerView()
    }
}