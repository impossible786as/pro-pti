package com.real.earn.paypal.utils

import android.accessibilityservice.AccessibilityService
import android.content.Context
import com.real.earn.paypal.service.AutomationState
import kotlinx.coroutines.runBlocking
import java.util.Random

/**
 * 🟢 Root-level Spoofing Controller.
 * Replaces Flight Mode with:
 *   1. Device Model/Brand/Fingerprint spoofing (via resetprop)
 *   2. MAC address spoofing
 *   3. IP change (via network reset)
 *
 * 🟢 Virtual-safe: اگر روٹ نہیں ہے تو سپوفنگ سکپ ہو جاتی ہے
 *    اور ٹول رکتا نہیں — سیدھا Clear Data پر چلا جاتا ہے۔
 */
object AirplaneModeController {

    data class DeviceProfile(
        val brand: String,
        val manufacturer: String,
        val model: String,
        val device: String,
        val product: String,
        val fingerprint: String,
        val sdkInt: Int
    )

    // 🟢 60 Premium Devices — Latest Google, Samsung, OnePlus, Xiaomi etc.
    private val DEVICE_PROFILES = listOf(
        // ═══════════════════════════════════════
        // 🟢 SAMSUNG (22 devices)
        // ═══════════════════════════════════════
        DeviceProfile("samsung", "samsung", "SM-S928B", "e3q", "e3qxxx",
            "samsung/e3qxxx/e3q:14/UP1A.231005.007/S928BXXU1AWLM:user/release-keys", 34),
        DeviceProfile("samsung", "samsung", "SM-S926B", "e2q", "e2qxxx",
            "samsung/e2qxxx/e2q:14/UP1A.231005.007/S926BXXU1AWLM:user/release-keys", 34),
        DeviceProfile("samsung", "samsung", "SM-S921B", "e1q", "e1qxxx",
            "samsung/e1qxxx/e1q:14/UP1A.231005.007/S921BXXU1AWLM:user/release-keys", 34),
        DeviceProfile("samsung", "samsung", "SM-S918B", "dm3q", "dm3qxxx",
            "samsung/dm3qxxx/dm3q:14/UP1A.231005.007/S918BXXS6CWL3:user/release-keys", 34),
        DeviceProfile("samsung", "samsung", "SM-S916B", "dm2q", "dm2qxxx",
            "samsung/dm2qxxx/dm2q:14/UP1A.231005.007/S916BXXS6CWL3:user/release-keys", 34),
        DeviceProfile("samsung", "samsung", "SM-S911B", "dm1q", "dm1qxxx",
            "samsung/dm1qxxx/dm1q:14/UP1A.231005.007/S911BXXS6CWL3:user/release-keys", 34),
        DeviceProfile("samsung", "samsung", "SM-S908B", "b0q", "b0qxxx",
            "samsung/b0qxxx/b0q:13/TP1A.220624.014/S908BXXS6DWL1:user/release-keys", 33),
        DeviceProfile("samsung", "samsung", "SM-S906B", "b0s", "b0sxxx",
            "samsung/b0sxxx/b0s:13/TP1A.220624.014/S906BXXS6DWL1:user/release-keys", 33),
        DeviceProfile("samsung", "samsung", "SM-S901B", "r0q", "r0qxxx",
            "samsung/r0qxxx/r0q:13/TP1A.220624.014/S901BXXS6DWL1:user/release-keys", 33),
        DeviceProfile("samsung", "samsung", "SM-G998B", "p3q", "p3qxxx",
            "samsung/p3qxxx/p3q:13/TP1A.220624.014/G998BXXS9FWL1:user/release-keys", 33),
        DeviceProfile("samsung", "samsung", "SM-G996B", "t2q", "t2qxxx",
            "samsung/t2qxxx/t2q:13/TP1A.220624.014/G996BXXS9FWL1:user/release-keys", 33),
        DeviceProfile("samsung", "samsung", "SM-G991B", "o1q", "o1qxxx",
            "samsung/o1qxxx/o1q:13/TP1A.220624.014/G991BXXS9FWL1:user/release-keys", 33),
        DeviceProfile("samsung", "samsung", "SM-F946B", "q5q", "q5qxxx",
            "samsung/q5qxxx/q5q:14/UP1A.231005.007/F946BXXS1AWLM:user/release-keys", 34),
        DeviceProfile("samsung", "samsung", "SM-F936B", "q4q", "q4qxxx",
            "samsung/q4qxxx/q4q:14/UP1A.231005.007/F936BXXS4DWL1:user/release-keys", 34),
        DeviceProfile("samsung", "samsung", "SM-F731B", "b5q", "b5qxxx",
            "samsung/b5qxxx/b5q:14/UP1A.231005.007/F731BXXS1AWLM:user/release-keys", 34),
        DeviceProfile("samsung", "samsung", "SM-F721B", "b4q", "b4qxxx",
            "samsung/b4qxxx/b4q:14/UP1A.231005.007/F721BXXS4DWL1:user/release-keys", 34),
        DeviceProfile("samsung", "samsung", "SM-N986B", "c2q", "c2qxxx",
            "samsung/c2qxxx/c2q:13/TP1A.220624.014/N986BXXS8HWL1:user/release-keys", 33),
        DeviceProfile("samsung", "samsung", "SM-N980F", "c1q", "c1qxxx",
            "samsung/c1qxxx/c1q:13/TP1A.220624.014/N980FXXS8HWL1:user/release-keys", 33),
        DeviceProfile("samsung", "samsung", "SM-A546B", "a54x", "a54xxx",
            "samsung/a54xxx/a54x:14/UP1A.231005.007/A546BXXS6CWL3:user/release-keys", 34),
        DeviceProfile("samsung", "samsung", "SM-A346B", "a34x", "a34xxx",
            "samsung/a34xxx/a34x:14/UP1A.231005.007/A346BXXS6CWL3:user/release-keys", 34),
        DeviceProfile("samsung", "samsung", "SM-M346B", "m34x", "m34xxx",
            "samsung/m34xxx/m34x:14/UP1A.231005.007/M346BXXS6CWL3:user/release-keys", 34),
        DeviceProfile("samsung", "samsung", "SM-A145F", "a14x", "a14xxx",
            "samsung/a14xxx/a14x:14/UP1A.231005.007/A145FXXS6CWL3:user/release-keys", 34),

        // ═══════════════════════════════════════
        // 🟢 GOOGLE PIXEL (10 devices)
        // ═══════════════════════════════════════
        DeviceProfile("google", "Google", "Pixel 8 Pro", "husky", "husky",
            "google/husky/husky:14/UQ1A.240105.004/10884880:user/release-keys", 34),
        DeviceProfile("google", "Google", "Pixel 8", "shiba", "shiba",
            "google/shiba/shiba:14/UQ1A.240105.004/10884880:user/release-keys", 34),
        DeviceProfile("google", "Google", "Pixel 7 Pro", "cheetah", "cheetah",
            "google/cheetah/cheetah:14/UQ1A.240105.004/10884880:user/release-keys", 34),
        DeviceProfile("google", "Google", "Pixel 7", "panther", "panther",
            "google/panther/panther:14/UQ1A.240105.004/10884880:user/release-keys", 34),
        DeviceProfile("google", "Google", "Pixel 6 Pro", "raven", "raven",
            "google/raven/raven:13/TQ3A.230901.001/10750268:user/release-keys", 33),
        DeviceProfile("google", "Google", "Pixel 6", "oriole", "oriole",
            "google/oriole/oriole:13/TQ3A.230901.001/10750268:user/release-keys", 33),
        DeviceProfile("google", "Google", "Pixel 6a", "bluejay", "bluejay",
            "google/bluejay/bluejay:13/TQ3A.230901.001/10750268:user/release-keys", 33),
        DeviceProfile("google", "Google", "Pixel 5", "redfin", "redfin",
            "google/redfin/redfin:13/TQ3A.230901.001/10750268:user/release-keys", 33),
        DeviceProfile("google", "Google", "Pixel 4a", "sunfish", "sunfish",
            "google/sunfish/sunfish:13/TQ3A.230901.001/10750268:user/release-keys", 33),
        DeviceProfile("google", "Google", "Pixel 4 XL", "coral", "coral",
            "google/coral/coral:13/TQ3A.230901.001/10750268:user/release-keys", 33),

        // ═══════════════════════════════════════
        // 🟢 ONEPLUS (6 devices)
        // ═══════════════════════════════════════
        DeviceProfile("OnePlus", "OnePlus", "CPH2581", "OP5D3BL1", "OP5D3BL1",
            "OnePlus/CPH2581/OP5D3BL1:14/UKQ1.230924.001/R.1707987618:user/release-keys", 34),
        DeviceProfile("OnePlus", "OnePlus", "CPH2585", "OP5D7BL1", "OP5D7BL1",
            "OnePlus/CPH2585/OP5D7BL1:14/UKQ1.230924.001/R.1707987618:user/release-keys", 34),
        DeviceProfile("OnePlus", "OnePlus", "CPH2449", "OP5958L1", "OP5958L1",
            "OnePlus/CPH2449/OP5958L1:14/UKQ1.230924.001/R.1707987618:user/release-keys", 34),
        DeviceProfile("OnePlus", "OnePlus", "NE2213", "OP516EL1", "OP516EL1",
            "OnePlus/NE2213/OP516EL1:13/TP1A.220905.001/R.1699010591:user/release-keys", 33),
        DeviceProfile("OnePlus", "OnePlus", "LE2121", "OP515BL1", "OP515BL1",
            "OnePlus/LE2121/OP515BL1:13/TP1A.220905.001/R.1699010591:user/release-keys", 33),
        DeviceProfile("OnePlus", "OnePlus", "CPH2491", "OP5D2BL1", "OP5D2BL1",
            "OnePlus/CPH2491/OP5D2BL1:14/UKQ1.230924.001/R.1707987618:user/release-keys", 34),

        // ═══════════════════════════════════════
        // 🟢 XIAOMI / REDMI / POCO (9 devices)
        // ═══════════════════════════════════════
        DeviceProfile("Xiaomi", "Xiaomi", "2311BPN23G", "aurora", "aurora_global",
            "Xiaomi/aurora_global/aurora:14/UKQ1.231003.002/V816.0.3.0.UNQMIXM:user/release-keys", 34),
        DeviceProfile("Xiaomi", "Xiaomi", "23116PN5BC", "shennong", "shennong_global",
            "Xiaomi/shennong_global/shennong:14/UKQ1.231003.002/V816.0.3.0.UNCMIXM:user/release-keys", 34),
        DeviceProfile("Xiaomi", "Xiaomi", "23127PN0CG", "houji", "houji_global",
            "Xiaomi/houji_global/houji:14/UKQ1.231003.002/V816.0.3.0.UNCMIXM:user/release-keys", 34),
        DeviceProfile("Xiaomi", "Xiaomi", "2210132G", "nuwa", "nuwa_global",
            "Xiaomi/nuwa_global/nuwa:13/TKQ1.221114.001/V816.0.1.0.TMBMIXM:user/release-keys", 33),
        DeviceProfile("Xiaomi", "Xiaomi", "2211133G", "fuxi", "fuxi_global",
            "Xiaomi/fuxi_global/fuxi:13/TKQ1.221114.001/V816.0.1.0.TMCMIXM:user/release-keys", 33),
        DeviceProfile("Redmi", "Xiaomi", "2312DRA50G", "sapphire", "sapphire_global",
            "Redmi/sapphire_global/sapphire:14/UKQ1.231003.002/OS1.0.3.0.UNGMIXM:user/release-keys", 34),
        DeviceProfile("Redmi", "Xiaomi", "22101316G", "rubyx", "rubyx_global",
            "Redmi/rubyx_global/rubyx:13/TKQ1.221114.001/OS1.0.1.0.TMQMIXM:user/release-keys", 33),
        DeviceProfile("POCO", "Xiaomi", "23122PCD1G", "marble", "marble_global",
            "POCO/marble_global/marble:14/UKQ1.231003.002/OS1.0.3.0.UNGMIXM:user/release-keys", 34),
        DeviceProfile("POCO", "Xiaomi", "2311DRK48G", "garnet", "garnet_global",
            "POCO/garnet_global/garnet:14/UKQ1.231003.002/OS1.0.3.0.UNGMIXM:user/release-keys", 34),

        // ═══════════════════════════════════════
        // 🟢 OPPO / VIVO / REALME (7 devices)
        // ═══════════════════════════════════════
        DeviceProfile("OPPO", "OPPO", "PHY110", "OP5958L1", "OP5958L1",
            "OPPO/PHY110/OP5958L1:14/UKQ1.230924.001/R.1707987618:user/release-keys", 34),
        DeviceProfile("OPPO", "OPPO", "PHY120", "OP5D7BL1", "OP5D7BL1",
            "OPPO/PHY120/OP5D7BL1:14/UKQ1.230924.001/R.1707987618:user/release-keys", 34),
        DeviceProfile("OPPO", "OPPO", "PJD110", "OP5D2BL1", "OP5D2BL1",
            "OPPO/PJD110/OP5D2BL1:14/UKQ1.230924.001/R.1707987618:user/release-keys", 34),
        DeviceProfile("vivo", "vivo", "V2309A", "PD2309", "PD2309",
            "vivo/PD2309/PD2309:14/UP1A.231005.007/1707987618:user/release-keys", 34),
        DeviceProfile("vivo", "vivo", "V2244", "PD2244", "PD2244",
            "vivo/PD2244/PD2244:13/TP1A.220624.014/1699010591:user/release-keys", 33),
        DeviceProfile("realme", "realme", "RMX3888", "RE5859", "RE5859",
            "realme/RMX3888/RE5859:14/UKQ1.230924.001/R.1707987618:user/release-keys", 34),
        DeviceProfile("realme", "realme", "RMX3867", "RE5857", "RE5857",
            "realme/RMX3867/RE5857:14/UKQ1.230924.001/R.1707987618:user/release-keys", 34),

        // ═══════════════════════════════════════
        // 🟢 OTHERS (6 devices)
        // ═══════════════════════════════════════
        DeviceProfile("Nothing", "Nothing", "A065", "spacewar", "spacewar",
            "Nothing/spacewar/spacewar:14/UKQ1.230924.001/2401241637:user/release-keys", 34),
        DeviceProfile("Nothing", "Nothing", "A063", "Spacewar", "Spacewar",
            "Nothing/Spacewar/Spacewar:13/TQ3A.230901.001/2311141834:user/release-keys", 33),
        DeviceProfile("asus", "asus", "ASUS_AI2205", "AI2205", "AI2205",
            "asus/WW_AI2205/ASUS_AI2205:14/UKQ1.230924.001/34.0210.0210.200:user/release-keys", 34),
        DeviceProfile("asus", "asus", "ASUS_AI2302", "AI2302", "AI2302",
            "asus/WW_AI2302/ASUS_AI2302:14/UKQ1.230924.001/34.0210.0210.200:user/release-keys", 34),
        DeviceProfile("motorola", "motorola", "moto g84 5G", "bangkk", "bangkk",
            "motorola/bangkk/bangkk:14/U1TBS34.54-21-5-3-1/3b1b0:user/release-keys", 34),
        DeviceProfile("Sony", "Sony", "XQ-DQ72", "pdx234", "pdx234",
            "Sony/pdx234/pdx234:14/68.1.A.2.131/068001A0020131003:user/release-keys", 34)
    )

    /**
     * 🟢 Main entry: Replaces flight mode with comprehensive spoofing.
     *
     * 🟢 IMPORTANT (Virtual-safe):
     *   - اگر روٹ نہیں ہے → سپوفنگ سکپ، `true` ریٹرن
     *   - کبھی `false` ریٹرن نہیں کرتا تاکہ آٹومیشن کبھی نہ رکے
     */
    fun strictToggle(service: AccessibilityService, cyclesOverride: Int? = null): Boolean {
        val cycles = cyclesOverride ?: 3

        // 🟢 اگر روٹ نہیں → سیدھا سکپ (کوئی ہینگ، کوئی کریش، کوئی رُکنا نہیں)
        if (!RootShell.isRootAvailable()) {
            AutomationState.log("⏭️ Skipping spoof — no root (proceeding to Clear Data)")
            return true
        }

        AutomationState.log("🛡 Root Mode: Spoofing $cycles cycles")
        var allOk = true
        for (i in 1..cycles) {
            AutomationState.log("  ↳ Spoof Cycle $i/$cycles")
            val ok = runBlocking { doFullSpoof(service) }
            if (!ok) allOk = false
            Thread.sleep(1500)
        }
        return allOk
    }

    /**
     * 🟢 Full spoof: Device + MAC + IP
     */
    private suspend fun doFullSpoof(context: Context): Boolean {
        val device = DEVICE_PROFILES.random()
        val newMac = generateRandomMac()

        AutomationState.log("    📱 Device: ${device.brand} ${device.model}")
        AutomationState.log("    🌐 MAC: $newMac")

        val commands = listOf(
            "am force-stop com.paypal.android.p2pmobile",
            "am force-stop com.google.android.gms",

            "resetprop ro.product.brand ${device.brand}",
            "resetprop ro.product.manufacturer ${device.manufacturer}",
            "resetprop ro.product.model ${device.model}",
            "resetprop ro.product.device ${device.device}",
            "resetprop ro.product.name ${device.product}",
            "resetprop ro.build.product ${device.device}",
            "resetprop ro.build.fingerprint ${device.fingerprint}",
            "resetprop ro.build.version.sdk ${device.sdkInt}",

            "ip link set wlan0 down",
            "ip link set wlan0 address $newMac",
            "ip link set wlan0 up",

            "svc wifi disable",
            "svc data disable",
            "sleep 2",
            "svc wifi enable",
            "svc data enable",

            "ndc resolver flushdefaultif",
            "ndc resolver flushif wlan0",
            "ndc resolver flushif rmnet0"
        )

        val ok = RootShell.executeMulti(commands)

        if (ok) {
            AutomationState.log("    ✔ Spoof complete (${device.brand} ${device.model})")
            Thread.sleep(3000)
            return true
        } else {
            AutomationState.log("    ⚠ Spoof partially failed — trying network reset")
            RootShell.executeMulti(listOf(
                "svc wifi disable", "svc data disable",
                "sleep 3",
                "svc wifi enable", "svc data enable"
            ))
            return true
        }
    }

    private fun generateRandomMac(): String {
        val rnd = Random()
        val bytes = ByteArray(6)
        rnd.nextBytes(bytes)
        bytes[0] = (bytes[0].toInt() and 0xFC or 0x02).toByte()
        return bytes.joinToString(":") { String.format("%02x", it) }
    }

    fun isAirplaneModeOn(context: Context): Boolean = try {
        android.provider.Settings.Global.getInt(
            context.contentResolver,
            android.provider.Settings.Global.AIRPLANE_MODE_ON,
            0
        ) == 1
    } catch (e: Exception) { false }
}
