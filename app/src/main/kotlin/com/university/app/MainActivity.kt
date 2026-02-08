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
import com.university.app.network.AuthResponse
import com.university.app.network.CheckInResult
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null

    // App State
    private var currentUser by mutableStateOf<AuthResponse?>(null)
    private var points by mutableIntStateOf(0)
    private var multiplier by mutableIntStateOf(1)
    private var scannedTag by mutableStateOf<String?>(null)
    private var checkInStatus by mutableStateOf("Ready to Scan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val user = currentUser
                    if (user == null) {
                        LoginScreen(onLoginSuccess = { newUser ->
                            currentUser = newUser
                        })
                    } else {
                        DashboardScreen(
                            user = user,
                            onLogout = { currentUser = null },
                            points = points,
                            multiplier = multiplier,
                            checkInStatus = checkInStatus,
                            scannedTag = scannedTag,
                            onPointsAdded = { points += it },
                            onResetStats = {
                                points = 0
                                multiplier = 1
                                scannedTag = null
                                checkInStatus = "Ready to Scan"
                            },
                            onScanSimulated = { handleScan(it) }
                        )
                    }
                }
            }
        }
    }

    private fun handleScan(tagId: String) {
        val userId = currentUser?.userId ?: return
        scannedTag = tagId
        checkInStatus = "Sending..."

        lifecycleScope.launch {
            val randomMood = (1..5).random()
            val result = ApiClient.performCheckIn(
                nfcId = tagId,
                mood = randomMood,
                studentId = userId
            )

            checkInStatus = when (result) {
                CheckInResult.SUCCESS -> {
                    points += 10 * multiplier
                    "✅ SUCCESS! (+${10 * multiplier} pts)"
                }
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
