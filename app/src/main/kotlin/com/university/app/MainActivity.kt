package com.university.app // Make sure this matches your folder structure

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // or androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import com.university.app.network.ApiClient // Ensure this matches your ApiClient package
import com.university.app.utils.NfcManager // Ensure you created this file!

class MainActivity : ComponentActivity() {
    private val _scannedTag = MutableStateFlow<String?>(null)
    // Add a state for the result
    private val _checkInStatus = MutableStateFlow<String>("Ready to Scan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ... (NFC Adapter setup from previous message) ...

        setContent {
            val scannedTag by _scannedTag.collectAsState()
            val status by _checkInStatus.collectAsState()
            val scope = rememberCoroutineScope() // Needed to run network calls

            // React to a scan
            LaunchedEffect(scannedTag) {
                scannedTag?.let { tagText ->
                    _checkInStatus.value = "Sending..."
                    // Call the API!
                    val success = ApiClient.performCheckIn(tagText, mood = 85) // 85 is dummy mood for now
                    _checkInStatus.value = if (success) "✅ Check-in Success!" else "❌ Failed"
                }
            }

            AppTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = status, style = MaterialTheme.typography.h4)
                    Spacer(modifier = Modifier.height(20.dp))
                    if (scannedTag != null) {
                        Text(text = "Tag: $scannedTag")
                    } else {
                        Text(text = "Please tap your phone on a class tag.")
                    }
                    
                    // SAFETY BUTTON (In case NFC fails during demo)
                    Button(onClick = { _scannedTag.value = "COMP2850_LIVE" }) {
                        Text("Debug Check-in")
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Enable "Foreground Dispatch" - priority reading when app is open
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

    // This triggers when a tag is tapped
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action || 
            NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            tag?.let {
                // Use our helper to get the clean text (e.g., "COMP2850_LIVE")
                val text = NfcManager.getTextFromNfc(it)
                _scannedTag.value = text
                
                // HACKATHON DEBUG: Print to logcat so you know it works
                println("📲 NFC SCANNED: $text")
            }
        }
    }
}