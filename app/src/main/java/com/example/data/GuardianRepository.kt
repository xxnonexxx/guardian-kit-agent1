package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RealtimeDeviceStatus(
    val batteryLevel: Int = 85,
    val isCharging: Boolean = true,
    val activeAppPackage: String = "com.google.android.youtube",
    val activeAppName: String = "YouTube",
    val ramUsedMb: Long = 2840,
    val ramTotalMb: Long = 6000,
    val storageFreeGb: Double = 32.4,
    val storageTotalGb: Double = 128.0,
    val networkStatus: String = "Wi-Fi (Guardian-Home)",
    val isLockedByParent: Boolean = false,
    val lockPasswordRequired: String = "2478",
    val lastPingTimestamp: Long = System.currentTimeMillis()
)

class GuardianRepository(private val db: AppDatabase) {

    val blockedApps: Flow<List<BlockedAppEntity>> = db.blockedAppDao().getAllBlockedApps()
    val capturedChats: Flow<List<CapturedChatEntity>> = db.capturedChatDao().getAllCapturedChats()
    val deviceLogs: Flow<List<DeviceLogEntity>> = db.deviceLogDao().getLogs()
    val mediaCaptures: Flow<List<MediaCaptureEntity>> = db.mediaCaptureDao().getAllMedia()

    private val _deviceStatus = MutableStateFlow(RealtimeDeviceStatus())
    val deviceStatus: StateFlow<RealtimeDeviceStatus> = _deviceStatus.asStateFlow()

    suspend fun setDeviceLockState(isLocked: Boolean) {
        _deviceStatus.value = _deviceStatus.value.copy(
            isLockedByParent = isLocked,
            lastPingTimestamp = System.currentTimeMillis()
        )
        db.deviceLogDao().insertLog(
            DeviceLogEntity(
                actionType = if (isLocked) "KUNCI_PERANGKAT" else "BUKA_KUNCI",
                description = if (isLocked) "Orang tua mengunci perangkat secara otomatis (PIN 2478)" else "Perangkat dibuka dengan PIN 2478"
            )
        )
    }

    suspend fun toggleAppBlock(packageName: String, appName: String, category: String, isBlocked: Boolean) {
        db.blockedAppDao().insertOrUpdate(
            BlockedAppEntity(
                packageName = packageName,
                appName = appName,
                isBlocked = isBlocked,
                category = category
            )
        )
        db.deviceLogDao().insertLog(
            DeviceLogEntity(
                actionType = "APLIKASI_STATUS",
                description = "Aplikasi $appName (${packageName}) ${if (isBlocked) "DIKUNCI" else "DIBUKA"}"
            )
        )
    }

    suspend fun logCapturedChat(appName: String, sender: String, text: String) {
        db.capturedChatDao().insertChat(
            CapturedChatEntity(
                appName = appName,
                sender = sender,
                messageText = text
            )
        )
    }

    suspend fun logMediaCapture(filePath: String, type: String) {
        db.mediaCaptureDao().insertMedia(
            MediaCaptureEntity(
                filePath = filePath,
                captureType = type
            )
        )
        db.deviceLogDao().insertLog(
            DeviceLogEntity(
                actionType = "MEDIA_TANGKAPAN",
                description = "Media $type berhasil diambil dan disimpan"
            )
        )
    }

    suspend fun logGeneralAction(action: String, description: String) {
        db.deviceLogDao().insertLog(
            DeviceLogEntity(actionType = action, description = description)
        )
    }

    fun updateRealtimeTelemetry(status: RealtimeDeviceStatus) {
        _deviceStatus.value = status
    }
}
