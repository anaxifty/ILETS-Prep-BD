import React from 'react';
import {
  ArrowLeft,
  BookOpen,
  Headphones,
  PenTool,
  Mic,
  Play,
  ChevronRight
} from 'lucide-react';
import { ReadingTest, ListeningTest, WritingTask, SpeakingTask, UserProfile } from '../types';

interface PracticeHubScreenProps {
  userProfile: UserProfile;
  readingTests: ReadingTest[];
  listeningTests: ListeningTest[];
  writingTasks: WritingTask[];
  speakingTasks: SpeakingTask[];
  onStartReadingTest: (testId: string) => void;
  onStartListeningTest: (testId: string) => void;
  onStartWritingTask: (taskId: string) => void;
  onStartSpeakingTask: (taskId: string) => void;
  onBack: () => void;
}

export const PracticeHubScreen: React.FC<PracticeHubScreenProps> = ({
  userProfile,
  readingTests,
  listeningTests,
  writingTasks,
  speakingTasks,
  onStartReadingTest,
  onStartListeningTest,
  onStartWritingTask,
  onStartSpeakingTask,
  onBack
}) => {
  // Select recommended reading test based on user's reading band
  const recommendedReading = readingTests.find((t) => {
    if (userProfile.readingBand < 6.0) return t.difficultyLevel === 'Easy';
    if (userProfile.readingBand >= 7.5) return t.difficultyLevel === 'Hard';
    return t.difficultyLevel === 'Medium';
  }) || readingTests[0];

  // Select recommended listening test
  const recommendedListening = listeningTests.find((t) => {
    if (userProfile.listeningBand < 6.0) return t.difficultyLevel === 'Easy';
    return t.difficultyLevel === 'Medium';
  }) || listeningTests[0];

  const recommendedWriting = writingTasks[0];
  const recommendedSpeaking = speakingTasks[0];

  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] pb-12">
      <div className="max-w-xl mx-auto px-4 sm:px-6 pt-5">
        {/* Top Header */}
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
              IELTS PRACTICE HUB
            </div>
            <h1 className="text-xl sm:text-2xl font-extrabold text-[#161D1A]">
              Target Skill Modules
            </h1>
          </div>
        </div>

        {/* User Baseline Info Banner */}
        <div className="bg-[#BFF1D8]/60 border border-[#96E2BD] rounded-3xl p-5 flex items-center justify-between mb-6 shadow-2xs">
          <div>
            <div className="text-xs text-[#00543C] font-semibold">
              Assessed Reading Level
            </div>
            <div className="text-lg font-black text-[#00543C] mt-0.5">
              Band {userProfile.readingBand.toFixed(1)} Baseline
            </div>
            <p className="text-xs text-[#00543C]/80 mt-1">
              Recommended passages tailored to your self-assessed target.
            </p>
          </div>
          <div className="w-12 h-12 rounded-full bg-white flex items-center justify-center text-[#00543C] shadow-xs">
            <BookOpen size={22} />
          </div>
        </div>

        <h2 className="text-lg font-bold text-[#161D1A] mb-3">
          Skill Practice Modules
        </h2>

        {/* Module 1: Reading */}
        <div className="bg-white rounded-3xl border border-[#00543C] p-5 mb-4 shadow-sm">
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-3">
              <div className="w-11 h-11 rounded-2xl bg-[#BFF1D8] text-[#00543C] flex items-center justify-center">
                <BookOpen size={20} />
              </div>
              <div>
                <h3 className="font-bold text-base text-[#161D1A]">Reading Practice</h3>
                <p className="text-xs text-[#45524B]">
                  60-Min Real IELTS Format Passage & Questions
                </p>
              </div>
            </div>
            <span className="text-xs font-bold px-2.5 py-1 rounded-lg bg-[#BFF1D8] text-[#00543C]">
              {recommendedReading?.difficultyLevel || 'Medium'}
            </span>
          </div>

          {recommendedReading && (
            <div className="mt-4 p-3.5 bg-[#F0F5F1] rounded-2xl border border-[#CBD7CF]">
              <div className="text-xs font-bold text-[#00543C] uppercase tracking-wide">
                RECOMMENDED TEST
              </div>
              <div className="font-bold text-sm text-[#161D1A] mt-0.5">
                {recommendedReading.title}
              </div>
            </div>
          )}

          <button
            type="button"
            onClick={() => onStartReadingTest(recommendedReading?.id || 'reading_test_medium_01')}
            className="mt-4 w-full py-3 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-sm flex items-center justify-center gap-2 shadow-xs transition-colors"
          >
            Start 60-Min Reading Test
            <Play size={16} fill="currentColor" />
          </button>
        </div>

        {/* Additional Reading Passages list */}
        {readingTests.length > 1 && (
          <div className="mb-6">
            <h4 className="text-xs font-bold uppercase tracking-wider text-[#45524B] mb-2 px-1">
              All Reading Passages ({readingTests.length})
            </h4>
            <div className="space-y-2">
              {readingTests.map((test) => (
                <div
                  key={test.id}
                  onClick={() => onStartReadingTest(test.id)}
                  className="cursor-pointer bg-white hover:border-[#00543C] rounded-2xl border border-[#CBD7CF] p-3 flex items-center justify-between transition-all"
                >
                  <div className="flex-1 pr-2">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="text-2xs font-extrabold px-2 py-0.5 rounded-md bg-[#FFDFBD] text-[#3A2500]">
                        {test.difficultyLevel}
                      </span>
                      <span className="text-xs text-[#45524B]">
                        {test.questions.length} Questions • {test.timeLimitMinutes}m
                      </span>
                    </div>
                    <div className="font-semibold text-xs sm:text-sm text-[#161D1A] line-clamp-1">
                      {test.title}
                    </div>
                  </div>
                  <ChevronRight size={18} className="text-[#00543C] shrink-0" />
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Module 2: Listening */}
        <div className="bg-white rounded-3xl border border-[#CBD7CF] p-5 mb-4 shadow-2xs">
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-3">
              <div className="w-11 h-11 rounded-2xl bg-[#BFF1D8] text-[#00543C] flex items-center justify-center">
                <Headphones size={20} />
              </div>
              <div>
                <h3 className="font-bold text-base text-[#161D1A]">Listening Practice</h3>
                <p className="text-xs text-[#45524B]">
                  Audio Streams with Simultaneous Questions & Transfer Time
                </p>
              </div>
            </div>
            <span className="text-xs font-bold px-2.5 py-1 rounded-lg bg-[#BFF1D8] text-[#00543C]">
              {recommendedListening?.difficultyLevel || 'Medium'}
            </span>
          </div>

          {recommendedListening && (
            <div className="mt-4 p-3.5 bg-[#F0F5F1] rounded-2xl border border-[#CBD7CF]">
              <div className="text-xs font-bold text-[#00543C] uppercase tracking-wide">
                RECOMMENDED TEST
              </div>
              <div className="font-bold text-sm text-[#161D1A] mt-0.5">
                {recommendedListening.title}
              </div>
            </div>
          )}

          <button
            type="button"
            onClick={() => onStartListeningTest(recommendedListening?.id || 'listening_test_medium_01')}
            className="mt-4 w-full py-3 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-sm flex items-center justify-center gap-2 shadow-xs transition-colors"
          >
            Start Listening Test
            <Play size={16} fill="currentColor" />
          </button>
        </div>

        {/* Module 3: Writing */}
        <div className="bg-white rounded-3xl border border-[#CBD7CF] p-5 mb-4 shadow-2xs">
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-3">
              <div className="w-11 h-11 rounded-2xl bg-[#BFF1D8] text-[#00543C] flex items-center justify-center">
                <PenTool size={20} />
              </div>
              <div>
                <h3 className="font-bold text-base text-[#161D1A]">Writing Practice</h3>
                <p className="text-xs text-[#45524B]">
                  Task 1 Charts & Task 2 Essays with AI Examiner Grading
                </p>
              </div>
            </div>
            <span className="text-xs font-bold px-2.5 py-1 rounded-lg bg-[#BFF1D8] text-[#00543C]">
              {recommendedWriting?.difficultyLevel || 'Medium'}
            </span>
          </div>

          {recommendedWriting && (
            <div className="mt-4 p-3.5 bg-[#F0F5F1] rounded-2xl border border-[#CBD7CF]">
              <div className="text-xs font-bold text-[#00543C] uppercase tracking-wide">
                RECOMMENDED TASK
              </div>
              <div className="font-bold text-sm text-[#161D1A] mt-0.5">
                {recommendedWriting.title}
              </div>
            </div>
          )}

          <button
            type="button"
            onClick={() => onStartWritingTask(recommendedWriting?.id || 'writing_task_01')}
            className="mt-4 w-full py-3 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-sm flex items-center justify-center gap-2 shadow-xs transition-colors"
          >
            Start Writing Practice
            <Play size={16} fill="currentColor" />
          </button>
        </div>

        {/* Module 4: Speaking */}
        <div className="bg-white rounded-3xl border border-[#CBD7CF] p-5 mb-4 shadow-2xs">
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-3">
              <div className="w-11 h-11 rounded-2xl bg-[#BFF1D8] text-[#00543C] flex items-center justify-center">
                <Mic size={20} />
              </div>
              <div>
                <h3 className="font-bold text-base text-[#161D1A]">Speaking Practice</h3>
                <p className="text-xs text-[#45524B]">
                  Part 1, 2 & 3 Voice Recording with Pronunciation & Criteria
                </p>
              </div>
            </div>
            <span className="text-xs font-bold px-2.5 py-1 rounded-lg bg-[#BFF1D8] text-[#00543C]">
              {recommendedSpeaking?.difficultyLevel || 'Medium'}
            </span>
          </div>

          {recommendedSpeaking && (
            <div className="mt-4 p-3.5 bg-[#F0F5F1] rounded-2xl border border-[#CBD7CF]">
              <div className="text-xs font-bold text-[#00543C] uppercase tracking-wide">
                RECOMMENDED TASK
              </div>
              <div className="font-bold text-sm text-[#161D1A] mt-0.5">
                {recommendedSpeaking.title}
              </div>
            </div>
          )}

          <button
            type="button"
            onClick={() => onStartSpeakingTask(recommendedSpeaking?.id || 'speaking_task_01')}
            className="mt-4 w-full py-3 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-sm flex items-center justify-center gap-2 shadow-xs transition-colors"
          >
            Start Speaking Practice
            <Play size={16} fill="currentColor" />
          </button>
        </div>
      </div>
    </div>
  );
};
