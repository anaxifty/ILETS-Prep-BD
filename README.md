# IELTS Prep BD (React Edition)

A comprehensive IELTS exam preparation web application tailored for Bangladeshi students, rewritten from Android into a React + TypeScript + Vite + Tailwind CSS application.

## Core Features Preserved & Ported

1. **Authentication & Profile Setup**:
   - Phone OTP verification flow with Bangladeshi (+880) mobile validation and quick demo autofill.
   - Alternative Email sign-in, Google sign-in, and guest skip access.
   - 3-Step Profile Onboarding:
     - Target Band selection (Band 5.5 to 9.0).
     - Exam Timeline pacing (1 Month, 3 Months, 6 Months, Flexible).
     - 4-Skill Self-Assessment sliders (Listening, Reading, Writing, Speaking) establishing a baseline band score.

2. **Goal-Gradient Progress Roadmap (Emerald Focus UI)**:
   - Dynamic SVG progress ring tracking candidate percentage toward target goal band.
   - Milestone and baseline cards with culturally resonant emerald green and paper palette.
   - Interactive skill breakdown rows with target gap badges.

3. **Target Skill Modules & Practice Hub**:
   - **Academic Reading**:
     - 60-minute countdown exam timer.
     - Split passage reader and question panel with font sizing (A-/A+).
     - Multiple-choice, True/False/Not Given, and Fill-in-the-blank questions.
     - Comprehensive scoring, IELTS band conversion, and answer explanations.
   - **Listening Practice**:
     - Interactive audio player with scrubber, duration, and volume controls.
     - Simultaneous form completion, multiple-choice, and matching questions.
     - 10-minute transfer time countdown.
     - Full performance breakdown with answer keys.
   - **Writing Practice**:
     - Academic Task 1 & Task 2 prompt cards with visual graphs/diagrams.
     - Live word counter with minimum target indicator.
     - Typed essay mode and photo scan / camera upload for handwritten scripts.
     - AI Senior Examiner evaluation grading against all 4 official criteria (Task Achievement/Response, Coherence & Cohesion, Lexical Resource, Grammatical Range & Accuracy).
   - **Speaking Practice**:
     - Part 1, Part 2 Cue Card (with 1-minute prep countdown timer and scratchpad), and Part 3 Deep Discussion.
     - Voice recorder with live recording timer and audio preview.
     - AI Examiner multimodal acoustic pronunciation analysis and 4-criteria feedback notes.

4. **Strategy Video Lessons Library**:
   - Skill filters (Listening, Reading, Writing, Speaking, All).
   - Video player with strategy takeaways.
   - Offline download simulation and persistence.

5. **Weekly & Monthly Full Sitting Mock Exams**:
   - Uninterrupted 160-minute continuous full exam sitting across all 4 skills.
   - 3-minute grace period buffer banner on timer expiration.
   - Official IELTS overall band rounding formula (.25 and .75 boundaries).
   - Candidate Test Report with per-skill breakdown and past attempt history.

6. **Partner Coaching Center Locator & SSLCommerz Booking**:
   - Searchable centers in Dhaka (Dhanmondi, Uttara), Chittagong (Agrabad), and Sylhet (Zindabazar).
   - Slot details, pricing in BDT (৳), and real-time seat tracking.
   - Modal SSLCommerz payment sheet supporting bKash, Nagad, and Card payments.
   - Atomic seat reservation decrements available capacity.
   - "My Bookings" confirmed passes with pass ID and QR code simulation.
   - Disclaimer: Booking is for partner center practice mock sitting and does not register for official IDP/British Council exam.

## Technology Stack

- **Framework**: React 19 + TypeScript + Vite
- **Styling**: Tailwind CSS v4 with custom Emerald Focus theme
- **Icons**: Lucide React
- **Persistence**: LocalStorage with automatic schema recovery
