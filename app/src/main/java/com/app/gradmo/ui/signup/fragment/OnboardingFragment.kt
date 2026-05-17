package com.app.gradmo.ui.signup.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentOnboardingBinding
import com.app.gradmo.model.onboarding_item.OnboardingItem
import com.app.gradmo.ui.signup.adapter.OnboardingAdapter

class OnboardingFragment : BaseFragment<FragmentOnboardingBinding>() {

    private lateinit var adapter: OnboardingAdapter
    private var currentPosition = 0

    private val onboardingList = listOf(
        OnboardingItem(
            R.drawable.onboarding1,
            "As a Student",
            "Discover amazing features in our app"
        ),
        OnboardingItem(
            R.drawable.onboarding2,
            "As a teacher",
            "Explore our App"
        ),
        OnboardingItem(
            R.drawable.onboarding3,
            "As an Institue",
            "Enjoy seamless experience"
        )
    )

    override fun getLayoutId(): Int = R.layout.fragment_onboarding

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupClicks()
    }
    private fun setupViewPager() {
        adapter = OnboardingAdapter(onboardingList)

        binding.viewPager.adapter = adapter
        binding.indicator.setViewPager(binding.viewPager)

        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPosition = position
                updateUI()
            }
        })
    }

    private fun updateUI() {
        if (currentPosition == onboardingList.lastIndex) {
            binding.skipButton.visibility = View.INVISIBLE
            binding.skipButton.isClickable = false
            binding.nextButton.text = "Get Started"
        } else {
            binding.skipButton.visibility = View.VISIBLE
            binding.skipButton.isClickable = true
            binding.nextButton.text = "Next"
        }
    }

    private fun setupClicks() {
        binding.nextButton.setOnClickListener {
            if (currentPosition < onboardingList.lastIndex) {
                binding.viewPager.currentItem = currentPosition + 1
            } else {
                navigateToHome()
            }
        }

        binding.skipButton.setOnClickListener {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        // Save onboarding completed
//        Preferences.saveBoolean(requireContext(), "IS_ONBOARDING_DONE", true)

        findNavController().navigate(R.id.getStartedFragment)
    }
}