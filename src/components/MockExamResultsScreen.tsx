import React from 'react';
import { Award, ArrowRight, BookOpen, Headphones, PenTool, Mic, CheckCircle } from 'lucide-react';
import { MockExamAttempt } from '../types';

interface MockExamResultsScreenProps {
  attempt: MockExamAttempt;
  onDone: () => void;
}

export const MockExamResultsScreen: React.FC<MockExamResultsScreenProps> = ({
  attempt,
  onDone
}) => {
  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] pb-12">
      <div className="max-w-2xl mx-auto px-4 sm:px-6 pt-6">
        <div className="text-center mb-6">
          <div className="inline-block bg-[#BFF1D8] text-[#00543C] text-xs font-bold px-3 py-1 rounded-full mb-3 tracking-wider">
            OFFICIAL IELTS MOCK EXAM SITTING
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-[#161D1A]">
            Candidate Test Report
          </h1>
          <p className="text-sm text-[#45524B] mt-1">{attempt.examTitle}</p>
        </div>

        {/* Hero Band Score Card */}
        <div className="bg-gradient-to-br from-[#00543C] to-[#00785A] text-white rounded-3xl p-6 sm:p-8 shadow-sm mb-6 text-center">
          <div className="text-xs uppercase tracking-wider font-semibold text-white/80">
            Overall IELTS Band Score
          </div>
          <div className="text-6xl sm:text-7xl font-black mt-2 mb-2 text-[#FFB84D]">
            {attempt.overallBand.toFixed(1)}
          </div>
          <p className="text-xs sm:text-sm text-white/90">
            Calculated via official IELTS rounding rules (average of 4 skills)
          </p>
        </div>

        {/* 4 Skill Cards Grid */}
        <h2 className="text-lg font-bold text-[#161D1A] mb-3">
          Band Breakdown Across 4 Skills
        </h2>

        <div className="grid grid-cols-2 gap-3 mb-6">
          {/* Listening */}
          <div className="bg-white rounded-2xl border border-[#CBD7CF] p-4 shadow-2xs">
            <div className="flex items-center gap-2 text-[#00543C] mb-1">
              <Headphones size={16} />
              <span className="text-xs font-bold uppercase">Listening</span>
            </div>
            <div className="text-2xl font-black text-[#161D1A]">
              Band {attempt.listeningBand.toFixed(1)}
            </div>
          </div>

          {/* Reading */}
          <div className="bg-white rounded-2xl border border-[#CBD7CF] p-4 shadow-2xs">
            <div className="flex items-center gap-2 text-[#00543C] mb-1">
              <BookOpen size={16} />
              <span className="text-xs font-bold uppercase">Reading</span>
            </div>
            <div className="text-2xl font-black text-[#161D1A]">
              Band {attempt.readingBand.toFixed(1)}
            </div>
          </div>

          {/* Writing */}
          <div className="bg-white rounded-2xl border border-[#CBD7CF] p-4 shadow-2xs">
            <div className="flex items-center gap-2 text-[#00543C] mb-1">
              <PenTool size={16} />
              <span className="text-xs font-bold uppercase">Writing</span>
            </div>
            <div className="text-2xl font-black text-[#161D1A]">
              Band {attempt.writingBand.toFixed(1)}
            </div>
          </div>

          {/* Speaking */}
          <div className="bg-white rounded-2xl border border-[#CBD7CF] p-4 shadow-2xs">
            <div className="flex items-center gap-2 text-[#00543C] mb-1">
              <Mic size={16} />
              <span className="text-xs font-bold uppercase">Speaking</span>
            </div>
            <div className="text-2xl font-black text-[#161D1A]">
              Band {attempt.speakingBand.toFixed(1)}
            </div>
          </div>
        </div>

        {/* Session details */}
        <div className="bg-white rounded-2xl border border-[#CBD7CF] p-4 mb-8 text-xs text-[#45524B] space-y-1">
          <div className="flex justify-between">
            <span>Sitting Session ID:</span>
            <span className="font-mono font-bold text-[#161D1A]">{attempt.attemptId}</span>
          </div>
          <div className="flex justify-between">
            <span>Status:</span>
            <span className="text-[#00543C] font-bold">Official Sitting Verified</span>
          </div>
        </div>

        <button
          type="button"
          onClick={onDone}
          className="w-full py-4 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-base shadow-sm transition-all text-center"
        >
          Done & Return to Mock Exams
        </button>
      </div>
    </div>
  );
};
