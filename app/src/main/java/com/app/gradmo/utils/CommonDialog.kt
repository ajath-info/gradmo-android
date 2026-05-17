package com.app.gradmo.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.app.gradmo.databinding.DialogCommonBinding

class CommonDialog(
    context: Context,
    private val title: String,
    private val imageRes: Int? = null,
    private val positiveText: String = "Yes",
    private val negativeText: String = "No",
    private val onPositiveClick: (() -> Unit)? = null,
    private val onNegativeClick: (() -> Unit)? = null
) : Dialog(context) {

    private lateinit var binding: DialogCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setupUI()
        setupClicks()
    }
    override fun onStart() {
        super.onStart()

        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupUI() {
        binding.tvTitle.text = title

        binding.btnPositive.text = positiveText
        binding.btnNegative.text = negativeText

        if (imageRes != null) {
            binding.ivImage.setImageResource(imageRes)
            binding.ivImage.visibility = View.VISIBLE
        } else {
            binding.ivImage.visibility = View.GONE
        }
    }

    private fun setupClicks() {
        binding.ivClose.setOnClickListener { dismiss() }

        binding.btnNegative.setOnClickListener {
            dismiss()
            onNegativeClick?.invoke()
        }

        binding.btnPositive.setOnClickListener {
            dismiss()
            onPositiveClick?.invoke()
        }
    }
}