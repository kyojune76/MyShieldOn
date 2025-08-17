package kr.hisec.hansei.myshieldon.ui.theme

import android.os.Build
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.decode.GifDecoder
import coil.request.ImageRequest
import kr.hisec.hansei.myshieldon.R

@Composable
fun LoadingScreen(onSettingsClick: () -> Unit) {
    val pixelFont = FontFamily(Font(R.font.neodgm))

    val decoderFactory = if (Build.VERSION.SDK_INT >= 28) {
        ImageDecoderDecoder.Factory()
    } else {
        GifDecoder.Factory()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEBD3C3))
    ) {
        //  상단 로고와 텍스트 (원래대로 복구)
        Row(
            modifier = Modifier
                .padding(start = 3.dp, top = 5.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_sword_dog),
                contentDescription = "로고",
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

        //  중앙 원형 박스 안에 걷는 애니메이션 (gif)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        spotColor = Color.Black.copy(alpha = 0.3f),
                        ambientColor = Color.Black.copy(alpha = 0.5f)
                    )
                    .background(color = Color(0xFFE4E0E1), shape = CircleShape)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.ic_mso_walk2)
                        .decoderFactory(decoderFactory)
                        .build(),
                    contentDescription = "로딩 애니메이션",
                    modifier = Modifier
                        .size(270.dp)
                        .align(Alignment.Center)
                )
            }
        }

        //  하단 문구 및 설정 아이콘
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "취약점 찾는중!",
                fontFamily = pixelFont,
                fontSize = 28.sp,
                color = Color(0xFF5C4330),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 100.dp)
            )


        }
    }
}
