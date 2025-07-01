package kr.hisec.hansei.myshieldon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Color를 사용하기 위한 import
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kr.hisec.hansei.myshieldon.ui.theme.MyShieldOnTheme


private val SuccessGreen = Color(0xFF4CAF50)
private val WarningRed = Color(0xFFD32F2F)
// --------------------------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyShieldOnTheme {
                val viewModel: ScanViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application))
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            viewModel = viewModel,
                            onScanFinished = {
                                navController.navigate("result") {
                                    popUpTo("main") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("result") {
                        ResultScreen(
                            viewModel = viewModel,
                            onGoBack = {
                                viewModel.returnToIdle()
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MainScreen(viewModel: ScanViewModel, onScanFinished: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is ScanUiState.Success || uiState is ScanUiState.Error) {
            onScanFinished()
        }
    }

    when (uiState) {
        is ScanUiState.Idle -> IdleScreen(onStartClick = { viewModel.startSecurityScan() })
        is ScanUiState.Scanning -> ScanningScreen()
        else -> {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun ResultScreen(viewModel: ScanViewModel, onGoBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text("점검 완료", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (val result = uiState) {
                is ScanUiState.Success -> {
                    if (result.detectedApps.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("모든 앱이 안전합니다.", color = SuccessGreen, style = MaterialTheme.typography.titleMedium)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("총 ${result.detectedApps.size}개의 보안 위협이 탐지되었습니다.", color = WarningRed, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(result.detectedApps) { app -> DetectedAppCard(app) }
                            }
                        }
                    }
                }
                is ScanUiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("오류 발생: ${result.message}", color = WarningRed)
                }
                else -> {}
            }
        }
        Button(onClick = onGoBack, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
            Text("다시 점검하기", fontSize = 18.sp)
        }
    }
}

@Composable
fun IdleScreen(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MyShieldOn",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_shield),
            contentDescription = "Shield Icon",
            modifier = Modifier.size(120.dp).padding(bottom = 32.dp)
        )
        Button(onClick = onStartClick) {
            Text(text = "점검 시작", fontSize = 18.sp)
        }
    }
}

@Composable
fun ScanningScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text("정밀 검사를 진행 중입니다...", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun DetectedAppCard(app: DetectedApp) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${app.appName} (${app.packageName})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            app.issues.forEach { issue ->
                when (issue) {
                    is SecurityIssue.DangerousPermissions -> Text("  - 이슈: 과도한 위험 권한 보유 (${issue.permissions.size}개)", color = WarningRed)
                    is SecurityIssue.TamperedSignature -> Text("  - 이슈: ★★★ 서명 변조 의심 ★★★", color = WarningRed, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}