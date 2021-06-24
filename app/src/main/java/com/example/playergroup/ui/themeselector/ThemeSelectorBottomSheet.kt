package com.example.playergroup.ui.themeselector

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playergroup.R
import com.example.playergroup.databinding.DialogThemeSelectorBinding
import com.example.playergroup.util.ConfigModule
import com.example.playergroup.util.ThemeMode
import com.example.playergroup.util.applyTheme
import com.example.playergroup.util.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ThemeSelectorBottomSheet: BottomSheetDialogFragment() {

    companion object {
        fun newInstance(): ThemeSelectorBottomSheet =
            ThemeSelectorBottomSheet().apply {
                //todo arguments..
            }
    }

    private val configModule by lazy { ConfigModule(requireContext()) }
    private val binding by viewBinding (DialogThemeSelectorBinding::bind)

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        dialog.setOnShowListener { setupBottomSheet(it) }
    }

    private fun setupBottomSheet(dialogInterface: DialogInterface) {
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        bottomSheetDialog.setCanceledOnTouchOutside(true)  // outside Touch Dismiss Disable
        val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet)
        bottomSheet?.let {
            it.setBackgroundColor(Color.TRANSPARENT)
        } ?: run {
            //TODO 예외 처리 ..?
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_theme_selector, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with (binding) {
            when(configModule.configThemeMode) {
                ThemeMode.LIGHT_MODE.value -> radioLightTheme.isChecked = true
                ThemeMode.DARK_MODE.value -> radioDarkTheme.isChecked = true
                else -> radioDefaultTheme.isChecked = true
            }

            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                val themeMode = when (checkedId) {
                    R.id.radioLightTheme -> ThemeMode.LIGHT_MODE
                    R.id.radioDarkTheme -> ThemeMode.DARK_MODE
                    else -> ThemeMode.DEFAULT_MODE
                }
                configModule.configThemeMode = themeMode.value
                applyTheme(themeMode)
                dismiss()
            }
        }
    }
}