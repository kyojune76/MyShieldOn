package kr.hisec.hansei.myshieldon

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File
import java.security.MessageDigest
import android.provider.Settings
import android.app.AppOpsManager
import android.content.pm.ApplicationInfo

class SecurityScanner(private val context: Context, private val config: SecurityConfig) {

    // 앱 설치 목록을 기반으로 보안 위험 요소 탐지 수행
    suspend fun scanInstalledApps(): List<DetectedApp> {
        val detectedApps = mutableListOf<DetectedApp>()
        val packageManager = context.packageManager
        val installedPackages = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS or PackageManager.GET_SIGNING_CERTIFICATES)

        for (packageInfo in installedPackages) {
            val appName = packageInfo.applicationInfo?.loadLabel(packageManager)?.toString() ?: "Unknown App"
            val packageName = packageInfo.packageName
            val issues = mutableListOf<SecurityIssue>()

            // 1. 서명 위조 확인
            if (config.officialSignatures.containsKey(packageName)) {
                val officialSignature = config.officialSignatures[packageName]
                val currentSignature = getAppSignature(packageName)
                if (currentSignature != null && currentSignature != officialSignature) {
                    issues.add(SecurityIssue.TamperedSignature)
                }
            }

            // 2. 위험 권한 과다 보유 앱
            try {
                val requestedPermissions = packageInfo.requestedPermissions?.toSet() ?: emptySet()
                val dangerous = requestedPermissions.intersect(config.dangerousPermissions)
                if (dangerous.size >= config.permissionThreshold) {
                    issues.add(SecurityIssue.DangerousPermissions(dangerous))
                }
            } catch (e: Exception) {
                Log.e("PermissionCheck", "$packageName 권한 확인 실패: ${e.message}")
            }

            // 3. 스토어 외 앱 설치 여부
            // 시스템 앱 필터링
            val isSystemApp = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0 ||
                    (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

// 스토어 외 설치 감지
            val installer = packageManager.getInstallerPackageName(packageName)
            val isFromStore = installer?.contains("google") == true ||
                    installer?.contains("samsung") == true ||
                    installer?.contains("one") == true ||
                    installer?.contains("baidu") == true ||
                    installer?.contains("market") == true

            if (!isSystemApp && !isFromStore) {
                issues.add(SecurityIssue.NonStoreInstallation)
            }

            // 감지된 보안 이슈가 있을 경우 리스트에 추가
            if (issues.isNotEmpty()) {
                detectedApps.add(DetectedApp(appName, packageName, issues))
            }
        }

        return detectedApps
    }

    // 특정 앱의 서명(SHA-256) 해시값 반환
    private fun getAppSignature(packageName: String): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }
            val cert = signatures[0].toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val hash = md.digest(cert)
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            null
        }
    }

    // 다운로드 폴더 내 .apk 파일 목록 수집
    fun checkApkInDownloadFolder(): List<String> {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val apkFiles = downloadDir?.listFiles { file -> file.extension == "apk" } ?: return emptyList()
        return apkFiles.map { it.name }
    }

    // request_install_packages 권한이 허용된 앱 목록 (알 수 없는 출처 설치 허용 앱)
    fun getAllowedUnknownSourceApps(): List<String> {
        val pm = context.packageManager
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { ai ->
                appOps.checkOpNoThrow(
                    "android:request_install_packages",
                    ai.uid,
                    ai.packageName
                ) == AppOpsManager.MODE_ALLOWED
            }
            .map { it.packageName }
    }

    // 개발자 옵션 메뉴 활성화 여부 확인
    fun isDeveloperOptionsMenuEnabled(): Boolean = try {
        Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            0
        ) == 1
    } catch (e: Settings.SettingNotFoundException) {
        false
    }

    // ADB(USB 디버깅) 활성화 여부 확인
    fun isAdbEnabled(): Boolean = try {
        Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.ADB_ENABLED,
            0
        ) == 1
    } catch (e: Settings.SettingNotFoundException) {
        false
    }
}
