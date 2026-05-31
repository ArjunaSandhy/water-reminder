# 💧 Water Reminder

**Water Reminder** adalah aplikasi Android native yang membantu pengguna memenuhi kebutuhan minum air harian dengan fitur pencatatan konsumsi, pengingat berkala, dan statistik riwayat.

## 📱 Fitur Utama

- ✅ **Pencatatan Konsumsi Air** - Catat setiap kali minum dengan mudah
- ✅ **Progress Harian Visual** - Circular progress menampilkan pencapaian target
- ✅ **Pengingat Berkala** - Notifikasi otomatis dengan interval yang dapat disesuaikan
- ✅ **Riwayat Lengkap** - Lihat histori konsumsi air per hari
- ✅ **Target Kustom** - Atur target harian sesuai kebutuhan (500-5000 ml)
- ✅ **Dark Mode** - Dukungan tema gelap yang mengikuti sistem
- ✅ **100% Offline** - Semua data tersimpan lokal, tidak perlu internet

## 🛠️ Tech Stack

| Komponen | Teknologi |
|----------|-----------|
| **Bahasa** | Kotlin 1.9+ |
| **UI Framework** | Jetpack Compose (BOM 2024.02+) |
| **Arsitektur** | Clean Architecture + MVVM |
| **Database** | Room 2.6+ |
| **State Management** | ViewModel + StateFlow |
| **Preferences** | DataStore |
| **Background Jobs** | WorkManager 2.9+ |
| **Navigation** | Navigation Compose |
| **Dependency Injection** | Hilt 2.50+ |
| **Testing** | JUnit, MockK, Turbine |
| **Min SDK** | API 24 (Android 7.0) |
| **Target SDK** | API 34 (Android 14) |

## 📐 Arsitektur

Aplikasi ini menggunakan **Clean Architecture** dengan pembagian layer:

```
├── data/
│   ├── local/          # Room Database (Entity, DAO, Database)
│   ├── repository/     # Repository pattern
│   └── datastore/      # DataStore untuk preferences
├── domain/
│   └── usecase/        # Business logic
├── ui/
│   ├── home/           # Layar beranda
│   ├── add/            # Input konsumsi air
│   ├── history/        # Riwayat konsumsi
│   ├── reminder/       # Pengaturan pengingat
│   ├── settings/       # Pengaturan aplikasi
│   ├── splash/         # Splash screen
│   ├── success/        # Konfirmasi berhasil
│   ├── theme/          # Theme & styling
│   └── navigation/     # Navigation graph
├── worker/             # WorkManager workers
└── di/                 # Hilt modules
```

## 🗄️ Data Schema

### Room Entity: WaterEntry

| Field | Type | Constraint |
|-------|------|------------|
| id | Long | PrimaryKey, autoGenerate |
| amountMl | Int | 50-2000, NOT NULL |
| note | String? | Max 200 chars, NULLABLE |
| timestamp | Long | currentTimeMillis() |
| date | String | yyyy-MM-dd format |

### DataStore Keys

- `DAILY_TARGET_ML` (Int) = 2000
- `UNIT` (String) = "ml" atau "oz"
- `REMINDER_ENABLED` (Boolean) = false
- `REMINDER_INTERVAL_MIN` (Int) = 60
- `REMINDER_START_HOUR` (Int) = 7
- `REMINDER_END_HOUR` (Int) = 22

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog | 2023.1.1+
- JDK 17
- Android SDK API 34
- Gradle 8.2+

### Build & Run

1. Clone repository:
```bash
git clone https://github.com/ArjunaSandhy/water-reminder.git
cd water-reminder
```

2. Buka project di Android Studio

3. Sync Gradle dan build project

4. Run di emulator atau device fisik (Min API 24)

### Run Tests

```bash
./gradlew test
```

## 📱 Screenshots

### Home Screen
- Circular progress menampilkan konsumsi harian
- Kartu target dan sisa konsumsi
- Tombol tambah minum yang mudah diakses

### Add Water Screen
- Stepper untuk input jumlah ml
- Quick selection chips (150, 200, 250, 500 ml)
- Field catatan opsional dengan validasi

### History Screen
- Daftar riwayat dikelompokkan per tanggal
- Badge "Target tercapai" untuk hari yang sukses
- Preview entri per hari

### Settings Screen
- Pengaturan target harian (500-5000 ml)
- Pilihan satuan (ml/oz)
- Info aplikasi dan share

## 🔔 Notification System

Aplikasi menggunakan **WorkManager** untuk notifikasi berkala:

- ⏰ Interval: 30, 60, 90, atau 120 menit
- 🕐 Jam aktif dapat disesuaikan (default 07:00 - 22:00)
- ✅ Otomatis berhenti jika target tercapai
- 🔄 Auto-restart setelah device reboot
- 🔋 Battery-friendly dengan constraints

## 📊 Unit Tests

Coverage utama:
- ✅ HomeViewModel - State management & progress calculation
- ✅ AddWaterUseCase - Validasi input & business logic
- ✅ Validation - Min/max constraints
- ✅ Error handling

Run tests:
```bash
./gradlew testDebugUnitTest
```

## 🎨 Design Decisions

### Warna
- **Primary**: #2196F3 (Blue)
- **Background Light**: #F5F9FF
- **Background Dark**: #1A2438
- **Surface Dark**: #243150

### Typography
- **Headings**: Material 3 Typography (Bold)
- **Body**: Default system font
- **Display**: Large numbers untuk progress

### UX Principles
- ⚡ Input cepat: Max 3 tap dari home ke saved
- 🎯 Visual feedback: Progress real-time via StateFlow
- 🌙 Dark mode: Follow system preference
- ♿ Accessibility: Content descriptions pada semua elemen interaktif

## 🔐 Permissions

- `POST_NOTIFICATIONS` - Untuk notifikasi (Android 13+)
- `RECEIVE_BOOT_COMPLETED` - Restart WorkManager setelah reboot
- `SCHEDULE_EXACT_ALARM` - Scheduling notifikasi
- `WAKE_LOCK` - WorkManager background execution

## 📝 License

Aplikasi ini dibuat untuk keperluan pembelajaran dan portfolio.

## 👤 Author

**Arjuna Sandhy**
- GitHub: [@ArjunaSandhy](https://github.com/ArjunaSandhy)

## 🙏 Acknowledgments

- PRD Document - VibeCoding Edition v1.0.0
- Jetpack Compose Material 3 Design
- Android Architecture Components

---

**💧 Stay Hydrated, Stay Healthy! 💧**
