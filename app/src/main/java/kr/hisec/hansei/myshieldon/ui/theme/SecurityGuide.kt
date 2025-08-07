package kr.hisec.hansei.myshieldon.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.hisec.hansei.myshieldon.R
import kr.hisec.hansei.myshieldon.ScanUiState
import kr.hisec.hansei.myshieldon.SecurityIssue

@Composable
fun SecurityGuide(scanResult: ScanUiState.Success) {
    val pixelFont = FontFamily(Font(R.font.neodgm))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEBD3C3))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("보안 취약점 안내", fontFamily = pixelFont, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(40.dp))
        if (scanResult.isRooted) {
            IssueBox(
                title = "루팅 감지",
                description = "루팅된 스마트폰은 앱이나 악성코드가 쉅게 관리자 권한으로 동작할 수 있습니다..",
                solution = "인터넷 뱅킹, 정부24 등의 보안을 필요로 하는 앱은 루팅된 기기에서는 실행되지 않을 수 있습니다. \n해결하려면 공장 초기화를 하거나 서비스 센터에 의뢰하는 것을 권장합니다.\n" +
                        "→ [설정 > 일반 > 초기화 > 모든 데이터 초기화] 실행 전 백업 필수",
                boxColor = Color(0xFFEF3B3B)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (scanResult.backgroundOverUsageCount > 0) {
            IssueBox(
                title = "백그라운드 과다 사용",
                description = "화면을 꺼도 계속 작동하는 앱은 배터리를 소모하고 위치 추적·정보 수집을 할 수 있어요.",
                solution = "[설정 > 배터리 또는 디바이스 케어]에서 백그라운드 사용 앱을 확인하세요.\n자주 사용하지 않는 앱은 ‘절전’ 또는 ‘앱 중지’ 처리하세요.\n활동 시간 긴 알 수 없는 앱은 삭제를 고려하세요.",
                boxColor = Color(0xFFEF3B3B)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        val issues = scanResult.detectedApps.flatMap { it.issues }

        if (issues.any { it is SecurityIssue.TamperedSignature }) {
            IssueBox(
                title = "설치 앱의 서명 검사",
                description = "정상적인 앱은 제조사의 공식 서명 값을 가지고 있습니다.\n 올바른 서명 값이 아니면 위장된 악성 앱일 가능성이 큽니다.",
                solution = "해당 앱을 삭제하고 공식 앱 스토어에서 다시 설치하세요.\n 공식 스토어에 없는 앱이라면 즉시 삭제를 권장합니다.",
                boxColor = Color(0xFFEF3B3B)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (issues.any { it is SecurityIssue.NonStoreInstallation }) {
            IssueBox(
                title = "비공식 스토어 설치 앱 탐지",
                description = "Google Play 등 공식 앱 스토어를 통해 설치되지 않은 앱은 위험할 수 있어요.",
                solution = "[설정 > 앱 > (앱 이름)]에서 설치 정보를 확인해 보세요. \n 의심 가는 앱은 삭제를 권장합니다. \n 추후 설치 방지를 위해 [설정 > 보안 > 알 수 없는 출처 허용]을 꺼두세요.",
                boxColor = Color(0xFFEF3B3B)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (issues.any { it is SecurityIssue.ApkInDownloadFolder }) {
            IssueBox(
                title = "다운로드 위치에 앱 설치 파일 존재",
                description = "다운로드 폴더에 남아 있는 앱 설치(.apk) 파일은 공식 앱 스토어에서 배포하지 않은 앱으로 안전하다고 볼 수 없습니다.",
                solution = "[내 파일 > 다운로드] 폴더에서 확장자가 apk 파일을 확인 후 삭제하세요. \n apk 파일은 실행하지 마세요.",
                boxColor = Color(0xFFEF3B3B)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (issues.any { it is SecurityIssue.DangerousPermissions }) {
            IssueBox(
                title = "권한 요청 과다 앱 탐지",
                description = "문자, 연락처, 위치, 카메라 접근 등 민감한 권한을 과도하게 요구하는 앱은 스파이웨어일 수 있습니다",
                solution = "[설정 > 앱 > 권한 관리자]에서 위험 권한을 가진 앱 목록을 확인하세요.\n 낯선 앱이 민감 권한을 갖고 있다면 삭제하거나 권한을 차단해주세요.",
                boxColor = Color(0xFFEF3B3B)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (issues.any { it is SecurityIssue.OsSecurityPatchOutdated }) {
            IssueBox(
                title = "운영체제 보안 패치 상태",
                description = "보안 패치가 오래되었다는 것은 이미 알려진 해킹 취약점이 고쳐지지 않았다는 뜻입니다.",
                solution = "[설정 > 휴대전화 정보 > 소프트웨어 업데이트]에서 최신 패치를 확인하세요.\n 너무 오래된 기기는 패치가 중단됐을 수 있으니 가능하다면 최신 기기로 교체를 고려하세요.",
                boxColor = Color(0xFF3BEF41)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (issues.any { it is SecurityIssue.UnsafeUnknownSources }) {
            IssueBox(
                title = "알 수 없는 출처 앱 설치 권한 보유 앱",
                description = "다른 앱을 설치할 수 있는 권한을 가지는 앱은 위험합니다. 악성 앱이 이 권한을 악용하면 사용자 몰래 앱을 설치할 수 있습니다.",
                solution = "의심되는 앱을 삭제하거나 이 권한을 비활성화하세요.\n" +
                        "[설정 > 앱 > 특별한 앱 접근 > 알 수 없는 앱 설치] 메뉴에서 설치 권한을 꺼주세요.",
                boxColor = Color(0xFFEF3B3B)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (issues.any { it is SecurityIssue.DeveloperOptionsEnabled }) {
            IssueBox(
                title = "개발자 옵션 활성화 유무",
                description = "앱을 개발하기 위해서 개발자에게 제공하는 이 기능을 이용해 스마트폰의 정보에 접근하거나 악성 앱을 설치할 수 있습니다.",
                solution = "[설정 > 시스템 > 개발자 옵션]에서 개발자 옵션을 꺼주세요. \n당신이 앱 개발자가 아니라면 비활성화 상태로 유지하세요.",
                boxColor = Color(0xFFEF3B3B)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (issues.any { it is SecurityIssue.AdbModeEnabled }) {
            IssueBox(
                title = "ADB 모드 활성화",
                description = "ADB 모드는 USB를 통해 시스템 접근을 허용하는 위험한 개발자 기능입니다.",
                solution = "[설정 > 시스템 > 개발자 옵션]에서 ADB 디버깅을 꺼주세요.\n불필요한 경우 항상 꺼두는 것이 좋습니다.",
                boxColor = Color(0xFFEF3B3B)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


@Composable
fun IssueBox(title: String, description: String, solution: String, boxColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, boxColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(boxColor)
                .padding(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = solution,
                    fontSize = 14.sp,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}