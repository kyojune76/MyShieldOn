package kr.hisec.hansei.myshieldon

sealed class SecurityIssue {
    data class DangerousPermissions(val permissions: Set<String>) : SecurityIssue()
    object TamperedSignature : SecurityIssue()
    object NonStoreInstallation : SecurityIssue()
    data class ApkInDownloadFolder(val apkFiles: List<String>) : SecurityIssue()
}
