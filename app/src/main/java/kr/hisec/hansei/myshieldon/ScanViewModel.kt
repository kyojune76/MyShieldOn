package kr.hisec.hansei.myshieldon

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState

    fun startSecurityScan() {
        if (_uiState.value is ScanUiState.Scanning) return

        viewModelScope.launch {
            _uiState.value = ScanUiState.Scanning
            try {
                // 1. 루팅 여부
                val isRooted = RootCheckUtils.isDeviceRooted()

                // 2. 스토어 외 설치 앱
                val nonStoreApps = AppInfoUtils.getNonStoreInstalledApps(getApplication())

                // 3. 보안 위협 앱 스캔
                val config = SecurityConfig(
                    permissionThreshold = 3,
                    dangerousPermissions = setOf(
                        "android.permission.READ_CONTACTS",
                        "android.permission.SEND_SMS"
                    ),
                    officialSignatures = mapOf(
                        "viva.republica.toss" to "45dbecb990a1374558d80a6c65a9de465f960f097df9d12ef913382cecbde05c",
                        "com.kakaopay.app" to "2b06cc3d47782d7c497c07f17cb5f859cd6bbcb66829f3e67b96b7a44820d2ce",
                        "com.kakao.talk" to "2b06cc3d47782d7c497c07f17cb5f859cd6bbcb66829f3e67b96b7a44820d2ce",
                        "com.iloen.melon" to "87305ff08ba156982ade6aa3f3ee749a1852e9f86cc46667c33db0b75c1c391d",
                        "com.spotify.music" to "6505b181933344f93893d586e399b94616183f04349cb572a9e81a3335e28ffd"
                    )
                )

                val scanner = SecurityScanner(getApplication(), config)
                val detectedApps = scanner.scanInstalledApps().toMutableList()
                val allowedUnknownApps = scanner.getAllowedUnknownSourceApps()

                // ① 개발자 옵션(ADB) 메뉴 활성화(잠재 위험)
                if (scanner.isDeveloperOptionsMenuEnabled() && !scanner.isDeveloperOptionsEnabled()) {
                    detectedApps += DetectedApp(
                        appName    = "설정: 개발자 옵션(ADB)",
                        packageName= "android.settings",
                        issues     = listOf(SecurityIssue.DeveloperOptionsAvailable)
                    )
                }
                // ② ADB 모드 비활성화
                if (!scanner.isDeveloperOptionsEnabled()) {
                    detectedApps += DetectedApp(
                        appName    = "개발자 옵션(ADB)",
                        packageName= "android.settings",
                        issues     = listOf(SecurityIssue.DeveloperModeDisabled)
                    )
                }
                // ③ 알 수 없는 출처 허용 여부
                if (scanner.isUnknownSourcesAllowed()) {
                    detectedApps += DetectedApp(
                        appName    = "설정: 알 수 없는 출처",
                        packageName= "android.settings",
                        issues     = listOf(SecurityIssue.UnsafeUnknownSources)
                    )
                }
                // 4. 백그라운드 과다 앱 수
                val heavyCount = UsageStatsManagerUtil
                    .getHeavyUsageApps(getApplication())
                    .size

                // 5. 운영체제 보안 패치 확인
                val patchDate = getSecurityPatchDate()
                val isPatchOld = isPatchOutdated(patchDate)
                if (isPatchOld) {
                    detectedApps.add(
                        DetectedApp(
                            appName = "운영체제",
                            packageName = "android",
                            issues = listOf(
                                SecurityIssue.OsSecurityPatchOutdated(patchDate ?: "Unknown")
                            )
                        )
                    )
                }

                // 최종 결과 전달
                _uiState.value = ScanUiState.Success(
                    isRooted = isRooted,
                    nonStoreApps = nonStoreApps,
                    detectedApps = detectedApps,
                    backgroundOverUsageCount = heavyCount,
                    isDeveloperOptionsMenuEnabled = scanner.isDeveloperOptionsMenuEnabled(),
                    isDeveloperOptionsEnabled     = scanner.isDeveloperOptionsEnabled(),
                    isUnknownSourcesAllowed       = scanner.isUnknownSourcesAllowed(),
                    allowedUnknownSourceApps = allowedUnknownApps
                )
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error("스캔 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    private fun getSecurityPatchDate(): String? {
        return try {
            Build.VERSION.SECURITY_PATCH
        } catch (e: Exception) {
            null
        }
    }

    private fun isPatchOutdated(dateStr: String?): Boolean {
        if (dateStr == null) return true
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val patchDate = sdf.parse(dateStr) ?: return true
            val today = Date()
            val diff = today.time - patchDate.time
            val thirtyDaysMillis = 30L * 24 * 60 * 60 * 1000
            diff > thirtyDaysMillis
        } catch (e: Exception) {
            true
        }
    }

    fun returnToIdle() {
        _uiState.value = ScanUiState.Idle
    }
}
