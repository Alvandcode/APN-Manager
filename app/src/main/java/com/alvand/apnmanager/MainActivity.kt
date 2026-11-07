package com.alvand.apnmanager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.json.JSONObject
import java.io.ByteArrayOutputStream

private const val MAX_PROFILE_BYTES = 256 * 1024

data class ApnProfile(
    val name: String,
    val apn: String,
    val mcc: String = "",
    val mnc: String = "",
    val apnType: String = "default,supl",
    val protocol: String = "IPV4V6",
    val authType: String = "NONE",
    val proxy: String = "",
    val port: String = ""
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("format", "apnpack")
        put("version", 1)
        put("profile", JSONObject().apply {
            put("name", name); put("apn", apn); put("mcc", mcc); put("mnc", mnc)
            put("apnType", apnType); put("protocol", protocol); put("authType", authType)
            put("proxy", proxy); put("port", port)
        })
    }

    companion object {
        private val PROTOCOLS = setOf("IPV4", "IPV6", "IPV4V6")
        private val AUTH_TYPES = setOf("NONE", "PAP", "CHAP", "PAP_OR_CHAP")
        private val APN_REGEX = Regex("[A-Za-z0-9._-]{1,100}")
        private val APN_TYPE_REGEX = Regex("[a-z][a-z0-9+]*(,[a-z][a-z0-9+]*)*")
        private val PROXY_REGEX = Regex("[A-Za-z0-9.-]{1,100}")
        private val PORT_REGEX = Regex("\\d{1,5}")

        fun parse(text: String): ApnProfile {
            val root = JSONObject(text)
            require(root.optString("format") == "apnpack") { "Unsupported profile format" }
            require(root.optInt("version", 0) == 1) { "Unsupported profile version" }
            val v = root.getJSONObject("profile")
            val result = ApnProfile(
                name = v.optString("name"),
                apn = v.optString("apn"),
                mcc = v.optString("mcc"),
                mnc = v.optString("mnc"),
                apnType = v.optString("apnType").ifBlank { "default,supl" },
                protocol = v.optString("protocol", "IPV4V6").ifBlank { "IPV4V6" },
                authType = v.optString("authType", "NONE").ifBlank { "NONE" },
                proxy = v.optString("proxy"),
                port = v.optString("port")
            )
            require(result.name.isNotBlank()) { "Profile name is required" }
            require(result.apn.isNotBlank()) { "APN value is required" }
            require(result.name.length <= 80) { "Profile name is too long" }
            require(result.apn.matches(APN_REGEX)) { "APN has invalid characters or is too long" }
            require(result.mcc.isEmpty() || result.mcc.matches(Regex("\\d{3}"))) { "MCC must contain 3 digits" }
            require(result.mnc.isEmpty() || result.mnc.matches(Regex("\\d{2,3}"))) { "MNC must contain 2 or 3 digits" }
            require(result.protocol in PROTOCOLS) { "Protocol must be IPV4, IPV6 or IPV4V6" }
            require(result.authType in AUTH_TYPES) { "Authentication type must be NONE, PAP, CHAP or PAP_OR_CHAP" }
            require(result.apnType.matches(APN_TYPE_REGEX)) { "APN type is not valid" }
            require(result.proxy.isEmpty() || result.proxy.matches(PROXY_REGEX)) { "Proxy address is not valid" }
            require(result.port.isEmpty() || (result.port.matches(PORT_REGEX) && result.port.toInt() in 1..65535)) { "Port must be a number between 1 and 65535" }
            return result
        }
    }
}

/** Reads a profile file with a hard size limit so a huge file cannot exhaust memory. */
private fun readProfile(context: Context, uri: Uri): ApnProfile {
    val buffer = context.contentResolver.openInputStream(uri)?.use { input ->
        val out = ByteArrayOutputStream()
        val chunk = ByteArray(8192)
        var total = 0
        while (true) {
            val n = input.read(chunk)
            if (n < 0) break
            if (n == 0) continue
            total += n
            if (total > MAX_PROFILE_BYTES) throw IllegalArgumentException("Profile file is larger than 256 KB")
            out.write(chunk, 0, n)
        }
        out
    } ?: throw IllegalArgumentException("Cannot read selected file")
    return ApnProfile.parse(buffer.toString("UTF-8"))
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("ui_prefs", Context.MODE_PRIVATE)
        setContent {
            ApnManagerApp(
                incomingUri = intent?.data,
                initialPersian = prefs.getBoolean("persian", true),
                initialDark = prefs.getBoolean("dark", false)
            )
        }
    }
}

@Composable
fun ApnManagerApp(incomingUri: Uri?, initialPersian: Boolean, initialDark: Boolean) {
    val context = LocalContext.current
    var isPersian by rememberSaveable { mutableStateOf(initialPersian) }
    var dark by rememberSaveable { mutableStateOf(initialDark) }
    var profileText by rememberSaveable { mutableStateOf("") }
    var status by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(isPersian, dark) {
        context.getSharedPreferences("ui_prefs", Context.MODE_PRIVATE).edit()
            .putBoolean("persian", isPersian)
            .putBoolean("dark", dark)
            .apply()
    }

    val profile = remember(profileText) {
        profileText.takeIf { it.isNotBlank() }?.let { runCatching { ApnProfile.parse(it) }.getOrNull() }
    }

    val persian = isPersian
    MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
        Surface(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SettingsCell, contentDescription = null, tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(34.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("APN Manager", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(if (persian) "مدیریت ساده پروفایل اینترنت" else "Simple mobile data profile manager",
                            style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(onClick = { isPersian = !isPersian }) { Text(if (persian) "EN" else "فا") }
                    IconButton(onClick = { dark = !dark }) {
                        Icon(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode, contentDescription = "Theme")
                    }
                }

                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(if (persian) "پروفایل اینترنت" else "Internet profile", style = MaterialTheme.typography.titleMedium)
                        Text(if (persian) "یک فایل .apnpack یا JSON سازگار را وارد کنید." else "Import an .apnpack or compatible JSON profile.")
                        val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                            if (uri != null) {
                                try {
                                    profileText = readProfile(context, uri).toJson().toString()
                                    status = if (persian) "فایل با موفقیت بررسی شد." else "Profile validated successfully."
                                } catch (e: Exception) {
                                    status = (if (persian) "فایل نامعتبر است: " else "Invalid file: ") + (e.message ?: "Unknown error")
                                }
                            }
                        }
                        Button(onClick = { picker.launch(arrayOf("*/*")) }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.FileOpen, null); Spacer(Modifier.width(8.dp))
                            Text(if (persian) "انتخاب فایل پروفایل" else "Choose profile file")
                        }
                    }
                }

                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(if (persian) "تشخیص اپراتور" else "Carrier detection", style = MaterialTheme.typography.titleMedium)
                        Text(
                            if (persian)
                                "کد اپراتور سیم‌کارت (MCC + MNC) مستقیماً و بدون هیچ مجوزی خوانده می‌شود. شماره تلفن، IMEI یا IMSI خوانده یا ذخیره نمی‌شود."
                            else
                                "Reading the SIM network code (MCC + MNC) needs no permission. No phone number, IMEI or IMSI is read or stored."
                        )
                        if (profile?.mcc?.isNotBlank() == true) {
                            Text(
                                (if (persian) "پروفایل واردشده متعلق به کد شبکه: " else "Imported profile network code: ") +
                                    profile.mcc + "/" + profile.mnc,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                try {
                                    val numeric = context.getSystemService(TelephonyManager::class.java)?.simOperator.orEmpty()
                                    status = if (numeric.isBlank()) {
                                        if (persian) "کد اپراتور در دسترس نیست؛ انتخاب دستی لازم است." else "Operator code unavailable; choose manually."
                                    } else {
                                        (if (persian) "کد اپراتور شناسایی شد: " else "Operator code detected: ") + numeric
                                    }
                                } catch (_: Exception) {
                                    status = if (persian) "خواندن کد اپراتور ممکن نشد." else "Could not read operator code."
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.NetworkCell, null); Spacer(Modifier.width(8.dp))
                            Text(if (persian) "خواندن کد سیم‌کارت" else "Read SIM network code")
                        }
                    }
                }

                profile?.let { p ->
                    Card {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            Text(if (persian) "جزئیات پروفایل" else "Profile details", style = MaterialTheme.typography.titleMedium)
                            DetailRow(if (persian) "نام" else "Name", p.name)
                            DetailRow("APN", p.apn)
                            DetailRow("MCC / MNC", "${p.mcc.ifBlank { "—" }} / ${p.mnc.ifBlank { "—" }}")
                            DetailRow(if (persian) "نوع APN" else "APN type", p.apnType)
                            DetailRow(if (persian) "پروتکل" else "Protocol", p.protocol)
                            DetailRow(if (persian) "احراز هویت" else "Auth type", p.authType)
                            DetailRow(if (persian) "پروکسی" else "Proxy", p.proxy.ifBlank { "—" })
                            DetailRow(if (persian) "پورت" else "Port", p.port.ifBlank { "—" })
                            HorizontalDivider()
                            Text(
                                if (persian) "برای ثبت پروفایل، صفحه تنظیمات APN باز می‌شود. مقادیر بالا را طبق راهنمای اپراتور در آن صفحه وارد و ذخیره کنید." else
                                    "Android will open APN settings. Enter and save the values above there as required by your carrier."
                            )
                            Button(
                                onClick = {
                                    try {
                                        context.startActivity(Intent(Settings.ACTION_APN_SETTINGS))
                                    } catch (_: Exception) {
                                        status = if (persian) "صفحه APN در این دستگاه در دسترس نیست؛ از تنظیمات شبکه وارد شوید." else
                                            "APN settings unavailable; open Network settings manually."
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Settings, null); Spacer(Modifier.width(8.dp))
                                Text(if (persian) "باز کردن تنظیمات APN" else "Open APN settings")
                            }
                        }
                    }
                }

                if (status.isNotBlank()) {
                    AssistChip(onClick = { status = "" }, label = { Text(status) }, leadingIcon = {
                        Icon(Icons.Default.Info, null)
                    })
                }

                Text(
                    if (persian) "این برنامه APN را مستقیماً در سیستم ثبت نمی‌کند. ذخیره نهایی باید از طریق تنظیمات مجاز دستگاه انجام شود." else
                        "This app does not write APN settings directly. Final saving must be completed in Android settings.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text("APN Manager • v1.0.1", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    LaunchedEffect(incomingUri) {
        if (incomingUri != null) {
            try {
                profileText = readProfile(context, incomingUri).toJson().toString()
                status = if (persian) "پروفایل دریافت و بررسی شد." else "Profile imported and validated."
            } catch (e: Exception) {
                status = (if (persian) "خطا در خواندن فایل: " else "File error: ") + (e.message ?: "")
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value)
    }
}
