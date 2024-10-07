package com.example.parentallock.presentation.ongoingscreens

import android.view.View
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.navigation.NavController
import androidx.viewpager2.widget.ViewPager2
import com.example.parentallock.R
import com.example.parentallock.utils.CustomizeCheckBox
import com.example.parentallock.utils.all_extension.showToast
import com.google.android.material.button.MaterialButton

fun MaterialButton.setupNextButton(
    viewPager: ViewPager2,
    pagerAdapter: OnGoingParentFragment.OnGoingPagerAdapter,
    navController: NavController
) {
    setOnClickListener {
        val currentItem = viewPager.currentItem
        if (currentItem < pagerAdapter.itemCount - 1) {
            viewPager.currentItem = currentItem + 1
            text = resources.getString(R.string.next)
        }
        if (currentItem == pagerAdapter.itemCount - 2) {
            text = context.getString(R.string.get_started)
        } else if (currentItem == pagerAdapter.itemCount - 1) {
            if (navController.currentDestination?.id == R.id.navigation_onGoing_parent) {
                navController.navigate(R.id.action_navigation_onGoing_parent_to_navigation_home)
            }
        }
    }
}

fun AppCompatCheckBox.setMutuallyExclusiveWith(otherCheckBox: CheckBox) {
    this.setOnClickListener {
        if (this.isChecked) {
            otherCheckBox.isChecked = false
        }
    }
}

fun View.safeNavigateOnClick(
    navController: NavController,
    currentDestId: Int,
    actionId: Int,
    actionIdForCustomLock: Int,
    btnCustomLock: CustomizeCheckBox,
    btnInstantLock: CustomizeCheckBox
) {
    setOnClickListener {
        if (btnCustomLock.isChecked) {
            if (navController.currentDestination?.id == currentDestId) {
                navController.navigate(actionIdForCustomLock)
            }
        } else if (btnInstantLock.isChecked) {
            if (navController.currentDestination?.id == currentDestId) {
                navController.navigate(actionId)
            }
        } else {
            this.context?.showToast(context.getString(R.string.please_select_a_lock_option))
        }
    }
}

fun ViewPager2.setupButtonWithPageChange(
    btn: MaterialButton,
    nextText: String = resources.getString(R.string.next),
    finalText: String = resources.getString(R.string.get_started),
) {
    this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            when (position) {
                0, 1 -> {
                    btn.text = nextText
                }

                2 -> {
                    btn.text = finalText
                }
            }
        }
    })
}
