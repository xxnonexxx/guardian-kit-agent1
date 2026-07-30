package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_apps")
data class BlockedAppEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val isBlocked: Boolean = true,
    val category: String = "Umum",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "captured_chats")
data class CapturedChatEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appName: String,
    val sender: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "device_logs")
data class DeviceLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actionType: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "media_captures")
data class MediaCaptureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filePath: String,
    val captureType: String, // "CAMERA" or "SCREENSHOT"
    val timestamp: Long = System.currentTimeMillis()
)
