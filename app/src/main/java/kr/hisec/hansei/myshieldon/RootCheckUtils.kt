package kr.hisec.hansei.myshieldon
//루팅여부 체크
import android.os.Build
import java.io.File

//test-keys, busybox, su, rw경로 체크
object RootCheckUtils {
    fun isDeviceRooted(): Boolean {
        return checkForSuBinary() || detectTestKeys() || checkForBusyBox() || checkSuExists() || checkForRWPaths()
    }

    private fun checkForSuBinary(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
            "/data/local/xbin/su","/data/local/bin/su","/system/sd/xbin/su",
            "/system/bin/failsafe/su","/data/local/su","/su/bin/su"
        )
        return paths.any { File(it).exists() }
    }

    private fun detectTestKeys(): Boolean = Build.TAGS?.contains("test-keys") == true
    private fun checkForBusyBox(): Boolean = File("/system/xbin/busybox").exists()

    private fun checkSuExists(): Boolean {
        return try {
            val p = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            p.waitFor() == 0
        } catch (_: Exception) { false }
    }

    private fun checkForRWPaths(): Boolean {
        val paths = arrayOf("/system","/system/bin","/system/xbin","/vendor/bin","/data")
        return paths.any {
            try {
                val f = File(it, "temp${System.currentTimeMillis()}")
                if (f.createNewFile()) { f.delete(); true } else false
            } catch (_: Exception) { false }
        }
    }
}