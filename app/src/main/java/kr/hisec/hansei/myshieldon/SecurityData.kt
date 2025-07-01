package kr.hisec.hansei.myshieldon

data class SecurityConfig(
    val permissionThreshold: Int,
    val dangerousPermissions: Set<String>,
    val officialSignatures: Map<String, String>
)

data class DetectedApp(
    val appName: String,
    val packageName: String,
    val issues: List<SecurityIssue>
)

sealed class SecurityIssue {
    data class DangerousPermissions(val permissions: Set<String>) : SecurityIssue()
    object TamperedSignature : SecurityIssue()
}