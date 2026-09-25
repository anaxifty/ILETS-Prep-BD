# IELTS Prep Application - Development Progress

## Overview
Built a production-grade Android application for IELTS Preparation featuring phone authentication, structured multi-step onboarding, dynamic band target setting, and a full-fledged IELTS Reading Practice Module with real-time scoring and Firestore synchronization.

---

## 1. Authentication & Onboarding
- **Phone Auth / OTP Verification**: Implemented standard phone auth verification flow with auto-formatting, test numbers support, and error handling.
- **Onboarding Flow**:
  - Target IELTS Band selection (5.0 – 9.0)
  - Self-assessed baseline skill levels for Reading, Listening, Writing, and Speaking
  - Preferred study timetable & weekly target hours
  - Automatic synchronization to local DataStore and remote Firestore database under user documents.

---

## 2. IELTS Practice Hub & Practice Modules
- **Practice Hub**:
  - Centralized module dashboard highlighting user's assessed baseline reading and listening band scores.
  - Dynamically recommends passage and listening test difficulty (Easy / Medium / Hard) matched to the user's band profile.
  - Interactive skill cards for Reading and Listening (Active & Unlocked), and Writing/Speaking (Coming Soon badges).
- **Timed Reading Practice Screen**:
  - Full-length reading passage viewer with smooth scrolling and split-tab interface (Passage vs. Questions).
  - Countdown timer initialized to test duration (e.g., 60 minutes) with auto-submit logic upon reaching `00:00`.
  - Supports multiple IELTS question formats: Multiple Choice, True / False / Not Given, Fill in the Blank.
  - Results breakdown with band score mapping and per-question rationale.
- **IELTS Listening Practice Module**:
  - **Firestore Collection**: `listeningTests` storing audio URLs, question sets, difficulty level, audio duration, and transfer time.
  - **ExoPlayer / Media3 Integration**: Audio player enforcing strict IELTS exam rules — audio plays **ONCE** only without scrubbing, rewinding, or replay options.
  - **Simultaneous Question Display**: Questions displayed on screen alongside audio stream for real-time answering.
  - **Question Formats Supported**: Multiple Choice, Form/Note Completion (fill-in-the-blank against displayed form), and Matching grids.
  - **Two-Phase Timer**: Audio duration phase followed by a 10-minute Transfer Time phase with visual notifications and auto-submission at 0:00.
  - **Band Score & Results**: Score calculation mapped to IELTS Listening Band scores with per-question rationale and attempt persistence in Firestore under `users/{userId}/listeningAttempts/{attemptId}`.

---

## 3. Video Lessons Library
- **Firestore Collection**: `videoLessons` collection categorized by IELTS skill (Listening, Reading, Writing, Speaking) and topic, with lesson progress tracking in `users/{userId}/lessonProgress/{lessonId}`.
- **Grouped Video Library UI**: Categorizes lessons cleanly by Skill → Topic with category tabs and progress bars.
- **Continuous Media3 ExoPlayer**: Video playback with smooth position saving, resume capability, and full video player controls.
- **Offline Download Manager**: Downloads video lessons to device local storage with file verification, status bar padding fix, and storage handling.

---

## 4. Weekly / Monthly Mock Exam Module
- **Firestore Collection**: `mockExams` collection storing scheduled full exam sittings referencing existing test IDs across all 4 skills.
- **Timed Full Sitting Flow & Grace Period Buffer**:
  - Chained 4 skills in real IELTS order: **Listening → Reading → Writing → Speaking**.
  - Single continuous timer synced to server start timestamp (`System.currentTimeMillis() - attempt.startTimeMillis`) preventing client-side clock cheating.
  - **Grace Period Buffer (2-3 min)**: On official time-up, displays a prominent error-styled warning banner and grace timer (`Grace: 03:00`), allowing students to finish their current answer instead of force-submitting instantly. Auto-submits when grace buffer expires.
  - **Timestamp Tracking**: Stores both `officialTimeUpTimeMillis` and `actualSubmissionTimeMillis` in Firestore for reliance analytics, displaying timing metrics on the results card.
  - Short 2-minute transition/break screens between sections with clear guidance.
  - Robust "Resume where I left off" story allowing students to resume active exams seamlessly after app closure.
- **Official IELTS Score Aggregation & Rounding**:
  - Calculates sub-scores for all 4 skills.
  - Implements official IELTS rounding rule (`.125` rounds down to `.0`, `.25`/`.375` rounds up to `.5`, `.75`/`.875` rounds up to next integer `.0`).
- **Results & Trend Analytics**:
  - High-emphasis dark feature band (`#003C33`) showcasing Overall Band score prominently.
  - Breakdown of 4 skill sub-scores.
  - Historical comparison against student's last 3 mock attempts.
  - Saves full attempt to Firestore under `users/{userId}/mockExamAttempts`.

---

## 5. Architecture & Design
- **Jetpack Compose & Material 3**: Clean, responsive, geometric balance layout, full edge-to-edge support, status bars padding, custom shapes, and M3 typography.
- **Coroutines & Flow Architecture**: Unidirectional data flow using `ViewModel`, `StateFlow`, and structured async loading.
- **Safe Firebase Fallbacks**: Non-blocking graceful fallbacks if Firebase initialization or network connectivity is unavailable.
