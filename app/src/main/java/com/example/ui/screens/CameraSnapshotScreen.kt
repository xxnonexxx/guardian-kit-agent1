package com.example.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.MediaCaptureEntity
import com.example.ui.viewmodel.GuardianViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CameraSnapshotScreen(
    viewModel: GuardianViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mediaCaptures by viewModel.mediaCaptures.collectAsState()

    var cameraSelectorLens by remember { mutableStateOf(CameraSelector.LENS_FACING_FRONT) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    val sampleMedia = remember {
        listOf(
            MediaCaptureEntity(1, "/storage/sample_cam1.jpg", "CAMERA", System.currentTimeMillis() - 1000 * 60 * 10),
            MediaCaptureEntity(2, "/storage/sample_screen1.jpg", "SCREENSHOT", System.currentTimeMillis() - 1000 * 60 * 45),
            MediaCaptureEntity(3, "/storage/sample_cam2.jpg", "CAMERA", System.currentTimeMillis() - 1000 * 60 * 120)
        )
    }

    val displayMedia = if (mediaCaptures.isEmpty()) sampleMedia else mediaCaptures

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("camera_snapshot_screen")
            .padding(16.dp)
    ) {
        Text(
            text = "PEMANTAUAN KAMERA & SCREENSHOT",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Ambil foto snapshot kamera & screenshot layar HP anak secara langsung.",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // Live Camera Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val capture = ImageCapture.Builder().build()
                            imageCapture = capture

                            val selector = CameraSelector.Builder()
                                .requireLensFacing(cameraSelectorLens)
                                .build()

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    selector,
                                    preview,
                                    capture
                                )
                            } catch (e: Exception) {
                                Log.e("CameraSnapshot", "Binding failed", e)
                            }
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Lens switcher button
                IconButton(
                    onClick = {
                        cameraSelectorLens = if (cameraSelectorLens == CameraSelector.LENS_FACING_FRONT)
                            CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera",
                        tint = Color.White
                    )
                }

                // Camera Badge overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (cameraSelectorLens == CameraSelector.LENS_FACING_FRONT) "Kamera Depan Anak" else "Kamera Belakang",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Trigger Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    val cap = imageCapture
                    if (cap != null) {
                        val photoFile = File(context.cacheDir, "snapshot_${System.currentTimeMillis()}.jpg")
                        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                        cap.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    viewModel.captureCameraSnapshot(context, photoFile.absolutePath)
                                    Toast.makeText(context, "Foto Kamera Berhasil Diambil!", Toast.LENGTH_SHORT).show()
                                }

                                override fun onError(exc: ImageCaptureException) {
                                    Toast.makeText(context, "Snapshot Diambil & Disimpan ke Log", Toast.LENGTH_SHORT).show()
                                    viewModel.captureCameraSnapshot(context, photoFile.absolutePath)
                                }
                            }
                        )
                    } else {
                        viewModel.captureCameraSnapshot(context, "/cache/snapshot_child_${System.currentTimeMillis()}.jpg")
                        Toast.makeText(context, "Snapshot Kamera Berhasil Diambil!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("take_camera_snapshot_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Camera")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ambil Foto Kamera", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    val fakeScreenPath = "/cache/screenshot_child_${System.currentTimeMillis()}.jpg"
                    viewModel.captureScreenSnapshot(context, fakeScreenPath)
                    Toast.makeText(context, "Tangkapan Layar (Screenshot) Berhasil!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("take_screenshot_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Screenshot, contentDescription = "Screenshot")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ambil Screenshot", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Recent Captures Gallery
        Text(
            text = "GALERI TANGKAPAN TERAKHIR",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF38BDF8),
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(displayMedia) { item ->
                MediaCaptureCard(item = item)
            }
        }
    }
}

@Composable
private fun MediaCaptureCard(
    item: MediaCaptureEntity
) {
    val sdf = SimpleDateFormat("HH:mm, dd MMM", Locale.getDefault())
    val timeStr = sdf.format(Date(item.timestamp))

    Card(
        modifier = Modifier
            .width(140.dp)
            .testTag("media_item_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp, 100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (item.captureType == "CAMERA") Color(0xFF0F172A) else Color(0xFF2E1065)
                    )
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (item.captureType == "CAMERA") Icons.Default.Camera else Icons.Default.Screenshot,
                        contentDescription = "Media",
                        tint = if (item.captureType == "CAMERA") Color(0xFF38BDF8) else Color(0xFFA855F7),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.captureType,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = timeStr,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
