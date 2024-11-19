package com.example.parentallock.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import com.parental.control.displaytime.kids.safety.R

class MyCustomCheckBox(context: Context?, attrs: AttributeSet?) : AppCompatCheckBox(
    context!!, attrs
) {
    override fun setChecked(t: Boolean) {
        if (t) {
            setBackgroundResource(R.drawable.check_box)
        } else {
            setBackgroundResource(R.drawable.uncheck_box)
        }
        super.setChecked(t)
    }
}