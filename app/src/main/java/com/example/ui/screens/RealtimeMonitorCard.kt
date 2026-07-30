package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RealtimeDeviceStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RealtimeMonitorCard(
    status: RealtimeDeviceStatus
) {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val lastUpdateStr = sdf.format(Date(status.lastPingTimestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("realtime_monitor_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MONITORING REALTIME",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8),
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "Update: $lastUpdateStr",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid Items
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Battery
                StatusTile(
                    modifier = Modifier.weight(1f),
                    title = "Baterai",
                    value = "${status.batteryLevel}%",
                    subtitle = if (status.isCharging) "Mengisi Daya" else "Tidak Diisi",
                    icon = Icons.Default.BatteryChargingFull,
                    accentColor = Color(0xFF10B981)
                )

                // Active App
                StatusTile(
                    modifier = Modifier.weight(1f),
                    title = "Aplikasi Aktif",
                    value = status.activeAppName,
                    subtitle = status.activeAppPackage.substringAfterLast('.'),
                    icon = Icons.Default.Android,
                    accentColor = Color(0xFFF59E0B)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Network
                StatusTile(
                    modifier = Modifier.weight(1f),
                    title = "Koneksi Jaringan",
                    value = "Wi-Fi",
                    subtitle = status.networkStatus,
                    icon = Icons.Default.Wifi,
                    accentColor = Color(0xFF38BDF8)
                )

                // Storage
                val usedGb = status.storageTotalGb - status.storageFreeGb
                StatusTile(
                    modifier = Modifier.weight(1f),
                    title = "Penyimpanan",
                    value = "${String.format(Locale.US, "%.1f", usedGb)} GB",
                    subtitle = "dari ${status.storageTotalGb.toInt()} GB",
                    icon = Icons.Default.SdCard,
                    accentColor = Color(0xFFA855F7)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // RAM Progress
            val ramPercent = (status.ramUsedMb.toFloat() / status.ramTotalMb.toFloat())
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = "RAM",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Penggunaan RAM System",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    Text(
                        text = "${status.ramUsedMb} MB / ${status.ramTotalMb} MB (${(ramPercent * 100).toInt()}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { ramPercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF38BDF8),
                    trackColor = Color(0xFF0F172A)
                )
            }
        }
    }
}

@Composable
private fun StatusTile(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                maxLines = 1
            )
        }
    }
}
