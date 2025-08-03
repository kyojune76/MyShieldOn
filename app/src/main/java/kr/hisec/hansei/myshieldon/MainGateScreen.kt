package kr.hisec.hansei.myshieldon

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

@Composable
fun MainGateScreen(onDone: () -> Unit) {
    val pixelFont = FontFamily(Font(R.font.neodgm))

    LaunchedEffect(Unit) {
        delay(5000)
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEBD3C3))
    ) {
        // 상단 강아지 + 텍스트 Row
        Row(
            modifier = Modifier
                .padding(start = 3.dp, top = 5.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_sword_dog),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "My Shield On",
                fontFamily = pixelFont,
                fontSize = 40.sp,
                color = Color(0xFF5C4330)
            )
        }

        // 중앙 콘텐츠
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_main_gate),
                contentDescription = null,
                modifier = Modifier
                    .size(280.dp)
                    .padding(bottom = 50.dp)
            )

            Text(
                text = "보안 탐색을 떠나지 않으신지",
                fontFamily = pixelFont,
                fontSize = 25.sp,
                color = Color(0xFF5C4330),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 0.dp)
            )

            Text(
                text = "(~)시간 지났습니다",
                fontFamily = pixelFont,
                fontSize = 25.sp,
                color = Color(0xFF5C4330),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}
