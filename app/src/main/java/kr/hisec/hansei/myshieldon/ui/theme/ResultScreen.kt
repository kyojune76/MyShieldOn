package kr.hisec.hansei.myshieldon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResultScreen(rootMessage: String, nonStoreAppMessage: String , onBack: () -> Unit ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "점검결과",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E40AF),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        ResultBox(title = "루팅여부", content = rootMessage)
        Spacer(modifier = Modifier.height(16.dp))
        ResultBox(title = "스토어 외 설치 앱", content = nonStoreAppMessage)
    }
}

@Composable
fun ResultBox(title: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFCCDAFF), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = content, fontSize = 13.sp)
    }
}