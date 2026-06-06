package com.app.gradmo.ui.fragment.teacher

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.app.gradmo.R
import com.app.gradmo.base.BaseFragment
import com.app.gradmo.databinding.FragmentAddLibraryBinding
import com.app.gradmo.databinding.FragmentAddVideoLectureBinding
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.view_model.AddLibraryViewModel
import com.app.gradmo.ui.view_model.AddVideoLectureViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.getValue

@AndroidEntryPoint
class AddVideoLectureFragment : BaseFragment<FragmentAddVideoLectureBinding>() {

    private val viewModel: AddVideoLectureViewModel by viewModels()
    private var batchId = ""
    private var accessToken = ""

    // Change the variable name for clarity
    private var selectedVideoFile: File? = null

    // Update file picker to accept video files
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handleSelectedFile(it) }
    }

    override fun initView(savedInstanceState: Bundle?) {
        batchId = arguments?.getString("batch_id", "") ?: ""
        accessToken = Preferences.getCustomModelPreference<LoginResponse>(
            requireContext(), LOGIN_DATA
        )?.data?.accessToken ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        clickEvent()
    }

    private fun clickEvent() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.browseButton.setOnClickListener {
            filePickerLauncher.launch("application/pdf")
        }

        // In clickEvent(), update browseButton to launch video picker
        binding.browseButton.setOnClickListener {
            filePickerLauncher.launch("video/*")
        }

// In clickEvent(), update uploadButton click
        binding.uploadButton.setOnClickListener {
            if (validateInputs()) {
                viewModel.hitAddVideoLectureApi(
                    token = "Bearer $accessToken",
                    batchId = batchId,
                    subject = binding.subject.text.toString().trim(),
                    title = binding.title.text.toString().trim(),
                    topic = binding.topic.text.toString().trim(),
                    description = binding.description.text.toString().trim(),
                    previewType = "url",
                    videoFile = selectedVideoFile!!
                )
            }
        }
    }

    // Update validateInputs() — replace selectedPdfFile with selectedVideoFile
// and add description validation
    private fun validateInputs(): Boolean {
        val subject = binding.subject.text.toString().trim()
        val title = binding.title.text.toString().trim()
        val topic = binding.topic.text.toString().trim()
        val description = binding.description.text.toString().trim()

        if (subject.isEmpty()) {
            binding.subject.error = "Subject is required"
            binding.subject.requestFocus()
            return false
        }
        if (title.isEmpty()) {
            binding.title.error = "Title is required"
            binding.title.requestFocus()
            return false
        }
        if (topic.isEmpty()) {
            binding.topic.error = "Topic is required"
            binding.topic.requestFocus()
            return false
        }
        if (description.isEmpty()) {
            binding.description.error = "Description is required"
            binding.description.requestFocus()
            return false
        }
        if (selectedVideoFile == null) {
            Toast.makeText(requireContext(), "Please select a video file", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // Update handleSelectedFile to use video MIME type
    private fun handleSelectedFile(uri: Uri) {
        try {
            val fileName = getFileNameFromUri(uri) ?: "selected_video.mp4"
            val tempFile = File(requireContext().cacheDir, fileName)

            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            selectedVideoFile = tempFile
            Toast.makeText(requireContext(), "File selected: $fileName", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to read file", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var name: String? = null
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) name = cursor.getString(index)
            }
        }
        return name
    }

    // Update observer to use new LiveData method
    private fun setObserver() {
        viewModel.addVideoLectureLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    if (it.data?.status == "true") {
                        Toast.makeText(requireContext(), "Video lecture added successfully", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "${it.data?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                Status.LOADING -> {
                    ProcessDialog.showDialog(requireContext(), true)
                }
                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                    Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun getLayoutId(): Int = R.layout.fragment_add_video_lecture
    override fun restoreView() {}
}