import React, { useState, useEffect } from 'react';
import {
  UserProfile,
  ReadingTest,
  ReadingAttempt,
  ListeningTest,
  ListeningAttempt,
  WritingTask,
  WritingAttempt,
  SpeakingTask,
  SpeakingAttempt,
  VideoLesson,
  MockExam,
  MockExamAttempt,
  PartnerCenter,
  PartnerSlot,
  CenterBooking
} from './types';
import {
  SAMPLE_READING_TESTS,
  SAMPLE_LISTENING_TESTS,
  SAMPLE_WRITING_TASKS,
  SAMPLE_SPEAKING_TASKS,
  SAMPLE_VIDEO_LESSONS,
  SAMPLE_MOCK_EXAMS
} from './data/mockData';
import { storage } from './services/storage';

// Screens
import { PhoneAuthScreen } from './components/PhoneAuthScreen';
import { OtpVerificationScreen } from './components/OtpVerificationScreen';
import { OnboardingScreen } from './components/OnboardingScreen';
import { HomeScreen } from './components/HomeScreen';
import { PracticeHubScreen } from './components/PracticeHubScreen';
import { ReadingPracticeScreen, ReadingResultsScreen } from './components/ReadingPracticeScreen';
import { ListeningPracticeScreen, ListeningResultsScreen } from './components/ListeningPracticeScreen';
import { WritingPracticeScreen, WritingResultsScreen } from './components/WritingPracticeScreen';
import { SpeakingPracticeScreen, SpeakingResultsScreen } from './components/SpeakingPracticeScreen';
import { VideoLessonsListScreen, VideoLessonPlayerScreen } from './components/VideoLessonsListScreen';
import { MockExamListScreen } from './components/MockExamListScreen';
import { MockExamFlowScreen } from './components/MockExamFlowScreen';
import { MockExamResultsScreen } from './components/MockExamResultsScreen';
import { CenterLocatorScreen } from './components/CenterLocatorScreen';
import { CenterDetailScreen } from './components/CenterDetailScreen';
import { MyCenterBookingsScreen } from './components/MyCenterBookingsScreen';

type Route =
  | 'PHONE_AUTH'
  | 'OTP_VERIFY'
  | 'ONBOARDING'
  | 'HOME'
  | 'PRACTICE_HUB'
  | 'READING_PRACTICE'
  | 'READING_RESULTS'
  | 'LISTENING_PRACTICE'
  | 'LISTENING_RESULTS'
  | 'WRITING_PRACTICE'
  | 'WRITING_RESULTS'
  | 'SPEAKING_PRACTICE'
  | 'SPEAKING_RESULTS'
  | 'VIDEO_LESSONS_LIST'
  | 'VIDEO_LESSON_PLAYER'
  | 'MOCK_EXAM_LIST'
  | 'MOCK_EXAM_FLOW'
  | 'MOCK_EXAM_RESULTS'
  | 'CENTER_LOCATOR'
  | 'CENTER_DETAIL'
  | 'MY_CENTER_BOOKINGS';

export const App: React.FC = () => {
  const [profile, setProfile] = useState<UserProfile>(() => storage.getUserProfile());
  const [route, setRoute] = useState<Route>(() => {
    const saved = storage.getUserProfile();
    if (saved.isOnboardingCompleted) return 'HOME';
    if (saved.phone) return 'ONBOARDING';
    return 'PHONE_AUTH';
  });

  const [enteredPhone, setEnteredPhone] = useState('');

  // Selected entities for detail screens
  const [selectedReadingTest, setSelectedReadingTest] = useState<ReadingTest>(SAMPLE_READING_TESTS[0]);
  const [currentReadingAttempt, setCurrentReadingAttempt] = useState<ReadingAttempt | null>(null);

  const [selectedListeningTest, setSelectedListeningTest] = useState<ListeningTest>(SAMPLE_LISTENING_TESTS[0]);
  const [currentListeningAttempt, setCurrentListeningAttempt] = useState<ListeningAttempt | null>(null);

  const [selectedWritingTask, setSelectedWritingTask] = useState<WritingTask>(SAMPLE_WRITING_TASKS[0]);
  const [currentWritingAttempt, setCurrentWritingAttempt] = useState<WritingAttempt | null>(null);

  const [selectedSpeakingTask, setSelectedSpeakingTask] = useState<SpeakingTask>(SAMPLE_SPEAKING_TASKS[0]);
  const [currentSpeakingAttempt, setCurrentSpeakingAttempt] = useState<SpeakingAttempt | null>(null);

  const [selectedVideoLesson, setSelectedVideoLesson] = useState<VideoLesson>(SAMPLE_VIDEO_LESSONS[0]);

  const [selectedMockExam, setSelectedMockExam] = useState<MockExam>(SAMPLE_MOCK_EXAMS[0]);
  const [currentMockAttempt, setCurrentMockAttempt] = useState<MockExamAttempt | null>(null);
  const [mockExamAttempts, setMockExamAttempts] = useState<MockExamAttempt[]>(() =>
    storage.getMockExamAttempts()
  );

  const [partnerCenters, setPartnerCenters] = useState<PartnerCenter[]>(() =>
    storage.getPartnerCenters()
  );
  const [selectedCenter, setSelectedCenter] = useState<PartnerCenter>(partnerCenters[0]);
  const [userBookings, setUserBookings] = useState<CenterBooking[]>(() =>
    storage.getUserCenterBookings()
  );

  // Sync profile update
  const handleUpdateProfile = (newProfile: UserProfile) => {
    setProfile(newProfile);
    storage.saveUserProfile(newProfile);
  };

  const handleSignOut = () => {
    storage.clearUserProfile();
    setProfile({
      phone: '',
      targetBand: 7.0,
      testDate: 'In 3 Months',
      listeningBand: 6.0,
      readingBand: 6.0,
      writingBand: 5.5,
      speakingBand: 5.5,
      isOnboardingCompleted: false
    });
    setRoute('PHONE_AUTH');
  };

  return (
    <div className="w-full min-h-screen bg-[#F5FAF7] text-[#161D1A]">
      {/* 1. Phone Auth Screen */}
      {route === 'PHONE_AUTH' && (
        <PhoneAuthScreen
          onOtpSent={(phone) => {
            setEnteredPhone(phone);
            setRoute('OTP_VERIFY');
          }}
          onAuthenticated={(phone) => {
            const updated = { ...profile, phone };
            handleUpdateProfile(updated);
            setRoute('ONBOARDING');
          }}
        />
      )}

      {/* 2. OTP Verification Screen */}
      {route === 'OTP_VERIFY' && (
        <OtpVerificationScreen
          phoneNumber={enteredPhone}
          onVerified={() => {
            const updated = { ...profile, phone: enteredPhone };
            handleUpdateProfile(updated);
            setRoute('ONBOARDING');
          }}
          onBack={() => setRoute('PHONE_AUTH')}
        />
      )}

      {/* 3. Onboarding Screen */}
      {route === 'ONBOARDING' && (
        <OnboardingScreen
          initialProfile={profile}
          onFinished={(completedProfile) => {
            handleUpdateProfile(completedProfile);
            setRoute('HOME');
          }}
        />
      )}

      {/* 4. Home Screen */}
      {route === 'HOME' && (
        <HomeScreen
          userProfile={profile}
          onSignOut={handleSignOut}
          onNavigateToPractice={() => setRoute('PRACTICE_HUB')}
          onNavigateToVideoLessons={() => setRoute('VIDEO_LESSONS_LIST')}
          onNavigateToMockExams={() => setRoute('MOCK_EXAM_LIST')}
          onNavigateToCenterLocator={() => {
            setPartnerCenters(storage.getPartnerCenters());
            setRoute('CENTER_LOCATOR');
          }}
        />
      )}

      {/* 5. Practice Hub */}
      {route === 'PRACTICE_HUB' && (
        <PracticeHubScreen
          userProfile={profile}
          readingTests={SAMPLE_READING_TESTS}
          listeningTests={SAMPLE_LISTENING_TESTS}
          writingTasks={SAMPLE_WRITING_TASKS}
          speakingTasks={SAMPLE_SPEAKING_TASKS}
          onStartReadingTest={(testId) => {
            const found = SAMPLE_READING_TESTS.find((t) => t.id === testId) || SAMPLE_READING_TESTS[0];
            setSelectedReadingTest(found);
            setRoute('READING_PRACTICE');
          }}
          onStartListeningTest={(testId) => {
            const found = SAMPLE_LISTENING_TESTS.find((t) => t.id === testId) || SAMPLE_LISTENING_TESTS[0];
            setSelectedListeningTest(found);
            setRoute('LISTENING_PRACTICE');
          }}
          onStartWritingTask={(taskId) => {
            const found = SAMPLE_WRITING_TASKS.find((t) => t.id === taskId) || SAMPLE_WRITING_TASKS[0];
            setSelectedWritingTask(found);
            setRoute('WRITING_PRACTICE');
          }}
          onStartSpeakingTask={(taskId) => {
            const found = SAMPLE_SPEAKING_TASKS.find((t) => t.id === taskId) || SAMPLE_SPEAKING_TASKS[0];
            setSelectedSpeakingTask(found);
            setRoute('SPEAKING_PRACTICE');
          }}
          onBack={() => setRoute('HOME')}
        />
      )}

      {/* 6. Reading Practice & Results */}
      {route === 'READING_PRACTICE' && (
        <ReadingPracticeScreen
          test={selectedReadingTest}
          onSubmit={(attempt) => {
            storage.saveReadingAttempt(attempt);
            setCurrentReadingAttempt(attempt);
            setRoute('READING_RESULTS');
          }}
          onBack={() => setRoute('PRACTICE_HUB')}
        />
      )}

      {route === 'READING_RESULTS' && currentReadingAttempt && (
        <ReadingResultsScreen
          test={selectedReadingTest}
          attempt={currentReadingAttempt}
          onDone={() => setRoute('PRACTICE_HUB')}
        />
      )}

      {/* 7. Listening Practice & Results */}
      {route === 'LISTENING_PRACTICE' && (
        <ListeningPracticeScreen
          test={selectedListeningTest}
          onSubmit={(attempt) => {
            storage.saveListeningAttempt(attempt);
            setCurrentListeningAttempt(attempt);
            setRoute('LISTENING_RESULTS');
          }}
          onBack={() => setRoute('PRACTICE_HUB')}
        />
      )}

      {route === 'LISTENING_RESULTS' && currentListeningAttempt && (
        <ListeningResultsScreen
          test={selectedListeningTest}
          attempt={currentListeningAttempt}
          onDone={() => setRoute('PRACTICE_HUB')}
        />
      )}

      {/* 8. Writing Practice & Results */}
      {route === 'WRITING_PRACTICE' && (
        <WritingPracticeScreen
          task={selectedWritingTask}
          onSubmit={(attempt) => {
            storage.saveWritingAttempt(attempt);
            setCurrentWritingAttempt(attempt);
            setRoute('WRITING_RESULTS');
          }}
          onBack={() => setRoute('PRACTICE_HUB')}
        />
      )}

      {route === 'WRITING_RESULTS' && currentWritingAttempt && (
        <WritingResultsScreen
          attempt={currentWritingAttempt}
          onReturnToHub={() => setRoute('PRACTICE_HUB')}
        />
      )}

      {/* 9. Speaking Practice & Results */}
      {route === 'SPEAKING_PRACTICE' && (
        <SpeakingPracticeScreen
          task={selectedSpeakingTask}
          onSubmit={(attempt) => {
            storage.saveSpeakingAttempt(attempt);
            setCurrentSpeakingAttempt(attempt);
            setRoute('SPEAKING_RESULTS');
          }}
          onBack={() => setRoute('PRACTICE_HUB')}
        />
      )}

      {route === 'SPEAKING_RESULTS' && currentSpeakingAttempt && (
        <SpeakingResultsScreen
          attempt={currentSpeakingAttempt}
          onReturnToHub={() => setRoute('PRACTICE_HUB')}
        />
      )}

      {/* 10. Video Lessons List & Player */}
      {route === 'VIDEO_LESSONS_LIST' && (
        <VideoLessonsListScreen
          lessons={SAMPLE_VIDEO_LESSONS}
          onSelectLesson={(lesson) => {
            setSelectedVideoLesson(lesson);
            setRoute('VIDEO_LESSON_PLAYER');
          }}
          onBack={() => setRoute('HOME')}
        />
      )}

      {route === 'VIDEO_LESSON_PLAYER' && (
        <VideoLessonPlayerScreen
          lesson={selectedVideoLesson}
          onBack={() => setRoute('VIDEO_LESSONS_LIST')}
        />
      )}

      {/* 11. Mock Exam List, Flow & Results */}
      {route === 'MOCK_EXAM_LIST' && (
        <MockExamListScreen
          mockExams={SAMPLE_MOCK_EXAMS}
          pastAttempts={mockExamAttempts}
          onStartExam={(exam) => {
            setSelectedMockExam(exam);
            setRoute('MOCK_EXAM_FLOW');
          }}
          onViewResult={(att) => {
            setCurrentMockAttempt(att);
            setRoute('MOCK_EXAM_RESULTS');
          }}
          onBack={() => setRoute('HOME')}
        />
      )}

      {route === 'MOCK_EXAM_FLOW' && (
        <MockExamFlowScreen
          exam={selectedMockExam}
          listeningTest={SAMPLE_LISTENING_TESTS[0]}
          readingTest={SAMPLE_READING_TESTS[0]}
          writingTask={SAMPLE_WRITING_TASKS[1]}
          speakingTask={SAMPLE_SPEAKING_TASKS[0]}
          onExamCompleted={(attempt) => {
            storage.saveMockExamAttempt(attempt);
            setMockExamAttempts(storage.getMockExamAttempts());
            setCurrentMockAttempt(attempt);
            setRoute('MOCK_EXAM_RESULTS');
          }}
          onExit={() => setRoute('MOCK_EXAM_LIST')}
        />
      )}

      {route === 'MOCK_EXAM_RESULTS' && currentMockAttempt && (
        <MockExamResultsScreen
          attempt={currentMockAttempt}
          onDone={() => setRoute('MOCK_EXAM_LIST')}
        />
      )}

      {/* 12. Partner Center Locator, Detail & My Bookings */}
      {route === 'CENTER_LOCATOR' && (
        <CenterLocatorScreen
          centers={partnerCenters}
          onSelectCenter={(center) => {
            setSelectedCenter(center);
            setRoute('CENTER_DETAIL');
          }}
          onNavigateToMyBookings={() => {
            setUserBookings(storage.getUserCenterBookings());
            setRoute('MY_CENTER_BOOKINGS');
          }}
          onBack={() => setRoute('HOME')}
        />
      )}

      {route === 'CENTER_DETAIL' && (
        <CenterDetailScreen
          center={selectedCenter}
          userPhone={profile.phone}
          onBookSlot={(slot, studentName, studentPhone, tranId) => {
            const booking: CenterBooking = {
              bookingId: `BK_${Date.now()}`,
              userId: 'default_user',
              userName: studentName,
              userPhone: studentPhone,
              centerId: selectedCenter.id,
              centerName: selectedCenter.name,
              centerAddress: selectedCenter.address,
              slotId: slot.slotId,
              slotDate: slot.date,
              slotTime: slot.time,
              slotTitle: slot.title,
              slotType: slot.slotType,
              priceBdt: slot.priceBdt,
              paymentStatus: 'SUCCESS',
              paymentTranId: tranId,
              bookingStatus: 'CONFIRMED',
              createdAtMillis: Date.now()
            };

            const result = storage.bookCenterSlot(booking);
            if (result.success) {
              setPartnerCenters(storage.getPartnerCenters());
              setUserBookings(storage.getUserCenterBookings());
              setRoute('MY_CENTER_BOOKINGS');
            }
          }}
          onBack={() => setRoute('CENTER_LOCATOR')}
        />
      )}

      {route === 'MY_CENTER_BOOKINGS' && (
        <MyCenterBookingsScreen
          bookings={userBookings}
          onBack={() => setRoute('CENTER_LOCATOR')}
          onExploreCenters={() => setRoute('CENTER_LOCATOR')}
        />
      )}
    </div>
  );
};

export default App;
