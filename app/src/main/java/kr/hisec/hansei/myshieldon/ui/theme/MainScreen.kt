package kr.hisec.hansei.myshieldon.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import kr.hisec.hansei.myshieldon.R

@Composable
fun MainScreen(onStartScanClick: () -> Unit) {
    val pixelFont = FontFamily(Font(R.font.neodgm))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEBD3C3)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 앱 타이틀
        Text(
            text = "My Shield On",
            fontFamily = pixelFont,
            fontSize = 24.sp,
            color = Color(0xFF5C4330),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 강아지 + 방패 이미지
        Image(
            painter = painterResource(R.drawable.ic_dog_shield), // 본인 이미지에 맞게 교체
            contentDescription = null,
            modifier = Modifier
                .size(160.dp)
                .padding(bottom = 16.dp)
        )

        // 안내 문구
        Text(
            text = "클릭하세요!",
            fontFamily = pixelFont,
            fontSize = 16.sp,
            color = Color(0xFF5C4330),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 설정 아이콘 (클릭 시 점검 시작)
        Image(
            painter = painterResource(R.drawable.ic_settings), // 설정 아이콘 이미지 교체
            contentDescription = "설정",
            modifier = Modifier
                .size(36.dp)
                .clickable { onStartScanClick() }
        )
    }
}
