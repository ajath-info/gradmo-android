package com.app.edtech.ui.signup.fragment

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.app.edtech.databinding.FragmentEditProfileNewBinding
import com.app.edtech.model.login.response.LoginResponse
import com.app.edtech.model.profile.request.UpdateProfileRequest
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
import java.io.File

class EditProfileNewFragment : Fragment() {
    private lateinit var binding:FragmentEditProfileNewBinding
    var imageUrl = ""
    var newImageUrl = ""
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private val viewModel: EditProfileViewModel by viewModels()

    var from = ""

    val tempDetails = UserPreference.loginRequest

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
        setUI()
        binding.root.setOnClickListener {
            touchHideKeyBoard(binding.root, requireActivity())
        }
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            if (from=="social"||from=="normal"){}
//            else findNavController().popBackStack()
//        }
        return binding.root
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
                        Preferences.setStringPreference(requireContext(), IS_LOGIN, "2")
                        Preferences.setCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA, it.data)
                        CommonUtils.hideKeyboard(requireActivity())
                        requireActivity().finish()
                        startActivity(Intent(requireActivity(), HomeActivity::class.java))
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
                etEmail.setText(tempDetails.email)
                etPhoneNumber.setText(tempDetails.mobile)
                etName.isEnabled = false
                etEmail.isEnabled = false
                etPhoneNumber.isEnabled = false
            }
        }
    }

    private fun initViews(){
        touchHideKeyBoard(binding.root, requireActivity())
        binding.ivEditIcon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            imagePickerLauncher.launch(intent)
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
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
                    Glide.with(requireContext()).load(imageUri).into(binding.ivUserImage)
                    val fileImage = uriToFile(imageUri ?: Uri.EMPTY,requireActivity())
                    uploadImage(fileImage)
                }else{
                    Toast.makeText(requireActivity(), "Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /*private fun openCropActivity(imageUri: Uri) {
        val options = UCrop.Options().apply {
            setFreeStyleCropEnabled(true)
        }
        val destinationUri = Uri.fromFile(File(requireActivity().cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        UCrop.of(imageUri, destinationUri)
            .withOptions(options)
            .start(requireContext(), this)
    }*/
    /*@Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data) // Always call super
        Log.i("TAG", "onActivityResult: "+"outside")
        if (resultCode==RESULT_OK){
            when(requestCode){
                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    Log.i("TAG", "onActivityResult: "+resultUri)
//                    binding.ivUserImage.setImageURI(resultUri)
                    Glide.with(requireContext()).load(resultUri).into(binding.ivUserImage)
                    val fileImage = uriToFile(resultUri ?: Uri.EMPTY,requireActivity())
                    uploadImage(fileImage)
                }
            }
        }else {
            Log.w("HomeFragment", " cropping was cancelled or failed with code: $resultCode")
        }

    }*/


    fun initializeS3Client(accessKey: String, secretKey: String): AmazonS3Client {
        val credentials = BasicAWSCredentials(accessKey, secretKey)
        val s3Client = AmazonS3Client(credentials)

        // Increase timeout settings
        val clientConfig = com.amazonaws.ClientConfiguration()
        clientConfig.connectionTimeout = 120000 // 120 sec
        clientConfig.socketTimeout = 120000 // 120 sec
        clientConfig.maxErrorRetry = 5 // Retry in case of network issues

        return AmazonS3Client(credentials, clientConfig)
    }
    fun uploadImageToS3(context: Context, file: File, bucketName: String, objectKey: String, accessKey: String, secretKey: String) {
        // Initialize S3 client
        val s3Client = initializeS3Client( accessKey, secretKey)

        // Initialize TransferUtility
        val transferUtility = TransferUtility.builder()
            .context(context)
            .s3Client(s3Client)
            .build()

        com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler.getInstance(context)

        // Start the upload
        val uploadObserver = transferUtility.upload(bucketName, objectKey, file)

        // Listen to upload events
        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (state == TransferState.COMPLETED) {
                    // Upload completed successfully
//                    val imageUrl = "https://$bucketName.s3.amazonaws.com/$objectKey"
//                    val urlCdn = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.payload?.AWS_CDN_URL
                    val urlCdn = "getCdnUrl"
                    val slash = "/"
                    val imageUrl = "$urlCdn$slash$objectKey"
                    newImageUrl = imageUrl
                    println("Image URL: $imageUrl")

                } else if (state == TransferState.FAILED) {
                    // Handle failure
                    println("Upload failed")
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                if (bytesTotal > 0) {
                    val percentDone = (bytesCurrent.toFloat() / bytesTotal * 100).toInt()
                    Log.d("UploadProgress", "Uploaded: $percentDone%")
                }
            }
            override fun onError(id: Int, ex: Exception) {
                // Handle error
                ex.printStackTrace()
            }
        })
    }
    private fun uploadImage(imageFile: File) {
//        val s3Data = Preferences.getCustomModelPreference<LoginResponse>(requireContext(), LOGIN_DATA)?.payload?.S3Details
//        val bucketName = s3Data?.BUCKET_NAME
//        val objectKey = "${System.currentTimeMillis()}"
//        uploadImageToS3(requireContext(), imageFile, bucketName ?: "", objectKey, s3Data?.ACCESS_KEY ?: "", s3Data?.SECRET_KEY ?: "")
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


        if(imageUrl==""){
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
            val model = UpdateProfileRequest(
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
            )
            Log.e("TAG", "checkValidation: $model")
            viewModel.hitUpdateProfile(model, "Bearer $accessToken")
            //updateApi()
        }
    }
}





