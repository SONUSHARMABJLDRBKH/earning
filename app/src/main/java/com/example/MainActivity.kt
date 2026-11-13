package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ai.configureAppCheck
import com.example.ui.navigation.RoyalBulletApp
import com.example.ui.theme.RoyalBulletTheme

class MainActivity : ComponentActivity() {
    private var incomingInviteCode by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        incomingInviteCode = extractInviteCode(intent)
        enableEdgeToEdge()
        setContent {
            RoyalBulletTheme {
                RoyalBulletApp(initialInviteCode = incomingInviteCode)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val parsed = extractInviteCode(intent)
        if (parsed.isNotBlank()) {
            incomingInviteCode = parsed
        }
    }

    private fun extractInviteCode(intent: Intent?): String {
        val data = intent?.data ?: return ""
        return data.getQueryParameter("code")
            ?.trim()
            ?.uppercase()
            ?: ""
    }
}
