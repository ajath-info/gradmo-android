package com.app.edtech.ui.signup.fragment

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.edtech.R
import com.app.edtech.databinding.FragmentEditProfileNewBinding
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.model.login.response.UserData
import com.app.edtech.preferences.IS_LOGIN
import com.app.edtech.preferences.LOGIN_DATA
import com.app.edtech.preferences.Preferences
import com.app.edtech.preferences.USER_TYPE
import com.app.edtech.preferences.UserPreference
import com.app.edtech.ui.activity.HomeActivity
import com.app.edtech.ui.signup.view_model.EditProfileViewModel
import com.app.edtech.utils.CommonUtils
import com.app.edtech.utils.CommonUtils.touchHideKeyBoard
import com.app.edtech.utils.network_utils.ProcessDialog
import com.app.edtech.utils.network_utils.Status
import com.bumptech.glide.Glide
import com.google.common.io.Files.getFileExtension
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class EditProfileNewFragment : Fragment() {
    private lateinit var binding:FragmentEditProfileNewBinding
    var imageUrl = ""
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private val viewModel: EditProfileViewModel by viewModels()

    var from = ""

    val tempDetails = UserPreference.loginRequest
    var userData = UserData()
    private var selectedImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        from = arguments?.getString("from").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileNewBinding.inflate(layoutInflater)
        initViews()
        backListener()
        setUI()
        binding.root.setOnClickListener {
            touchHideKeyBoard(binding.root, requireActivity())
        }
        return binding.root
    }

    private fun backListener() {
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            if (from!="signup")
//            findNavController().popBackStack()
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
    }

    private fun setObserver() {
        viewModel.getUpdateProfileLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data?.status=="true"){
                        Toast.makeText(requireContext(), it.data.msg, Toast.LENGTH_SHORT).show()
//                        Preferences.setCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA, it.data)
                        CommonUtils.hideKeyboard(requireActivity())
                        if (from=="signup"){
                            Preferences.setStringPreference(requireContext(), IS_LOGIN, "2")
                            requireActivity().finish()
                            startActivity(Intent(requireActivity(), HomeActivity::class.java))
                        }else{
                            findNavController().popBackStack()
                        }
                    }else{
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("TAG", "interest list success: ${Gson().toJson(it)}")

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

    private fun setUI() {
        binding.apply {
            if (from=="signup"){
                etName.setText(tempDetails.name)
                nameLabel.text = tempDetails.name
                etEmail.setText(tempDetails.email)
                etPhoneNumber.setText(tempDetails.mobile)
                etName.isEnabled = false
                etEmail.isEnabled = false
                etPhoneNumber.isEnabled = false
            }else{
                etName.setText(userData.name)
                nameLabel.text = userData.name
                etEmail.setText(userData.email)
                etPhoneNumber.setText(userData.mobile)
                etLocality.setText(userData.address)
                etState.setText(userData.state)
                etCity.setText(userData.city)
                etPincode.setText(userData.pincode)
                etSchoolName.setText(userData.schoolCollegeName)
                etGrade.setText(userData.grade)
                Glide.with(requireContext()).load(userData.image).placeholder(R.drawable.profile_placeholder).into(binding.ivUserImage)
            }
        }
    }

    private fun initViews(){
        userData = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data ?: UserData()
        touchHideKeyBoard(binding.root, requireActivity())
        binding.ivEditIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            imagePickerLauncher.launch(intent)
        }
        binding.backButton.setOnClickListener {
            (activity as HomeActivity).navigateToHome()
//            findNavController().navigate(R.id.homeFragment)
        }
        binding.updateButton.setOnClickListener {
            imageUrl = "https://www.flaticon.com/free-icon/user_219970"
            checkValidation()
        }
            imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
//                    openCropActivity(imageUri)
                    selectedImageFile = uriToFile(imageUri, requireActivity())
                    Glide.with(requireContext()).load(imageUri).into(binding.ivUserImage)
                }else{
                    Toast.makeText(requireActivity(), "Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun uriToFile(uri: Uri, activity: Activity): File {
        val contentResolver = activity.contentResolver
        val fileExtension = getFileExtension(uri.toString())
        val fileName = "image_${System.currentTimeMillis()}.$fileExtension"
        val file = File(activity.cacheDir, fileName)

        contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return file
    }


    private fun checkValidation(){
        val etName = binding.etName.text.trim()
        val etEmail = binding.etEmail.text.trim()
        val etPhone = binding.etPhoneNumber.text.trim()
        val etLocality = binding.etLocality.text.trim()
        val etPincode = binding.etPincode.text.trim()
        val etSchoolName = binding.etSchoolName.text.trim()
        val etGrade = binding.etGrade.text.trim()


        if(selectedImageFile == null){
            Toast.makeText(requireActivity(), "Please select your image", Toast.LENGTH_SHORT).show()
        }else if(etName.isEmpty()){
            Toast.makeText(requireActivity(), "Please enter your name", Toast.LENGTH_SHORT).show()
        }else if(etEmail.isEmpty()){
            Toast.makeText(requireActivity(), "Please enter your email", Toast.LENGTH_SHORT).show()
        }else if(!CommonUtils.isValidEmail(etEmail.toString())){
            Toast.makeText(requireActivity(), "Please enter valid email", Toast.LENGTH_SHORT).show()
        }else if(etPhone.isEmpty()){
            Toast.makeText(requireActivity(), "Please enter your phone number", Toast.LENGTH_SHORT).show()
        }else if(etLocality.isEmpty()){
            Toast.makeText(requireActivity(), "Please enter your locality", Toast.LENGTH_SHORT).show()
        }else if(etPincode.isEmpty()){
            Toast.makeText(requireActivity(), "Please enter the pincode", Toast.LENGTH_SHORT).show()
        }else if(etSchoolName.isEmpty()){
            Toast.makeText(requireActivity(), "Please enter your school name", Toast.LENGTH_SHORT).show()
        }else if(etGrade.isEmpty()){
            Toast.makeText(requireActivity(), "Please enter your grade", Toast.LENGTH_SHORT).show()
        }else{
            val studentId = UserPreference.studentId.ifEmpty { Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.studentId }
            val userType = Preferences.getStringPreference(requireActivity(), USER_TYPE)
            val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.access_token
            /*val model = UpdateProfileRequest(
                image = imageUrl,
                name = etName.toString(),
                email = etEmail.toString(),
                mobile = etPhone.toString(),
                address = etLocality.toString(),
                country = "India",
                state = "Uttar Pradesh",
                city = "Noida",
                pincode = etPincode.toString(),
                school_college_name = etSchoolName.toString(),
                grade = etGrade.toString(),
                user_type = userType,
                student_id = studentId
            )*/
            val map = HashMap<String, RequestBody>()

            fun String.toRequestBody(): RequestBody {
                return this.toRequestBody("text/plain".toMediaTypeOrNull())
            }

            map["name"] = etName.toString().toRequestBody()
            map["email"] = etEmail.toString().toRequestBody()
            map["mobile"] = etPhone.toString().toRequestBody()
            map["address"] = etLocality.toString().toRequestBody()
            map["country"] = "India".toRequestBody()
            map["state"] = "Uttar Pradesh".toRequestBody()
            map["city"] = "Noida".toRequestBody()
            map["pincode"] = etPincode.toString().toRequestBody()
            map["school_college_name"] = etSchoolName.toString().toRequestBody()
            map["grade"] = etGrade.toString().toRequestBody()
            map["user_type"] = userType!!.toRequestBody()
            map["student_id"] = studentId!!.toString().toRequestBody()
            Log.e("TAG", "checkValidation: $map")
//            viewModel.hitUpdateProfile(model, "Bearer $accessToken")
            viewModel.hitUpdateProfile(imagePart, map, "Bearer $accessToken")
            //updateApi()
        }
    }
    val imagePart = selectedImageFile?.let {
        val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
        MultipartBody.Part.createFormData("image", it.name, requestFile)
    }
}





