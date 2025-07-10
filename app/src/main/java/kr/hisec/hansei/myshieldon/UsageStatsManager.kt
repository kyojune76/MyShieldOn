package kr.hisec.hansei.myshieldon

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import java.util.concurrent.TimeUnit

/**
 * UsageStats API를 사용해 백그라운드 과다 사용 앱을 감지하며,
 * 권한 요청 및 확인 로직을 포함합니다.
 */
object UsageStatsManagerUtil {

    // 포그라운드 누적 사용시간이 이 임계값(밀리초)이상인 경우 과다 사용으로 간주 (기본: 1시간)
    private const val THRESHOLD_MS = 60 * 60 * 1000L

    /**
     * Usage Access 권한이 허용되었는지 확인합니다.
     */
    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /**
     * Usage Access 권한 설정 화면으로 이동시킵니다.
     */
    fun requestUsageStatsPermission(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    /**
     * 지난 dayCount 일 동안 포그라운드 사용시간이 THRESHOLD_MS 이상인 앱 패키지 리스트 반환.
     * 권한이 없으면 빈 집합을 반환합니다.
     */
    fun getHeavyUsageApps(context: Context, dayCount: Long = 1): Set<String> {
        if (!hasUsageStatsPermission(context)) return emptySet()

        val endTime = System.currentTimeMillis()
        val startTime = endTime - TimeUnit.DAYS.toMillis(dayCount)
        val usageStats = (context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager)
            .queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            ) ?: return emptySet()

        return usageStats
            .filter { it.totalTimeInForeground >= THRESHOLD_MS }
            .map { it.packageName }
            .toSet()
    }
}