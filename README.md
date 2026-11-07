# APN Manager

**APN Manager** — helper مدرن و محلی برای مدیریت پروفایل اینترنت در اندروید، همراه با استودیوی وب استاتیک برای ساخت و اعتبارسنجی فایل‌های `.apnpack`.

> 🇮🇷 این پروژه به فارسی و 🇬🇧 English به‌صورت دو زبانه (bilingual) ارائه می‌شود.

[![Build Android APK](https://github.com/Alvandcode/APN-Manager/actions/workflows/android-build.yml/badge.svg)](https://github.com/Alvandcode/APN-Manager/actions/workflows/android-build.yml)
[![Release](https://img.shields.io/github/v/release/Alvandcode/APN-Manager)](https://github.com/Alvandcode/APN-Manager/releases/latest)
[![Android](https://img.shields.io/badge/Android-7.0%20%7C%2015-3DDC84?style=flat&logo=android)](https://developer.android.com/about/versions/15)

---

## 🇬🇧 English

### Overview
**APN Manager** is a privacy-first companion for Android mobile-data APN (Access Point Name) profiles. It lets you create, validate, import and export portable `.apnpack` profile files — entirely on your device or in a static web page — before you hand the values to the official carrier APN settings screen.

⚠️ **Important Android limitation:** ordinary third-party apps cannot write carrier APN records directly. APN Manager therefore never claims to install APNs silently. It validates the imported profile and opens `Settings.ACTION_APN_SETTINGS`; the user completes saving in system settings.

### Features
- 📱 **Android app** (Kotlin + Jetpack Compose) supporting **Android 7.0 (API 24) – Android 15 (API 35)**.
- 📥 **Import** `.apnpack` JSON profiles through Android Sharesheet / file manager.
- 🔍 **Local validation** of the profile structure and key fields (format, version, required fields, MCC/MNC, APN type, protocol, auth type, proxy, port).
- ⚡ **Open Android APN settings** (`ACTION_APN_SETTINGS`) for user-assisted saving.
- 📶 Reads the **SIM network code** (MCC + MNC) as a convenience helper — requires **no Android permission**.
- 🌗 **Light/dark theme** and **Persian/English** UI.
- 🖥️ **Static web panel** (GitHub Pages): create, validate and download `.apnpack` files.

### How it works
1. Create or import a `.apnpack` profile.
2. App (or web panel) validates the profile locally.
3. App opens Android's APN settings screen; you enter and save the values.
4. Final persistence happens through the device settings.

### Security & Privacy
- Zero Android permissions are requested.
- Local parsing; no account, no analytics, no remote fetch, no network permission.
- Profile files are treated as untrusted input; no executable content is accepted.

### Build
- The repository ships a **Gradle 8.9 wrapper**.
- GitHub Actions (`Build Android APK`) builds a debug APK on every push and publishes it to the **`latest` release**.
- Debug APK is for testing; a release APK should be signed with a private release key.

### Contribute
Contributions are welcome! Opening a pull request — bug fixes, validation improvements, translations, tests — is appreciated.

---

## 🇮🇷 به فارسی

### معرفی
**APN Manager** یک دستیار محلی و حریم‌خصوصی‌محور برای مدیریت پروفایل اینترنت در اندروید است و همراه با استودیوی وب استاتیک، برای ساخت، اعتبارسنجی و صادر کردن فایل‌های پروفایل قابل حمل `.apnpack` کاربرد دارد.

⚠️ **محدودیت مهم اندروید:** اپلیکیشن‌های عادی سوم‌تایی قادر به نوشتن مستقیم رکوردهای APN سیستم نیستند. بنابراین این اپلیکیشن ادعای نصب ناگهانی APN را نمی‌زند؛ ابتدا پروفایل را بررسی می‌کند و سپس صفحه تنظیمات APN سیستم را باز می‌کند و کاربر در نهایت مقادیر را ذخیره می‌کند.

### امکانات
- 📱 اپلیکیشن اندروید (Kotlin + Jetpack Compose) با پشتیبانی از **اندروید ۷ تا اندروید ۱۵**.
- 📥 واردسازی فایل `.apnpack` از طریق سوییفت گوشی یا فایل‌مدیریت‌ر.
- 🔍 اعتبارسنجی محلی ساختار و فیلدهای کلیدی پروفایل.
- ⚡ باز کردن تنظیمات APN سیستم برای ثبت کاربرانه.
- 📶 خواندن کد شبکه سیم‌کارت (MCC+MNC) — بدون هیچ مجوز لازم.
- 🌗 پشتیبانی از تم روشن/تاریک و رابط فارسی/انگلیسی.
- 🖥️ استودیوی وب (GitHub Pages) برای ساخت و دانلود `.apnpack`.

### نحوه کار
۱. پروفایل جدید بساز یا وارد کن.
۲. اپلیکیشن/استودیو اعتبارسنجی را انجام می‌دهد.
۳. اپلیکیشن تنظیمات APN را باز می‌کند و مقادیر را کاربر وارد و ذخیره می‌کند.
۴. در نهایت ذخیره از طریق تنظیمات سیستم انجام می‌شود.

### امنیت و حریم خصوصی
- هیچ مجوزی از سمت کاربر خواسته نمی‌شود.
- پردازش محلی و بدون نیاز به حساب/آنالیتیک/فیچر رادیو.
- فایل‌های پروفایل به‌عنوان ورودی غیرقابل اعتماد در نظر گرفته می‌شوند.

### بیلد
- مخزن شامل **Gradle wrapper نسخه ۸.۹** است.
- GitHub Actions در هر push به `main`, APK دیباگ بسازد و در **Release با نام `latest`** منتشر کند.
- APK دیباگ فقط برای تست است؛ APK ریسِل باید با کلید خصوصی امضا شود.

---

## 🚀 Getting Started / شروع سریع

```bash
# Download the latest APK from:
# https://github.com/Alvandcode/APN-Manager/releases/latest/download/APN-Manager.apk

# Or download from the Releases page:
# https://github.com/Alvandcode/APN-Manager/releases

# Build locally (requires JDK 17 + Android SDK 35):
./gradlew assembleDebug
```

**For contributors / برای مشارکت‌کنندگان:**

```bash
git clone https://github.com/Alvandcode/APN-Manager.git
cd APN-Manager
./gradlew assembleDebug
```

### 📥 Download the app
1. Go to the [Releases](../../releases) page.
2. Download `APN-Manager.apk` from the **`latest`** release.
3. Install on a test device.
4. Open the app → choose an `.apnpack` profile → confirm SIM carrier info if desired → open APN settings.

### 🌐 Use the web panel
The web panel is published via GitHub Pages from the `docs/` folder of the `main` branch:

- **https://alvandcode.github.io/APN-Manager/**

### 🤝 How to contribute / مشارکت
- Fork the repository and open a Pull Request.
- Keep changes focused; update both Persian and English strings.
- Do not add permissions or network access without strong justification.
- Update `apn-format/schema.json` and the app/web validation together.

---

## 📄 License
See [LICENSE](LICENSE).

---

## 🤝 دعوت به مشارکت / Call for Contributions
همکاری با شما برای تکمیل این پروژه بسیار ارزشمند است. اگر مشکلی دیدید، یا ایده‌ای برای ویژگی‌افزایی دارید، لطفاً **Pull Request** باز کنید. ما از هرگونه بازخورد، گزارش باگ و پیشنهاد بهره‌برداری خوشحال می‌شویم!

---

**⭐ Star this repository · ⬇️ Download latest APK · 🤝 Contribute · 📖 Read the docs**
