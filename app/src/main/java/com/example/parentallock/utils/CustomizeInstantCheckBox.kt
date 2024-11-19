package com.example.parentallock.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import com.parental.control.displaytime.kids.safety.R

class CustomizeInstantCheckBox(context: Context, attrs: AttributeSet?) :
    AppCompatCheckBox(context, attrs) {
    init {
        setupBackground(isChecked)
    }

    private fun setupBackground(isChecked: Boolean) {
        background = if (isChecked) {
            ContextCompat.getDrawable(context, R.drawable.checked_shape)
        } else {
            null
        }
    }

    override fun setChecked(isChecked: Boolean) {
        super.setChecked(isChecked)
        setupBackground(isChecked)
    }
}
