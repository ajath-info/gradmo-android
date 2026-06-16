package com.app.gradmo.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentInstituteDetailsBinding
import com.app.gradmo.model.banner.response.BannerResponse
import com.app.gradmo.model.enums.UserType
import com.app.gradmo.model.institute_detail.request.InstituteDetailRequest
import com.app.gradmo.model.institute_detail.response.InstituteDetailResponse
import com.app.gradmo.model.institute_list.response.InstituteListResponse
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.preferences.UserPreference
import com.app.gradmo.ui.adapter.AdapterInstituteBatch
import com.app.gradmo.ui.adapter.AdapterInstituteRating
import com.app.gradmo.ui.adapter.HomeBannerAdapter
import com.app.gradmo.ui.view_model.InstituteDetailsViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.bumptech.glide.Glide
import com.google.gson.Gson

class InstituteDetailsFragment : BaseFragment<FragmentInstituteDetailsBinding>() {
    private lateinit var adapterInstituteBatch: AdapterInstituteBatch  // replace with your adapter
    private lateinit var adapterInstituteRating: AdapterInstituteRating  // replace with your adapter
    private lateinit var homeBannerAdapter: HomeBannerAdapter


    private val viewModel: InstituteDetailsViewModel by viewModels()

    private var institute: InstituteListResponse.Institute = InstituteListResponse.Institute()

    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private val AUTO_SCROLL_DELAY = 7000L  // 3 seconds
    private var isEnrolledInAnyBatch = ""

    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val itemCount = homeBannerAdapter.itemCount
            if (itemCount == 0) return
            val nextItem = binding.bannerViewPager.currentItem + 1
            binding.bannerViewPager.setCurrentItem(nextItem, true)
            autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY)
        }
    }

    // ── Dot Indicator ────────────────────────────────────────────────────
    private val dots = mutableListOf<ImageView>()

    override fun initView(savedInstanceState: Bundle?) {
        institute = arguments?.getParcelable("institute") ?: InstituteListResponse.Institute()
        Log.i("TAG", "institute in institute detail: " + Gson().toJson(institute))
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
        viewModel.hitInstitutesDataApi("Bearer $accessToken", InstituteDetailRequest(institute.instituteId.toString()))
        setupUI()
        if (viewModel.getBannerLiveData().value?.data == null) {
            viewModel.hitBannerDataApi("Bearer $accessToken", institute.instituteId.toString())
        }
    }

    private fun setupUI() {
        binding.apply {
            Glide.with(root.context).load(institute.imageUrl).placeholder(R.drawable.banner_placeholder).error(R.drawable.banner_placeholder).into(instituteImage)
            instituteName.text = institute.name
            phoneNumber.text = institute.mobile?.ifBlank { "N/A" } ?: ""
            emailAddress.text = institute.email?.ifBlank { "N/A" } ?: ""
            instituteAddress.text = institute.address?.ifBlank { "N/A" } ?: ""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickEvent()
        observeViewModel()
    }
    // ── Banner Setup ─────────────────────────────────────────────────────
    fun setUpBannerViewPager(banners: List<BannerResponse.Data.Banner>) {
        homeBannerAdapter = HomeBannerAdapter(banners)
        binding.bannerViewPager.adapter = homeBannerAdapter

        // Start at the middle for infinite feel
        val startPos = homeBannerAdapter.getStartPosition()
        binding.bannerViewPager.setCurrentItem(startPos, false)

        setupDotIndicator(banners.size)
        setupPageChangeListener(banners.size)
        startAutoScroll()
    }

    // ── Dots ─────────────────────────────────────────────────────────────
    private fun setupDotIndicator(count: Int) {
        dots.clear()
        binding.dotsIndicator.removeAllViews()

        repeat(count) { index ->
            val dot = ImageView(requireContext()).apply {
                setImageResource(
                    if (index == 0) R.drawable.dot_active else R.drawable.dot_inactive
                )
                val size = 10.dpToPx()
                val params = LinearLayout.LayoutParams(size, size).apply {
                    setMargins(4.dpToPx(), 0, 4.dpToPx(), 0)
                }
                layoutParams = params
            }
            dots.add(dot)
            binding.dotsIndicator.addView(dot)
        }
    }

    private fun updateDots(realPosition: Int) {
        dots.forEachIndexed { index, dot ->
            dot.setImageResource(
                if (index == realPosition) R.drawable.dot_active else R.drawable.dot_inactive
            )
        }
    }

    private fun setupPageChangeListener(realCount: Int) {
        binding.bannerViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val realPos = homeBannerAdapter.getRealPosition(position)
                updateDots(realPos)
            }
        })
    }

    // ── Auto Scroll Controls ─────────────────────────────────────────────
    private fun startAutoScroll() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY)
    }

    private fun stopAutoScroll() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
    }

    // Stop when fragment is not visible, resume when it is
    override fun onPause() {
        super.onPause()
        stopAutoScroll()
    }

    override fun onResume() {
        super.onResume()
        if (::homeBannerAdapter.isInitialized) startAutoScroll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoScroll()
    }

    // ── Extension ────────────────────────────────────────────────────────
    private fun Int.dpToPx(): Int =
        (this * resources.displayMetrics.density).toInt()


    private fun setupRatingRecycler(rating: InstituteDetailResponse.Rating) {
        adapterInstituteRating = AdapterInstituteRating(rating, ::onRatingSelected)
        binding.ratingReviewRecycler.apply {
            adapter = adapterInstituteRating          // attach your adapter here
        }
    }
    private fun setupBatchRecycler(batches: List<InstituteDetailResponse.Batche>) {
        adapterInstituteBatch = AdapterInstituteBatch(batches, ::onBatchSelected)
        binding.categoryRecyclerView.apply {
            adapter = adapterInstituteBatch          // attach your adapter here
        }
    }

    fun onBatchSelected(batch:InstituteDetailResponse.Batche){
        val bundle=Bundle()
        bundle.putParcelable("batch", batch)
        bundle.putString("instituteName", institute.name)
        if (UserPreference.userType==UserType.STUDENT){
            findNavController().navigate(R.id.batchDetailFragment, bundle)
        }else{
            findNavController().navigate(R.id.teacherBatchDetailFragment, bundle)

        }
    }
    fun onRatingSelected(){

    }

    private fun clickEvent() {
        binding.backButton.setOnClickListener{
            findNavController().popBackStack()
        }
        binding.seeAllLayout.setOnClickListener {
            var bundle = Bundle()
            bundle.putParcelable("institute", institute)
            bundle.putString("instituteName", institute.name)
            findNavController().navigate(R.id.batchListFragment, bundle)
        }
    }

    private fun observeViewModel() {
        viewModel.getBannerLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.status == "true") {
                        setUpBannerViewPager(it.data.data.banners)
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

        viewModel.getInstitutesLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        setupBatchRecycler(it.data.batches)
//                        setupRatingRecycler(it.data.rating)
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
        setupUI()
        viewModel.getInstitutesLiveData().value?.data?.batches?.let {
            setupBatchRecycler(it)
        }
        viewModel.getBannerLiveData().value?.data?.data?.banners?.let {
            setUpBannerViewPager(it)
        }
    }
    override fun getLayoutId(): Int = R.layout.fragment_institute_details
}