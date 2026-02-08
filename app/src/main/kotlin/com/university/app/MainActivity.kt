package com.university.app

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // Using Material 3
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.university.app.network.ApiClient
import com.university.app.NfcManager // Fixed import
import kotlinx.coroutines.launch // You might need to add this import
import androidx.lifecycle.lifecycleScope // AND this one

class MainActivity : ComponentActivity() {
    // 1. We declare the variables here so we can use them later
    private var nfcAdapter: NfcAdapter? = null

    // 2. These store our app's state
    private val _scannedTag = mutableStateOf<String?>(null)
    private val _checkInStatus = mutableStateOf("Ready to Scan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 3. Initialize the NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            // This is a simple Hackathon Theme wrapper
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    // The UI Layout
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title
                        Text(
                            text = _checkInStatus.value,
                            style = MaterialTheme.typography.headlineMedium // Fixed: h4 -> headlineMedium
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Status Message
                        if (_scannedTag.value != null) {
                            Text(text = "Scanned Tag: ${_scannedTag.value}")
                        } else {
                            Text(text = "Please tap your phone on a class NFC tag.")
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // SAFETY BUTTON: Use this if NFC fails during the demo!
                        Button(onClick = {
                            handleScan("COMP2850_LIVE")
                        }) {
                            Text("Simulate Scan (Debug)")
                        }
                    }
                }
            }
        }
    }

    // 4. This function handles the logic when a tag is found
    private fun handleScan(tagText: String) {
        _scannedTag.value = tagText
        _checkInStatus.value = "Sending..."

        // Use lifecycleScope to run this in the background
        lifecycleScope.launch {
            // 1. Random mood for demo purposes (since we don't have a UI slider for it yet)
            val randomMood = (1..5).random()

            // 2. Actually call the server
            val success = ApiClient.performCheckIn(
                nfcText = tagText,
                mood = randomMood
            )

            // 3. Update UI based on result
            if (success) {
                _checkInStatus.value = "✅ SUCCESS! (+10 pts)"
            } else {
                _checkInStatus.value = "❌ Network Error or Invalid Tag"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Enable priority reading when app is open
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

    // This triggers when a real physical tag is tapped
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