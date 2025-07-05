package kr.hisec.hansei.myshieldon


import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.File
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate


class SecurityScanner(private val context: Context, private val config: SecurityConfig) {

    suspend fun scanInstalledApps(): List<DetectedApp> {
        val detectedApps = mutableListOf<DetectedApp>()
        val packageManager = context.packageManager
        val installedPackages = packageManager.getInstalledPackages(0)

        // 다운로드 폴더에 있는 APK 파일의 패키지명 추출
        val downloadApkPackages = getDownloadedApkPackageNames()

        for (packageInfo in installedPackages) {
            val appName = packageInfo.applicationInfo?.loadLabel(packageManager)?.toString() ?: "Unknown App"
            val packageName = packageInfo.packageName
            val issues = mutableListOf<SecurityIssue>()

            // 1. 공식 서명 확인
            if (config.officialSignatures.containsKey(packageName)) {
                val officialSignature = config.officialSignatures[packageName]
                val currentSignature = getAppSignature(packageName)

                Log.d("SignatureCheck", "앱: $appName, 공식서명: $officialSignature, 현재서명: $currentSignature")

                if (currentSignature != null && officialSignature != currentSignature) {
                    issues.add(SecurityIssue.TamperedSignature)
                }
            }

            // 2. 위험 권한 검사
            try {
                val requestedPermissions = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
                    .requestedPermissions?.toSet() ?: emptySet()
                val dangerous = requestedPermissions.intersect(config.dangerousPermissions)
                if (dangerous.size >= config.permissionThreshold) {
                    issues.add(SecurityIssue.DangerousPermissions(dangerous))
                }
            } catch (e: Exception) {
                Log.e("PermissionCheck", "$packageName 권한 확인 실패: ${e.message}")
            }

            // 3. 해당 앱이 다운로드 폴더에 있는 APK로 설치된 경우
            if (packageName in downloadApkPackages) {
                issues.add(SecurityIssue.InstalledFromDownloadedApk)
            }

            if (issues.isNotEmpty()) {
                detectedApps.add(DetectedApp(appName, packageName, issues))
            }
        }

        return detectedApps
    }

    private fun getAppSignature(packageName: String): String? {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            signatures?.firstOrNull()?.let { signature ->
                val certFactory = CertificateFactory.getInstance("X.509")
                val cert = certFactory.generateCertificate(ByteArrayInputStream(signature.toByteArray())) as X509Certificate
                val md = MessageDigest.getInstance("SHA-256")
                val publicKey = md.digest(cert.encoded)
                publicKey.joinToString("") { "%02X".format(it) }
            }
        } catch (e: Exception) {
            Log.e("SignatureCheck", "서명을 가져오는 중 오류 발생: ${e.message}")
            null
        }
    }

    private fun getDownloadedApkPackageNames(): Set<String> {
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return downloads?.listFiles()
            ?.filter { it.extension == "apk" }
            ?.mapNotNull { getPackageNameFromApk(it) }
            ?.toSet() ?: emptySet()
    }

    private fun getPackageNameFromApk(apkFile: File): String? {
        return try {
            val pm = context.packageManager
            val info = pm.getPackageArchiveInfo(apkFile.absolutePath, 0)
            info?.applicationInfo?.let {
                if (Build.VERSION.SDK_INT >= 8) {
                    it.sourceDir = apkFile.absolutePath
                    it.publicSourceDir = apkFile.absolutePath
                }
            }
            info?.packageName
        } catch (e: Exception) {
            Log.e("APKScan", "APK에서 패키지명 추출 실패: ${e.message}")
            null
        }
    }
}
