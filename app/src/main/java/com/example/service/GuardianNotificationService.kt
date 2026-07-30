package com.example.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.data.AppDatabase
import com.example.data.CapturedChatEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GuardianNotificationService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val packageName = sbn.packageName ?: return
        val extras = sbn.notification?.extras ?: return

        val title = extras.getCharSequence("android.title")?.toString() ?: "Pesan Baru"
        val text = extras.getCharSequence("android.text")?.toString() ?: return

        if (text.isNotBlank()) {
            val appName = resolveAppName(packageName)
            val capturedChat = CapturedChatEntity(
                appName = appName,
                sender = title,
                messageText = text,
                timestamp = System.currentTimeMillis()
            )

            serviceScope.launch {
                try {
                    val db = AppDatabase.getDatabase(applicationContext)
                    db.capturedChatDao().insertChat(capturedChat)
                    Log.d("GuardianNotification", "Berhasil membaca chat dari $appName: $title -> $text")
                } catch (e: Exception) {
                    Log.e("GuardianNotification", "Gagal menyimpan chat", e)
                }
            }
        }
    }

    private fun CharSequence?.isNull_or_blank_custom(text: String?): Boolean {
        return text == null || text.trim().isEmpty()
    }

    private fun resolveAppName(pkg: String): String {
        return when {
            pkg.contains("whatsapp") -> "WhatsApp"
            pkg.contains("telegram") -> "Telegram"
            pkg.contains("instagram") -> "Instagram"
            pkg.contains("facebook") || pkg.contains("orca") -> "Messenger"
            pkg.contains("mms") || pkg.contains("messaging") || pkg.contains("sms") -> "SMS / Pesan"
            pkg.contains("tiktok") -> "TikTok"
            pkg.contains("twitter") || pkg.contains("x.android") -> "X / Twitter"
            else -> pkg.substringAfterLast('.')
        }
    }
}
