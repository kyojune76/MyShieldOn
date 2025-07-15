package kr.hisec.hansei.myshieldon.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import kr.hisec.hansei.myshieldon.*

private val SuccessGreen = Color(0xFF4CAF50)
private val WarningRed = Color(0xFFD32F2F)

@Composable
fun ResultScreen(
    viewModel: ScanViewModel,
    onGoBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isRooted = (uiState as? ScanUiState.Success)?.isRooted ?: false
    val nonStoreCount = (uiState as? ScanUiState.Success)?.nonStoreApps?.size ?: 0
    val heavyCount = (uiState as? ScanUiState.Success)?.backgroundOverUsageCount ?: 0

    val allDetectedApps = (uiState as? ScanUiState.Success)?.detectedApps ?: emptyList()

    // ✅ 운영체제 감지 앱은 따로 빼서 제일 위로
    val osIssues = allDetectedApps.filter { it.packageName == "android" }
    val otherIssues = allDetectedApps.filter { it.packageName != "android" }
    val sortedApps = osIssues + otherIssues

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(60.dp))
            Text("점검 완료", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(24.dp))

            ResultBox(
                "루팅여부",
                if (isRooted) "루팅된 디바이스입니다." else "루팅되지 않았습니다.",
                if (isRooted) WarningRed else SuccessGreen
            )
            Spacer(modifier = Modifier.height(16.dp))
            ResultBox(
                "스토어 외 설치 앱",
                if (nonStoreCount > 0) "총 $nonStoreCount 개의 앱이 설치되어 있습니다." else "스토어 외 앱이 없습니다.",
                if (nonStoreCount > 0) WarningRed else SuccessGreen
            )
            Spacer(modifier = Modifier.height(16.dp))
            ResultBox(
                "백그라운드 과다 사용 앱",
                if (heavyCount > 0) "총 $heavyCount 개의 앱이 과다 사용되었습니다." else "장시간 사용된 앱이 없습니다.",
                if (heavyCount > 0) WarningRed else SuccessGreen
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState is ScanUiState.Error) {
                Text(
                    "오류 발생: ${(uiState as ScanUiState.Error).message}",
                    color = WarningRed
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // ✅ 운영체제 관련 이슈가 먼저 뜨도록 정렬
        items(sortedApps) { app ->
            DetectedAppCard(app)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onGoBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("다시 점검하기", fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun ResultBox(title: String, content: String, boxColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(boxColor.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
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
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "${app.appName} (${app.packageName})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            app.issues.forEach { issue ->
                when (issue) {
                    is SecurityIssue.DangerousPermissions ->
                        Text("  - 과도한 위험 권한 요청 (${issue.permissions.size}개)", color = WarningRed)

                    is SecurityIssue.TamperedSignature ->
                        Text("  - ★★★ 서명 변조 의심 ★★★", color = WarningRed, fontWeight = FontWeight.Bold)

                    is SecurityIssue.NonStoreInstallation ->
                        Text("  - 공식 마켓이 아닌 경로로 설치 확인됨", color = WarningRed)

                    is SecurityIssue.ApkInDownloadFolder ->
                        Text("  - 다운로드 폴더에 APK 잔존 ${issue.apkFiles.size}개 발견", color = WarningRed)

                    is SecurityIssue.InstalledFromDownloadedApk ->
                        Text("  - APK 파일에서 직접 설치 감지됨", color = WarningRed)

                    is SecurityIssue.OsSecurityPatchOutdated ->
                        Text("  - 운영체제 보안 패치 오래됨 (${issue.patchDate})", color = WarningRed)
                }
            }
        }
    }
}
