package com.app.edtech.ui.bottom_sheet

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.edtech.R
import com.app.edtech.model.menu_item.MenuItemModel
import com.app.edtech.ui.adapter.MenuAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class MenuBottomSheet(
    private val context: Context,
    private val menuList: List<MenuItemModel>,
    private val onItemClick: (MenuItemModel) -> Unit
) {

    private var dialog: BottomSheetDialog? = null

    fun show() {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.layout_menu_bottom_sheet, null)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerMenu)
        val txtName = view.findViewById<TextView>(R.id.txtName)
        val txtViewProfile = view.findViewById<TextView>(R.id.txtViewProfile)

        txtName.text = "Ujjwal Gupta" // dynamic
        txtViewProfile.setOnClickListener {
            Toast.makeText(context, "View Profile clicked", Toast.LENGTH_SHORT).show()
        }

        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = MenuAdapter(menuList) {
            onItemClick(it)
            dialog?.dismiss()
        }

        dialog = BottomSheetDialog(context)
        dialog?.setContentView(view)
        dialog?.show()
    }
}