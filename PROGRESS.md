# IELTS Prep App - Development Progress

## Completed Modules

### 1. Reading Practice Module
- 60-minute IELTS Reading passage tests.
- Questions loaded from Firestore `readingTests` collection.
- Local countdown timer with auto-submit at 00:00.
- Accurate band score calculation against correct answers.
- Attempt history saved to Firestore under user profile (`users/{userId}/readingAttempts`).

### 2. Listening Practice Module
- IELTS Listening tests with Media3 audio stream playback.
- Single-play audio constraint (no scrub/rewind allowed once started).
- Audio timer + distinct 10-minute transfer time phase.
- Questions displayed simultaneously alongside playing audio.
- Firestore persistence under `listeningTests` and `users/{userId}/listeningAttempts`.

### 3. Writing Practice Module
- IELTS Writing Task 1 (150 words/20 mins) & Task 2 (250 words/40 mins) module reachable from Practice Hub.
- Task prompts & visual chart diagrams loaded from Firestore `writingTasks` collection.
- Dual input methods: Direct text input with real-time word counter AND photo scan of handwritten answers.
- AI Examiner Evaluation Service (Gemini API / multimodal analysis):
  - Task Achievement / Task Response
  - Coherence and Cohesion
  - Lexical Resource
  - Grammatical Range and Accuracy
- Comprehensive results screen with overall band score banner, 4 mono-labeled criteria breakdown cards, feedback notes, and answer text/transcription.
- Evaluated attempts persisted to Firestore under `users/{userId}/writingAttempts`.

### 4. Speaking Practice Module
- Unlocked and active from Practice Hub, completing all 4 IELTS skill modules.
- Firestore `speakingTasks` collection seeded with Part 1 (Short Personal Questions), Part 2 (Cue Card with 1-min prep + 2-min recording), and Part 3 (Deep Discussion Questions).
- Interactive voice recording flow with runtime `RECORD_AUDIO` permission handling and live MediaRecorder audio capture saved to local cache.
- Pulse visualizer and timer state management for 1-minute prep phase and question-by-question response recording.
- Multimodal AI Examiner Evaluation Service powered by Gemini API:
  - Transcribes spoken audio accurately.
  - Grades across all 4 official IELTS criteria: Fluency and Coherence, Lexical Resource, Grammatical Range and Accuracy, and Pronunciation.
  - Generates explicit pronunciation assessment methodology note (flagging direct multimodal audio stream evaluation vs phoneme text estimation).
  - Includes `instructorReview: null` placeholder field for human examiner review.
- Results screen displaying overall band score, 4 criterion breakdown cards, full transcript, and an embedded audio player for candidate response playback.
- Evaluated attempts saved to Firestore under `users/{userId}/speakingAttempts`.

### 5. Video Lessons Module
- Dedicated passive learning library separate from timed Practice Hub, accessible directly from Home screen.
- Firestore `videoLessons` collection populated with video lessons categorized by skill (Listening, Reading, Writing, Speaking) and topic.
- Soft-stone card list screen with category filter pills (`surfaceVariant`, 8dp radius) and signature 22dp media-card radius thumbnails.
- Media3/ExoPlayer video player screen with unrestricted controls (play/pause, rewind 10s, fast-forward 10s, seek bar).
- Per-user watch progress persistence (last position timestamp and completion status) saved to Firestore `users/{userId}/lessonProgress/{lessonId}`.
- Download-for-offline support:
  - Storage space pre-check via `StatFs` before starting download (min 15 MB).
  - Streamed OkHttp download with live percentage progress state updates.
  - Saved to local storage (`filesDir/video_lessons/{lessonId}.mp4`) with automatic offline/online playback fallback.
  - Full state handling (Not Downloaded, Downloading, Downloaded, Error with retry).

