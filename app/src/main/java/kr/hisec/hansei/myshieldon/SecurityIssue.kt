package kr.hisec.hansei.myshieldon

sealed class SecurityIssue {

    data class DangerousPermissions(val permissions: Set<String>) : SecurityIssue()
    object TamperedSignature : SecurityIssue()
    object NonStoreInstallation : SecurityIssue()
    object DeveloperOptionsAvailable : SecurityIssue()   // 개발자 옵션 메뉴 활성화(잠재 위험)
    object DeveloperModeDisabled    : SecurityIssue()   // ADB 모드 비활성화
    object UnsafeUnknownSources     : SecurityIssue()   // 알 수 없는 출처 허용
    // 기존: 파일명 기반
    data class ApkInDownloadFolder(val apkFiles: List<String>) : SecurityIssue()

    data class OsSecurityPatchOutdated(val patchDate: String) : SecurityIssue()

    // 새로 추가된 설치 여부 기반 탐지
    object InstalledFromDownloadedApk : SecurityIssue()
}