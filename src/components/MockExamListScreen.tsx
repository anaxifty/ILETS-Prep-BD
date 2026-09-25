import React from 'react';
import {
  ArrowLeft,
  Timer,
  Calendar,
  Award,
  ChevronRight,
  BookOpen,
  Headphones,
  PenTool,
  Mic,
  Play
} from 'lucide-react';
import { MockExam, MockExamAttempt } from '../types';

interface MockExamListScreenProps {
  mockExams: MockExam[];
  pastAttempts: MockExamAttempt[];
  onStartExam: (exam: MockExam) => void;
  onViewResult: (attempt: MockExamAttempt) => void;
  onBack: () => void;
}

export const MockExamListScreen: React.FC<MockExamListScreenProps> = ({
  mockExams,
  pastAttempts,
  onStartExam,
  onViewResult,
  onBack
}) => {
  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] pb-12">
      <div className="max-w-xl mx-auto px-4 sm:px-6 pt-5">
        {/* Header */}
        <div className="flex items-center gap-3 py-2 mb-4">
          <button
            type="button"
            onClick={onBack}
            className="w-10 h-10 rounded-full bg-white border border-[#CBD7CF] flex items-center justify-center text-[#45524B] hover:text-[#161D1A] transition-colors shadow-2xs"
          >
            <ArrowLeft size={18} />
          </button>
          <div>
            <div className="text-xs font-bold tracking-wider text-[#00543C] uppercase">
              EXAM SIMULATION
            </div>
            <h1 className="text-xl sm:text-2xl font-extrabold text-[#161D1A]">
              Weekly & Monthly Mocks
            </h1>
          </div>
        </div>

        {/* Hero Info Banner */}
        <div className="bg-[#00543C] text-white rounded-3xl p-5 sm:p-6 mb-6 shadow-sm">
          <div className="flex items-center gap-3 mb-2">
            <div className="w-10 h-10 rounded-xl bg-white/20 flex items-center justify-center">
              <Timer size={22} />
            </div>
            <div>
              <h2 className="font-bold text-base">Full Exam Condition Sittings</h2>
              <p className="text-xs text-white/80">
                160 minutes uninterrupted test across all 4 skills.
              </p>
            </div>
          </div>
          <div className="grid grid-cols-4 gap-2 pt-3 border-t border-white/20 text-center text-xs">
            <div className="bg-white/10 rounded-xl py-1.5">Listening (30m)</div>
            <div className="bg-white/10 rounded-xl py-1.5">Reading (60m)</div>
            <div className="bg-white/10 rounded-xl py-1.5">Writing (60m)</div>
            <div className="bg-white/10 rounded-xl py-1.5">Speaking (10m)</div>
          </div>
        </div>

        <h3 className="text-base font-bold text-[#161D1A] mb-3">
          Available Mock Exams
        </h3>

        {/* Mock Exam Cards */}
        <div className="space-y-4 mb-8">
          {mockExams.map((exam) => (
            <div
              key={exam.id}
              className="bg-white rounded-3xl border border-[#CBD7CF] p-5 sm:p-6 shadow-2xs hover:border-[#00543C] transition-all"
            >
              <div className="flex items-start justify-between mb-3">
                <span className="text-2xs font-extrabold uppercase px-2.5 py-1 rounded-full bg-[#BFF1D8] text-[#00543C]">
                  {exam.examType} MOCK • {exam.difficultyLevel}
                </span>
                <span className="text-xs font-semibold text-[#45524B] flex items-center gap-1">
                  <Calendar size={13} /> {exam.startDate} – {exam.endDate}
                </span>
              </div>

              <h4 className="font-extrabold text-base sm:text-lg text-[#161D1A] mb-1">
                {exam.title}
              </h4>
              <p className="text-xs text-[#45524B] mb-4">
                Total duration: {exam.totalDurationMinutes} minutes • Timed official session
              </p>

              <button
                type="button"
                onClick={() => onStartExam(exam)}
                className="w-full py-3 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-sm flex items-center justify-center gap-2 shadow-xs transition-colors"
              >
                Start Full Sitting (160 Mins)
                <Play size={15} fill="currentColor" />
              </button>
            </div>
          ))}
        </div>

        {/* Past Attempts Section */}
        {pastAttempts.length > 0 && (
          <div>
            <h3 className="text-base font-bold text-[#161D1A] mb-3">
              Past Mock Exam Sittings ({pastAttempts.length})
            </h3>
            <div className="space-y-3">
              {pastAttempts.map((attempt) => (
                <div
                  key={attempt.attemptId}
                  onClick={() => onViewResult(attempt)}
                  className="cursor-pointer bg-white hover:border-[#00543C] rounded-2xl border border-[#CBD7CF] p-4 flex items-center justify-between transition-all"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-11 h-11 rounded-xl bg-[#FFDFBD] text-[#B26A00] flex items-center justify-center font-black text-sm">
                      {attempt.overallBand.toFixed(1)}
                    </div>
                    <div>
                      <div className="font-bold text-sm text-[#161D1A]">
                        {attempt.examTitle}
                      </div>
                      <div className="text-2xs text-[#45524B] mt-0.5">
                        L: {attempt.listeningBand.toFixed(1)} | R: {attempt.readingBand.toFixed(1)} | W: {attempt.writingBand.toFixed(1)} | S: {attempt.speakingBand.toFixed(1)}
                      </div>
                    </div>
                  </div>
                  <ChevronRight size={18} className="text-[#00543C]" />
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
