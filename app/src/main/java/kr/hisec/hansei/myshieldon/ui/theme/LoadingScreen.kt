package kr.hisec.hansei.myshieldon.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.hisec.hansei.myshieldon.R

@Composable
fun LoadingScreen(onSettingsClick: () -> Unit) {
    val pixelFont = FontFamily(Font(R.font.neodgm))
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEBD3C3))
    ) {
        // 상단 로고와 텍스트 (MainScreen과 동일)
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
                text = "My Shield On",
                fontFamily = pixelFont,
                fontSize = 45.sp,
                color = Color(0xFF5C4330)
            )
        }

        // 중앙 콘텐츠 (다른 강아지 이미지, 문구, 설정 아이콘)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 다른 강아지 이미지 (원형 배경 포함)
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        spotColor = Color.Black.copy(alpha = 0.5f),
                        ambientColor = Color.Black.copy(alpha = 0.5f)
                    )
                    .background(color = Color(0xFFE4E0E1), shape = CircleShape)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_sword_dog),
                    contentDescription = null,
                    modifier = Modifier
                        .size(220.dp)
                        .align(Alignment.Center)
                )
            }
        }

        // "취약점 탐색중!" 와 설정 아이콘을 별도의 Column으로 배치
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 안내 문구 (변경됨)
            Text(
                text = "취약점 찾는중!",
                fontFamily = pixelFont,
                fontSize = 20.sp,
                color = Color(0xFF5C4330),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // 설정 아이콘 (동일)
            Image(
                painter = painterResource(R.drawable.ic_settings),
                contentDescription = "설정",
                modifier = Modifier
                    .size(38.dp)

            )
        }
    }
}