package com.example.service

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.widget.Toast

object WallpaperHelper {

    fun setCustomColorWallpaper(context: Context, primaryHex: String = "#0F172A", secondaryHex: String = "#1E293B"): Boolean {
        return try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val width = 1080
            val height = 2400
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val paint = Paint().apply {
                isAntiAlias = true
                shader = LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    Color.parseColor(primaryHex),
                    Color.parseColor(secondaryHex),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

            // Draw a subtle protective shield motif in center
            val textPaint = Paint().apply {
                color = Color.parseColor("#38BDF8")
                textSize = 60f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("Guardian Protected Device", width / 2f, height / 2f, textPaint)

            wallpaperManager.setBitmap(bitmap)
            Toast.makeText(context, "Wallpaper berhasil diubah!", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal mengubah wallpaper: ${e.message}", Toast.LENGTH_LONG).show()
            false
        }
    }
}
