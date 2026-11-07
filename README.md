# APN Manager

Persian/English Android APN profile helper + static web profile builder.

## Current v1 scope
- Android app (Kotlin + Jetpack Compose), supporting Android 7.0 (API 24) through Android 15 (API 35).
- Import `.apnpack` JSON profiles through Android Sharesheet/file manager.
- Validate profile structure and key fields locally.
- Open Android APN settings for user-assisted registration.
- Light/dark theme and Persian/English app UI.
- Static web panel to create and download `.apnpack` files.
- GitHub Actions builds a debug APK on every push and uploads it as an artifact.

## Important Android limitation
Ordinary third-party apps cannot write carrier APN records directly. The app therefore does not claim to install APNs silently. It validates the imported profile and opens `Settings.ACTION_APN_SETTINGS`; the user completes saving in system settings. Device manufacturers and Android versions may hide or change this settings screen.

The app reads only the SIM network code (MCC + MNC) through `TelephonyManager.getSimOperator()`, which requires no Android permission at all. Consequently the app requests no permissions whatsoever: no phone-state, location, SMS, contacts, or internet permission, and no phone number, IMEI or IMSI is ever read or stored. Universal automatic SIM-carrier selection is still confirmed by the user; only the network code is shown as a helper.

## Build on GitHub (phone-friendly)
1. Create a GitHub repository named `APN-Manager`.
2. Upload all files and folders from this project to the repository root.
3. Open **Actions** and enable workflows if GitHub asks.
4. Push any change or run **Build Android APK** from Actions > Run workflow.
5. Open the completed workflow run and download **APN-Manager-debug** from Artifacts.
6. Extract the ZIP; install `app-debug.apk` on a test phone. Android may ask to allow installation from the file manager.

## Publish the web panel
Enable GitHub Pages for the repository, using the `main` branch and `/web-panel` as the publishing folder if supported by repository Pages settings. If Pages does not offer a subfolder, place/copy the contents of `web-panel` into a `docs/` folder and select `/docs`.

## Profile format
A `.apnpack` file is JSON with `format: "apnpack"` and `version: 1`. See `apn-format/example.apnpack` and `apn-format/schema.json`. Use only APN values verified by the relevant carrier. The example APN is deliberately a placeholder.

Both the app and the web panel enforce the same validation rules as the schema: name (1–80 chars), APN (1–100 chars, letters/digits/dots/dashes/underscores), optional MCC (3 digits) and MNC (2–3 digits), lowercase APN-type tokens, protocol `IPV4`/`IPV6`/`IPV4V6`, auth type `NONE`/`PAP`/`CHAP`/`PAP_OR_CHAP`, proxy limited to hostname-safe characters, and port between 1 and 65535. Imported files are validated locally with a 256 KB size limit.

## Build locally
The repository includes a Gradle 8.9 wrapper. On Linux/macOS make it executable once (`chmod +x gradlew` or `git update-index --chmod=+x gradlew`) and run:

```
./gradlew assembleDebug
```


## Security and privacy
- Profile parsing is local.
- No account, analytics, or remote profile fetch is included.
- No sensitive Android permissions are requested.
- Treat profile files as untrusted input; do not include executable code or secrets.
- A future signed-profile feature should use a maintained cryptographic library and a key-management process; do not treat a plain checksum as a signature.

## Current limitations / next development steps
- Profile details are shown in-app, but the app cannot populate Android's APN editor automatically on ordinary devices.
- No verified carrier database is bundled yet.
- SIM-based auto-detection is deferred until permission behavior and privacy implications are validated across target devices.
- Debug APK is for testing. A release APK should be signed with a private release key stored in GitHub Secrets, never committed to the repository.
