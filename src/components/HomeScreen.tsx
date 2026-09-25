import React from 'react';
import {
  LogOut,
  Trophy,
  Calendar,
  BookOpen,
  Headphones,
  PenTool,
  Mic,
  Video,
  Timer,
  MapPin,
  ArrowRight
} from 'lucide-react';
import { UserProfile, calculateStartingBand } from '../types';

interface HomeScreenProps {
  userProfile: UserProfile;
  onSignOut: () => void;
  onNavigateToPractice: () => void;
  onNavigateToVideoLessons: () => void;
  onNavigateToMockExams: () => void;
  onNavigateToCenterLocator: () => void;
}

export const HomeScreen: React.FC<HomeScreenProps> = ({
  userProfile,
  onSignOut,
  onNavigateToPractice,
  onNavigateToVideoLessons,
  onNavigateToMockExams,
  onNavigateToCenterLocator
}) => {
  const baseline = calculateStartingBand(userProfile);
  const target = userProfile.targetBand;
  const currentAvg = (
    userProfile.readingBand +
    userProfile.listeningBand +
    userProfile.writingBand +
    userProfile.speakingBand
  ) / 4.0;

  const progress = target > baseline
    ? Math.min(1, Math.max(0, (currentAvg - baseline) / (target - baseline)))
    : 1;

  const progressPercent = Math.round(progress * 100);
  const gapRemaining = Math.max(0, target - currentAvg);

  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] pb-12">
      <div className="max-w-xl mx-auto px-4 sm:px-6 pt-5">
        {/* Header Bar */}
        <div className="flex items-center justify-between py-2 mb-4">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-full bg-[#BFF1D8] border-2 border-white flex items-center justify-center font-black text-[#00543C] text-base shadow-xs">
              BD
            </div>
            <div>
              <div className="text-xs text-[#45524B] font-medium">Assalamu Alaikum 👋</div>
              <div className="font-bold text-base text-[#161D1A]">
                {userProfile.phone || 'IELTS Candidate'}
              </div>
            </div>
          </div>

          <button
            type="button"
            onClick={onSignOut}
            title="Sign Out"
            className="w-10 h-10 rounded-full bg-white border border-[#CBD7CF] flex items-center justify-center text-[#45524B] hover:text-red-600 transition-colors shadow-xs"
          >
            <LogOut size={18} />
          </button>
        </div>

        {/* HERO: Goal-gradient band progress card */}
        <div className="rounded-3xl p-5 sm:p-6 bg-gradient-to-r from-[#00543C] to-[#00785A] text-white shadow-sm mb-4">
          <div className="flex items-center gap-4">
            {/* SVG Circular Progress Ring */}
            <div className="relative w-16 h-16 shrink-0 flex items-center justify-center">
              <svg className="w-16 h-16 transform -rotate-90" viewBox="0 0 36 36">
                <path
                  className="text-white/20"
                  strokeWidth="3.5"
                  stroke="currentColor"
                  fill="none"
                  d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                />
                <path
                  className="text-[#FFB84D]"
                  strokeDasharray={`${Math.max(5, progressPercent)}, 100`}
                  strokeLinecap="round"
                  strokeWidth="3.5"
                  stroke="currentColor"
                  fill="none"
                  d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                />
              </svg>
              <span className="absolute text-xs font-black text-white">
                {progressPercent}%
              </span>
            </div>

            <div className="flex-1">
              <h2 className="text-base sm:text-lg font-bold">
                Target Band {target.toFixed(1)} Roadmap
              </h2>
              <p className="text-xs text-white/85 mt-1 leading-relaxed">
                {progress >= 1 ? (
                  'Goal reached — keep it sharp! 🏆'
                ) : (
                  <>
                    Now Band {currentAvg.toFixed(1)} • +{gapRemaining.toFixed(1)} to go. You're {progressPercent}% there.
                  </>
                )}
              </p>
            </div>
          </div>
        </div>

        {/* 2 Grid Cards */}
        <div className="grid grid-cols-2 gap-3 mb-4">
          {/* Target Goal */}
          <div className="bg-[#BFF1D8]/60 border border-[#96E2BD] rounded-3xl p-4 flex flex-col justify-between h-34">
            <div className="w-9 h-9 rounded-full bg-white flex items-center justify-center text-[#00543C] shadow-2xs">
              <Trophy size={18} />
            </div>
            <div>
              <div className="text-xl sm:text-2xl font-black text-[#00543C]">
                Band {target.toFixed(1)}
              </div>
              <div className="text-xs font-semibold text-[#00543C]/80">
                Target Goal
              </div>
            </div>
          </div>

          {/* Exam Timeline */}
          <div className="bg-[#FFDFBD]/60 border border-[#F6C697] rounded-3xl p-4 flex flex-col justify-between h-34">
            <div className="w-9 h-9 rounded-full bg-white flex items-center justify-center text-[#B26A00] shadow-2xs">
              <Calendar size={18} />
            </div>
            <div>
              <div className="text-base sm:text-lg font-black text-[#3A2500] truncate">
                {userProfile.testDate}
              </div>
              <div className="text-xs font-semibold text-[#3A2500]/80">
                Exam Timeline
              </div>
            </div>
          </div>
        </div>

        {/* Baseline Estimate Card */}
        <div className="bg-white rounded-3xl border border-[#CBD7CF] p-4 sm:p-5 flex items-center justify-between shadow-2xs mb-6">
          <div>
            <div className="text-xs text-[#45524B]">Starting Estimated Level</div>
            <div className="text-lg font-bold text-[#161D1A] mt-0.5">
              Overall Band {baseline.toFixed(1)}
            </div>
          </div>
          <span className="text-xs font-bold px-3 py-1.5 rounded-full bg-[#F0F5F1] text-[#45524B] border border-[#CBD7CF]">
            Initial Baseline
          </span>
        </div>

        {/* 4 Skills Section */}
        <div className="flex items-center justify-between mb-3">
          <h3 className="font-bold text-base sm:text-lg text-[#161D1A]">
            Practice your 4 skills
          </h3>
          <button
            type="button"
            onClick={onNavigateToPractice}
            className="text-xs font-bold text-[#00543C] hover:underline flex items-center gap-1"
          >
            Practice Hub <ArrowRight size={14} />
          </button>
        </div>

        <div className="space-y-2.5 mb-6">
          <SkillRow
            name="Reading"
            icon={<BookOpen size={18} />}
            current={userProfile.readingBand}
            target={target}
            onClick={onNavigateToPractice}
          />
          <SkillRow
            name="Listening"
            icon={<Headphones size={18} />}
            current={userProfile.listeningBand}
            target={target}
            onClick={onNavigateToPractice}
          />
          <SkillRow
            name="Writing"
            icon={<PenTool size={18} />}
            current={userProfile.writingBand}
            target={target}
            onClick={onNavigateToPractice}
          />
          <SkillRow
            name="Speaking"
            icon={<Mic size={18} />}
            current={userProfile.speakingBand}
            target={target}
            onClick={onNavigateToPractice}
          />
        </div>

        {/* Level up & book in person section */}
        <h3 className="font-bold text-base sm:text-lg text-[#161D1A] mb-3">
          Level up & book in person
        </h3>

        <div className="space-y-3">
          {/* Video Lessons */}
          <div
            onClick={onNavigateToVideoLessons}
            className="cursor-pointer bg-white hover:bg-[#F8FAF9] rounded-3xl border border-[#CBD7CF] p-4 sm:p-5 flex items-center justify-between transition-all shadow-2xs"
          >
            <div className="flex items-center gap-3.5">
              <div className="w-12 h-12 rounded-2xl bg-[#00543C] text-white flex items-center justify-center shadow-xs">
                <Video size={22} />
              </div>
              <div>
                <div className="font-bold text-sm sm:text-base text-[#161D1A]">
                  Video Lessons Library
                </div>
                <div className="text-xs text-[#45524B]">
                  Watch strategy lessons, download for offline
                </div>
              </div>
            </div>
            <ArrowRight size={18} className="text-[#00543C]" />
          </div>

          {/* Full Sitting Mock Exam */}
          <div
            onClick={onNavigateToMockExams}
            className="cursor-pointer bg-[#00543C] hover:bg-[#004732] text-white rounded-3xl p-4 sm:p-5 flex items-center justify-between transition-all shadow-xs"
          >
            <div className="flex items-center gap-3.5">
              <div className="w-12 h-12 rounded-2xl bg-white/20 text-white flex items-center justify-center">
                <Timer size={22} />
              </div>
              <div>
                <div className="font-bold text-sm sm:text-base">
                  Weekly & Monthly Mock Exams
                </div>
                <div className="text-xs text-white/80">
                  Full 4-skill exam, timed exactly like the real thing
                </div>
              </div>
            </div>
            <ArrowRight size={18} className="text-white" />
          </div>

          {/* Center Locator */}
          <div
            onClick={onNavigateToCenterLocator}
            className="cursor-pointer bg-white hover:bg-[#F8FAF9] rounded-3xl border border-[#CBD7CF] p-4 sm:p-5 flex items-center justify-between transition-all shadow-2xs"
          >
            <div className="flex items-center gap-3.5">
              <div className="w-12 h-12 rounded-2xl bg-[#FFDFBD] text-[#B26A00] flex items-center justify-center shadow-xs">
                <MapPin size={22} />
              </div>
              <div>
                <div className="font-bold text-sm sm:text-base text-[#161D1A]">
                  Find Partner Practice Center
                </div>
                <div className="text-xs text-[#45524B]">
                  Book mock exam slots in Dhaka, CTG & Sylhet
                </div>
              </div>
            </div>
            <ArrowRight size={18} className="text-[#00543C]" />
          </div>
        </div>
      </div>
    </div>
  );
};

interface SkillRowProps {
  name: string;
  icon: React.ReactNode;
  current: number;
  target: number;
  onClick: () => void;
}

const SkillRow: React.FC<SkillRowProps> = ({
  name,
  icon,
  current,
  target,
  onClick
}) => {
  const gap = target - current;
  const progressPercent = Math.min(100, Math.round((current / target) * 100));

  return (
    <div
      onClick={onClick}
      className="cursor-pointer bg-white hover:border-[#00543C] rounded-2xl border border-[#CBD7CF] p-3.5 flex items-center justify-between transition-all shadow-2xs"
    >
      <div className="flex items-center gap-3">
        <div className="p-2 rounded-xl bg-[#BFF1D8] text-[#00543C]">
          {icon}
        </div>
        <div>
          <div className="font-bold text-sm text-[#161D1A]">{name}</div>
          <div className="text-xs text-[#45524B] mb-1.5">
            Current: Band {current.toFixed(1)} / Target {target.toFixed(1)}
          </div>
          {/* Mini progress bar */}
          <div className="w-32 sm:w-44 h-1.5 bg-[#F0F5F1] rounded-full overflow-hidden">
            <div
              className="h-full bg-gradient-to-r from-[#00543C] to-[#00785A] rounded-full"
              style={{ width: `${progressPercent}%` }}
            />
          </div>
        </div>
      </div>

      <span
        className={`text-xs font-bold px-2.5 py-1 rounded-xl ${
          gap <= 0
            ? 'bg-[#BFF1D8] text-[#00543C]'
            : 'bg-[#F0F5F1] text-[#45524B]'
        }`}
      >
        {gap <= 0 ? 'Goal Reached' : `+${gap.toFixed(1)} needed`}
      </span>
    </div>
  );
};
