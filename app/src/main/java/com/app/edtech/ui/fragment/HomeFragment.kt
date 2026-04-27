package com.app.edtech.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.app.edtech.R
import com.app.edtech.base.BaseFragment
import com.app.edtech.databinding.FragmentHomeBinding
import com.app.edtech.model.banner.response.BannerResponse
import com.app.edtech.model.institute_list.request.InstitutesListRequest
import com.app.edtech.model.institute_list.response.InstituteListResponse
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.preferences.LOGIN_DATA
import com.app.edtech.preferences.Preferences
import com.app.edtech.ui.activity.HomeActivity
import com.app.edtech.ui.adapter.HomeBannerAdapter
import com.app.edtech.ui.adapter.HomeInstituteAdapter
import com.app.edtech.ui.view_model.HomeViewModel
import com.app.edtech.utils.network_utils.ProcessDialog
import com.app.edtech.utils.network_utils.Status
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
    private lateinit var homeBannerAdapter: HomeBannerAdapter

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST = 1001

    // ── Auto-scroll ──────────────────────────────────────────────────────
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private val AUTO_SCROLL_DELAY = 3000L  // 3 seconds

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocationAndHitApi()
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(
            requireContext(), LOGIN_DATA
        )?.data?.accessToken
        if (viewModel.getBannerLiveData().value?.data == null) {
            viewModel.hitBannerDataApi("Bearer $accessToken")
        }
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
        homeInstituteAdapter = HomeInstituteAdapter(institutes)
        binding.categoryRecyclerView.apply {
            adapter = homeInstituteAdapter
        }
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
    }

    override fun restoreView() {
        viewModel.getBannerLiveData().value?.data?.data?.banners?.let {
            setUpBannerViewPager(it)
        }
        viewModel.getInstitutesLiveData().value?.data?.institutes?.let {
            setupInstitutesRecycler(it)
        }
    }
}