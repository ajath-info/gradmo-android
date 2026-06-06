package com.app.gradmo.ui.signup.fragment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.gradmo.R
import com.app.gradmo.databinding.FragmentEditProfileNewBinding
import com.app.gradmo.model.address.city.GetCitiesRequest
import com.app.gradmo.model.address.city.GetCitiesResponse
import com.app.gradmo.model.address.state.GetStatesRequest
import com.app.gradmo.model.address.state.GetStatesResponse
import com.app.gradmo.model.batch_list.BatchListRequest
import com.app.gradmo.model.enums.UserType
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.model.login.response.UserData
import com.app.gradmo.preferences.IS_LOGIN
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.preferences.USER_TYPE
import com.app.gradmo.preferences.UserPreference
import com.app.gradmo.ui.activity.HomeActivity
import com.app.gradmo.ui.signup.view_model.EditProfileViewModel
import com.app.gradmo.utils.CommonUtils
import com.app.gradmo.utils.CommonUtils.touchHideKeyBoard
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class EditProfileNewFragment : Fragment() {
    private lateinit var binding:FragmentEditProfileNewBinding
    var imageUrl = ""
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private val viewModel: EditProfileViewModel by viewModels()

    var from = ""

    val tempDetails = UserPreference.loginRequest
    var userData = UserData()
    private var selectedImageFile: File? = null

    private var stateList = ArrayList<GetStatesResponse.State>()
    private var cityList = ArrayList<GetCitiesResponse.City>()

    private var selectedStateId: Int? = null
    private var selectedStateName: String = ""

    private var selectedCityId: Int? = null
    private var selectedCityName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController()
        val navGraph = navController.graph

        from = arguments?.getString("from") ?: ""
        if(navController.currentDestination?.id == navGraph.startDestinationId && from.isEmpty()){
            from = "incompleteProfile"
        }
        Log.i("TAG", "onCreate from: $from")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileNewBinding.inflate(layoutInflater)
        setUserType()
        initViews()
        backListener()
        setUI()
        binding.root.setOnClickListener {
            touchHideKeyBoard(binding.root, requireActivity())
        }
        return binding.root
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
    private fun backListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (from!="signup"||from!="incompleteProfile"){
                (activity as HomeActivity).updateUserInMenu()
//                            findNavController().popBackStack()
                (activity as HomeActivity).navigateToHome()
            }

        }
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
                        Preferences.setCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA, it.data)
                        CommonUtils.hideKeyboard(requireActivity())
                        if (from=="signup" || from=="incompleteProfile"){
                            Preferences.setStringPreference(requireContext(), IS_LOGIN, "2")
                            requireActivity().finish()
                            startActivity(Intent(requireActivity(), HomeActivity::class.java))
                        }else{
                            (activity as HomeActivity).updateUserInMenu()
//                            findNavController().popBackStack()
                            (activity as HomeActivity).navigateToHome()
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
        viewModel.getCitiesLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "Cities success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        if (it.data.status == "true") {
                            cityList = ArrayList(it.data.cities)
                            showCityPopup()
                        }
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.msg}", Toast.LENGTH_SHORT).show()
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
        viewModel.getStatesLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.e("TAG", "States success: ${Gson().toJson(it)}")
                    if (it.data?.status == "true") {
                        if (it.data.status == "true") {
                            stateList.clear()
                            stateList = ArrayList(it.data.states)
                            showStatePopup()
                        }
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

    private fun setUI() {
        binding.apply {
            when(UserPreference.userType){
                UserType.STUDENT ->{
                    binding.educationalInfoText.isVisible = true
                    binding.etSchoolName.isVisible = true
                    binding.etGrade.isVisible = true
                }
                UserType.TEACHER -> {
                    binding.educationalInfoText.isVisible = false
                    binding.etSchoolName.isVisible = false
                    binding.etGrade.isVisible = false
                }
                UserType.INSTITUTE -> {

                }
            }
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
                selectedStateName = userData.state.toString()
                etCity.setText(userData.city)
                selectedCityName = userData.city.toString()
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
        binding.etState.setOnClickListener {
            viewModel.hitStatesDataApi(GetStatesRequest(country_id = "105")) // call API
        }

        binding.etCity.setOnClickListener {
            if (selectedStateId == null) {
                Toast.makeText(requireContext(), "Please select state first", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.hitCitiesDataApi(GetCitiesRequest(selectedStateId.toString(), limit = "100"))
            }
        }
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
            checkValidation()
        }
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri: Uri? = result.data?.data

                Log.e("IMAGE_DEBUG", "Picked URI: $imageUri")

                if (imageUri != null) {

                    selectedImageFile = uriToFile(imageUri, requireActivity())

                    Log.e("IMAGE_DEBUG", "File path: ${selectedImageFile?.absolutePath}")
                    Log.e("IMAGE_DEBUG", "File exists: ${selectedImageFile?.exists()}")
                    Log.e("IMAGE_DEBUG", "File size: ${selectedImageFile?.length()} bytes")
                    Log.e("IMAGE_DEBUG", "File name: ${selectedImageFile?.name}")

                    Glide.with(requireContext())
                        .load(imageUri)
                        .into(binding.ivUserImage)

                } else {
                    Log.e("IMAGE_DEBUG", "Image URI is NULL")
                    Toast.makeText(requireActivity(), "Cancelled", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("IMAGE_DEBUG", "Result not OK")
            }

        }
    }

    fun uriToFile(uri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri)

        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType) ?: "jpg"

        val file = File(context.cacheDir, "image_${System.currentTimeMillis()}.$extension")

        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

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


        if(selectedImageFile == null && from=="signup"){
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
        }else if(etSchoolName.isEmpty() && UserPreference.userType == UserType.STUDENT){
            Toast.makeText(requireActivity(), "Please enter your school name", Toast.LENGTH_SHORT).show()
        }else if(etGrade.isEmpty() && UserPreference.userType == UserType.STUDENT){
            Toast.makeText(requireActivity(), "Please enter your grade", Toast.LENGTH_SHORT).show()
        }else if(selectedStateName.isEmpty()){
            Toast.makeText(requireActivity(), "Please select state", Toast.LENGTH_SHORT).show()
        }else if(selectedCityName.isEmpty()) {
            Toast.makeText(requireActivity(), "Please select city", Toast.LENGTH_SHORT).show()
        }else{
            val studentId = UserPreference.studentId.ifEmpty { Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.studentId }
            val userType = Preferences.getStringPreference(requireActivity(), USER_TYPE)
            val accessToken = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.data?.accessToken
            val map = HashMap<String, RequestBody>()

            fun String.toRequestBody(): RequestBody {
                return this.toRequestBody("text/plain".toMediaTypeOrNull())
            }

            map["name"] = etName.toString().toRequestBody()
            map["email"] = etEmail.toString().toRequestBody()
            map["mobile"] = etPhone.toString().toRequestBody()
            map["address"] = etLocality.toString().toRequestBody()
            map["country"] = "India".toRequestBody()
            map["state"] = selectedStateName.toRequestBody()
            map["city"] = selectedCityName.toRequestBody()
            map["pincode"] = etPincode.toString().toRequestBody()
            map["school_college_name"] = etSchoolName.toString().toRequestBody()
            map["grade"] = etGrade.toString().toRequestBody()
            map["user_type"] = userType!!.toRequestBody()
            map["student_id"] = studentId!!.toString().toRequestBody()
            Log.e("TAG", "checkValidation: $map")
//            viewModel.hitUpdateProfile(model, "Bearer $accessToken")
            Log.e("IMAGE_DEBUG", "Before API selectedImageFile: $selectedImageFile")
            val imagePart = selectedImageFile?.let {
                val fileName = if (it.name.contains(".")) it.name else "${it.name}.jpg"
                Log.e("IMAGE_DEBUG", "Final file name: $fileName")
                val mimeType = if (fileName.endsWith(".png", ignoreCase = true)) "image/png" else "image/jpeg"
                val requestFile = it.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", fileName, requestFile)
            }
            viewModel.hitUpdateProfile(imagePart, map, "Bearer $accessToken")
        }
    }
    private fun showStatePopup() {
        showDropdownPopup(
            anchor = binding.etState,
            list = stateList,
            getText = { it.name },
            isSelected = { it.name == selectedStateName },
            onItemClick = {
                selectedStateId = it.id
                selectedStateName = it.name

                binding.etState.text = it.name

                // 🔥 reset city
                selectedCityId = null
                selectedCityName = ""
                binding.etCity.text = ""
            }
        )
    }
    private fun showCityPopup() {
        showDropdownPopup(
            anchor = binding.etCity,
            list = cityList,
            getText = { it.city },
            isSelected = { it.city == selectedCityName },
            onItemClick = {
                selectedCityId = it.id
                selectedCityName = it.city

                binding.etCity.text = it.city
            }
        )
    }
    private fun <T> showDropdownPopup(
        anchor: View,
        list: List<T>,
        getText: (T) -> String,
        isSelected: (T) -> Boolean,
        onItemClick: (T) -> Unit
    ) {
        val view = layoutInflater.inflate(R.layout.popup_list, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        val popupWindow = PopupWindow(
            view,
            (anchor.width * 0.95).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = object : RecyclerView.Adapter<PopupVH>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopupVH {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_simple_text, parent, false)
                return PopupVH(v)
            }

            override fun getItemCount() = list.size

            override fun onBindViewHolder(holder: PopupVH, position: Int) {
                val item = list[position]

                holder.tvText.text = getText(item)

                holder.viewDot.visibility =
                    if (isSelected(item)) View.VISIBLE else View.GONE

                holder.itemView.setOnClickListener {
                    onItemClick(item)
                    popupWindow.dismiss()
                }
            }
        }

        popupWindow.elevation = 10f
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        popupWindow.isOutsideTouchable = true

        popupWindow.showAsDropDown(anchor, 0, 8)
    }

    class PopupVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvText: TextView = view.findViewById(R.id.tvText)
        val viewDot: View = view.findViewById(R.id.viewDot)
    }
}





