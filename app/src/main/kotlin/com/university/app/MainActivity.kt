package com.university.app

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    private var _scannedTag = mutableStateOf<String?>(null)
    private var _checkInStatus = mutableStateOf("Ready to Scan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    if (currentUser == null) {
                        SignInScreen(onSignInSuccess = { user ->
                            currentUser = user
                        })
                    } else {
                        CheckInScreen()
                    }
                }
            }
        }
    }

    @Composable
    fun SignInScreen(onSignInSuccess: (AuthResponse) -> Unit) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "University Login", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        isLoading = true
                        scope.launch {
                            val response = ApiClient.signIn(email, password)
                            isLoading = false
                            if (response != null) {
                                onSignInSuccess(response)
                            } else {
                                Toast.makeText(this@MainActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign In")
                }
            }
        }
    }

    @Composable
    fun CheckInScreen() {
        // Reset state when the CheckInScreen is loaded
        LaunchedEffect(Unit) {
            _scannedTag.value = null
            _checkInStatus.value = "Ready to Scan"
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome, ${currentUser?.userName}",
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

            Spacer(modifier = Modifier.height(40.dp))

            // Simulation Button for Testing
            Button(onClick = { handleScan("COMP2850_LIVE") }) {
                Text("Test Scan (COMP2850_LIVE)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { currentUser = null }) {
                Text("Logout")
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
