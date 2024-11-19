package com.example.parentallock.presentation.ongoingscreens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.parental.control.displaytime.kids.safety.R
import com.parental.control.displaytime.kids.safety.data.model.OnGoingScreenModel
import com.parental.control.displaytime.kids.safety.databinding.FragmentOnGoingFragmentItemBinding
import com.example.parentallock.utils.AppConstants.ON_GOING_DATA_MODEL
import com.example.parentallock.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import parcelable

@AndroidEntryPoint
class OnGoingFragmentItem : Fragment(R.layout.fragment_on_going_fragment_item) {

    private val binding by viewBinding(FragmentOnGoingFragmentItemBinding::bind)
    private var onGoingScreenModel: OnGoingScreenModel? = null
    private val viewModel: OnGoingFragmentItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            onGoingScreenModel = arguments?.parcelable(ON_GOING_DATA_MODEL)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.onGoingModel = onGoingScreenModel
    }

}