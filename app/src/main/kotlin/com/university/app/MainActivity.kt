package com.university.app

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.university.app.network.ApiClient
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null

    // APP STATE
    // -1 means not logged in
    private val _loggedInUserId = mutableStateOf<Int?>(null)
    private val _loggedInUserName = mutableStateOf<String>("")

    // NFC STATE
    private val _scannedTag = mutableStateOf<String?>(null)
    private val _checkInStatus = mutableStateOf("Ready to Scan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var showQuiz by remember { mutableStateOf(false) }
                    val currentSessionId by remember { mutableStateOf(1) } // Default or passed from NFC

                    if (_loggedInUserId.value == null) {
                        // 1. Show Login Screen
                        LoginScreen(onLoginSuccess = { userId, name ->
                            _loggedInUserId.value = userId
                            _loggedInUserName.value = name
                        })
                    } else {
                        // User is logged in
                        if (showQuiz) {
                            QuizScreen(
                                sessionId = currentSessionId,
                                studentId = _loggedInUserId.value ?: 0,
                                onQuizFinished = { showQuiz = false }
                            )
                        } else {
                            // Show screen with NFC and Quiz button
                            NfcScreen(onStartQuiz = { showQuiz = true })
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun NfcScreen(onStartQuiz: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome, ${_loggedInUserName.value}!",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(40.dp))

            // Debug Button
            Button(onClick = { handleScan("COMP2850_LIVE") }) {
                Text("Simulate Scan (Debug)")
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onStartQuiz) {
                Text("Start Post-Lecture Quiz")
            }

            // Logout Button (Optional but useful)
            Spacer(modifier = Modifier.height(20.dp))
            TextButton(onClick = { _loggedInUserId.value = null }) {
                Text("Log Out")
            }
        }
    }

    private fun handleScan(tagText: String) {
        val userId = _loggedInUserId.value ?: return // Don't scan if not logged in

        _scannedTag.value = tagText
        _checkInStatus.value = "Sending..."

        lifecycleScope.launch {
            val randomMood = (1..5).random()

            // Pass the REAL userId now!
            val success = ApiClient.performCheckIn(
                nfcText = tagText,
                mood = randomMood,
                userId = userId
            )

            if (success) {
                _checkInStatus.value = "✅ SUCCESS! (+10 pts)"
            } else {
                _checkInStatus.value = "❌ Network Error or Invalid Tag"
            }
        }
    }

    // --- Standard NFC Boilerplate below (Keep existing onResume/onPause/onNewIntent) ---
    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) }
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
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            tag?.let {
                val text = NfcManager.getTextFromNfc(it) ?: "Unknown Tag"
                handleScan(text)
            }
        }
    }
}