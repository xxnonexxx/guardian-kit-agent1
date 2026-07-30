package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BlockedAppEntity
import com.example.ui.viewmodel.GuardianViewModel

@Composable
fun AppLockerScreen(
    viewModel: GuardianViewModel
) {
    val blockedApps by viewModel.blockedApps.collectAsState()
    var newAppName by remember { mutableStateOf("") }
    var newAppPkg by remember { mutableStateOf("") }

    // Preset list if empty
    val defaultApps = remember {
        listOf(
            BlockedAppEntity("com.google.android.youtube", "YouTube", true, "Hiburan"),
            BlockedAppEntity("com.ss.android.ugc.trill", "TikTok", true, "Media Sosial"),
            BlockedAppEntity("com.mobile.legends", "Mobile Legends", true, "Game"),
            BlockedAppEntity("com.instagram.android", "Instagram", false, "Media Sosial"),
            BlockedAppEntity("com.android.chrome", "Chrome Browser", true, "Browser")
        )
    }

    val displayList = if (blockedApps.isEmpty()) defaultApps else blockedApps

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_locker_screen")
            .padding(16.dp)
    ) {
        Text(
            text = "PENGUNCI APLIKASI (APP LOCKER)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "Kunci aplikasi yang tidak diinginkan agar anak tidak dapat membukanya tanpa izin.",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Add custom app input
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Tambah Kunci Aplikasi Baru",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newAppName,
                        onValueChange = { newAppName = it },
                        placeholder = { Text("Nama App (mis. Roblox)", fontSize = 12.sp, color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newAppPkg,
                        onValueChange = { newAppPkg = it },
                        placeholder = { Text("Package Name", fontSize = 12.sp, color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (newAppName.isNotBlank() && newAppPkg.isNotBlank()) {
                            viewModel.toggleAppBlockState(newAppPkg, newAppName, "Kustom", true)
                            newAppName = ""
                            newAppPkg = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_block_app_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Simpan & Kunci Aplikasi", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(displayList) { app ->
                AppBlockRowItem(
                    app = app,
                    onToggle = { isBlocked ->
                        viewModel.toggleAppBlockState(app.packageName, app.appName, app.category, isBlocked)
                    }
                )
            }
        }
    }
}

@Composable
private fun AppBlockRowItem(
    app: BlockedAppEntity,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_item_${app.packageName}"),
        colors = CardDefaults.cardColors(
            containerColor = if (app.isBlocked) Color(0xFF220A0A) else Color(0xFF1E293B)
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (app.isBlocked) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF3B30).copy(alpha = 0.4f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (app.isBlocked) Color(0xFFFF3B30).copy(alpha = 0.2f) else Color(0xFF334155)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (app.isBlocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = "Status",
                    tint = if (app.isBlocked) Color(0xFFFF3B30) else Color(0xFF94A3B8),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = app.appName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0F172A))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = app.category,
                            fontSize = 10.sp,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
                Text(
                    text = app.packageName,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            Switch(
                checked = app.isBlocked,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFFF3B30),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color(0xFF334155)
                )
            )
        }
    }
}
