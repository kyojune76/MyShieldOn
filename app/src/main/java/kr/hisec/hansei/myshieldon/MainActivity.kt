// MainActivity.kt
package kr.hisec.hansei.myshieldon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kr.hisec.hansei.myshieldon.ui.MainScreen
import kr.hisec.hansei.myshieldon.ui.ResultScreen
import kr.hisec.hansei.myshieldon.ui.theme.MyShieldOnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyShieldOnTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable("main") {
                        MainScreen(onStartScanClick = {
                            val isRooted = RootCheckUtils.isDeviceRooted()
                            val rootMsg = if (isRooted) "경고: 루팅됨!" else "안전: 루팅 안됨!"

                            val nonStoreApps = AppInfoUtils.getNonStoreInstalledApps(this@MainActivity)
                            val nonStoreMsg = if (nonStoreApps.isNotEmpty())
                                "스토어 외 설치앱: ${nonStoreApps.size}개 발견됨"
                            else "스토어 외 설치 앱 없음"

                            navController.navigate("result/${rootMsg}/${nonStoreMsg}")
                        })
                    }
                    composable(
                        route = "result/{root}/{nonstore}",
                        arguments = listOf(
                            navArgument("root") { type = NavType.StringType },
                            navArgument("nonstore") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val root = backStackEntry.arguments?.getString("root") ?: ""
                        val nonstore = backStackEntry.arguments?.getString("nonstore") ?: ""
                        ResultScreen(rootMessage = root, nonStoreAppMessage = nonstore) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}
