package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import com.example.ui.LockOverlayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppLockMonitoringService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private var isMonitoring = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceNotification()
        startAppMonitoring()
    }

    private fun startForegroundServiceNotification() {
        val channelId = "guardian_agent_channel"
        val channelName = "Guardian Agent Monitoring"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Guardian Agent Aktif")
            .setContentText("Pemantauan dan perlindungan orang tua aktif di latar belakang.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(1001, notification)
    }

    private fun startAppMonitoring() {
        if (isMonitoring) return
        isMonitoring = true

        serviceScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)

            while (isActive) {
                try {
                    val currentForegroundPkg = getForegroundPackageName()
                    if (currentForegroundPkg != null) {
                        val blockedApps = db.blockedAppDao().getActiveBlockedApps().firstOrNull() ?: emptyList()
                        val isBlocked = blockedApps.any { it.packageName == currentForegroundPkg }

                        if (isBlocked && currentForegroundPkg != packageName) {
                            val lockIntent = Intent(applicationContext, LockOverlayActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                putExtra("BLOCKED_PACKAGE", currentForegroundPkg)
                                putExtra("LOCK_REASON", "APLIKASI_DILINDUNGI")
                            }
                            startActivity(lockIntent)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(1500)
            }
        }
    }

    private fun getForegroundPackageName(): String? {
        return try {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                time - 1000 * 10,
                time
            )
            if (!stats.isNullOrEmpty()) {
                val sortedStats = stats.sortedByDescending { it.lastTimeUsed }
                sortedStats.firstOrNull()?.packageName
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
