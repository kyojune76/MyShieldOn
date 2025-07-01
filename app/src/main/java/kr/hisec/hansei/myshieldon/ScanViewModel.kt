package kr.hisec.hansei.myshieldon

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ScanUiState {
    object Idle : ScanUiState()
    object Scanning : ScanUiState()
    data class Success(val detectedApps: List<DetectedApp>) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

class ScanViewModel(private val application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState

    // ScanViewModel.kt 파일의 startSecurityScan 함수

    fun startSecurityScan() {
        if (_uiState.value is ScanUiState.Scanning) return
        viewModelScope.launch {
            _uiState.value = ScanUiState.Scanning
            try {
                val securityConfig = SecurityConfig(
                    permissionThreshold = 3,
                    dangerousPermissions = setOf("android.permission.READ_CONTACTS", "android.permission.SEND_SMS"),

                    // 공식 서명 주기적으로 추가하기
                    officialSignatures = mapOf(
                        // 토스
                        "viva.republica.toss" to "45dbecb990a1374558d80a6c65a9de465f960f097df9d12ef913382cecbde05c",

                        "com.kakaopay.app" to "2b06cc3d47782d7c497c07f17cb5f859cd6bbcb66829f3e67b96b7a44820d2ce",

                        "com.kakao.talk" to "2b06cc3d47782d7c497c07f17cb5f859cd6bbcb66829f3e67b96b7a44820d2ce",

                        "com.iloen.melon" to "87305ff08ba156982ade6aa3f3ee749a1852e9f86cc46667c33db0b75c1c391d",

                        "com.spotify.music" to "6505b181933344f93893d586e399b94616183f04349cb572a9e81a3335e28ffd"
                    )
                )
                // --------------------------------

                val scanner = SecurityScanner(application, securityConfig)
                val detectedApps = scanner.scanInstalledApps()
                _uiState.value = ScanUiState.Success(detectedApps)
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error("스캔 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    fun returnToIdle() {
        _uiState.value = ScanUiState.Idle
    }
}