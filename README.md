# Parlons – French for Kenyans (English / Kiswahili / Sheng)

**100% offline. No API key, no account, no server, no subscription — ever.**
All progress lives on-device in SharedPreferences. System TTS/STT is used
when available; bundled Vosk + Piper/ONNX assets (optional, user-supplied
per spec §9.3) enable the fully-offline voice path with no code changes.

## What's inside (merged build)
- 6 units / 65 lessons, ~860 unique phrases (deduplicated per spec §4.2),
  13 exercise types + 60s speed round.
- Simba 🦁: 12 scripted scenarios x 2 registers (Débutant/Intermédiaire),
  branching replies, fuzzy correction — all on-device. Simba roasts gently
  (food/football/effort only, always with love) in lessons, chats, calls
  and results; `roast` lesson in Unit 5 collects the classics.
- Hearts/gems/XP/streaks/daily goal/stars, 18 badges, daily quests,
  Leitner SRS review, word bank, weekly recap line, custom lessons.

## Set up in Android Studio
1. File > New > New Project > **Empty Activity**. Name: Parlons, package: `com.francofun`, Minimum SDK: API 26, Language: Kotlin. Finish.
2. Close the project's Gradle sync popup if it appears. In the project folder replace/copy:
   - `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`
   - `gradle/wrapper/gradle-wrapper.properties`
   - `app/build.gradle.kts`
   - `app/src/main/AndroidManifest.xml`
   - Delete the template's files in `app/src/main/java/com/francofun/` (MainActivity.kt and the `ui/theme` folder) and copy in all the `.kt` files from this project.
   - Delete `gradle/libs.versions.toml` if it exists.
3. File > Sync Project with Gradle Files.
4. Run on a phone/emulator. French system voice recommended for best TTS.
5. Build APK: Build > Build Bundle(s) / APK(s) > Build APK(s).
6. Optional offline voice (§9.3): drop `vosk-model-fr/` + `piper-fr/voice.onnx[.json]`
   into `app/src/main/assets/` — free downloads (Vosk `vosk-model-small-fr-0.22`,
   Piper `fr_FR-siwis-medium`). No key, no account, redistributable.
