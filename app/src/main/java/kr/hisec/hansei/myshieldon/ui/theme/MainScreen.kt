package kr.hisec.hansei.myshieldon.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.hisec.hansei.myshieldon.R

@Composable
fun MainScreen(onStartScanClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("MyShieldOn", fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = Color(0xFF1E40AF), textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Image(painterResource(R.drawable.ic_shield), contentDescription = null,
            modifier = Modifier.size(100.dp).padding(bottom = 32.dp)
        )
        Button(onClick = onStartScanClick) { Text("점검 시작", fontSize = 18.sp) }
    }
}