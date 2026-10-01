# Parlons

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Offline--First-238636?style=for-the-badge" />
  <img src="https://img.shields.io/badge/65_Lessons-860_Phrases-8957e5?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Status-Active_Development-ff5e6c?style=for-the-badge" />
</p>

> **French for Kenyans — English x Kiswahili x Sheng. Zero accounts. Zero servers. Zero keys.**

Parlons is an offline-first Android French-learning application designed around short lessons, interactive exercises and a distinctly Kenyan learning experience.

## What makes it different

The app is designed to work without an account, server, subscription or mandatory API key. Progress is stored locally on the device.

### Learning system

- 6 units
- 65 lessons
- ~860 unique phrases
- 13 exercise types
- 60-second speed round
- Leitner spaced-repetition review
- Word bank
- Daily goals and quests
- XP, gems, hearts and streaks
- 18 badges
- Weekly recap
- Custom lessons

### Simba

Simba is the conversational learning character:

- 12 scripted scenarios
- Beginner and Intermediate registers
- Branching replies
- Fuzzy correction
- Contextual responses
- Light humour around food, football and effort

The goal is to make practice feel more like a conversation than a worksheet.

## Offline architecture

```text
Android UI
   │
   ├── Lessons
   ├── Exercises
   ├── Review / SRS
   ├── Progress
   └── Simba conversations
           │
           ▼
      Local state
           │
           ▼
    SharedPreferences
```

System TTS/STT is used when available. Optional Vosk + Piper/ONNX assets can provide a more fully offline voice path.

## Build in Android Studio

1. Create an **Empty Activity** project.
2. Use package `com.francofun`.
3. Minimum SDK: API 26.
4. Replace the generated Gradle/configuration and Kotlin source files with this project's files.
5. Sync Gradle.
6. Run on a phone or emulator.
7. Build the APK from Android Studio.

## Optional voice assets

Place the required French Vosk/Piper assets under:

```
app/src/main/assets/
```

The voice path remains optional; the core learning experience does not depend on it.

## Project status

**Active development**

The project combines language learning, offline mobile engineering, gamification and conversational interaction into one Android application.

## Engineering focus

Parlons explores a simple question:

> How much of a useful language-learning experience can be built locally, without requiring a cloud backend?