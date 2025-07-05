package kr.hisec.hansei.myshieldon

sealed class SecurityIssue {

    data class DangerousPermissions(val permissions: Set<String>) : SecurityIssue()
    object TamperedSignature : SecurityIssue()
    object NonStoreInstallation : SecurityIssue()

    // 기존: 파일명 기반
    // data class ApkInDownloadFolder(val apkFiles: List<String>) : SecurityIssue()

    // 새로 추가된 설치 여부 기반 탐지
    object InstalledFromDownloadedApk : SecurityIssue()
}