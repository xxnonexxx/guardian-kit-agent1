package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.LockOverlayActivity
import com.example.ui.screens.ChildAgentScreen
import com.example.ui.screens.DeviceLockScreen
import com.example.ui.screens.ParentDashboardScreen
import com.example.ui.screens.RoleSelectionScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppViewRole
import com.example.ui.viewmodel.GuardianViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GuardianViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val currentRole by viewModel.currentRole.collectAsState()
                val deviceStatus by viewModel.deviceStatus.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F172A)
                ) {
                    if (deviceStatus.isLockedByParent) {
                        // Priority Lock Screen Overlay with PIN 2478 (Pitch Black Background & Red Button)
                        DeviceLockScreen(
                            viewModel = viewModel,
                            onUnlockedSuccess = {
                                // Device unlocked with PIN 2478
                            }
                        )
                    } else {
                        AnimatedContent(
                            targetState = currentRole,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "roleNavigation"
                        ) { role ->
                            when (role) {
                                AppViewRole.ROLE_SELECTION -> RoleSelectionScreen(viewModel = viewModel)
                                AppViewRole.PARENT_DASHBOARD -> ParentDashboardScreen(
                                    viewModel = viewModel,
                                    onTriggerLockOverlay = {
                                        val lockIntent = Intent(this@MainActivity, LockOverlayActivity::class.java)
                                        startActivity(lockIntent)
                                    }
                                )
                                AppViewRole.CHILD_AGENT -> ChildAgentScreen(
                                    viewModel = viewModel,
                                    onTriggerLockOverlay = {
                                        val lockIntent = Intent(this@MainActivity, LockOverlayActivity::class.java)
                                        startActivity(lockIntent)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
