export interface UserProfile {
  phone: string;
  targetBand: number;
  testDate: string;
  listeningBand: number;
  readingBand: number;
  writingBand: number;
  speakingBand: number;
  isOnboardingCompleted: boolean;
}

export function calculateStartingBand(profile: UserProfile): number {
  const avg = (profile.listeningBand + profile.readingBand + profile.writingBand + profile.speakingBand) / 4.0;
  return Math.floor(avg * 2) / 2.0;
}

export type QuestionType = 'MULTIPLE_CHOICE' | 'TRUE_FALSE_NOT_GIVEN' | 'FILL_IN_BLANK';

export interface ReadingQuestion {
  id: number;
  type: QuestionType;
  questionText: string;
  options?: string[];
  correctAnswer: string;
  explanation: string;
}

export interface ReadingTest {
  id: string;
  title: string;
  difficultyLevel: 'Easy' | 'Medium' | 'Hard';
  targetBand: number;
  timeLimitMinutes: number;
  passageText: string;
  questions: ReadingQuestion[];
}

export interface ReadingAttempt {
  id: string;
  testId: string;
  testTitle: string;
  userId: string;
  userAnswers: Record<string, string>;
  score: number;
  totalQuestions: number;
  bandScore: number;
  timeTakenSeconds: number;
  timestamp: number;
}

export type ListeningQuestionType = 'MULTIPLE_CHOICE' | 'FORM_COMPLETION' | 'MATCHING';

export interface ListeningQuestion {
  id: number;
  sectionNumber: number;
  type: ListeningQuestionType;
  questionText: string;
  formContext?: string;
  options?: string[];
  matchingOptions?: string[];
  correctAnswer: string;
  explanation: string;
}

export interface ListeningTest {
  id: string;
  title: string;
  difficultyLevel: 'Easy' | 'Medium' | 'Hard';
  targetBand: number;
  audioUrl: string;
  audioDurationSeconds: number;
  transferTimeSeconds: number;
  questions: ListeningQuestion[];
}

export interface ListeningAttempt {
  id: string;
  testId: string;
  testTitle: string;
  userId: string;
  userAnswers: Record<string, string>;
  score: number;
  totalQuestions: number;
  bandScore: number;
  timeTakenSeconds: number;
  timestamp: number;
}

export interface WritingTask {
  id: string;
  taskType: 'Task 1' | 'Task 2';
  title: string;
  prompt: string;
  imageUrl?: string | null;
  difficultyLevel: 'Easy' | 'Medium' | 'Hard';
  targetWordCount: number;
  recommendedTimeMinutes: number;
}

export interface WritingCriterionScore {
  criterionName: string;
  score: number;
  feedbackNote: string;
}

export interface WritingAttempt {
  id: string;
  taskId: string;
  taskTitle: string;
  taskType: 'Task 1' | 'Task 2';
  userId: string;
  inputMethod: 'TEXT' | 'PHOTO_SCAN';
  answerText: string;
  photoUri?: string | null;
  overallBand: number;
  criteriaScores: WritingCriterionScore[];
  generalFeedback: string;
  wordCount: number;
  timeTakenSeconds: number;
  timestamp: number;
}

export interface SpeakingTask {
  id: string;
  partNumber: number; // 1, 2, 3 or 0 (full)
  title: string;
  topic: string;
  part1Questions: string[];
  part2CueCard: string;
  part2Bullets: string[];
  part3Questions: string[];
  difficultyLevel: 'Easy' | 'Medium' | 'Hard';
}

export interface SpeakingCriterionScore {
  criterionName: string;
  score: number;
  feedbackNote: string;
}

export interface SpeakingAttempt {
  id: string;
  taskId: string;
  taskTitle: string;
  partNumber: number;
  userId: string;
  audioFilePath?: string | null;
  transcription: string;
  overallBand: number;
  criteriaScores: SpeakingCriterionScore[];
  generalFeedback: string;
  pronunciationAssessmentNote: string;
  instructorReview?: string | null;
  timestamp: number;
}

export interface VideoLesson {
  id: string;
  title: string;
  skillCategory: 'Listening' | 'Reading' | 'Writing' | 'Speaking' | 'General';
  topic: string;
  description: string;
  duration: string;
  thumbnailUrl: string;
  videoUrl: string;
}

export interface LessonProgress {
  lessonId: string;
  lastPositionMs: number;
  durationMs: number;
  completed: boolean;
  lastUpdatedTimestamp: number;
}

export type DownloadStatus = 'NOT_DOWNLOADED' | 'DOWNLOADING' | 'DOWNLOADED' | 'ERROR';

export interface DownloadState {
  status: DownloadStatus;
  progressPercent: number;
  localFilePath?: string | null;
  errorMessage?: string | null;
}

export interface MockExam {
  id: string;
  title: string;
  examType: 'WEEKLY' | 'MONTHLY';
  difficultyLevel: string;
  startDate: string;
  endDate: string;
  listeningTestId: string;
  readingTestId: string;
  writingTaskId: string;
  speakingTaskId: string;
  totalDurationMinutes: number;
}

export interface MockExamAttempt {
  attemptId: string;
  mockExamId: string;
  userId: string;
  examTitle: string;
  examType: 'WEEKLY' | 'MONTHLY';
  startTimeMillis: number;
  endTimeMillis: number;
  status: 'IN_PROGRESS' | 'COMPLETED';
  currentSection: 'LISTENING' | 'TRANSITION_READING' | 'READING' | 'TRANSITION_WRITING' | 'WRITING' | 'TRANSITION_SPEAKING' | 'SPEAKING' | 'COMPLETED';
  listeningBand: number;
  readingBand: number;
  writingBand: number;
  speakingBand: number;
  overallBand: number;
  listeningAnswersJson: string;
  readingAnswersJson: string;
  writingEssayText: string;
  speakingAudioUri: string;
  officialTimeUpTimeMillis: number;
  actualSubmissionTimeMillis: number;
}

export interface PartnerSlot {
  slotId: string;
  date: string;
  time: string;
  title: string;
  slotType: 'PRACTICE_MOCK' | 'COACHING_SESSION';
  priceBdt: number;
  capacity: number;
  seatsRemaining: number;
}

export interface PartnerCenter {
  id: string;
  name: string;
  address: string;
  city: string;
  latitude: number;
  longitude: number;
  description: string;
  contactPhone: string;
  contactEmail: string;
  rating: number;
  slots: PartnerSlot[];
}

export interface CenterBooking {
  bookingId: string;
  userId: string;
  userName: string;
  userPhone: string;
  centerId: string;
  centerName: string;
  centerAddress: string;
  slotId: string;
  slotDate: string;
  slotTime: string;
  slotTitle: string;
  slotType: string;
  priceBdt: number;
  paymentStatus: 'SUCCESS' | 'FAILED' | 'PENDING';
  paymentTranId: string;
  bookingStatus: 'CONFIRMED' | 'COMPLETED' | 'CANCELLED';
  createdAtMillis: number;
}

export class IeltsScoreCalculator {
  static calculateOverallBand(
    listening: number,
    reading: number,
    writing: number,
    speaking: number
  ): number {
    const average = (listening + reading + writing + speaking) / 4.0;
    const integerPart = Math.floor(average);
    const fraction = average - integerPart;

    if (fraction < 0.25) {
      return integerPart;
    } else if (fraction < 0.75) {
      return integerPart + 0.5;
    } else {
      return integerPart + 1.0;
    }
  }
}
