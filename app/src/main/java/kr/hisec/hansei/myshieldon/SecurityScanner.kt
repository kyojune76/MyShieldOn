package kr.hisec.hansei.myshieldon

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class SecurityScanner(private val context: Context, private val config: SecurityConfig) {

    /**
     * 설치된 앱들을 스캔하여 보안 이슈가 있는 앱 목록을 반환합니다.
     */
    suspend fun scanInstalledApps(): List<DetectedApp> {
        val detectedApps = mutableListOf<DetectedApp>()
        val packageManager = context.packageManager
        val installedPackages = packageManager.getInstalledPackages(0)

        for (packageInfo in installedPackages) {
            val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
            val packageName = packageInfo.packageName
            val issues = mutableListOf<SecurityIssue>()

            // 우리가 가진 공식 서명 목록에 있는 앱인지 확인
            if (config.officialSignatures.containsKey(packageName)) {
                val officialSignature = config.officialSignatures[packageName]
                val currentSignature = getAppSignature(packageName)

                Log.d("SignatureCheck", "앱: $appName, 공식서명: $officialSignature, 현재서명: $currentSignature")

                // 서명이 다르다면 '서명 변조' 이슈 추가
                if (currentSignature != null && officialSignature != currentSignature) {
                    issues.add(SecurityIssue.TamperedSignature)
                }
            }

            // (추후 다른 검사 로직도 여기에 추가 가능)

            if (issues.isNotEmpty()) {
                detectedApps.add(DetectedApp(appName, packageName, issues))
            }
        }
        return detectedApps
    }

    /**
     * 특정 패키지명의 앱 서명(SHA-256)을 가져옵니다.
     * @param packageName 앱의 패키지명
     * @return SHA-256 서명 해시 문자열, 실패 시 null
     */
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
                // 바이트 배열을 16진수 문자열로 변환
                publicKey.joinToString("") { "%02X".format(it) }
            }
        } catch (e: Exception) {
            Log.e("SignatureCheck", "서명을 가져오는 중 오류 발생: ${e.message}")
            null
        }
    }
}