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
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CapturedChatEntity
import com.example.ui.viewmodel.GuardianViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatMonitorScreen(
    viewModel: GuardianViewModel
) {
    val capturedChats by viewModel.capturedChats.collectAsState()

    val sampleChats = remember {
        listOf(
            CapturedChatEntity(1, "WhatsApp", "Budi (Teman Sekolah)", "Main ML yuk nanti malam jam 8!", System.currentTimeMillis() - 1000 * 60 * 5),
            CapturedChatEntity(2, "Telegram", "Grup Kelas 8A", "Pengumuman PR Matematika hal 45 dikumpul besok.", System.currentTimeMillis() - 1000 * 60 * 30),
            CapturedChatEntity(3, "Instagram", "rendi_gaming", "Lihat video ini lucu banget bro!", System.currentTimeMillis() - 1000 * 60 * 120),
            CapturedChatEntity(4, "SMS / Pesan", "Ibu", "Jangan lupa makan siang ya nak.", System.currentTimeMillis() - 1000 * 60 * 240)
        )
    }

    val displayList = if (capturedChats.isEmpty()) sampleChats else capturedChats

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("chat_monitor_screen")
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BACA CHAT (PEMANTAUAN NOTIFIKASI)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Log pesan chat yang terbaca dari HP anak",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Button(
                onClick = {
                    val apps = listOf("WhatsApp", "Telegram", "Instagram", "TikTok", "SMS")
                    val names = listOf("Rian", "Dewi", "Grup Game", "Kevin", "Bintang")
                    val msgs = listOf(
                        "Besok kumpul di kantin jam 10 ya!",
                        "Bro, kirimin tugas fisika dong.",
                        "Lagi main game apa sekarang?",
                        "Oke mantap bro!",
                        "Ketemuan di taman yuk nanti sore."
                    )
                    val randomApp = apps.random()
                    val randomName = names.random()
                    val randomMsg = msgs.random()

                    viewModel.addDemoSampleChat(randomApp, randomName, randomMsg)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("test_add_chat_button")
            ) {
                Icon(imageVector = Icons.Default.AddComment, contentDescription = "Test", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Simulasi Chat", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(displayList) { chat ->
                ChatItemRow(chat = chat)
            }
        }
    }
}

@Composable
private fun ChatItemRow(
    chat: CapturedChatEntity
) {
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val timeStr = sdf.format(Date(chat.timestamp))

    val appBadgeColor = when (chat.appName) {
        "WhatsApp" -> Color(0xFF25D366)
        "Telegram" -> Color(0xFF229ED9)
        "Instagram" -> Color(0xFFE1306C)
        "SMS / Pesan" -> Color(0xFFF59E0B)
        else -> Color(0xFF38BDF8)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_item_${chat.id}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(appBadgeColor.copy(alpha = 0.2f))
                            .border(1.dp, appBadgeColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat App",
                            tint = appBadgeColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = chat.sender,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(appBadgeColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = chat.appName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = appBadgeColor
                            )
                        }
                    }
                }

                Text(
                    text = timeStr,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F172A))
                    .padding(10.dp)
            ) {
                Text(
                    text = chat.messageText,
                    fontSize = 13.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 18.sp
                )
            }
        }
    }
}
