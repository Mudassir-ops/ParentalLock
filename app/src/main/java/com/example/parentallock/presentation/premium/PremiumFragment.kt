package com.example.parentallock.presentation.premium

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.parentallock.R
import com.example.parentallock.databinding.FragmentHomeBinding
import com.example.parentallock.databinding.FragmentPremiumBinding
import com.example.parentallock.presentation.ongoingscreens.setMutuallyExclusiveWith
import com.example.parentallock.utils.viewBinding

class PremiumFragment : Fragment(R.layout.fragment_premium) {
    private val binding by viewBinding(FragmentPremiumBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun FragmentHomeBinding.clickListeners() {
        btnCustomLock.setMutuallyExclusiveWith(btnInstantLock)
        btnInstantLock.setMutuallyExclusiveWith(btnCustomLock)

    }
}