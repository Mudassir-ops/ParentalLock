package com.example.parentallock.presentation.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.parentallock.R
import com.example.parentallock.databinding.FragmentHomeBinding
import com.example.parentallock.presentation.ongoingscreens.safeNavigateOnClick
import com.example.parentallock.presentation.ongoingscreens.setMutuallyExclusiveWith
import com.example.parentallock.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding by viewBinding(FragmentHomeBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.clickListeners()
    }

    private fun FragmentHomeBinding.clickListeners() {
        btnCustomLock.setMutuallyExclusiveWith(btnInstantLock)
        btnInstantLock.setMutuallyExclusiveWith(btnCustomLock)
        binding?.btnNext?.safeNavigateOnClick(
            navController = findNavController(),
            currentDestId = R.id.navigation_home,
            actionId = R.id.action_navigation_home_to_navigation_parent_app_lock
        )
    }
}