package com.example.parentallock.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.parental.control.displaytime.kids.safety.R
import com.parental.control.displaytime.kids.safety.databinding.FragmentSettingsBinding
import com.example.parentallock.utils.all_extension.gone
import com.example.parentallock.utils.all_extension.visible
import com.example.parentallock.utils.viewBinding
import feedBackWithEmail
import moreApps
import privacyPolicyUrl
import setSafeOnClickListener
import shareApp

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
        binding?.let { binding ->
            binding.tvLabelOne.setOnClickListener {
                activity?.moreApps()
            }
            binding.tvLabel2.setOnClickListener {
                activity?.feedBackWithEmail(
                    title = "Feedback",
                    message = "Any FeedBack",
                    emailId = "cisco7865@gmail.com"
                )
            }
            binding.tvLabel3.setOnClickListener {
                activity?.shareApp()
            }
            binding.tvLabel5.setOnClickListener {
                activity?.privacyPolicyUrl()
            }
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