// EntryRouter.kt
package kr.hisec.hansei.myshieldon

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kr.hisec.hansei.myshieldon.ui.theme.MainScreen


@Composable
fun EntryRouter(navController: NavController) {
    val context = LocalContext.current

    MainGateScreen(
        onDone = {
            navController.navigate("main") {
                popUpTo("entry") { inclusive = true } // 뒤로가기 시 대문으로 안 돌아가게
            }
        }
    )
}