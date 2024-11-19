package com.example.parentallock.utils.slidetolock

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat

internal object SlideToActIconUtil {
    @SuppressLint("UseCompatLoadingForDrawables")
    internal fun loadIconCompat(
        context: Context,
        value: Int,
    ): Drawable {
        return AnimatedVectorDrawableCompat.create(context, value)
            ?: ContextCompat.getDrawable(context, value)!!
    }

    internal fun tintIconCompat(
        icon: Drawable,
        color: Int,
    ) {
        DrawableCompat.setTint(icon, color)
    }

    internal fun startIconAnimation(icon: Drawable) {
        when (icon) {
            is AnimatedVectorDrawable -> icon.start()
            is AnimatedVectorDrawableCompat -> icon.start()
            else -> {
                // Do nothing as the icon can't be animated
            }
        }
    }

    internal fun stopIconAnimation(icon: Drawable) {
        when (icon) {
            is AnimatedVectorDrawable -> icon.stop()
            is AnimatedVectorDrawableCompat -> icon.stop()
            else -> {
                // Do nothing as the icon can't be animated
            }
        }
    }

    fun createIconAnimator(
        view: SlideToActView,
        icon: Drawable,
        listener: ValueAnimator.AnimatorUpdateListener,
    ): ValueAnimator {
        if (fallbackToFadeAnimation(icon)) {
            // Fallback not using AVD.
            val tickAnimator = ValueAnimator.ofInt(0, 255)
            tickAnimator.addUpdateListener(listener)
            tickAnimator.addUpdateListener {
                icon.alpha = it.animatedValue as Int
                view.invalidate()
            }
            return tickAnimator
        } else {
            // Used AVD Animation.
            val tickAnimator = ValueAnimator.ofInt(0)
            var startedOnce = false
            tickAnimator.addUpdateListener(listener)
            tickAnimator.addUpdateListener {
                if (!startedOnce) {
                    startIconAnimation(icon)
                    view.invalidate()
                    startedOnce = true
                }
            }
            return tickAnimator
        }
    }

    private fun fallbackToFadeAnimation(icon: Drawable) =
        when {
            // We don't use AVD at all for <= N.
            SDK_INT <= N -> true
            icon !is AnimatedVectorDrawable -> true
            else -> false
        }
}