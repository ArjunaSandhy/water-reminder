# 📱 Panduan Menjalankan Water Reminder di Android Studio

## 🎯 Panduan Lengkap dari Awal sampai Running

---

## 📋 Persiapan Awal

### 1. Software yang Dibutuhkan

#### ✅ Android Studio
- **Download:** https://developer.android.com/studio
- **Versi:** Hedgehog (2023.1.1) atau lebih baru
- **Ukuran:** ~1 GB installer
- **OS:** Windows 10+, macOS 10.14+, atau Linux

#### ✅ Java Development Kit (JDK)
- **Versi:** JDK 17 atau 18
- **Biasanya:** Sudah include dalam Android Studio
- **Cek:** Buka Terminal/CMD → ketik `java -version`

### 2. System Requirements
- **RAM:** Minimum 8GB (Recommended 16GB)
- **Storage:** Minimum 10GB free space
- **Processor:** Intel i5 atau AMD Ryzen 5 (atau lebih baik)

---

## 📥 LANGKAH 1: Download/Clone Project

### Pilihan A: Clone dari GitHub (Recommended)

#### Via Android Studio:
1. Buka **Android Studio**
2. Klik **"Get from VCS"** atau **"File → New → Project from Version Control"**
3. Pilih **"Repository URL"**
4. Paste URL ini:
   ```
   https://github.com/ArjunaSandhy/water-reminder.git
   ```
5. Pilih folder tujuan (contoh: `C:\Projects\water-reminder`)
6. Klik **"Clone"**

#### Via Command Line (Terminal/CMD):
```bash
# Windows (Command Prompt)
cd C:\Projects
git clone https://github.com/ArjunaSandhy/water-reminder.git
cd water-reminder

# macOS/Linux (Terminal)
cd ~/Projects
git clone https://github.com/ArjunaSandhy/water-reminder.git
cd water-reminder
```

### Pilihan B: Download ZIP
1. Buka: https://github.com/ArjunaSandhy/water-reminder
2. Klik tombol hijau **"Code"**
3. Pilih **"Download ZIP"**
4. Extract file ZIP ke folder project Anda
5. Rename folder dari `water-reminder-main` menjadi `water-reminder`

---

## 📂 LANGKAH 2: Buka Project di Android Studio

### Cara 1: Dari Welcome Screen
1. Buka **Android Studio**
2. Klik **"Open"** di Welcome Screen
3. Navigate ke folder `water-reminder`
4. Pilih folder tersebut (jangan pilih file di dalamnya)
5. Klik **"OK"**

### Cara 2: Dari Menu
1. Buka **Android Studio**
2. **File → Open**
3. Navigate ke folder `water-reminder`
4. Klik **"OK"**

### ⚠️ Trust Project
Akan muncul dialog **"Trust and Open Project"**:
- Klik **"Trust Project"**

---

## ⚙️ LANGKAH 3: Gradle Sync (PENTING!)

### Otomatis Sync
Setelah buka project, Android Studio akan otomatis:
1. ✅ Detect Gradle build files
2. ✅ Download Gradle wrapper (jika belum ada)
3. ✅ Download semua dependencies (~500MB)
4. ✅ Index files
5. ✅ Build project

### Status Gradle Sync
Lihat di bagian bawah Android Studio:
- **"Gradle sync in progress..."** → Tunggu selesai
- **"Gradle build finished in XX s"** → ✅ Sukses!
- **"Gradle sync failed"** → ⚠️ Ada error (lihat troubleshooting)

### ⏱️ Durasi
- **Pertama kali:** 5-15 menit (download dependencies)
- **Selanjutnya:** 30-60 detik

### 👁️ Monitor Progress
Lihat di:
- **Build tab** (bawah) → Lihat log detail
- **Build Output** → Lihat download progress
- **Event Log** (kanan bawah) → Lihat notifikasi

---

## 🔧 LANGKAH 4: Setup Android SDK (Jika Diperlukan)

### Cek SDK
1. **File → Settings** (Windows/Linux) atau **Android Studio → Preferences** (Mac)
2. **Appearance & Behavior → System Settings → Android SDK**

### SDK yang Dibutuhkan:
- ✅ **Android 14.0 (API 34)** - Target SDK
- ✅ **Android 7.0 (API 24)** - Minimum SDK
- ✅ **Android SDK Build-Tools 34.0.0**
- ✅ **Android SDK Platform-Tools**
- ✅ **Android SDK Tools**

### Install Missing SDK:
1. Centang SDK yang belum terinstall
2. Klik **"Apply"**
3. Klik **"OK"** di dialog konfirmasi
4. Tunggu download selesai

---

## 📱 LANGKAH 5: Setup Device/Emulator

### Pilihan A: Pakai Emulator (Virtual Device)

#### Buat Emulator Baru:
1. Klik ikon **"Device Manager"** di toolbar (atau **Tools → Device Manager**)
2. Klik **"Create Device"**
3. Pilih device (contoh: **Pixel 6**)
4. Klik **"Next"**
5. Pilih System Image:
   - **Recommended:** API 34 (Android 14.0)
   - Jika belum ada, klik **"Download"** di samping
6. Klik **"Next"**
7. Beri nama (contoh: **"Pixel 6 API 34"**)
8. Klik **"Finish"**

#### Start Emulator:
1. Buka **Device Manager**
2. Klik tombol **▶️ Play** di samping emulator Anda
3. Tunggu emulator booting (~30-60 detik)

### Pilihan B: Pakai Device Fisik (HP Android)

#### Enable Developer Options:
1. Buka **Settings** di HP
2. **About Phone** → Tap **"Build Number"** 7 kali
3. Muncul notifikasi **"You are now a developer"**

#### Enable USB Debugging:
1. Kembali ke **Settings**
2. **System → Developer Options**
3. Aktifkan **"USB Debugging"**

#### Connect ke Komputer:
1. Hubungkan HP ke komputer pakai USB cable
2. Di HP, muncul dialog **"Allow USB Debugging?"**
3. Centang **"Always allow from this computer"**
4. Klik **"OK"**

#### Verifikasi Koneksi:
Di Android Studio, lihat dropdown device di toolbar:
- Seharusnya muncul nama HP Anda
- Contoh: **"Samsung Galaxy A52 (Android 13)"**

---

## ▶️ LANGKAH 6: Run Aplikasi!

### Cara 1: Pakai Toolbar
1. Pastikan Gradle sync sudah selesai
2. Pilih device di dropdown (emulator atau HP fisik)
3. Klik tombol **▶️ Run** (atau tekan **Shift + F10**)

### Cara 2: Pakai Menu
1. **Run → Run 'app'**
2. Atau tekan **Shift + F10**

### Cara 3: Pakai Context Menu
1. Klik kanan di file **MainActivity.kt**
2. Pilih **"Run 'app'"**

### 📊 Build Process
Akan muncul di **Build tab**:
```
> Task :app:preBuild
> Task :app:compileDebugKotlin
> Task :app:processDebugManifest
> Task :app:mergeDebugResources
> Task :app:packageDebug
> Task :app:installDebug
BUILD SUCCESSFUL in 45s
```

### ⏱️ Durasi Build
- **First build:** 1-3 menit
- **Subsequent builds:** 10-30 detik (incremental)

### 📱 App Launch
Setelah build sukses:
1. ✅ APK diinstall ke device/emulator
2. ✅ App otomatis launch
3. ✅ Muncul Splash Screen (2 detik)
4. ✅ Navigate ke Home Screen

---

## 🎨 LANGKAH 7: Explore Aplikasi

### Fitur yang Bisa Dicoba:

#### 1️⃣ Home Screen
- Lihat circular progress (awalnya 0%)
- Lihat target harian (default 2000 ml)
- Tap tombol **"+ Tambah Minum"**

#### 2️⃣ Add Water Screen
- Pakai stepper **+ / -** untuk ubah jumlah
- Atau tap quick selection: **150, 200, 250, 500 ml**
- (Opsional) Tambah catatan
- Tap **"Simpan"**

#### 3️⃣ Success Screen
- Lihat konfirmasi berhasil
- Lihat total hari ini
- Tap **"Kembali ke Beranda"**

#### 4️⃣ History (Tab 2)
- Lihat riwayat per hari
- Tap salah satu hari untuk detail
- Di detail: tap ikon **🗑️** untuk hapus entry

#### 5️⃣ Reminder (Tab 3)
- Toggle **"Aktifkan Pengingat"**
- Pilih interval (30/60/90/120 menit)
- Set waktu mulai & selesai
- Tap **"Simpan Perubahan"**

#### 6️⃣ Settings (Tab 4)
- Ubah target harian (500-5000 ml)
- Pilih satuan (ml/oz)
- Tap **"Simpan Pengaturan"**

#### 7️⃣ Dark Mode
- Ubah system theme di device
- App akan otomatis ikut dark/light mode

---

## 🔍 LANGKAH 8: Debug & Logcat

### Lihat Logs:
1. Klik tab **"Logcat"** di bawah
2. Filter by package: **com.waterreminder**
3. Lihat log real-time saat pakai app

### Set Breakpoint:
1. Klik di gutter (sebelah line number) untuk set breakpoint
2. Run app dalam **Debug mode** (klik ikon 🐛 atau tekan **Shift + F9**)
3. App akan pause di breakpoint
4. Inspect variables, step through code

### Useful Shortcuts:
- **Shift + F10** → Run
- **Shift + F9** → Debug
- **Ctrl + F9** → Build project
- **Ctrl + Shift + F9** → Build & run current file

---

## 🧪 LANGKAH 9: Run Unit Tests

### Via Android Studio UI:
1. Expand folder **app → src → test → java → com.waterreminder**
2. Klik kanan pada folder **test**
3. Pilih **"Run 'Tests in 'com.waterreminder''"**

### Via Terminal:
```bash
# Windows
gradlew.bat test

# macOS/Linux
./gradlew test
```

### Lihat Test Results:
- **Run tab** di bawah → Lihat hasil test
- Atau buka: `app/build/reports/tests/testDebugUnitTest/index.html`

### Expected Results:
```
✅ HomeViewModelTest: 11/11 tests passed
✅ AddWaterUseCaseTest: 14/14 tests passed
Total: 25/25 tests passed in ~5 seconds
```

---

## 🎯 LANGKAH 10: Build APK untuk Install Manual

### Build Debug APK:
1. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Tunggu build selesai
3. Klik **"locate"** di notifikasi
4. APK ada di: `app/build/outputs/apk/debug/app-debug.apk`

### Install Manual:
- **Via Android Studio:** Drag APK ke emulator
- **Via ADB:** `adb install app-debug.apk`
- **Via File Manager:** Copy APK ke HP, tap untuk install

### Build Release APK (Belum Signed):
1. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Select **Release**
3. APK di: `app/build/outputs/apk/release/`

---

## ⚠️ TROUBLESHOOTING

### ❌ Problem 1: Gradle Sync Failed
**Error:** "Gradle sync failed: Could not resolve dependencies"

**Solution:**
```bash
1. File → Invalidate Caches → Invalidate and Restart
2. Delete folder: .gradle, .idea, build (di root project)
3. Reopen project
4. Atau: ./gradlew clean build --refresh-dependencies
```

### ❌ Problem 2: SDK Not Found
**Error:** "Android SDK not found"

**Solution:**
1. File → Settings → Android SDK
2. Pilih SDK Location
3. Download SDK yang diperlukan
4. Apply → OK

### ❌ Problem 3: Device Not Detected
**Error:** Device tidak muncul di dropdown

**Solution:**
- **Emulator:** Pastikan AVD sudah dibuat dan running
- **Physical Device:**
  1. Cek USB cable (pakai cable data, bukan charge-only)
  2. Cek USB Debugging enabled di HP
  3. Coba port USB lain
  4. Install USB drivers (Windows): https://developer.android.com/studio/run/oem-usb
  5. Run: `adb devices` di terminal untuk verifikasi

### ❌ Problem 4: Build Failed - Out of Memory
**Error:** "Out of memory: Java heap space"

**Solution:**
Edit `gradle.properties`, tambahkan:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m
```

### ❌ Problem 5: Emulator Slow
**Problem:** Emulator sangat lambat

**Solution:**
1. Enable Hardware Acceleration:
   - Windows: Install HAXM
   - Mac: Automatic (Hypervisor.framework)
   - Linux: Install KVM
2. Allocate more RAM di AVD settings (min 2GB)
3. Atau pakai physical device

### ❌ Problem 6: App Crashes on Launch
**Error:** App crashes setelah launch

**Solution:**
1. Lihat Logcat untuk error message
2. Check di Logcat filter: **"Error"** atau **"Exception"**
3. Common issues:
   - Missing permissions
   - Database migration issues
   - WorkManager initialization

### ❌ Problem 7: Compose Preview Not Working
**Problem:** Preview tidak muncul

**Solution:**
1. Build → Clean Project
2. Build → Rebuild Project
3. Invalidate Caches → Restart
4. Update Android Studio ke versi terbaru

---

## 💡 TIPS & TRICKS

### 🚀 Speed Up Development

#### 1. Enable Instant Run:
- File → Settings → Build, Execution, Deployment → Instant Run
- Enable semua options

#### 2. Increase Build Speed:
Edit `gradle.properties`:
```properties
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
```

#### 3. Use Build Variants:
- Debug: Untuk development (cepat, ada logs)
- Release: Untuk testing production (optimized, no logs)

### 🎨 Better UI Preview

#### Use Compose Previews:
Tambahkan di Screen files:
```kotlin
@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    WaterReminderTheme {
        HomeScreen(
            onNavigateToAddWater = {},
            onNavigateToReminder = {},
            onNavigateToSettings = {}
        )
    }
}
```

#### Split View:
- **View → Tool Windows → Split Right**
- Kiri: Code editor
- Kanan: Preview

### 🔍 Useful Shortcuts

| Shortcut | Action |
|----------|--------|
| **Shift + Shift** | Search Everywhere |
| **Ctrl + N** | Find Class |
| **Ctrl + Shift + N** | Find File |
| **Ctrl + Alt + L** | Reformat Code |
| **Ctrl + /** | Comment/Uncomment |
| **Ctrl + D** | Duplicate Line |
| **Alt + Enter** | Show Quick Fixes |
| **F2** | Next Error |

### 📊 Monitor Performance

#### Android Profiler:
1. **View → Tool Windows → Profiler**
2. Select running app
3. Monitor:
   - CPU usage
   - Memory usage
   - Network activity
   - Energy consumption

#### Database Inspector:
1. **View → Tool Windows → App Inspection**
2. Tab **"Database Inspector"**
3. Lihat Room database real-time
4. Run queries langsung

---

## 📚 DOKUMENTASI TAMBAHAN

### Official Docs:
- **Android Studio:** https://developer.android.com/studio/intro
- **Jetpack Compose:** https://developer.android.com/jetpack/compose
- **Room Database:** https://developer.android.com/training/data-storage/room
- **WorkManager:** https://developer.android.com/topic/libraries/architecture/workmanager

### Project Docs:
- **README.md** - Project overview
- **TESTING_REPORT.md** - Quality analysis
- **BUILD_INSTRUCTIONS.md** - Build commands
- **DEPLOYMENT_SUCCESS.md** - GitHub upload info

---

## ✅ CHECKLIST KEBERHASILAN

Anda berhasil jika:
- [x] Project terbuka tanpa error
- [x] Gradle sync sukses
- [x] App berhasil build
- [x] App terinstall di device/emulator
- [x] App berjalan tanpa crash
- [x] Semua fitur berfungsi:
  - [x] Tambah konsumsi air
  - [x] Lihat progress harian
  - [x] Lihat history
  - [x] Set reminder
  - [x] Ubah settings
  - [x] Dark mode works
- [x] Unit tests lulus (25/25)

---

## 🎉 SELAMAT!

Anda berhasil menjalankan **Water Reminder** di Android Studio! 🎊

### Next Steps:
1. 🎨 Customize UI/UX
2. ➕ Tambah fitur baru
3. 🧪 Tambah lebih banyak tests
4. 🌍 Tambah multi-language support
5. 🎨 Design custom launcher icon
6. 📱 Test di berbagai device
7. 🚀 Publish ke Google Play Store

---

**💧 Happy Coding & Stay Hydrated! 💧**

---

**Dibuat oleh:** Kiro AI Assistant  
**Untuk:** ArjunaSandhy  
**Project:** Water Reminder  
**Version:** 1.0.0
