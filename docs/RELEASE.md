# KELMA 216 — release signing

This repository does **not** contain a keystore. Create one yourself and keep it out of git.
`app/build.gradle.kts` reads signing values from `keystore.properties` at the project root
(already git-ignored). If that file is absent, release builds are produced **unsigned**.

## 1. Create a keystore (one time)

Run from the project root (PowerShell). Replace the placeholders and keep the password safe:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
& "$env:JAVA_HOME\bin\keytool.exe" -genkeypair -v `
  -keystore "$PWD\release.jks" `
  -alias kelma216 `
  -keyalg RSA -keysize 2048 -validity 10000 `
  -storepass "CHANGE_ME_STORE" -keypass "CHANGE_ME_KEY" `
  -dname "CN=KELMA 216, OU=Game, O=Kelma, L=Tunis, S=Tunis, C=TN"
```

## 2. Point Gradle at it

Create `keystore.properties` at the project root (never commit it):

```properties
storeFile=C:\\path\\to\\kelma216\\release.jks
storePassword=CHANGE_ME_STORE
keyAlias=kelma216
keyPassword=CHANGE_ME_KEY
```

## 3. Build signed artifacts

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat assembleRelease   # -> app\build\outputs\apk\release\app-release.apk  (signed)
.\gradlew.bat bundleRelease     # -> app\build\outputs\bundle\release\app-release.aab (signed)
```

## 4. Verify

```powershell
& "$env:JAVA_HOME\bin\apksigner.bat" verify --print-certs `
  "app\build\outputs\apk\release\app-release.apk"
```

If `apksigner` is not on the PATH, use the copy under
`C:\Users\<you>\AppData\Local\Android\Sdk\build-tools\<version>\apksigner.bat`.

## Notes

- Without `keystore.properties`, the Gradle build still succeeds and emits
  `app-release-unsigned.apk` / unsigned `.aab` — useful for size checks and lint, but not
  distributable.
- Never commit `release.jks`, `keystore.properties`, or `*.keystore`.
