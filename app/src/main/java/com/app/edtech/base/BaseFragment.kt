package com.app.edtech.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<DB : ViewDataBinding> : Fragment() {
    private var _binding: DB? = null
    protected val binding get() = _binding!!
    open fun getLayoutId(): Int {
        return 0 // Default: you should override this
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isViewInitialized) {
            isViewInitialized = true
            initView(savedInstanceState)
        }else{
            restoreView()
        }
    }
    open fun restoreView() {

    }
    var isViewInitialized = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        // ✅ Remove initView from here
        return binding.root
    }
    abstract fun initView(savedInstanceState: Bundle?)

    override fun onResume() {
        super.onResume()

        // Get the fragment background color (or the color of a parent view)
        val backgroundColor = (view?.background as? ColorDrawable)?.color ?: Color.WHITE

        // Update status bar icon color
        (activity as? BaseActivity<*>)?.isColorLight(backgroundColor)
            ?.let { (activity as? BaseActivity<*>)?.updateStatusBarIcons(it) }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
