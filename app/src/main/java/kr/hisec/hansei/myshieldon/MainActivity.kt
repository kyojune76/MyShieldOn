package kr.hisec.hansei.myshieldon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.delay
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import kr.hisec.hansei.myshieldon.ui.theme.MainScreen
import kr.hisec.hansei.myshieldon.ui.theme.LoadingScreen
import kr.hisec.hansei.myshieldon.ui.theme.ResultScreen
import kr.hisec.hansei.myshieldon.ui.theme.MyShieldOnTheme
import kr.hisec.hansei.myshieldon.ui.theme.SecurityGuide

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: ScanViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            )
            val navController = rememberNavController()
            val context = LocalContext.current

            MyShieldOnTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController, startDestination = "entry") {
                        // Entry point (MainGateScreen)
                        composable("entry") {
                            EntryRouter(navController = navController)
                        }

                        // MainScreen
                        composable("main") {
                            MainScreen(
                                onStartScanClick = {
                                    if (!UsageStatsManagerUtil.hasUsageStatsPermission(context)) {
                                        UsageStatsManagerUtil.requestUsageStatsPermission(context)
                                    } else {
                                        navController.navigate("loading")
                                    }
                                }
                            )
                        }

                        composable("loading") {
                            LaunchedEffect(Unit) {
                                // 1. 스캔 시작
                                viewModel.startSecurityScan()

                                // 2. 스캔이 완료될 때까지 기다림

                                var done = false
                                while (!done) {
                                    val state = viewModel.uiState.value
                                    if (state is ScanUiState.Success || state is ScanUiState.Error) {
                                        done = true
                                    }
                                    delay(100) // ⬅️ 짧은 간격으로 상태 체크
                                }

                                // 3. 스캔이 완료된 후, 사용자에게 화면을 보여줄 최소 시간(1.5초)을 기다립니다.
                                delay(4000)

                                // 4. ResultScreen으로 이동합니다.
                                navController.navigate("result") {
                                    popUpTo("loading") { inclusive = true }
                                }
                            }

                            LoadingScreen(
                                onSettingsClick = {}
                            )
                        }

                        // ResultScreen
                        composable("result") {
                            ResultScreen(
                                viewModel = viewModel,
                                onGoBack = {
                                    viewModel.returnToIdle()
                                    navController.navigate("main") {
                                        popUpTo("main") { inclusive = true }
                                    }
                                },
                                        onGuideClick = {
                                            navController.navigate("security_guide")
                                        }
                            )
                        }
                        composable("security_guide") {
                            val state = viewModel.uiState.collectAsState().value
                            if (state is ScanUiState.Success) {
                                SecurityGuide(scanResult = state)
                            }
                        }

                    }
                }
            }
        }
    }
}