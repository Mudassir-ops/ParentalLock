package com.example.parentallock.presentation.premium

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.parental.control.displaytime.kids.safety.R
import com.parental.control.displaytime.kids.safety.databinding.FragmentPremiumBinding
import com.example.parentallock.utils.viewBinding

class PremiumFragment : Fragment(R.layout.fragment_premium) {
    private val binding by viewBinding(FragmentPremiumBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}