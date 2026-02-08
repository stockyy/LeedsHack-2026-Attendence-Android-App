package com.university.app

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.university.app.network.AuthResponse
import com.university.app.ui.theme.AttendanceAppTheme
import com.university.app.network.ApiClient
import com.university.app.network.CheckInResult
import com.university.app.ui.theme.spaceMonoFamily
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

enum class AppScreen {
    LOGIN,
    DASHBOARD,
    NFC_SCANNER,
    QUIZ,
    TIMETABLE,
    REWARDS,
    ANALYTICS
}

class MainActivity : ComponentActivity() {
    private var nfcAdapter: NfcAdapter? = null

    // App State
    private var currentUser by mutableStateOf<AuthResponse?>(null)
    private var currentScreen by mutableStateOf(AppScreen.DASHBOARD)
    private var _scannedTag = mutableStateOf<String?>(null)
    private var _checkInStatus = mutableStateOf("Ready to Scan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            AttendanceAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val user = currentUser
                    if (user == null) {
                        LoginScreen(onLoginSuccess = { loggedInUser ->
                            currentUser = loggedInUser
                            currentScreen = AppScreen.DASHBOARD
                        })
                    } else {
                        when (currentScreen) {
                            AppScreen.DASHBOARD -> DashboardScreen(user)
                            AppScreen.NFC_SCANNER -> NfcScannerScreen(user) { currentScreen = AppScreen.DASHBOARD }
                            AppScreen.QUIZ -> QuizScreen(
                                studentId = user.userId,
                                onBack = { currentScreen = AppScreen.DASHBOARD }
                            )
                            AppScreen.TIMETABLE -> TimetableScreen { currentScreen = AppScreen.DASHBOARD }

                            // --- FIXED SECTION START ---
                            AppScreen.REWARDS -> RewardsScreen(
                                studentId = user.userId, // <--- ADDED THIS TO FIX THE ERROR
                                onBack = { currentScreen = AppScreen.DASHBOARD }
                            )
                            // --- FIXED SECTION END ---

                            AppScreen.ANALYTICS -> AnalyticsScreen(
                                onBack = { currentScreen = AppScreen.DASHBOARD }
                            )

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DashboardScreen(user: AuthResponse) {
        Scaffold(
            topBar = { HomeBar(user) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Minerva 2", fontFamily = spaceMonoFamily, fontSize = 72.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { currentScreen = AppScreen.NFC_SCANNER },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                ) {
                    Text("Check In", fontSize = 24.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardButton(text = "Timetable", modifier = Modifier.weight(1f)) {
                        currentScreen = AppScreen.TIMETABLE
                    }
                    DashboardButton(text = "Post-Lecture Quiz", modifier = Modifier.weight(1f)) {
                        currentScreen = AppScreen.QUIZ
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardButton(text = "Rewards", modifier = Modifier.weight(1f)) {
                        currentScreen = AppScreen.REWARDS
                    }
                    DashboardButton(text = "Analytics", modifier = Modifier.weight(1f)) {
                        currentScreen = AppScreen.ANALYTICS
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    Image(
                        painter = painterResource(id = R.drawable.about_hero_banner),
                        contentDescription = "University Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .fillMaxHeight(0.5f)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeBar(user: AuthResponse) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("POINTS: ${user.totalPoints}")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("x1", style = MaterialTheme.typography.bodySmall)
                }
            },
            navigationIcon = {
                IconButton(onClick = { currentScreen = AppScreen.DASHBOARD }) {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                }
            },
            actions = {
                Image(
                    painter = painterResource(id = R.drawable.partner_logo_university_of_leeds),
                    contentDescription = "University of Leeds Logo",
                    modifier = Modifier
                        .height(40.dp)
                        .padding(end = 16.dp)
                )
            }
        )
    }

    @Composable
    fun DashboardButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            modifier = modifier.aspectRatio(1f)
        ) {
            Text(text, fontSize = 24.sp, color = Color.White)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NfcScannerScreen(user: AuthResponse, onBack: () -> Unit) {
        // Reset state when the CheckInScreen is loaded
        LaunchedEffect(Unit) {
            _scannedTag.value = null
            _checkInStatus.value = "Ready to Scan"
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("NFC Scanner") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                // Simulation Button for Testing
                Button(
                    onClick = { handleScan("COMP2850_LIVE") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Test Scan (COMP2850_LIVE)")
                }
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

            // REFRESH POINTS IF SUCCESSFUL
            if (result == CheckInResult.SUCCESS) {
                val updatedUser = ApiClient.getUser(userId)
                if (updatedUser != null) {
                    currentUser = updatedUser // This triggers the UI to update points!
                }
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
            currentScreen = AppScreen.NFC_SCANNER
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            tag?.let {
                val id = NfcManager.getUniqueId(it)
                handleScan(id)
            }
        }
    }
}