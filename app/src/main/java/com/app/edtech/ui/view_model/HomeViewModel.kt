package com.app.edtech.ui.view_model

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

//    private val _categories = MutableStateFlow<List<CategoryItem>>(emptyList())
//    val categories: StateFlow<List<CategoryItem>> = _categories.asStateFlow()

    fun search(query: String) {
        // trigger search logic here
    }
}