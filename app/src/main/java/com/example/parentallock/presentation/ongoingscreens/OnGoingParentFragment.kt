package com.example.parentallock.presentation.ongoingscreens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.parentallock.R
import com.example.parentallock.data.model.OnGoingScreenModel
import com.example.parentallock.databinding.FragmentOnGoingParentBinding
import com.example.parentallock.utils.AppConstants.ON_GOING_DATA_MODEL
import com.example.parentallock.utils.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
class OnGoingParentFragment : Fragment(R.layout.fragment_on_going_parent) {
    private val binding by viewBinding(FragmentOnGoingParentBinding::bind)

    /**
     * Weak Reference to the Pager Adapter
     **/
    private var pagerAdapterRef: WeakReference<OnGoingPagerAdapter>? = null
    private val onGoingPagesList: Array<OnGoingScreenModel> by lazy {
        arrayOf(
            OnGoingScreenModel(
                labelOne = getString(R.string.welcome_to_child_app_lock),
                labelSecond = getString(R.string.smart_parental_controls_give) + getString(R.string.you_peace_of_mind),
                imageRes = R.drawable.ongoing_first_image
            ), OnGoingScreenModel(
                labelOne = getString(R.string.get_device_from_child) + getString(R.string.without_fight),
                labelSecond = "",
                imageRes = R.drawable.ongoing_second_image
            ), OnGoingScreenModel(
                labelOne = getString(R.string.set_timer_select_allowed) + getString(R.string.apps_get_phone_back),
                labelSecond = "",
                imageRes = R.drawable.ongoing_third_image
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPagerAdapter()
        clickListeners()
    }

    private fun setupPagerAdapter() {
        val adapter = OnGoingPagerAdapter(childFragmentManager, lifecycle)
        pagerAdapterRef = WeakReference(adapter)
        binding?.apply {
            viewPager.adapter = adapter
            viewPager.isUserInputEnabled = true
            TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
            tabLayout.touchables.forEach { it.isClickable = false }
            viewPager.setupButtonWithPageChange(
                btnContinue,
                nextText = resources.getString(R.string.next),
                finalText = resources.getString(R.string.get_started)
            )
        }
    }

    private fun clickListeners() {
        binding?.apply {
            btnContinue.setupNextButton(
                viewPager, pagerAdapterRef?.get() ?: return, findNavController()
            )
        }
    }

    inner class OnGoingPagerAdapter(
        fragmentManager: FragmentManager, lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int = onGoingPagesList.size

        override fun createFragment(position: Int): Fragment {
            return OnGoingFragmentItem().apply {
                arguments = Bundle().apply {
                    putParcelable(ON_GOING_DATA_MODEL, onGoingPagesList[position])
                }
            }
        }
    }
}
