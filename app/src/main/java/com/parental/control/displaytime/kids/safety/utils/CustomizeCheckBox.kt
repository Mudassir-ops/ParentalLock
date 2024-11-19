package com.parental.control.displaytime.kids.safety.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import com.parental.control.displaytime.kids.safety.R

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