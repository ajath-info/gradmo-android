//package com.app.hihlo.ui.signup.fragment
//
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import com.app.hihlo.R
//import com.app.hihlo.databinding.FragmentEmailBinding
//import com.app.hihlo.ui.signup.model.SignUp
//import com.app.hihlo.ui.signup.view_model.SendMailOtpViewModel
//import com.app.hihlo.utils.CommonUtils
//import com.app.hihlo.utils.network_utils.ProcessDialog
//import com.app.hihlo.utils.network_utils.Status
//import com.google.gson.Gson
//import kotlin.getValue
//
//class EmailFragment : Fragment() {
//    private lateinit var binding:FragmentEmailBinding
//    var from = ""
//    private val viewModel: SendMailOtpViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            from = it.getString("from").toString()
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentEmailBinding.inflate(layoutInflater)
//        initViews()
//        return binding.root
//    }
//
//    private fun initViews() {
//        binding.apply {
//            ivBack.setOnClickListener {
//                findNavController().popBackStack()
//            }
//            btnResetPassword.setOnClickListener {
//                checkValidation()
//            }
//            clMain.setOnClickListener {
//                CommonUtils.hideKeyboard(requireActivity())
//            }
//        }
//    }
//
//    private fun checkValidation() {
//        val etEmail = binding.emailInput.text.toString()
//        if(etEmail.isEmpty()){
//            Toast.makeText(requireActivity(), "Please enter your email", Toast.LENGTH_SHORT).show()
//        }else if(!CommonUtils.isValidEmail(etEmail)){
//            Toast.makeText(requireActivity(), "Please enter valid email", Toast.LENGTH_SHORT).show()
//        }else{
//
//            viewModel.hitSendEmailOtp(etEmail,null,"forgot_password")
//            initObserver(etEmail,"forgot_password")
//
//            /*val bundle = Bundle()
//            bundle.putString("from",from)
//            findNavController().navigate(R.id.otpFragment,bundle)*/
//            // hit api
//        }
//    }
//
//    private fun initObserver(etEmail: String, from: String) {
//        viewModel.getLoginLiveData().observe(viewLifecycleOwner) {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    Log.e("TAG", "interest list success: ${Gson().toJson(it)}")
//                    if (it.data?.status==1){
//                        if (it.data.code == 200){
//                            val list = it.data.payload
//                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
//                            val data = SignUp(
//                                email = etEmail
//                            )
//                            val bundle = Bundle()
//                            bundle.putString("from",from)
//                            bundle.putParcelable("data",data)
//                            Log.i("TAG", "initObserver: "+from)
//                            findNavController().navigate(R.id.otpFragment,bundle)
//                        }else{
//                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
//                        }
//                    }else{
//                        Toast.makeText(requireContext(), "${it.data?.message}", Toast.LENGTH_SHORT).show()
//                    }
//                    ProcessDialog.dismissDialog(true)
//                }
//                Status.LOADING -> {
//                    ProcessDialog.showDialog(requireContext(), true)
//                }
//                Status.ERROR -> {
//                    Log.e("TAG", "Login Failed: ${it.message}")
//                    ProcessDialog.dismissDialog(true)
//                }
//            }
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        CommonUtils.touchHideKeyBoard(view,requireActivity())
//    }
//}