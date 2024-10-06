package com.example.parentallock.service

import android.annotation.SuppressLint
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.app.ServiceCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.parentallock.R
import com.example.parentallock.data.data.NotificationRepository
import com.example.parentallock.data.model.MessageEvent
import com.example.parentallock.data.model.ServiceEvent
import com.example.parentallock.domian.usecases.GetAllDeviceAppsUseCase
import com.example.parentallock.utils.SessionManager
import com.example.parentallock.utils.slidetolock.SlideToActView
import com.example.parentallock.utils.slidetolock.SlideToActView.OnSlideCompleteListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject


@AndroidEntryPoint
class LockScreenService : Service() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var getAllDeviceAppsUseCase: GetAllDeviceAppsUseCase

    @Inject
    lateinit var sessionManager: SessionManager

    private var mediaPlayer: MediaPlayer? = null
    private val serviceScope = CoroutineScope(IO)
    private lateinit var windowManager: WindowManager
    private var lottieAnimationView: LottieAnimationView? = null
    private var backgroundView: View? = null
    private var slideView: SlideToActView? = null
    private var timer: Timer? = null
    private var lastApp: String? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateEvent(event: ServiceEvent) {
        if (event.isSerViceRunning) {
            /**
             * For Instant Lock Just Show WindowManger
             **/
            displayMyLock()
        } else {
            EventBus.getDefault().post(MessageEvent(true))
            stopTimer()
            startTimer()
        }
    }

    override fun onCreate() {
        super.onCreate()
        val notificationBuilder = notificationRepository.buildNotification()
        val notification = notificationBuilder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= 34) {
                ServiceCompat.startForeground(
                    this@LockScreenService, 1, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            } else {
                startForeground(1, notification)
            }
        } else {
            startForeground(1, notification)
        }
        EventBus.getDefault().register(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sessionManager.setServiceRunning(true)
        /**
         * Here True Means Instant Lock  And Else Custom Lock
         **/
        val lockType = sessionManager.getSelectedLockType()
        EventBus.getDefault().post(MessageEvent(true))
        if (lockType == true) {
            displayMyLock()
        } else {
            startTimer()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        stopServiceAndRemoveView()
        stopTimer()
        EventBus.getDefault().unregister(this)
        sessionManager.setServiceRunning(false)
    }

    override fun onBind(intent: Intent): IBinder? = null

    @SuppressLint("ClickableViewAccessibility", "InflateParams")
    private fun setupWindowManagerDelayed(selectedResourceId: Int, selectedSoundResourceId: Int) {
        mediaPlayer = MediaPlayer.create(this, selectedSoundResourceId).apply {
            setOnPreparedListener { start() }
            isLooping = true
        }
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val backgroundParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            @Suppress("DEPRECATION") WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT
        )
        backgroundView = View(this).apply {
            setBackgroundColor(Color.parseColor("#1A2980"))
        }
        windowManager.addView(backgroundView, backgroundParams)
        val lottieParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            @Suppress("DEPRECATION") WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT
        )
        lottieAnimationView = LottieAnimationView(this).apply {
            setAnimation(selectedResourceId)
            repeatCount = LottieDrawable.INFINITE
            playAnimation()
        }
        windowManager.addView(lottieAnimationView, lottieParams)
        val slideViewParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            x = 0
            y = 100
        }

        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        slideView = layoutInflater.inflate(R.layout.slide_to_act_view, null) as SlideToActView
        windowManager.addView(slideView, slideViewParams)
        slideView?.onSlideCompleteListener = object : OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                stopServiceAndRemoveView()
            }
        }

    }

    private fun stopServiceAndRemoveView() {
        try {
            if (backgroundView?.isAttachedToWindow == true) {
                windowManager.removeView(backgroundView)
            }
            if (slideView?.isAttachedToWindow == true) {
                windowManager.removeView(slideView)
            }
            if (lottieAnimationView?.isAttachedToWindow == true) {
                windowManager.removeView(lottieAnimationView)
            }
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            backgroundView = null
            lottieAnimationView = null
            slideView = null
            EventBus.getDefault().post(MessageEvent(false))
        } catch (e: Exception) {
            Log.e("LockScreenService", "View is not attached to window manager: ${e.message}")
        }
    }

    private fun getCurrentApp() {
        val usageStatsManager = getSystemService(UsageStatsManager::class.java)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, -1)
        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, calendar.timeInMillis, System.currentTimeMillis()
        )
        if (usageStatsList.isNotEmpty()) {
            val sortedList = usageStatsList.sortedByDescending { it.lastTimeUsed }
            val currentApp = sortedList.firstOrNull()?.packageName
            if (currentApp != lastApp) {
                lastApp = currentApp
                serviceScope.launch {
                    withContext(IO) {
                        val ifCurrentAppLocked =
                            getAllDeviceAppsUseCase.isAppCustomLocked(currentApp ?: "")
                        Log.d("Current App", "Package ifExists:$ifCurrentAppLocked---> $currentApp")
                        if (ifCurrentAppLocked == true) {
                            withContext(Main) {
                                Log.d("Current App", "Package:Inside Condition $currentApp")
                                displayMyLock()
                            }
                        }
                    }
                }
            } else {
                Log.d("Current App", "Same app detected: $currentApp, no action taken.")
            }
            Log.d("Current App", "Package: $currentApp")
        }
    }

    private fun startTimer() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                serviceScope.launch {
                    getCurrentApp()
                }
            }
        }, 0, 5000)
    }

    private fun stopTimer() {
        timer?.cancel()
    }

    private fun displayMyLock() {
        val selectedResourceId = sessionManager.getSelectedLottieResource()
        val selectedSoundResourceId = sessionManager.getSelectedSoundResource()
        if (selectedResourceId != null) {
            if (selectedSoundResourceId != null) {
                setupWindowManagerDelayed(selectedResourceId, selectedSoundResourceId)
            }
        }
    }
}