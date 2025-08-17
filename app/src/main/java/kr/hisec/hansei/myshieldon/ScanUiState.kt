package kr.hisec.hansei.myshieldon


 //보안 점검 상태를 나타내는 UI 상태 클래스

sealed class ScanUiState {
    object Idle : ScanUiState()
    object Scanning : ScanUiState()

    /**
     * 점검 성공 시 사용되는 상태
     * @param isRooted 기기 루팅 여부
     * @param nonStoreApps 스토어 외 설치된 앱들의 패키지명 리스트
     * @param detectedApps 보안 위협 앱 리스트
     */
    data class Success(
        val isRooted: Boolean,
        val nonStoreApps: List<String>,
        val detectedApps: List<DetectedApp>,
        val backgroundOverUsageCount: Int,
        val isDeveloperOptionsMenuEnabled: Boolean,
        val isDeveloperOptionsEnabled: Boolean,
        val isUnknownSourcesAllowed: Boolean,
        val allowedUnknownSourceApps: List<String> = emptyList(),

    ) : ScanUiState()

    data class Error(val message: String) : ScanUiState()
}
