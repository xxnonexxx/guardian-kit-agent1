package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BlockedAppEntity
import com.example.data.CapturedChatEntity
import com.example.data.DeviceLogEntity
import com.example.data.GuardianRepository
import com.example.data.MediaCaptureEntity
import com.example.data.RealtimeDeviceStatus
import com.example.service.WallpaperHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppViewRole {
    ROLE_SELECTION,
    PARENT_DASHBOARD,
    CHILD_AGENT
}

class GuardianViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = GuardianRepository(db)

    private val _currentRole = MutableStateFlow(AppViewRole.ROLE_SELECTION)
    val currentRole: StateFlow<AppViewRole> = _currentRole.asStateFlow()

    val deviceStatus: StateFlow<RealtimeDeviceStatus> = repository.deviceStatus

    val blockedApps: StateFlow<List<BlockedAppEntity>> = repository.blockedApps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val capturedChats: StateFlow<List<CapturedChatEntity>> = repository.capturedChats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deviceLogs: StateFlow<List<DeviceLogEntity>> = repository.deviceLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mediaCaptures: StateFlow<List<MediaCaptureEntity>> = repository.mediaCaptures
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _enteredPin = MutableStateFlow("")
    val enteredPin: StateFlow<String> = _enteredPin.asStateFlow()

    private val _pinErrorMessage = MutableStateFlow<String?>(null)
    val pinErrorMessage: StateFlow<String?> = _pinErrorMessage.asStateFlow()

    init {
        // Seed default preset apps if empty
        viewModelScope.launch {
            repository.logGeneralAction("INISIALISASI", "Aplikasi Guardian Agent berhasil diaktifkan")
        }
    }

    fun selectRole(role: AppViewRole) {
        _currentRole.value = role
    }

    fun triggerRemoteLockDevice(context: Context) {
        viewModelScope.launch {
            repository.setDeviceLockState(true)
        }
    }

    fun unlockDeviceWithPin(pin: String): Boolean {
        return if (pin == "2478") {
            _enteredPin.value = ""
            _pinErrorMessage.value = null
            viewModelScope.launch {
                repository.setDeviceLockState(false)
            }
            true
        } else {
            _pinErrorMessage.value = "PIN Salah! Hanya PIN Orang Tua (2478) yang valid."
            false
        }
    }

    fun appendPinDigit(digit: String) {
        if (_enteredPin.value.length < 4) {
            _enteredPin.value = _enteredPin.value + digit
            _pinErrorMessage.value = null
        }
    }

    fun backspacePin() {
        if (_enteredPin.value.isNotEmpty()) {
            _enteredPin.value = _enteredPin.value.dropLast(1)
        }
    }

    fun clearPin() {
        _enteredPin.value = ""
        _pinErrorMessage.value = null
    }

    fun toggleAppBlockState(pkg: String, name: String, category: String, isBlocked: Boolean) {
        viewModelScope.launch {
            repository.toggleAppBlock(pkg, name, category, isBlocked)
        }
    }

    fun triggerChangeWallpaper(context: Context, themeHex: String = "#0F172A") {
        viewModelScope.launch {
            WallpaperHelper.setCustomColorWallpaper(context, themeHex, "#020617")
            repository.logGeneralAction("UBAH_WALLPAPER", "Wallpaper perangkat diubah secara remote ke tema Guardian")
        }
    }

    fun captureCameraSnapshot(context: Context, snapshotPath: String) {
        viewModelScope.launch {
            repository.logMediaCapture(snapshotPath, "CAMERA")
        }
    }

    fun captureScreenSnapshot(context: Context, snapshotPath: String) {
        viewModelScope.launch {
            repository.logMediaCapture(snapshotPath, "SCREENSHOT")
        }
    }

    fun addDemoSampleChat(appName: String, sender: String, messageText: String) {
        viewModelScope.launch {
            repository.logCapturedChat(appName, sender, messageText)
        }
    }
}
