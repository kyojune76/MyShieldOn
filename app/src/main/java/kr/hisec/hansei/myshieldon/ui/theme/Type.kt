package kr.hisec.hansei.myshieldon.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import kr.hisec.hansei.myshieldon.R

// 픽셀 폰트 등록
val PixelFont = FontFamily(Font(R.font.neodgm))

// Typography 전체에 적용
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = PixelFont,
        fontSize = 30.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PixelFont,
        fontSize = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = PixelFont,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PixelFont,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = PixelFont,
        fontSize = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PixelFont,
        fontSize = 12.sp
    )
)
