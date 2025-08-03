package kr.hisec.hansei.myshieldon.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kr.hisec.hansei.myshieldon.DetectedApp
import kr.hisec.hansei.myshieldon.ScanViewModel
import kr.hisec.hansei.myshieldon.ScanUiState
import kr.hisec.hansei.myshieldon.SecurityIssue
import kr.hisec.hansei.myshieldon.ui.theme.WarningRed
import kr.hisec.hansei.myshieldon.ui.theme.SuccessGreen
import kotlin.reflect.KClass

@Composable
fun ResultScreen(
    viewModel: ScanViewModel,
    onGoBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState is ScanUiState.Success) {
        val allDetected = (uiState as ScanUiState.Success).detectedApps.filter { it.issues.isNotEmpty() }

        //  이슈 한 번에 처리해서 O(n) 캐싱
        val issueTypes = allDetected.flatMap { it.issues }
        val issuesByType: Map<KClass<out SecurityIssue>, List<SecurityIssue>> = issueTypes.groupBy { it::class }

        val hasIssue = { type: KClass<out SecurityIssue> -> issuesByType.containsKey(type) }

        val hasRoot = issueTypes.any { it.toString().contains("Root") }  // 루팅은 따로 예외

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            val issueGroups = listOf(
                Triple("개발자 옵션 메뉴", hasIssue(SecurityIssue.DeveloperOptionsEnabled::class), Pair("개발자 옵션 메뉴가 켜져 있습니다.", "개발자 옵션 메뉴가 꺼져 있습니다.")),
                Triple("ADB 모드", hasIssue(SecurityIssue.AdbModeEnabled::class), Pair("ADB 모드가 켜져 있습니다.", "ADB 모드가 꺼져 있습니다.")),
                Triple("다운로드 폴더 APK 파일", hasIssue(SecurityIssue.ApkInDownloadFolder::class), Pair("다운로드 폴더에 APK 파일이 존재합니다.", "다운로드 폴더에 APK 파일이 없습니다.")),
                Triple("루팅 여부", hasRoot, Pair("루팅이 감지되었습니다.", "루팅이 감지되지 않았습니다.")),
                Triple("스토어 외 설치 앱", hasIssue(SecurityIssue.NonStoreInstallation::class), Pair("공식 앱스토어 외 설치된 앱이 있습니다.", "모든 앱이 공식 스토어를 통해 설치되었습니다.")),
                Triple("알 수 없는 출처 허용 앱", hasIssue(SecurityIssue.UnsafeUnknownSources::class), Pair("일부 앱이 알 수 없는 출처의 앱 설치를 허용하고 있습니다.", "알 수 없는 출처 허용 앱이 없습니다.")),
                Triple("위험 권한 과다 보유 앱", hasIssue(SecurityIssue.DangerousPermissions::class), Pair("위험 권한을 과도하게 사용하는 앱이 감지되었습니다.", "위험 권한을 과도하게 사용하는 앱이 없습니다.")),
                Triple("서명 위조 앱", hasIssue(SecurityIssue.TamperedSignature::class), Pair("공식 서명과 다른 앱이 감지되었습니다.", "서명이 위조된 앱은 감지되지 않았습니다.")),
                Triple("보안 패치 상태", hasIssue(SecurityIssue.OsSecurityPatchOutdated::class), Pair("운영체제 보안 패치가 오래되었습니다.", "운영체제 보안 패치가 최신 상태입니다."))
            )

            issueGroups.forEach { (title, isRisk, messages) ->
                val (warn, safe) = messages
                ResultBox(
                    title = title,
                    description = if (isRisk) warn else safe,
                    boxColor = if (isRisk) WarningRed else SuccessGreen
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    } else {
        Text("점검 결과를 불러오는 중입니다.")
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
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = Color.White)
        }
    }
}