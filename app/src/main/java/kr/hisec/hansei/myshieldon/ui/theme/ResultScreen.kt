package kr.hisec.hansei.myshieldon.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.hisec.hansei.myshieldon.*
import androidx.compose.ui.zIndex
import kr.hisec.hansei.myshieldon.R

@Composable
fun ResultScreen(
    viewModel: ScanViewModel,
    onGoBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pixelFont = FontFamily(Font(R.font.neodgm))
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val showDetails = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEBD3C3))
    ) {
        // 상단 고정 로고
        Row(
            modifier = Modifier
                .padding(start = 8.dp, top = 10.dp)
                .align(Alignment.TopStart)
                .zIndex(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_sword_dog),
                contentDescription = null,
                modifier = Modifier.size(45.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "My Shield On",
                fontFamily = pixelFont,
                fontSize = 45.sp,
                color = Color(0xFF5C4330)
            )
        }

        if (uiState is ScanUiState.Success) {
            val successState = uiState as ScanUiState.Success
            val issueTypes = mutableSetOf<String>()

            val groupedIssues = mutableMapOf<String, MutableList<String>>()

            successState.detectedApps.forEach { app ->
                app.issues.forEach { issue ->
                    val type = when (issue) {
                        is SecurityIssue.DeveloperOptionsEnabled -> "개발자 옵션 메뉴"
                        is SecurityIssue.AdbModeEnabled -> "ADB 모드"
                        is SecurityIssue.UnsafeUnknownSources -> "알 수 없는 출처 허용 앱"
                        is SecurityIssue.NonStoreInstallation -> "스토어 외 앱 설치"
                        is SecurityIssue.DangerousPermissions -> "위험 권한 과다 보유"
                        is SecurityIssue.TamperedSignature -> "서명 위조 앱"
                        is SecurityIssue.ApkInDownloadFolder -> "다운로드 경로 APK"
                        is SecurityIssue.OsSecurityPatchOutdated -> "보안 패치 오래됨"
                    }
                    val detail = when (issue) {
                        is SecurityIssue.DangerousPermissions -> "위험 권한: ${issue.permissions.joinToString()}"
                        is SecurityIssue.ApkInDownloadFolder -> "APK 파일: ${issue.apkFiles.joinToString()}"
                        is SecurityIssue.OsSecurityPatchOutdated -> "패치 날짜: ${issue.patchDate}"
                        else -> app.appName
                    }
                    groupedIssues.getOrPut(type) { mutableListOf() }.add(detail)
                    issueTypes += type
                }
            }

            if (successState.isRooted) issueTypes += "루팅 감지"
            if (successState.backgroundOverUsageCount > 0) issueTypes += "백그라운드 과다 사용"

            val totalIssues = issueTypes.size

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(screenHeight / 2 - 125.dp))

                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Color(0xFFE4E0E1), CircleShape)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_hunting_dog),
                        contentDescription = null,
                        modifier = Modifier.size(220.dp).align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                ResultBox(
                    title = "취약점 요약",
                    description = "총 ${totalIssues}개의 항목에서 취약점이 발견되었습니다.",
                    boxColor = Color(0xFFFF5252)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showDetails.value = !showDetails.value },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26C6DA))
                ) {
                    Text(text = if (showDetails.value) "간단히 보기" else "보안 길잡이", fontFamily = pixelFont)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (showDetails.value) {
                    if (successState.isRooted) {
                        ResultBox("루팅 감지", "이 기기는 루팅된 상태입니다.", Color(0xFFEF9A9A))
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    if (successState.backgroundOverUsageCount > 0) {
                        ResultBox(
                            "백그라운드 과다 사용",
                            "${successState.backgroundOverUsageCount}개의 앱이 과도하게 실행 중입니다.",
                            Color(0xFFEF9A9A)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    groupedIssues.forEach { (title, items) ->
                        ResultBox(
                            title = "$title (${items.size}개)",
                            description = items.joinToString("\n"),
                            boxColor = Color(0xFFEF9A9A)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onGoBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("다시 점검", fontFamily = pixelFont)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            Text("점검 결과를 불러오는 중입니다.", modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun ResultBox(title: String, description: String, boxColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = boxColor,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = Color.White)
        }
    }
}
