import {
  UserProfile,
  ReadingAttempt,
  ListeningAttempt,
  WritingAttempt,
  SpeakingAttempt,
  MockExamAttempt,
  CenterBooking,
  LessonProgress,
  PartnerCenter
} from '../types';
import { SAMPLE_PARTNER_CENTERS } from '../data/mockData';

const STORAGE_KEYS = {
  USER_PROFILE: 'ielts_bd_user_profile',
  AUTH_TOKEN: 'ielts_bd_auth_token',
  READING_ATTEMPTS: 'ielts_bd_reading_attempts',
  LISTENING_ATTEMPTS: 'ielts_bd_listening_attempts',
  WRITING_ATTEMPTS: 'ielts_bd_writing_attempts',
  SPEAKING_ATTEMPTS: 'ielts_bd_speaking_attempts',
  MOCK_EXAM_ATTEMPTS: 'ielts_bd_mock_exam_attempts',
  CENTER_BOOKINGS: 'ielts_bd_center_bookings',
  LESSON_PROGRESS: 'ielts_bd_lesson_progress',
  PARTNER_CENTERS: 'ielts_bd_partner_centers',
  DOWNLOADED_LESSONS: 'ielts_bd_downloaded_lessons'
};

const DEFAULT_PROFILE: UserProfile = {
  phone: '',
  targetBand: 7.0,
  testDate: 'In 3 Months',
  listeningBand: 6.0,
  readingBand: 6.0,
  writingBand: 5.5,
  speakingBand: 5.5,
  isOnboardingCompleted: false
};

export const storage = {
  getUserProfile(): UserProfile {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.USER_PROFILE);
      return data ? JSON.parse(data) : DEFAULT_PROFILE;
    } catch {
      return DEFAULT_PROFILE;
    }
  },

  saveUserProfile(profile: UserProfile): void {
    try {
      localStorage.setItem(STORAGE_KEYS.USER_PROFILE, JSON.stringify(profile));
    } catch (e) {
      console.error('Failed to save user profile', e);
    }
  },

  clearUserProfile(): void {
    try {
      localStorage.removeItem(STORAGE_KEYS.USER_PROFILE);
      localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
    } catch (e) {
      console.error('Failed to clear user profile', e);
    }
  },

  // Reading attempts
  getReadingAttempts(): ReadingAttempt[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.READING_ATTEMPTS);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  },

  saveReadingAttempt(attempt: ReadingAttempt): void {
    const current = this.getReadingAttempts();
    const updated = [attempt, ...current.filter(a => a.id !== attempt.id)];
    localStorage.setItem(STORAGE_KEYS.READING_ATTEMPTS, JSON.stringify(updated));
  },

  // Listening attempts
  getListeningAttempts(): ListeningAttempt[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.LISTENING_ATTEMPTS);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  },

  saveListeningAttempt(attempt: ListeningAttempt): void {
    const current = this.getListeningAttempts();
    const updated = [attempt, ...current.filter(a => a.id !== attempt.id)];
    localStorage.setItem(STORAGE_KEYS.LISTENING_ATTEMPTS, JSON.stringify(updated));
  },

  // Writing attempts
  getWritingAttempts(): WritingAttempt[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.WRITING_ATTEMPTS);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  },

  saveWritingAttempt(attempt: WritingAttempt): void {
    const current = this.getWritingAttempts();
    const updated = [attempt, ...current.filter(a => a.id !== attempt.id)];
    localStorage.setItem(STORAGE_KEYS.WRITING_ATTEMPTS, JSON.stringify(updated));
  },

  // Speaking attempts
  getSpeakingAttempts(): SpeakingAttempt[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.SPEAKING_ATTEMPTS);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  },

  saveSpeakingAttempt(attempt: SpeakingAttempt): void {
    const current = this.getSpeakingAttempts();
    const updated = [attempt, ...current.filter(a => a.id !== attempt.id)];
    localStorage.setItem(STORAGE_KEYS.SPEAKING_ATTEMPTS, JSON.stringify(updated));
  },

  // Mock exam attempts
  getMockExamAttempts(): MockExamAttempt[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.MOCK_EXAM_ATTEMPTS);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  },

  saveMockExamAttempt(attempt: MockExamAttempt): void {
    const current = this.getMockExamAttempts();
    const updated = [attempt, ...current.filter(a => a.attemptId !== attempt.attemptId)];
    localStorage.setItem(STORAGE_KEYS.MOCK_EXAM_ATTEMPTS, JSON.stringify(updated));
  },

  // Partner Centers & Atomic Bookings
  getPartnerCenters(): PartnerCenter[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.PARTNER_CENTERS);
      if (data) {
        return JSON.parse(data);
      }
      localStorage.setItem(STORAGE_KEYS.PARTNER_CENTERS, JSON.stringify(SAMPLE_PARTNER_CENTERS));
      return SAMPLE_PARTNER_CENTERS;
    } catch {
      return SAMPLE_PARTNER_CENTERS;
    }
  },

  savePartnerCenters(centers: PartnerCenter[]): void {
    localStorage.setItem(STORAGE_KEYS.PARTNER_CENTERS, JSON.stringify(centers));
  },

  bookCenterSlot(booking: CenterBooking): { success: boolean; error?: string; booking?: CenterBooking } {
    try {
      const centers = this.getPartnerCenters();
      const centerIndex = centers.findIndex(c => c.id === booking.centerId);
      if (centerIndex === -1) {
        return { success: false, error: 'Partner Center not found' };
      }

      const center = centers[centerIndex];
      const slotIndex = center.slots.findIndex(s => s.slotId === booking.slotId);
      if (slotIndex === -1) {
        return { success: false, error: 'Practice slot not found' };
      }

      const slot = center.slots[slotIndex];
      if (slot.seatsRemaining <= 0) {
        return { success: false, error: 'Sorry, this practice slot is fully booked! No seats remaining.' };
      }

      // Decrement seats
      center.slots[slotIndex] = {
        ...slot,
        seatsRemaining: slot.seatsRemaining - 1
      };
      centers[centerIndex] = { ...center };
      this.savePartnerCenters(centers);

      // Save user booking
      const currentBookings = this.getUserCenterBookings();
      const updatedBookings = [booking, ...currentBookings];
      localStorage.setItem(STORAGE_KEYS.CENTER_BOOKINGS, JSON.stringify(updatedBookings));

      return { success: true, booking };
    } catch (e: any) {
      return { success: false, error: e?.message || 'Transaction failed' };
    }
  },

  getUserCenterBookings(): CenterBooking[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.CENTER_BOOKINGS);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  },

  // Lesson Progress
  getLessonProgressMap(): Record<string, LessonProgress> {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.LESSON_PROGRESS);
      return data ? JSON.parse(data) : {};
    } catch {
      return {};
    }
  },

  saveLessonProgress(progress: LessonProgress): void {
    const current = this.getLessonProgressMap();
    current[progress.lessonId] = progress;
    localStorage.setItem(STORAGE_KEYS.LESSON_PROGRESS, JSON.stringify(current));
  },

  // Downloaded Lessons simulation
  getDownloadedLessons(): string[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.DOWNLOADED_LESSONS);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  },

  toggleDownloadLesson(lessonId: string): boolean {
    const list = this.getDownloadedLessons();
    let isDownloaded: boolean;
    if (list.includes(lessonId)) {
      const updated = list.filter(id => id !== lessonId);
      localStorage.setItem(STORAGE_KEYS.DOWNLOADED_LESSONS, JSON.stringify(updated));
      isDownloaded = false;
    } else {
      const updated = [...list, lessonId];
      localStorage.setItem(STORAGE_KEYS.DOWNLOADED_LESSONS, JSON.stringify(updated));
      isDownloaded = true;
    }
    return isDownloaded;
  }
};
