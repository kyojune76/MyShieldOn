package kr.hisec.hansei.myshieldon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import kr.hisec.hansei.myshieldon.ui.theme.MainScreen
import kr.hisec.hansei.myshieldon.ui.theme.ResultScreen
import kr.hisec.hansei.myshieldon.ui.theme.MyShieldOnTheme

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
                NavHost(navController, startDestination = "entry") {
                    // 1️⃣ Entry point 추가
                    composable("entry") {
                        EntryRouter(navController = navController)
                    }

                    // 2️⃣ 기존 Main 화면
                    composable("main") {
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                        LaunchedEffect(uiState) {
                            if (uiState is ScanUiState.Success || uiState is ScanUiState.Error) {
                                navController.navigate("result") {
                                    popUpTo("main") { inclusive = true }
                                }
                            }
                        }

                        MainScreen(
                            onStartScanClick = {
                                if (!UsageStatsManagerUtil.hasUsageStatsPermission(context)) {
                                    UsageStatsManagerUtil.requestUsageStatsPermission(context)
                                } else {
                                    viewModel.startSecurityScan()
                                }
                            }
                        )
                    }

                    // 3️⃣ 결과 화면
                    composable("result") {
                        ResultScreen(
                            viewModel = viewModel,
                            onGoBack = {
                                viewModel.returnToIdle()
                                navController.navigate("main") {
                                    popUpTo("main") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}