package com.app.gradmo.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentPromocodeListBinding
import com.app.gradmo.model.institute_list.response.InstituteListResponse
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.promocode.response.PromocodeListResponse
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.adapter.AdapterPromocodeList
import com.app.gradmo.ui.view_model.PromocodeListViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import kotlin.getValue

class PromocodeListFragment : BaseFragment<FragmentPromocodeListBinding>() {

    private lateinit var adapterPromocodeList: AdapterPromocodeList
    private val viewModel: PromocodeListViewModel by viewModels()
    private var institute: InstituteListResponse.Institute = InstituteListResponse.Institute()

    override fun initView(savedInstanceState: Bundle?) {
        institute = arguments?.getParcelable("institute") ?: InstituteListResponse.Institute()
        val accessToken = Preferences.getCustomModelPreference<LoginResponse>(
            requireContext(), LOGIN_DATA
        )?.data?.accessToken
        viewModel.hitPromoCodesApi("Bearer $accessToken", "1")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickEvent()
        observeViewModel()
        setupSearchInput()
    }

    private fun setupBatchRecycler(batches: List<PromocodeListResponse.Data.PromoCode>) {
        adapterPromocodeList = AdapterPromocodeList(batches) { selectedCode ->
            // Called on item tap — just selection, no navigation yet
        }
        binding.batchRecycler.adapter = adapterPromocodeList
    }

    // ✅ Local search — filters list as user types
    private fun setupSearchInput() {
        binding.promoCodeEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (::adapterPromocodeList.isInitialized) {
                    adapterPromocodeList.filter(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun clickEvent() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // ✅ Apply from EditText — validate code exists in list
        binding.applyPromocode.setOnClickListener {
            val typedCode = binding.promoCodeEditText.text.toString().trim()
            if (typedCode.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a promo code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (::adapterPromocodeList.isInitialized && adapterPromocodeList.selectByCode(typedCode)) {
                // Code found → navigate back with selection
                navigateBackWithCode()
            } else {
                Toast.makeText(requireContext(), "Promo code not found", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ Apply Button — use whatever is selected in the list
        binding.applyButton.setOnClickListener {
            if (::adapterPromocodeList.isInitialized) {
                val selected = adapterPromocodeList.getSelectedPromoCode()
                if (selected != null) {
                    navigateBackWithCode()
                } else {
                    Toast.makeText(requireContext(), "Please select a promo code", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ✅ Pass selected code back to previous fragment via savedStateHandle
    private fun navigateBackWithCode() {
        val selected = adapterPromocodeList.getSelectedPromoCode() ?: return
        // Set result in backstack entry so previous fragment receives it
        findNavController()
            .previousBackStackEntry
            ?.savedStateHandle
            ?.set("selected_promo_code", selected)   // pass any key-value here
        findNavController().popBackStack()
    }

    private fun observeViewModel() {
        viewModel.getPromoCodesLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.status == "true") {
                        setupBatchRecycler(it.data.data.promoCodes)
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.message}", Toast.LENGTH_SHORT).show()
                    }
                    ProcessDialog.dismissDialog(true)
                }
                Status.LOADING -> ProcessDialog.showDialog(requireContext(), true)
                Status.ERROR -> {
                    Log.e("TAG", "Failed: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                }
            }
        }
    }

    override fun restoreView() {
        viewModel.getPromoCodesLiveData().value?.data?.data?.promoCodes?.let {
            setupBatchRecycler(it)
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_promocode_list
}