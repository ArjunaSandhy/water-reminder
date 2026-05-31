# ⚡ Quick Start Guide - Water Reminder

## 🎯 Cara Tercepat Menjalankan Aplikasi

---

## 📥 Step 1: Download
```bash
git clone https://github.com/ArjunaSandhy/water-reminder.git
```

## 📂 Step 2: Buka di Android Studio
1. Buka **Android Studio**
2. **File → Open**
3. Pilih folder **water-reminder**
4. Klik **OK**

## ⏳ Step 3: Tunggu Gradle Sync
- Otomatis download dependencies (~5-10 menit pertama kali)
- Tunggu sampai muncul: **"Gradle build finished"**

## 📱 Step 4: Pilih Device
- **Emulator:** Buat AVD baru (Tools → Device Manager)
- **HP Fisik:** Enable USB Debugging, connect via USB

## ▶️ Step 5: Run!
- Klik tombol **▶️ Run** di toolbar
- Atau tekan **Shift + F10**
- App akan install dan launch otomatis

---

## 🎉 Done!

App sekarang running di device Anda!

---

## 📚 Butuh Bantuan?

- **Detail lengkap:** Baca [ANDROID_STUDIO_GUIDE.md](ANDROID_STUDIO_GUIDE.md)
- **Build manual:** Baca [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md)
- **Testing:** Baca [TESTING_REPORT.md](TESTING_REPORT.md)

---

## ⚠️ Troubleshooting Cepat

### Gradle Sync Failed?
```bash
File → Invalidate Caches → Invalidate and Restart
```

### Device Not Detected?
```bash
adb devices  # Check koneksi
```

### Build Failed?
```bash
./gradlew clean build
```

---

## 💡 Pro Tips

- **First build:** 1-3 menit
- **Incremental build:** 10-30 detik
- **Shift + Shift:** Search everything
- **Ctrl + Alt + L:** Format code

---

**💧 Happy Coding!**
