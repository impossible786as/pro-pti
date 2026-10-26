package com.real.earn.paypal.utils

import com.real.earn.paypal.service.AutomationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.DataOutputStream

/**
 * 🟢 Root Shell — Executes commands with superuser (su) privileges.
 * Bypasses Shizuku entirely.
 */
object RootShell {

    /**
     * Check if root is available.
     */
    fun isRootAvailable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su -c id")
            process.waitFor()
            process.exitValue() == 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Execute a single command as root.
     */
    suspend fun execute(command: String): Boolean = withContext(Dispatchers.IO) {
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec("su")
            os = DataOutputStream(process.outputStream)
            os.writeBytes("$command\n")
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor()
            process.exitValue() == 0
        } catch (e: Exception) {
            AutomationState.log("❌ Root Error: ${e.message}")
            false
        } finally {
            try { os?.close() } catch (_: Exception) {}
            try { process?.destroy() } catch (_: Exception) {}
        }
    }

    /**
     * Execute multiple commands in one root shell session (faster).
     */
    suspend fun executeMulti(commands: List<String>): Boolean = withContext(Dispatchers.IO) {
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec("su")
            os = DataOutputStream(process.outputStream)
            for (cmd in commands) {
                os.writeBytes("$cmd\n")
            }
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor()
            process.exitValue() == 0
        } catch (e: Exception) {
            AutomationState.log("❌ Root Multi Error: ${e.message}")
            false
        } finally {
            try { os?.close() } catch (_: Exception) {}
            try { process?.destroy() } catch (_: Exception) {}
        }
    }
}
