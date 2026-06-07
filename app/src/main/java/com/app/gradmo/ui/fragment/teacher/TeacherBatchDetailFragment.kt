package com.app.gradmo.ui.fragment.teacher

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
import com.app.gradmo.databinding.FragmentTeacherBatchDetailBinding
import com.app.gradmo.model.batch_detail.BatchDetails
import com.app.gradmo.model.batch_list.BatchListResponse.Data.EnrolledBatche
import com.app.gradmo.model.institute_detail.response.InstituteDetailResponse
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.staticdata.BatchDetailItem
import com.app.gradmo.model.staticdata.StaticLists
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.activity.ZoomVideoActivity
import com.app.gradmo.ui.adapter.AdapterBatchDetailsItems
import com.app.gradmo.ui.fragment.BatchDetailFragment
import com.app.gradmo.ui.view_model.BatchDetailsViewModel
import com.app.gradmo.utils.CommonUtils.convertTimeFormat
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class TeacherBatchDetailFragment : BaseFragment<FragmentTeacherBatchDetailBinding>() {
    private lateinit var adapterBatchDetailsItems: AdapterBatchDetailsItems  // replace with your adapter
    var batch: BatchDetails = BatchDetails()
    var instituteName: String = ""
    private val viewModel: BatchDetailsViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        var teacherBatch = arguments?.getParcelable("teacherBatch") ?: EnrolledBatche()
        instituteName = arguments?.getString("instituteName") ?: ""
        setupRecyclerView()
        observeViewModel()
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
        viewModel.hitBatchDetailsApi("Bearer $accessToken", teacherBatch.batch_id.toString())
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
            enrollButton.isVisible=false
            Glide.with(root.context).load(batch.batchImage).placeholder(R.drawable.batch_placeholder).error(R.drawable.batch_placeholder).into(imageView)
            instituteName.text = this@TeacherBatchDetailFragment.instituteName
            batchName.text = batch.batchName
            tvTeacherName.text = batch.instructor
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
                openMarkAttendance()
            }
            4->{
                openUpcomingExams()
            }
            5->{
                openHomework()
            }
        }
    }
    private fun openMarkAttendance() {
        var bundle = Bundle()
        bundle.putString("batch_id", batch.batch_id.toString())
        findNavController().navigate(R.id.markAttendanceFragment, bundle)
    }
    private fun openUpcomingExams() {
        var bundle = Bundle()
        bundle.putString("batch_id", batch.batch_id.toString())
        findNavController().navigate(R.id.teacherExamListFragment, bundle)
    }
    private fun openVideoLecture() {
        var bundle = Bundle()
        bundle.putString("batch_id", batch.batch_id.toString())
        findNavController().navigate(R.id.teacherVideoLectureFragment, bundle)
    }
    private fun openLibrary() {
        var bundle = Bundle()
        bundle.putString("batch_id", batch.batch_id.toString())
        findNavController().navigate(R.id.teacherLibraryFragment, bundle)
    }
    private fun openHomework() {
        var bundle = Bundle()
        bundle.putString("batch_id", batch.batch_id.toString())
        findNavController().navigate(R.id.teacherHomeworkFragment, bundle)
    }

    companion object {
        // For testing — later this will come from your API (batch.zoom_link or batch.meeting_url)
        private const val TEST_ZOOM_LINK = "https://zoom.us/test"
    }

    private fun openZoomClass() {
        var bundle = Bundle()
        bundle.putString("batch_id", batch.batch_id.toString())
        bundle.putString("batchName", batch.batchName.toString())
        findNavController().navigate(R.id.teacherZoomFragment, bundle)
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

    override fun getLayoutId(): Int = R.layout.fragment_teacher_batch_detail

    override fun restoreView() {
        setupUI()
        setupRecyclerView()
    }
}