// EntryRouter.kt
package kr.hisec.hansei.myshieldon

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kr.hisec.hansei.myshieldon.ui.theme.MainScreen

@Composable
fun EntryRouter() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("entry_prefs", Context.MODE_PRIVATE)
    val now = System.currentTimeMillis()
    val lastEntry = prefs.getLong("last_entry_time", 0L)

    val showIntro = lastEntry == 0L || now - lastEntry < 86_400_000

    LaunchedEffect(Unit) {
        prefs.edit().putLong("last_entry_time", now).apply()
    }

    if (showIntro) {
        MainScreen(onStartScanClick = { /* TODO: 연결 */ })
    } else {
        MainGateScreen()
    }
}
