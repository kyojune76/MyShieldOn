package kr.hisec.hansei.myshieldon

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kr.hisec.hansei.myshieldon.R
import java.util.concurrent.TimeUnit
import androidx.compose.ui.platform.LocalContext // ⬅️ LocalContext import

@Composable
fun MainGateScreen(
    onDone: () -> Unit // ⬅️ Context 인자 제거
) {
    val pixelFont = FontFamily(Font(R.font.neodgm))

    val context = LocalContext.current // ⬅️ LocalContext를 사용하여 Context 가져오기

    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val lastEntry = sharedPreferences.getLong("last_login_time", 0L)

    val timeText =
        if (lastEntry == 0L) "처음 접속했습니다"
        else {
            val min = (System.currentTimeMillis() - lastEntry) / (1000 * 60)
            if (min < 60) "보안 탐색을 한시간 이내에 떠났습니다"
            else "보안 탐색을 떠나지 않으신지 \n ${min / 60}시간 지났습니다"
        }

    // ⏳ 대문 노출 시간만 책임
    LaunchedEffect(Unit) {
        delay(5000)      // 3~5초 원하는 값
        onDone()         // 저장+네비는 EntryRouter에서
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEBD3C3))
    ) {
        // 상단 로고와 텍스트
        Row(
            modifier = Modifier
                .padding(start = 3.dp, top = 5.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_sword_dog),
                contentDescription = null,
                modifier = Modifier.size(45.dp)
            )
            Spacer(modifier = Modifier.width(1.dp))
            Text(
                text = "MY SHIELD ON",
                fontFamily = pixelFont,
                fontSize = 45.sp,
                color = Color(0xFF5C4330)
            )
        }

        // 중앙 콘텐츠를 담을 Box
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_main_gate),
                    contentDescription = null,
                    modifier = Modifier
                        .size(280.dp)
                )
                Text(
                    text = "마지막 보안탐색 :",
                    fontFamily = pixelFont,
                    fontSize = 20.sp,
                    color = Color(0xFF5C4330),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top=30.dp,bottom = 0.dp)
                )
                Text(
                    text = timeText,
                    fontFamily = pixelFont,
                    fontSize = 20.sp,
                    color = Color(0xFF5C4330),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top= 2.dp)
                )
            }
        }
    }
}