# 🚀 Build Instructions - Water Reminder

## ✅ Pre-Build Checklist

Sebelum build, pastikan sudah mengecek:

- [x] ✅ 35 Kotlin files created
- [x] ✅ 2 Unit test files created  
- [x] ✅ All syntax errors fixed
- [x] ✅ Dependencies configured
- [x] ✅ AndroidManifest complete
- [x] ✅ Git committed

**Status:** READY TO BUILD 🎉

---

## 📋 Prerequisites

### Required Software
- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17** or newer
- **Android SDK API 34**
- **Gradle 8.2+** (will be downloaded automatically)

### System Requirements
- **OS:** Windows 10+, macOS 10.14+, or Linux
- **RAM:** Minimum 8GB (16GB recommended)
- **Storage:** 10GB free space

---

## 🔧 Setup Instructions

### 1. Clone Repository
```bash
git clone https://github.com/ArjunaSandhy/water-reminder.git
cd water-reminder
```

### 2. Open in Android Studio
1. Launch Android Studio
2. Select **"Open an Existing Project"**
3. Navigate to `water-reminder` folder
4. Click **"OK"**

### 3. Gradle Sync
Android Studio will automatically:
- Download required Gradle version
- Download all dependencies
- Index project files

**⏱️ First sync may take 5-10 minutes**

---

## 🏗️ Build Commands

### Build Debug APK
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Build Release APK (Unsigned)
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Clean Build
```bash
./gradlew clean
./gradlew build
```

---

## 🧪 Run Tests

### Unit Tests
```bash
./gradlew test
```

### Test with Coverage
```bash
./gradlew testDebugUnitTest
```

### View Test Reports
After running tests, open:
```
app/build/reports/tests/testDebugUnitTest/index.html
```

---

## 📱 Install & Run

### Using Android Studio
1. Connect Android device or start emulator
2. Click **"Run"** button (▶️) or press `Shift + F10`
3. Select target device
4. Wait for installation and launch

### Using Command Line

#### Install on Connected Device
```bash
./gradlew installDebug
```

#### Uninstall
```bash
./gradlew uninstallDebug
```

#### Run on Emulator
```bash
# Start emulator
emulator -avd <emulator_name>

# Install app
./gradlew installDebug

# Launch app
adb shell am start -n com.waterreminder/.MainActivity
```

---

## 🐛 Troubleshooting

### Problem: Gradle Sync Failed
**Solution:**
```bash
./gradlew clean
# Delete .gradle folder
rm -rf .gradle
# Sync again in Android Studio
```

### Problem: Build Failed - Missing SDK
**Solution:**
1. Open Android Studio
2. Tools → SDK Manager
3. Install **Android SDK Platform 34**
4. Install **Android SDK Build-Tools 34.0.0**

### Problem: KSP Error
**Solution:**
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### Problem: Out of Memory
**Solution:**
Add to `gradle.properties`:
```
org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=512m
```

### Problem: Dependencies Not Downloading
**Solution:**
1. Check internet connection
2. Add proxy if needed in `gradle.properties`:
```
systemProp.http.proxyHost=your.proxy.host
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=your.proxy.host
systemProp.https.proxyPort=8080
```

---

## 📊 Build Variants

### Debug Build
- Debuggable: Yes
- Minification: No
- Shrinking: No
- Use for: Development & Testing

### Release Build
- Debuggable: No
- Minification: Yes (ProGuard)
- Shrinking: Yes
- Use for: Production

---

## 🔐 Signing Release APK

### 1. Generate Keystore
```bash
keytool -genkey -v -keystore water-reminder.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias water-reminder-key
```

### 2. Configure in `app/build.gradle.kts`
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../water-reminder.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = "water-reminder-key"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... other configs
        }
    }
}
```

### 3. Build Signed Release
```bash
export KEYSTORE_PASSWORD=your_store_password
export KEY_PASSWORD=your_key_password
./gradlew assembleRelease
```

---

## 📦 Build Outputs

### Debug APK
- **Path:** `app/build/outputs/apk/debug/`
- **File:** `app-debug.apk`
- **Size:** ~15-20 MB

### Release APK
- **Path:** `app/build/outputs/apk/release/`
- **File:** `app-release.apk`
- **Size:** ~10-15 MB (after ProGuard)

### AAB (Android App Bundle)
```bash
./gradlew bundleRelease
```
- **Path:** `app/build/outputs/bundle/release/`
- **File:** `app-release.aab`
- **Use for:** Google Play Store upload

---

## 🎯 Build Success Criteria

✅ No compilation errors  
✅ All 25 unit tests pass  
✅ APK size < 15 MB  
✅ Min SDK: API 24 (Android 7.0)  
✅ Target SDK: API 34 (Android 14)  
✅ Cold start < 2 seconds  
✅ No memory leaks  

---

## 📈 Performance Optimization

### Before Release
1. **Run Lint**
   ```bash
   ./gradlew lint
   ```

2. **Check APK Size**
   ```bash
   ./gradlew assembleRelease
   ls -lh app/build/outputs/apk/release/
   ```

3. **Analyze APK**
   - Build → Analyze APK in Android Studio
   - Check for large resources

4. **Profile App**
   - Use Android Profiler
   - Check memory leaks
   - Monitor CPU usage

---

## 🚀 Deployment

### Google Play Console
1. Build AAB:
   ```bash
   ./gradlew bundleRelease
   ```

2. Upload to Play Console:
   - Go to Google Play Console
   - Create new release
   - Upload `app-release.aab`
   - Fill store listing
   - Submit for review

### Direct Distribution
1. Build signed APK
2. Share via:
   - Website download
   - Firebase App Distribution
   - Email/messaging

---

## 📞 Support

For build issues:
1. Check [TESTING_REPORT.md](TESTING_REPORT.md)
2. Review error logs in `build/outputs/logs/`
3. Clean and rebuild
4. Check Android Studio Event Log

---

**Last Updated:** May 31, 2026  
**Build Status:** ✅ READY
