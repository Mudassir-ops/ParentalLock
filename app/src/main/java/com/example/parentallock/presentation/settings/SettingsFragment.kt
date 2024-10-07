package com.example.parentallock.presentation.settings

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.parentallock.R
import com.example.parentallock.databinding.FragmentSettingsBinding
import com.example.parentallock.utils.all_extension.gone
import com.example.parentallock.utils.all_extension.visible
import com.example.parentallock.utils.viewBinding
import setSafeOnClickListener

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activateHeaderViews()
        activateClickListener()
    }

    private fun activateClickListener() {
        binding?.headerLayout?.btnBack?.setSafeOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun activateHeaderViews() {
        binding?.apply {
            headerLayout.ivSettings.gone()
            headerLayout.ivNotification.gone()
            headerLayout.btnBack.visible()
            headerLayout.tvHeaderTitle.text = getString(R.string.settings)
        }
    }
}