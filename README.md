# 💼 Job Tracker

An Android app to track job applications from "Saved" to "Offer", built with Kotlin and Jetpack Compose.

## 🚀 Features

- Add, edit and delete job applications with notes and a job link
- Status pipeline: Saved, Applied, Interview, Offer, Rejected
- Link validation with automatic `https://`
- Find Jobs shortcut: prefilled LinkedIn and Indeed searches (Global, Germany, Netherlands)
- Light, Dark or system theme, saved across restarts
- Custom Material 3 palette, Poppins font and adaptive app icon

## 🛠️ Technologies Used

- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Architecture:** MVVM, repository pattern
- **DI:** Hilt
- **Database:** Room
- **Settings:** DataStore
- **Async:** Coroutines, Flow, StateFlow
- **Navigation:** Navigation Compose

## 📁 Project Structure

```
JobTracker/
│
├── app/src/main/
│   ├── java/com/deepika/jobtracker/
│   │   ├── JobTrackerApp.kt
│   │   ├── MainActivity.kt
│   │   │
│   │   ├── data/
│   │   │   ├── Job.kt
│   │   │   ├── JobStatus.kt
│   │   │   ├── JobDao.kt
│   │   │   ├── JobDatabase.kt
│   │   │   ├── JobRepository.kt
│   │   │   ├── ThemeMode.kt
│   │   │   └── SettingsRepository.kt
│   │   │
│   │   ├── di/
│   │   │   └── AppModule.kt
│   │   │
│   │   └── ui/
│   │       ├── components/
│   │       ├── navigation/
│   │       ├── list/
│   │       ├── edit/
│   │       ├── detail/
│   │       ├── settings/
│   │       └── theme/
│   │
│   └── res/
│
├── gradle/libs.versions.toml
├── .gitignore
└── README.md
```

Each screen talks to a ViewModel, the ViewModel talks to a repository, and the repository talks to Room. Room exposes jobs as a Flow, so the UI updates automatically when data changes, and everything works without internet.

## 📁 Future Enhancements

- Gemini-powered company insights and job description auto-fill
- Interview preparation section
- Stats screen and follow-up reminders
- Discover tab with live job listings
- Tests and GitHub Actions CI
