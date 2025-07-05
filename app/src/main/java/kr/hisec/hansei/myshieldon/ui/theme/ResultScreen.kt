package kr.hisec.hansei.myshieldon.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hisec.hansei.myshieldon.ScanUiState
import kr.hisec.hansei.myshieldon.ScanViewModel
import kr.hisec.hansei.myshieldon.DetectedApp
import kr.hisec.hansei.myshieldon.SecurityIssue

private val SuccessGreen = Color(0xFF4CAF50)
private val WarningRed   = Color(0xFFD32F2F)

@Composable
fun ResultScreen(
    viewModel: ScanViewModel,
    onGoBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text("점검 완료", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))

        val isRooted = (uiState as? ScanUiState.Success)?.isRooted ?: false
        val nonStoreCount = (uiState as? ScanUiState.Success)?.nonStoreApps?.size ?: 0

        ResultBox("루팅여부", if (isRooted) "루팅된 디바이스입니다." else "루팅되지 않았습니다.")
        Spacer(modifier = Modifier.height(16.dp))
        ResultBox(
            "스토어 외 설치 앱",
            if (nonStoreCount > 0) "총 $nonStoreCount 개의 앱이 설치되어 있습니다." else "스토어 외 앱이 없습니다."
        )
        Spacer(modifier = Modifier.height(24.dp))

        when (uiState) {
            is ScanUiState.Success -> {
                val detected = (uiState as ScanUiState.Success).detectedApps
                if (detected.isNotEmpty()) {
                    Text("보안 위협 앱 목록", color = WarningRed, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(detected) { app -> DetectedAppCard(app) }
                    }
                }
            }
            is ScanUiState.Error -> Text("오류 발생: ${(uiState as ScanUiState.Error).message}", color = WarningRed)
            else -> Unit
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onGoBack, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("다시 점검하기", fontSize = 18.sp)
        }
    }
}

@Composable
private fun ResultBox(title: String, content: String) {
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

@Composable
private fun DetectedAppCard(app: DetectedApp) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${app.appName} (${app.packageName})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            app.issues.forEach { issue ->
                when (issue) {
                    is SecurityIssue.DangerousPermissions ->
                        Text("  - 과도한 위험 권한 보유 (${issue.permissions.size}개)", color = WarningRed)
                    is SecurityIssue.TamperedSignature ->
                        Text("  - ★★★ 서명 변조 의심 ★★★", color = WarningRed, fontWeight = FontWeight.Bold)
                    is SecurityIssue.NonStoreInstallation ->
                        Text("  - 비공식 경로로 설치됨", color = WarningRed)
                    is SecurityIssue.ApkInDownloadFolder ->
                        Text("  - 다운로드 폴더에 APK 파일 존재 (${issue.apkFiles.size}개)", color = WarningRed)
                }
            }
        }
    }
}
