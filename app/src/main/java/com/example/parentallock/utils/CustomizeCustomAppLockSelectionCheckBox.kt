package com.example.parentallock.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import com.example.parentallock.R

class CustomizeCustomAppLockSelectionCheckBox(context: Context, attrs: AttributeSet?) :
    AppCompatCheckBox(context, attrs) {
    init {
        setupBackground(isChecked)
    }

    private fun setupBackground(isChecked: Boolean) {
        background = if (isChecked) {
            ContextCompat.getDrawable(context, R.drawable.check_box)
        } else {
            ContextCompat.getDrawable(context, R.drawable.uncheck_box)
        }
    }

    override fun setChecked(isChecked: Boolean) {
        super.setChecked(isChecked)
        setupBackground(isChecked)
    }
}
