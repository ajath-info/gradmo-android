package com.app.gradmo.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentHomeBinding
import com.app.gradmo.model.banner.response.BannerResponse
import com.app.gradmo.model.batch_list.BatchListRequest
import com.app.gradmo.model.batch_list.BatchListResponse
import com.app.gradmo.model.enums.UserType
import com.app.gradmo.model.institute_list.request.InstitutesListRequest
import com.app.gradmo.model.institute_list.response.InstituteListResponse
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.PAYMENT_GATEWAY_ID
import com.app.gradmo.preferences.PAYMENT_GATEWAY_SECRET_KEY
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.preferences.USER_TYPE
import com.app.gradmo.preferences.UserPreference
import com.app.gradmo.ui.activity.HomeActivity
import com.app.gradmo.ui.adapter.AdapterTeacherEnrolledBatch
import com.app.gradmo.ui.adapter.HomeBannerAdapter
import com.app.gradmo.ui.adapter.HomeInstituteAdapter
import com.app.gradmo.ui.fragment.MyBatchFragment.Companion.toBatche
import com.app.gradmo.ui.view_model.HomeViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var homeInstituteAdapter: HomeInstituteAdapter
    private lateinit var adapterTeacherEnrolledBatch: AdapterTeacherEnrolledBatch
    private lateinit var adapterStudentEnrolledBatch: AdapterTeacherEnrolledBatch
    private lateinit var homeBannerAdapter: HomeBannerAdapter

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST = 1001

    // ── Auto-scroll ──────────────────────────────────────────────────────
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
        setUserType()
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
        isEnrolledInAnyBatch = Preferences.getStringPreference(requireContext(), com.app.gradmo.preferences.isEnrolledInAnyBatch) ?: ""
        setUIForUserType()
        when(UserPreference.userType){
            UserType.STUDENT ->{
                if (isEnrolledInAnyBatch!="N"&&isEnrolledInAnyBatch!="Y"){
                    viewModel.hitStudentBatchesDataApi("Bearer $accessToken", BatchListRequest())
                }else if(isEnrolledInAnyBatch=="Y"){
                    viewModel.hitStudentBatchesDataApi("Bearer $accessToken", BatchListRequest())
                }else{
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                    getLocationAndHitApi()
                }
            }
            UserType.TEACHER -> {
                viewModel.hitBatchesDataApi("Bearer $accessToken", BatchListRequest())
            }
            UserType.INSTITUTE -> {

            }
        }
        setHeaderName()
        viewModel.hitThirdPartyCredentialsDataApi("Bearer $accessToken")
        if (viewModel.getBannerLiveData().value?.data == null) {
            viewModel.hitBannerDataApi("Bearer $accessToken")
        }
    }

    private fun setUIForUserType() {
        when(UserPreference.userType){
            UserType.STUDENT ->{
                binding.layoutForTeacher.isVisible=false
                if(isEnrolledInAnyBatch=="Y"){
                    binding.layoutForStudent.isVisible=false
                    binding.studentBatchesLayout.isVisible=true
                }else{
                    binding.studentBatchesLayout.isVisible=false
                    binding.layoutForStudent.isVisible=true
                }
            }
            UserType.TEACHER -> {
                binding.studentBatchesLayout.isVisible=false
                binding.layoutForStudent.isVisible=false
                binding.layoutForTeacher.isVisible=true
            }
            UserType.INSTITUTE -> {

            }
        }
    }

    private fun setUserType() {
        var userType = Preferences.getStringPreference(requireContext(), USER_TYPE)
        when(userType){
            "student" -> {
                UserPreference.userType = UserType.STUDENT
            }
            "teacher" -> {
                UserPreference.userType = UserType.TEACHER
            }
            "institute" -> {
                UserPreference.userType = UserType.INSTITUTE
            }
        }
    }

    private fun setHeaderName() {
        val name = Preferences.getCustomModelPreference<LoginResponse>(
            requireContext(), LOGIN_DATA
        )?.data?.name
        binding.headerName.text = "Hi, ${name}"
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


    // ✅ STEP 1: Check permission → fetch location
    private fun getLocationAndHitApi() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchLocation()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    // ✅ STEP 2: Fetch location
    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val lat = location.latitude.toString()
                val lng = location.longitude.toString()

                Log.e("TAG", "LAT: $lat LNG: $lng")

                callApis(lat, lng)
            } else {
                // fallback if last location is null
                requestFreshLocation()
            }
        }
    }

    // ✅ STEP 3: Request fresh location if null
    @SuppressLint("MissingPermission")
    private fun requestFreshLocation() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000
        ).setMaxUpdates(1).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation
                    if (location != null) {
                        val lat = location.latitude.toString()
                        val lng = location.longitude.toString()

                        Log.e("TAG", "FRESH LAT: $lat LNG: $lng")

                        callApis(lat, lng)
                    }
                    fusedLocationClient.removeLocationUpdates(this)
                }
            },
            Looper.getMainLooper()
        )
    }

    // ✅ STEP 4: Call APIs with dynamic lat/lng
    private fun callApis(lat: String, lng: String) {
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken

        val request = InstitutesListRequest(
            latitude = lat,
            longitude = lng,
            order_field = "name",
            order_type = "DESC",
        )

        viewModel.hitInstitutesDataApi("Bearer $accessToken", request)
    }

    // ✅ STEP 5: Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                fetchLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location permission is required",
                    Toast.LENGTH_SHORT
                ).show()

                // Optional fallback (default location)
                callApis("28.6139", "77.2090") // Delhi fallback
            }
        }
    }

    private fun setupInstitutesRecycler(institutes: List<InstituteListResponse.Institute>) {
        homeInstituteAdapter = HomeInstituteAdapter(institutes, ::onItemSelected)
        binding.categoryRecyclerView.apply {
            adapter = homeInstituteAdapter
        }
    }
    private fun setupBatchesRecycler(batches: List<BatchListResponse.Data.EnrolledBatche>) {
        adapterTeacherEnrolledBatch = AdapterTeacherEnrolledBatch(batches, ::onBatchSelected)
        binding.batchRecyclerView.apply {
            adapter = adapterTeacherEnrolledBatch
        }

        binding.searchBatch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapterTeacherEnrolledBatch.filter(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun setupStudentBatchesRecycler(batches: List<BatchListResponse.Data.EnrolledBatche>) {
        adapterStudentEnrolledBatch = AdapterTeacherEnrolledBatch(batches, ::onBatchSelected)
        binding.studentBatchRecyclerView.apply {
            adapter = adapterStudentEnrolledBatch
        }

        binding.studentBatchSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapterStudentEnrolledBatch.filter(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    fun onBatchSelected(batch:BatchListResponse.Data.EnrolledBatche){
        val bundle=Bundle()
        bundle.putString("instituteName", batch.instituteName)
        if (UserPreference.userType== UserType.STUDENT){
            bundle.putParcelable("batch", batch.toBatche())
            findNavController().navigate(R.id.batchDetailFragment, bundle)
        }else{
            bundle.putParcelable("teacherBatch", batch.toBatche())
            findNavController().navigate(R.id.teacherBatchDetailFragment, bundle)
        }
    }
    fun onItemSelected(institute:InstituteListResponse.Institute){
        var bundle = Bundle()
        bundle.putParcelable("institute", institute)
        findNavController().navigate(R.id.instituteDetailsFragment, bundle)
    }
    private fun clickEvent() {
        binding.seeAllLayout.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("flow", "seeAll")
            findNavController().navigate(R.id.searchInstituteFragment, bundle)
        }
        binding.sideMenu.setOnClickListener {
            (activity as? HomeActivity)?.openDrawer()
        }

        binding.searchField.setOnClickListener {
            findNavController().navigate(R.id.searchInstituteFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }

    override fun getLayoutId(): Int = R.layout.fragment_home

    private fun setObserver() {
        viewModel.getBatchesLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        setupBatchesRecycler(it.data.data.enrolled_batches ?: listOf())
//                        setupRatingRecycler(it.data.rating)
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
                    Log.e("TAG", "Login Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

        viewModel.getStudentBatchesLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Login success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        if(isEnrolledInAnyBatch=="Y"){
                            setupStudentBatchesRecycler(it.data.data.enrolled_batches ?: listOf())
                        }else{
                            if ((it.data.data.enrolled_batches?.size ?: 0) > 0){
                                Preferences.setStringPreference(requireContext(), com.app.gradmo.preferences.isEnrolledInAnyBatch, "Y")
                                setupStudentBatchesRecycler(it.data.data.enrolled_batches ?: listOf())
                            }else{
                                Preferences.setStringPreference(requireContext(), com.app.gradmo.preferences.isEnrolledInAnyBatch, "N")
                                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                                getLocationAndHitApi()
                            }
                        }
//                        setupRatingRecycler(it.data.rating)
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
                    Log.e("TAG", "Login Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }

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
                    if (it.data?.status == "true") {
                        setupInstitutesRecycler(it.data.institutes)
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

        viewModel.getThirdPartyCredentialsLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.status == "true") {
                        Log.i("TAG", "setObserver: "+ Gson().toJson(it.data))
                        Preferences.setStringPreference(requireContext(), PAYMENT_GATEWAY_ID, it.data.payment_gateway_api_credentials.Key_id)
                        Preferences.setStringPreference(requireContext(), PAYMENT_GATEWAY_SECRET_KEY, it.data.payment_gateway_api_credentials.secret_key)
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
    }

    override fun restoreView() {
        setHeaderName()
        setUIForUserType()
        viewModel.getBannerLiveData().value?.data?.data?.banners?.let {
            setUpBannerViewPager(it)
        }
        viewModel.getInstitutesLiveData().value?.data?.institutes?.let {
            setupInstitutesRecycler(it)
        }
        viewModel.getBatchesLiveData().value?.data?.data?.enrolled_batches?.let {
            setupBatchesRecycler(it)
        }
        viewModel.getStudentBatchesLiveData().value?.data?.data?.enrolled_batches?.let {
            setupStudentBatchesRecycler(it)
        }
    }
}