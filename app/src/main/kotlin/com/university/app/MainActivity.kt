package com.university.app

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.university.app.network.ApiClient
import com.university.app.network.AuthResponse
import com.university.app.network.CheckInResult
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

enum class AppScreen {
    CHECK_IN,
    QUIZ,
    STATS
}

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null

    // App State
    private var currentUser by mutableStateOf<AuthResponse?>(null)
    private var currentScreen by mutableStateOf(AppScreen.CHECK_IN)
    private var _scannedTag = mutableStateOf<String?>(null)
    private var _checkInStatus = mutableStateOf("Ready to Scan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val user = currentUser
                    if (user == null) {
                        LoginScreen(onLoginSuccess = { loggedInUser ->
                            currentUser = loggedInUser
                            currentScreen = AppScreen.CHECK_IN
                        })
                    } else {
                        when (currentScreen) {
                            AppScreen.CHECK_IN -> CheckInScreen(user)
                            AppScreen.QUIZ -> QuizScreen(
                                studentId = user.userId,
                                onBack = { currentScreen = AppScreen.CHECK_IN }
                            )
                            AppScreen.STATS -> StatsScreen(
                                studentId = user.userId,
                                onBack = { currentScreen = AppScreen.CHECK_IN }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CheckInScreen(user: AuthResponse) {
        // Reset state when the CheckInScreen is loaded
        LaunchedEffect(Unit) {
            _scannedTag.value = null
            _checkInStatus.value = "Ready to Scan"
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome, ${user.userName}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = _checkInStatus.value,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (_scannedTag.value != null) {
                Text(text = "Scanned Tag: ${_scannedTag.value}")
            } else {
                Text(text = "Please tap your phone on a class NFC tag.")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Simulation Button for Testing - Using valid ID from Starling session
            Button(
                onClick = { handleScan("02335C41B6A000") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Test Scan (Starling Module)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quiz and Stats Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionCard(
                    title = "Take Quiz",
                    icon = Icons.Default.Assignment,
                    modifier = Modifier.weight(1f),
                    onClick = { currentScreen = AppScreen.QUIZ }
                )
                ActionCard(
                    title = "My Stats",
                    icon = Icons.Default.BarChart,
                    modifier = Modifier.weight(1f),
                    onClick = { currentScreen = AppScreen.STATS }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = { currentUser = null }) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }

    @Composable
    fun ActionCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
        ElevatedCard(
            onClick = onClick,
            modifier = modifier.height(100.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = title, style = MaterialTheme.typography.labelLarge)
            }
        }
    }

    private fun handleScan(tagId: String) {
        val userId = currentUser?.userId ?: return
        _scannedTag.value = tagId
        _checkInStatus.value = "Sending..."

        lifecycleScope.launch {
            val randomMood = (1..5).random()
            val result = ApiClient.performCheckIn(
                nfcId = tagId,
                mood = randomMood,
                studentId = userId
            )

            _checkInStatus.value = when (result) {
                CheckInResult.SUCCESS -> "✅ SUCCESS! (+10 pts)"
                CheckInResult.ALREADY_CHECKED_IN -> "ℹ️ Already Checked In"
                CheckInResult.INVALID_TAG -> "❌ Invalid Tag"
                CheckInResult.NETWORK_ERROR -> "⚠️ Network Error"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (currentUser != null && (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == intent.action)) {

            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            tag?.let {
                val id = NfcManager.getUniqueId(it)
                handleScan(id)
            }
        }
    }
}
