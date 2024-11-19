package com.example.parentallock.presentation.customlock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parental.control.displaytime.kids.safety.data.model.AllAppsEntity
import com.parental.control.displaytime.kids.safety.data.model.DeviceAppEntity
import com.example.parentallock.domian.usecases.GetAllDeviceAppsUseCase
import com.example.parentallock.domian.usecases.InsertDeviceAppsUseCase
import com.example.parentallock.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomAppLockViewModel @Inject constructor(
    private val getAllDeviceAppsUseCase: GetAllDeviceAppsUseCase,
    private val insertDeviceAppsUseCase: InsertDeviceAppsUseCase
) : ViewModel() {

    private val ioDispatcher = IO
    private val _allUnInstallApps = MutableStateFlow<List<AllAppsEntity?>>(arrayListOf())
    val allUnInstallApps: StateFlow<List<AllAppsEntity?>> get() = _allUnInstallApps

    private val state =
        MutableStateFlow<AllUnInstallAppFragmentState>(AllUnInstallAppFragmentState.Init)
    val mState: StateFlow<AllUnInstallAppFragmentState> get() = state

    private fun setLoading() {
        state.value = AllUnInstallAppFragmentState.IsLoading(true)
    }

    private fun hideLoading() {
        state.value = AllUnInstallAppFragmentState.IsLoading(false)
    }

    private fun showToast(message: String) {
        state.value = AllUnInstallAppFragmentState.ShowToast(message)
    }

    fun invokeAllUnInstallAppsUseCase() {
        viewModelScope.launch {
            getAllDeviceAppsUseCase.invokeAllDeviceAppsUseCase().onStart {
                setLoading()
            }.catch { exception ->
                hideLoading()
                showToast(exception.message.toString())
                exception.printStackTrace()
            }.collect { result ->
                hideLoading()
                when (result) {
                    is DataState.Success -> {
                        val newData = result.data
                        if (newData != _allUnInstallApps.value) {
                            _allUnInstallApps.value = newData
                        }
                    }

                    else -> {
                        showToast("result.Some thing went Wrong")
                    }
                }
            }
        }
    }

    fun invokeInsertCustomLockedApp(deviceAppEntity: DeviceAppEntity) {
        viewModelScope.launch(ioDispatcher) {
            insertDeviceAppsUseCase.invokeInsertCustomLockedApp(deviceAppEntity)
        }
    }

    fun invokeDeleteCustomLockedApp(deviceAppEntity: DeviceAppEntity) {
        viewModelScope.launch {
            insertDeviceAppsUseCase.delete(deviceAppEntity)
        }
    }

    suspend fun isTableEmpty(): Boolean? {
        return getAllDeviceAppsUseCase.isTableEmpty().firstOrNull()
    }

    suspend fun invokeAllCustomLockApp(): List<DeviceAppEntity>? {
        return getAllDeviceAppsUseCase.invokeAllCustomLockApp().firstOrNull()
    }
}

sealed class AllUnInstallAppFragmentState {
    data object Init : AllUnInstallAppFragmentState()
    data class IsLoading(val isLoading: Boolean) : AllUnInstallAppFragmentState()
    data class ShowToast(val message: String) : AllUnInstallAppFragmentState()
}