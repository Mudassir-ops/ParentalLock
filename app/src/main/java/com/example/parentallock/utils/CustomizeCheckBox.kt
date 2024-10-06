package com.example.parentallock.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import com.example.parentallock.R

class CustomizeCheckBox(context: Context, attrs: AttributeSet?) : AppCompatCheckBox(
    context, attrs
) {
    override fun setChecked(t: Boolean) {
        if (t) {
            setBackgroundResource(R.drawable.button_shape_less_rounded)
        } else {
            setBackgroundResource(R.drawable.button_shape_less_rounded_uncehcked)
        }
        super.setChecked(t)
    }
}