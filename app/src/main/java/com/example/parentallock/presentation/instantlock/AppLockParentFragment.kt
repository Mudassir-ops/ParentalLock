package com.example.parentallock.presentation.instantlock

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.parental.control.displaytime.kids.safety.R
import com.parental.control.displaytime.kids.safety.data.model.InstantLockAdapterItem
import com.parental.control.displaytime.kids.safety.data.model.MessageEvent
import com.parental.control.displaytime.kids.safety.data.model.ServiceEvent
import com.parental.control.displaytime.kids.safety.databinding.FragmentAppLockParentBinding
import com.example.parentallock.service.LockScreenService
import com.example.parentallock.utils.SessionManager
import com.example.parentallock.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import setSafeOnClickListener
import javax.inject.Inject

@AndroidEntryPoint
class AppLockParentFragment : Fragment(R.layout.fragment_app_lock_parent) {
    private val binding by viewBinding(FragmentAppLockParentBinding::bind)

    @Inject
    lateinit var sessionManager: SessionManager
    private val instantAppLocksList: List<InstantLockAdapterItem> by lazy {
        listOf(
            InstantLockAdapterItem.NORMAL(
                lottieRawRes = R.raw.instant_lock_danger_animation,
                isSelected = true,
                soundRawRes = R.raw.cactus
            ), InstantLockAdapterItem.NORMAL(
                lottieRawRes = R.raw.instant_lock_warning_animation,
                isSelected = false,
                soundRawRes = R.raw.danger_sound_here
            ), InstantLockAdapterItem.CALL(
                lottieRawRes = R.raw.instant_lock_call_animation,
                labelOne = getString(R.string.unknown_calling),
                labelTwo = getString(R.string.unknown_calling_number),
                dpSrc = R.drawable.caller_dp,
                isSelected = false,
                soundRawRes = R.raw.old_phone
            ), InstantLockAdapterItem.NORMAL(
                lottieRawRes = R.raw.instant_lock_siren_animation,
                isSelected = false,
                soundRawRes = R.raw.police_siren
            ), InstantLockAdapterItem.NORMAL(
                lottieRawRes = R.raw.instant_lock_anger_animation,
                isSelected = false,
                soundRawRes = R.raw.angry_birds_outro
            ), InstantLockAdapterItem.NORMAL(
                lottieRawRes = R.raw.instant_lock_moody_animation,
                isSelected = false,
                soundRawRes = R.raw.demon_ritual
            )
        )
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            startService()
        } else {
            if (Settings.canDrawOverlays(context ?: return@registerForActivityResult)) {
                startService()
            }
        }
    }

    private fun startService() {
        if (sessionManager.isServiceRunning() == true) {
            EventBus.getDefault().post(ServiceEvent(true))
            return
        }
        ContextCompat.startForegroundService(
            context ?: return, Intent(context ?: return, LockScreenService::class.java)
        )
    }

    private val instantAppLockItemsAdapter: InstantAppLockItemsAdapter by lazy {
        InstantAppLockItemsAdapter(callback = { lottieRaw, soundRaw, selectedPos ->
            sessionManager.setSelectedLottieResource(lottieRaw)
            sessionManager.setSelectedSoundResource(soundRaw)
            sessionManager.setSelectedResourcePos(selectedPos)
        }, instantAppEntityList = instantAppLocksList)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateEvent(event: MessageEvent) {
        if (event.isServiceRunning) {
            binding?.btnActivate?.apply {
                isEnabled = false
                isClickable = false
                text = getString(R.string.instant_lock_activated)
            }
        } else {
            binding?.btnActivate?.apply {
                isEnabled = true
                isClickable = true
                text = getString(R.string.activate_now)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        activateClickListener()
        sessionManager.getSelectedResourcePos()?.let {
            Log.e("SelectedPostion", "onViewCreated: $it")
            instantAppLockItemsAdapter.selectItem(it)
        }
    }

    private fun setupRecyclerView() {
        binding?.rvInstantLock?.run {
            adapter = instantAppLockItemsAdapter
            setHasFixedSize(true)
        }
    }

    private fun activateClickListener() {
        binding?.btnActivate?.setSafeOnClickListener {
            sessionManager.setSelectedLockType(true)
            checkAndRequestOverlayPermission()
        }
        binding?.customLockLayout?.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.navigation_parent_app_lock) {
                findNavController().navigate(R.id.action_navigation_parent_app_lock_to_navigation_custom_app_lock)
            }
        }
        binding?.headerLayout?.ivNotification?.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.navigation_parent_app_lock) {
                findNavController().navigate(R.id.action_navigation_parent_app_lock_to_navigation_premium_screen)
            }
        }
        binding?.headerLayout?.ivSettings?.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.navigation_parent_app_lock) {
                findNavController().navigate(R.id.action_navigation_parent_app_lock_to_navigation_settings_screen)
            }
        }
    }

    private fun checkAndRequestOverlayPermission() {
        if (!Settings.canDrawOverlays(context ?: return)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context?.packageName}")
            )
            requestPermissionLauncher.launch(intent)
        } else {
            startService()
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this@AppLockParentFragment)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this@AppLockParentFragment)
        super.onStop()
    }

}