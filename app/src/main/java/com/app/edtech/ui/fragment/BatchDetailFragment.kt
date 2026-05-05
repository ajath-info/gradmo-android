package com.app.edtech.ui.fragment

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentBatchDetailBinding
import com.app.edtech.model.institute_detail.response.InstituteDetailResponse
import com.app.edtech.model.institute_list.response.InstituteListResponse
import com.app.edtech.model.static.BatchDetailItem
import com.app.edtech.model.static.StaticLists
import com.app.edtech.ui.activity.ZoomVideoActivity
import com.app.edtech.ui.adapter.AdapterBatchDetailsItems
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import org.openjdk.tools.javac.util.Position

class BatchDetailFragment : BaseFragment<FragmentBatchDetailBinding>() {
    private lateinit var adapterBatchDetailsItems: AdapterBatchDetailsItems  // replace with your adapter
    private var batch: InstituteDetailResponse.Batche = InstituteDetailResponse.Batche()

    override fun initView(savedInstanceState: Bundle?) {
        batch = arguments?.getParcelable("batch") ?: InstituteDetailResponse.Batche()
        setupRecyclerView()
        setupUI()
        clickEvent()
        observeViewModel()
    }

    private fun setupUI() {
        binding.apply {
            Glide.with(root.context).load(batch.batch_image).placeholder(R.drawable.banner_placeholder).error(R.drawable.banner_placeholder).into(imageView)
            instituteName.text = batch.batch_name
            tvTeacherName.text = "N/A"
            tvTiming.text = "${batch.start_time} - ${batch.end_time}"
        }
    }

    private fun setupRecyclerView() {
        adapterBatchDetailsItems = AdapterBatchDetailsItems(StaticLists.batchDetailList, ::onBatchItemSelected)
        binding.recyclerBatchDetailItem.apply {
            adapter = adapterBatchDetailsItems
        }
    }
    fun onBatchItemSelected(item:BatchDetailItem, position: Int){
        when(position){
            0->{
                openZoomClass()
//                requestPermissionsAndJoin()
            }
            3->{
                openZoomClass()
//                requestPermissionsAndJoin()
            }
        }
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
            putExtra(ZoomVideoActivity.EXTRA_SESSION_NAME, "batch-${batch.batch_name}-live")
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
            findNavController().navigate(R.id.selectPlanFragment)
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
        setupRecyclerView()
    }
}