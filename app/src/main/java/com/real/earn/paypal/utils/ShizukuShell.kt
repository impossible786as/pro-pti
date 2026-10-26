package com.real.earn.paypal.utils

/**
 * 🟢 Shizuku مکمل ختم۔
 * ورچوئل میں Shizuku کام نہیں کرتا، اس لیے یہ stub صرف backward
 * compatibility کے لیے ہے۔ ہر چیز false/null return کرے گی،
 * اور اصل کام RootShell یا Accessibility fallback سے ہوگا۔
 */
object ShizukuShell {

    fun isAvailable(): Boolean = false
    fun hasPermission(): Boolean = false
    fun requestPermission() { /* no-op */ }

    suspend fun executeShell(command: String): ShellResult? = null
    suspend fun executeShellMulti(commands: List<String>): List<ShellResult> =
        commands.map { ShellResult(-1, "") }

    suspend fun setAirplaneMode(enable: Boolean): Boolean = false
    suspend fun clearAppData(packageName: String): Boolean = false
    suspend fun forceStopApp(packageName: String): Boolean = false
    suspend fun launchApp(packageName: String): Boolean = false

    data class ShellResult(val exitCode: Int, val output: String)
}
