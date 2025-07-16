package kr.hisec.hansei.myshieldon.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items      // ← items 확장 함수
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
import kr.hisec.hansei.myshieldon.*           // ScanUiState, DetectedApp, SecurityIssue 등

private val SuccessGreen = Color(0xFF4CAF50)
private val WarningRed   = Color(0xFFD32F2F)

@Composable
fun ResultScreen(
    viewModel: ScanViewModel,
    onGoBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val success = uiState as? ScanUiState.Success

    var showAllowedAppsDialog by remember { mutableStateOf(false) }

    // 요약 박스 데이터
    val isRooted      = success?.isRooted ?: false
    val nonStoreCount = success?.nonStoreApps?.size ?: 0
    val heavyCount    = success?.backgroundOverUsageCount ?: 0

    // 카드 리스트 데이터
    val allDetected = success?.detectedApps.orEmpty()
    val osIssues    = allDetected.filter { it.packageName == "android" }
    val otherIssues = allDetected.filter { it.packageName != "android" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ─── 요약 박스 + 버튼 + 에러 ───
        item {
            Spacer(Modifier.height(60.dp))
            Text("점검 완료", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(24.dp))

            ResultBox(
                "루팅여부",
                if (isRooted) "루팅된 디바이스입니다." else "루팅되지 않았습니다.",
                if (isRooted) WarningRed else SuccessGreen
            )
            Spacer(Modifier.height(16.dp))

            ResultBox(
                "스토어 외 설치 앱",
                if (nonStoreCount > 0) "총 $nonStoreCount 개 앱이 스토어 외에서 설치됨" else "스토어 외 앱이 없습니다.",
                if (nonStoreCount > 0) WarningRed else SuccessGreen
            )
            Spacer(Modifier.height(16.dp))

            ResultBox(
                "백그라운드 과다 사용 앱",
                if (heavyCount > 0) "총 $heavyCount 개 앱이 과다 사용됨" else "장시간 사용된 앱이 없습니다.",
                if (heavyCount > 0) WarningRed else SuccessGreen
            )
            Spacer(Modifier.height(16.dp))

            ResultBox(
                "ADB 모드",
                if (success?.isDeveloperOptionsEnabled == true) "ADB 모드가 켜져 있습니다."
                else "ADB 모드가 꺼져 있습니다.",
                if (success?.isDeveloperOptionsEnabled == true) WarningRed else SuccessGreen
            )
            Spacer(Modifier.height(16.dp))

            ResultBox(
                "비공식 출처 설치",
                if (success?.isUnknownSourcesAllowed == true) "허용되어 있습니다."
                else "허용되어 있지 않습니다.",
                if (success?.isUnknownSourcesAllowed == true) WarningRed else SuccessGreen
            )

            if (success?.isUnknownSourcesAllowed == true) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(onClick = { showAllowedAppsDialog = true }) {
                        Text("허용된 앱 보기", fontSize = 12.sp)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))

            if (uiState is ScanUiState.Error) {
                Text(
                    "오류 발생: ${(uiState as ScanUiState.Error).message}",
                    color = WarningRed
                )
                Spacer(Modifier.height(24.dp))
            }
        }

        // ─── 카드 리스트 ───
        items(osIssues + otherIssues) { app ->
            DetectedAppCard(app)   // ← 여기가 정의돼 있어야 합니다
        }

        // ─── 다시 점검하기 버튼 ───
        item {
            Spacer(Modifier.height(24.dp))
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

    // ─── 허용된 앱 다이얼로그 ───
    if (showAllowedAppsDialog) {
        AlertDialog(
            onDismissRequest = { showAllowedAppsDialog = false },
            title = { Text("허용된 비공식 출처 앱") },
            text = {
                val list = success?.allowedUnknownSourceApps.orEmpty()
                if (list.isEmpty()) {
                    Text("허용된 앱이 없습니다.")
                } else {
                    LazyColumn {
                        items(list) { pkg ->
                            Text("• $pkg", fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAllowedAppsDialog = false }) {
                    Text("닫기")
                }
            }
        )
    }
}

@Composable
private fun ResultBox(
    title: String,
    content: String,
    boxColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(boxColor.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        Text(content, fontSize = 13.sp)
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
            Spacer(Modifier.height(8.dp))
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
                    is SecurityIssue.DeveloperOptionsAvailable ->
                        Text("  - ⚠️ 개발자 옵션 메뉴가 활성화되어 있습니다.", color = WarningRed)
                    is SecurityIssue.DeveloperModeDisabled ->
                        Text("  - ❌ ADB 모드(개발자 옵션)가 비활성화되어 있습니다.", color = WarningRed)
                    is SecurityIssue.UnsafeUnknownSources ->
                        Text("  - ⚠️ 비공식 출처 앱 설치가 활성화되어 있습니다.", color = WarningRed)
                }
            }
        }
    }
}
