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
    val prefs = context.getSharedPreferences("entry_prefs", Context.MODE_PRIVATE)

    // ✅ 분기 없이 무조건 대문부터
    MainGateScreen(
        onDone = {
            // 대문을 보고 넘어가는 '그 순간' 기록
            prefs.edit().putLong("last_entry_time", System.currentTimeMillis()).apply()
            navController.navigate("main") {
                popUpTo("entry") { inclusive = true }
            }
        }
    )
}