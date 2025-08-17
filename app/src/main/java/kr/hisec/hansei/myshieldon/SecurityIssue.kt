package kr.hisec.hansei.myshieldon

sealed class SecurityIssue {

    data class DangerousPermissions(val permissions: Set<String>) : SecurityIssue()
    object NonStoreInstallation : SecurityIssue()
    object DeveloperOptionsEnabled : SecurityIssue()   // 개발자 옵션 메뉴 활성화
    object AdbModeEnabled : SecurityIssue()            // ADB 모드 활성화
    object UnsafeUnknownSources : SecurityIssue()      // 알 수 없는 출처 허용 앱 존재
    object TamperedSignature : SecurityIssue()
    data class ApkInDownloadFolder(val apkFiles: List<String>) : SecurityIssue()
    data class OsSecurityPatchOutdated(val patchDate: String) : SecurityIssue()
    data class BackgroundOverUsage(val packageNames: List<String>) : SecurityIssue()
}