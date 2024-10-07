package com.example.parentallock.presentation.customlock

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.parentallock.R
import com.example.parentallock.data.model.AllAppsEntity
import com.example.parentallock.data.model.MessageEvent
import com.example.parentallock.data.model.ServiceEvent
import com.example.parentallock.databinding.FragmentCustomAppLockBinding
import com.example.parentallock.service.LockScreenService
import com.example.parentallock.utils.SessionManager
import com.example.parentallock.utils.all_extension.gone
import com.example.parentallock.utils.all_extension.showToast
import com.example.parentallock.utils.all_extension.visible
import com.example.parentallock.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import setSafeOnClickListener
import toDeviceAppEntity
import javax.inject.Inject

@AndroidEntryPoint
class CustomAppLockFragment : Fragment(R.layout.fragment_custom_app_lock) {
    private val binding by viewBinding(FragmentCustomAppLockBinding::bind)
    private val viewModel by viewModels<CustomAppLockViewModel>()
    private var myDeviceAppsAdapter: MyDeviceAppsAdapter? = null
    private var lastObservedSysApps: List<AllAppsEntity?>? = null

    @Inject
    lateinit var sessionManager: SessionManager

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
        ContextCompat.startForegroundService(
            context ?: return, Intent(context ?: return, LockScreenService::class.java)
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateEvent(event: MessageEvent) {
        if (event.isServiceRunning) {
            binding?.btnActivate?.apply {
                isEnabled = false
                isClickable = false
                text = getString(R.string.custom_lock_activated)
            }
        } else {
            binding?.btnActivate?.apply {
                isEnabled = true
                isClickable = true
                text = getString(R.string.activate_now)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myDeviceAppsAdapter = MyDeviceAppsAdapter(callbackSelection = { deviceApp ->
            if (deviceApp.isSelected) {
                val deviceAppEntity = deviceApp.toDeviceAppEntity()
                viewModel.invokeInsertCustomLockedApp(
                    deviceAppEntity = deviceAppEntity
                )
            } else {
                val deviceAppEntity = deviceApp.toDeviceAppEntity()
                viewModel.invokeDeleteCustomLockedApp(deviceAppEntity)
            }
        }, dataList = arrayListOf())
        viewModel.invokeAllUnInstallAppsUseCase()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            if (sessionManager.isServiceRunning() == true) {
                lifecycleScope.launch {
                    withContext(IO) {
                        if (viewModel.isTableEmpty() != true) {
                            withContext(Main) {
                                btnActivate.apply {
                                    isEnabled = false
                                    isClickable = false
                                    text = getString(R.string.custom_lock_activated)
                                }
                            }
                        }
                    }
                }
            }
            headerLayout.ivSettings.gone()
            headerLayout.ivNotification.gone()
            headerLayout.btnBack.visible()
            headerLayout.tvHeaderTitle.text = getString(R.string.apply_lock_on_custom_apps)
        }
        setupVersionsRecyclerView()
        observeVersionsData()
        activateClickListener()
    }

    private fun observeVersionsData() {
        observeVersionsDataState()
        observeVersions()
    }

    private fun setupVersionsRecyclerView() {
        binding?.rvUnInstallAdapter?.apply {
            adapter = myDeviceAppsAdapter
            hasFixedSize()
        }
    }

    private fun observeVersionsDataState() {
        viewModel.mState.flowWithLifecycle(
            viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED
        ).onEach { state ->
            handleState(state)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeVersions() {
        viewModel.allUnInstallApps.flowWithLifecycle(
            viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED
        ).onEach { entityList ->
            if (entityList != lastObservedSysApps) {
                lastObservedSysApps = entityList
                val dbAppsList = viewModel.invokeAllCustomLockApp()
                val updatedAppsList = entityList.mapNotNull { versionEntity ->
                    versionEntity?.let {
                        val matchedApp =
                            dbAppsList?.firstOrNull { dbApp -> dbApp.pName == versionEntity.pName }
                        versionEntity.copy(isSelected = matchedApp != null)
                    }
                }
                handleApps(updatedAppsList)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleApps(versionsEntity: List<AllAppsEntity?>) {
        binding?.rvUnInstallAdapter?.adapter?.let {
            if (it is MyDeviceAppsAdapter) {
                versionsEntity.let { it1 -> it.updateVersionsList(it1 as ArrayList<AllAppsEntity?>) }
            }
        }
    }

    private fun handleState(state: AllUnInstallAppFragmentState) {
        when (state) {
            is AllUnInstallAppFragmentState.IsLoading -> handleLoading(state.isLoading)
            is AllUnInstallAppFragmentState.ShowToast -> context?.showToast(state.message)
            is AllUnInstallAppFragmentState.Init -> Unit
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            binding?.loadingProgressBarUnInstall?.visible()
        } else {
            binding?.loadingProgressBarUnInstall?.gone()
        }
    }

    private fun activateClickListener() {
        binding?.btnActivate?.setSafeOnClickListener {
            sessionManager.setSelectedLockType(false)
            if (sessionManager.isServiceRunning() == true) {
                EventBus.getDefault().post(ServiceEvent(false))
                return@setSafeOnClickListener
            }
            checkAndRequestOverlayPermission()
        }
        binding?.headerLayout?.btnBack?.setSafeOnClickListener {
            findNavController().navigateUp()
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
        EventBus.getDefault().register(this@CustomAppLockFragment)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this@CustomAppLockFragment)
        super.onStop()
    }
}