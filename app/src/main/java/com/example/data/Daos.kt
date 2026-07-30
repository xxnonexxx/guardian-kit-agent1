package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedAppDao {
    @Query("SELECT * FROM blocked_apps ORDER BY appName ASC")
    fun getAllBlockedApps(): Flow<List<BlockedAppEntity>>

    @Query("SELECT * FROM blocked_apps WHERE isBlocked = 1")
    fun getActiveBlockedApps(): Flow<List<BlockedAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(app: BlockedAppEntity)

    @Query("DELETE FROM blocked_apps WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)
}

@Dao
interface CapturedChatDao {
    @Query("SELECT * FROM captured_chats ORDER BY timestamp DESC")
    fun getAllCapturedChats(): Flow<List<CapturedChatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: CapturedChatEntity)

    @Query("DELETE FROM captured_chats")
    suspend fun clearAllChats()
}

@Dao
interface DeviceLogDao {
    @Query("SELECT * FROM device_logs ORDER BY timestamp DESC LIMIT 100")
    fun getLogs(): Flow<List<DeviceLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DeviceLogEntity)
}

@Dao
interface MediaCaptureDao {
    @Query("SELECT * FROM media_captures ORDER BY timestamp DESC")
    fun getAllMedia(): Flow<List<MediaCaptureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: MediaCaptureEntity)
}
