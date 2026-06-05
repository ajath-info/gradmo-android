package com.app.gradmo.ui.fragment.teacher

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
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
import com.app.gradmo.databinding.FragmentCreateHomeworkBinding
import com.app.gradmo.model.login.response.LoginResponse
import com.app.gradmo.preferences.LOGIN_DATA
import com.app.gradmo.preferences.Preferences
import com.app.gradmo.ui.view_model.AddLibraryViewModel
import com.app.gradmo.ui.view_model.CreateHomeWorkViewModel
import com.app.gradmo.ui.view_model.LibraryViewModel
import com.app.gradmo.utils.network_utils.ProcessDialog
import com.app.gradmo.utils.network_utils.Status
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.getValue

@AndroidEntryPoint
class AddLibraryFragment : BaseFragment<FragmentAddLibraryBinding>() {

    private val viewModel: AddLibraryViewModel by viewModels()
    private var batchId = ""
    private var selectedPdfFile: File? = null
    private var accessToken = ""

    // File picker launcher
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

        binding.createButton.setOnClickListener {
            if (validateInputs()) {
                viewModel.hitAddLibraryDataApi(
                    token = "Bearer $accessToken",
                    batchId = batchId,
                    subject = binding.subject.text.toString().trim(),
                    title = binding.title.text.toString().trim(),
                    topic = binding.topic.text.toString().trim(),
                    pdfFile = selectedPdfFile!!
                )
            }
        }
    }

    private fun validateInputs(): Boolean {
        val subject = binding.subject.text.toString().trim()
        val title = binding.title.text.toString().trim()
        val topic = binding.topic.text.toString().trim()

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
        if (selectedPdfFile == null) {
            Toast.makeText(requireContext(), "Please select a PDF file", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun handleSelectedFile(uri: Uri) {
        try {
            val fileName = getFileNameFromUri(uri) ?: "selected_file.pdf"
            val tempFile = File(requireContext().cacheDir, fileName)

            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            selectedPdfFile = tempFile
            // Show the selected filename next to Browse button
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

    private fun setObserver() {
        viewModel.addLibraryDataLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    if (it.data?.status == "true") {
                        Toast.makeText(requireContext(), "Book added successfully", Toast.LENGTH_SHORT).show()
                        // Navigate back and signal the list fragment to refresh
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

    override fun getLayoutId(): Int = R.layout.fragment_add_library
    override fun restoreView() {}
}