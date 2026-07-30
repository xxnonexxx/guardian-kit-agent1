package com.example.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.DeviceLockScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GuardianViewModel

class LockOverlayActivity : ComponentActivity() {

    private val viewModel: GuardianViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                DeviceLockScreen(
                    viewModel = viewModel,
                    onUnlockedSuccess = {
                        finish()
                    }
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Prevent back press to bypass parental lock
        // Only valid PIN 2478 unlocks
    }
}
