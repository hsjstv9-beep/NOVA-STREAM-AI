# NOVA STREAM AI 🛰️📺
### Next-Generation Android IPTV & Stream Intelligence Suite

NOVA STREAM AI is an enterprise-grade Android application designed for Android phones, tablets, and Android TV (Leanback). Inspired by high-speed stream inspection and playlist parsing workflows, NOVA STREAM AI delivers a completely original 2026 cyber-aesthetic visual identity with glassmorphism, electric orange and neon violet accents, and an offline-first architecture.

---

## 🌟 Key Features & Modules

### 1. 📊 Intelligence Dashboard
* **Real-Time Stream Metrics:** Total playlists, active channels, live streams vs VOD movies, and favorites.
* **System Health Monitor:** Instant connection status and memory utilization.
* **Quick Actions:** Direct Stream URL Player, M3U/M3U8 Importer, Speed Diagnostics, and Profile Switcher.
* **Activity Audit Trail:** Real-time logging of user actions, imports, and system events.

### 2. 📂 Playlist Manager & Extended Parser
* **Multi-Format Support:** Robust parsing of standard and `#EXTM3U` playlists (URLs or raw text).
* **Tag Extraction:** Automatic detection of `tvg-id`, `tvg-name`, `tvg-logo`, and `group-title`.
* **Smart Content Classification:** Automatically classifies channels into **LIVE**, **VOD (Movies)**, **SERIES**, or **RADIO**.
* **Combolist Fixer & Formatter:** Inspired by IPTV combo editors—cleans whitespace, repairs delimiter inconsistencies (`;`, `\t`, `|` $\rightarrow$ `:`), and deduplicates entries.

### 3. 🎬 Media Player (ExoPlayer / Media3 Engine)
* **Hardware-Accelerated Video Surface:** Supports HLS (`.m3u8`), MPEG-TS, MP4, and MKV streams with zero buffering delay.
* **Rich Glassmorphic Controls:** Play/Pause, $\pm 10\text{s}$ seek, aspect ratio switcher (`16:9`, `4:3`, `FILL`), and technical stream inspector.
* **Fullscreen & Picture-in-Picture:** Adaptive layout for phone and 10-foot TV experience.
* **Quick Channel Switcher:** Browse and switch channels directly within the player view without interruption.
* **Android TV D-Pad Remote Navigation:** Full key handling for `DPAD_CENTER`, `DPAD_UP/DOWN`, and media remote buttons.

### 4. ⚡ Stream Diagnostics & Health Audit
* **Direct Diagnostic Ping:** Tests stream reachability, HTTP response code (`200`, `206`, `302`, `403`, `404`), latency in milliseconds, and CDN headers.
* **Speed Profiles:** Configurable testing modes: Normal, Fast, and Turbo ping.
* **One-Tap Playback:** Immediately launch tested streams in the player upon successful diagnostic response.

### 5. 📈 Analytics & Viewing History
* **Visual Content Breakdown:** Proportional progress bars displaying Live vs VOD vs Series distribution.
* **Top Categories:** Ranked channel groupings by count.
* **Viewing History:** Complete record of watched channels, durations, and timestamps with instant replay action.

### 6. 👤 Multi-Profile Management
* **Family & Kids Profiles:** Multi-user isolation with customizable avatars.
* **Parental Controls:** Kids Mode category restriction and optional 4-digit PIN security lock.

### 7. ⚙️ Settings & Portable Backup
* **Themes:** Dark Graphite (Default 2026), Cyber Violet, Pure Black (OLED), and Light.
* **Localization:** English, Arabic (Morocco / Darija), and French.
* **Engine Presets:** Stream timeout customization (5s–30s) and hardware GPU acceleration toggle.
* **Full JSON Backup & Restore:** Export and import complete application state (playlists, channels, profiles, settings) in portable JSON format.

---

## 🏗️ Architecture & Tech Stack

```
com.novastream.ai/
├── NovaStreamApp.kt                  # Application initialization & DI container
├── MainActivity.kt                   # Single Activity, D-Pad interceptor, Compose host
├── data/
│   ├── model/Models.kt              # Domain entities, enums & backup structures
│   ├── db/NovaDatabaseHelper.kt     # High-performance SQLite engine & CRUD
│   ├── parser/M3UParser.kt          # Extended M3U/M3U8 parser & combolist cleaner
│   ├── diagnostics/StreamHealthTester.kt # Async HTTP stream ping & latency tester
│   ├── backup/BackupManager.kt      # Portable JSON import/export engine
│   └── repository/NovaRepository.kt # Coroutine repository pattern
└── ui/
    ├── theme/                       # Color, Type, and Theme definitions
    ├── components/                  # GlassCard, NovaButton, StatusBadge, HeaderBar
    ├── screens/                     # 8 dedicated module screens
    └── viewmodel/NovaViewModel.kt   # MVVM reactive StateFlow viewmodel
```

---

## 📱 Building on Android via Termux

NOVA STREAM AI is 100% manageable and buildable directly on an Android device using Termux:

### 1. Install Prerequisites in Termux
```bash
pkg update && pkg upgrade -y
pkg install openjdk-17 git gradle -y
```

### 2. Clone and Setup Environment
```bash
git clone https://github.com/your-username/NovaStreamAI.git
cd NovaStreamAI
export ANDROID_HOME=$HOME/android-sdk
```

### 3. Build Debug APK
```bash
gradle assembleDebug --no-daemon --stacktrace
```
The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🚀 PocketDev / Container Build Command

```bash
timeout 15m gradle -Dorg.gradle.jvmargs= --no-daemon --max-workers=2 \
  --init-script /root/.gradle/init.d/pocketdev-android.gradle \
  -Pandroid.aapt2FromMavenOverride=/root/android-sdk/build-tools/35.0.0/aapt2 \
  assembleDebug --console=plain --stacktrace
```

---

## 🛠️ Troubleshooting Guide

| Issue | Solution |
| :--- | :--- |
| **Stream Won't Play (HTTP 403 / 404)** | Use the built-in **Diagnostics** tab to ping the URL. Many IPTV streams require specific User-Agents or token refresh. |
| **VideoView Black Screen** | Verify that `Hardware Acceleration` is enabled in Settings, and ensure device has internet permission. |
| **TV Remote D-Pad Navigation** | Focus rings glow in Electric Orange and Neon Violet. Press `DPAD_CENTER` or `ENTER` to select cards and buttons. |
| **Corrupted M3U Formatting** | Paste the playlist into the **Combo Editor** tab in the Playlist Manager to automatically fix separator syntax. |

---

## 📜 License
Distributed under the MIT License. Developed for educational, diagnostic, and lawful personal stream playback.
