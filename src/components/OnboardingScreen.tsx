import React, { useState } from 'react';
import { ArrowLeft, ArrowRight, Check, Calendar, Headphones, BookOpen, PenTool, Mic } from 'lucide-react';
import { UserProfile, calculateStartingBand } from '../types';

interface OnboardingScreenProps {
  initialProfile: UserProfile;
  onFinished: (updatedProfile: UserProfile) => void;
}

export const OnboardingScreen: React.FC<OnboardingScreenProps> = ({
  initialProfile,
  onFinished
}) => {
  const [step, setStep] = useState(0);
  const [profile, setProfile] = useState<UserProfile>(initialProfile);

  const bands = [5.5, 6.0, 6.5, 7.0, 7.5, 8.0, 8.5, 9.0];

  const bandDescriptions: Record<number, string> = {
    5.5: 'Modest User',
    6.0: 'Competent User',
    6.5: 'Competent +',
    7.0: 'Good User',
    7.5: 'Very Good User',
    8.0: 'Expert User',
    8.5: 'Near Native',
    9.0: 'Expert Native'
  };

  const timelineOptions = [
    { label: 'Within 1 Month', desc: 'Upcoming test date soon' },
    { label: 'In 3 Months', desc: 'Standard preparation schedule' },
    { label: 'In 6 Months', desc: 'Comprehensive long-term plan' },
    { label: 'Flexible / Undecided', desc: 'Learning at custom pace' }
  ];

  const handleNext = () => {
    if (step < 2) {
      setStep(step + 1);
    } else {
      const completedProfile: UserProfile = {
        ...profile,
        isOnboardingCompleted: true
      };
      onFinished(completedProfile);
    }
  };

  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] flex flex-col p-4 sm:p-6 max-w-xl mx-auto">
      {/* Top Bar with Step Indicators */}
      <div className="flex items-center justify-between py-4 mb-2">
        <div className="flex items-center gap-2">
          {step > 0 ? (
            <button
              type="button"
              onClick={() => setStep(step - 1)}
              className="w-9 h-9 rounded-full bg-white border border-[#CBD7CF] flex items-center justify-center text-[#45524B] hover:text-[#161D1A]"
            >
              <ArrowLeft size={18} />
            </button>
          ) : (
            <div className="w-9 h-9" />
          )}
          <span className="font-bold text-sm text-[#161D1A]">Setup Profile</span>
        </div>

        {/* Step Pills */}
        <div className="flex items-center gap-1.5">
          {[0, 1, 2].map((s) => (
            <div
              key={s}
              className={`h-2 rounded-full transition-all duration-300 ${
                step === s ? 'w-6 bg-[#00543C]' : 'w-2 bg-[#CBD7CF]'
              }`}
            />
          ))}
        </div>
      </div>

      {/* Step Content */}
      <div className="flex-1 bg-white rounded-3xl border border-[#CBD7CF] p-6 sm:p-8 shadow-sm flex flex-col justify-between">
        <div>
          {step === 0 && (
            <div>
              <div className="inline-block bg-[#BFF1D8] text-[#00543C] text-xs font-bold px-3 py-1 rounded-full mb-3 tracking-wider">
                STEP 1 OF 3 • TARGET SCORE
              </div>
              <h2 className="text-2xl font-extrabold text-[#161D1A] tracking-tight mb-2">
                What is your target overall band score?
              </h2>
              <p className="text-sm text-[#45524B] mb-6">
                Most universities and migration visas require Band 6.5 to 7.5.
              </p>

              <div className="grid grid-cols-2 gap-3">
                {bands.map((b) => {
                  const isSelected = profile.targetBand === b;
                  return (
                    <button
                      key={b}
                      type="button"
                      onClick={() => setProfile({ ...profile, targetBand: b })}
                      className={`p-3.5 rounded-2xl border text-left flex items-center justify-between transition-all ${
                        isSelected
                          ? 'border-[#00543C] bg-[#BFF1D8]/40 ring-1 ring-[#00543C]'
                          : 'border-[#CBD7CF] hover:border-gray-400 bg-white'
                      }`}
                    >
                      <div>
                        <div className="font-bold text-base text-[#161D1A]">
                          Band {b.toFixed(1)}
                        </div>
                        <div className="text-xs text-[#45524B]">
                          {bandDescriptions[b] || 'Goal'}
                        </div>
                      </div>
                      {isSelected && (
                        <div className="w-5 h-5 rounded-full bg-[#00543C] text-white flex items-center justify-center">
                          <Check size={12} strokeWidth={3} />
                        </div>
                      )}
                    </button>
                  );
                })}
              </div>
            </div>
          )}

          {step === 1 && (
            <div>
              <div className="inline-block bg-[#BFF1D8] text-[#00543C] text-xs font-bold px-3 py-1 rounded-full mb-3 tracking-wider">
                STEP 2 OF 3 • TIMELINE
              </div>
              <h2 className="text-2xl font-extrabold text-[#161D1A] tracking-tight mb-2">
                When do you plan to take the exam?
              </h2>
              <p className="text-sm text-[#45524B] mb-6">
                This helps us pace your daily practice modules and mock exam schedules.
              </p>

              <div className="space-y-3">
                {timelineOptions.map((opt) => {
                  const isSelected = profile.testDate === opt.label;
                  return (
                    <button
                      key={opt.label}
                      type="button"
                      onClick={() => setProfile({ ...profile, testDate: opt.label })}
                      className={`w-full p-4 rounded-2xl border text-left flex items-center justify-between transition-all ${
                        isSelected
                          ? 'border-[#00543C] bg-[#BFF1D8]/40 ring-1 ring-[#00543C]'
                          : 'border-[#CBD7CF] hover:border-gray-400 bg-white'
                      }`}
                    >
                      <div className="flex items-center gap-3">
                        <div className={`p-2 rounded-xl ${isSelected ? 'bg-[#00543C] text-white' : 'bg-[#F0F5F1] text-[#45524B]'}`}>
                          <Calendar size={18} />
                        </div>
                        <div>
                          <div className="font-bold text-sm text-[#161D1A]">
                            {opt.label}
                          </div>
                          <div className="text-xs text-[#45524B]">
                            {opt.desc}
                          </div>
                        </div>
                      </div>
                      {isSelected && (
                        <div className="w-5 h-5 rounded-full bg-[#00543C] text-white flex items-center justify-center">
                          <Check size={12} strokeWidth={3} />
                        </div>
                      )}
                    </button>
                  );
                })}
              </div>
            </div>
          )}

          {step === 2 && (
            <div>
              <div className="inline-block bg-[#BFF1D8] text-[#00543C] text-xs font-bold px-3 py-1 rounded-full mb-3 tracking-wider">
                STEP 3 OF 3 • SELF ASSESSMENT
              </div>
              <h2 className="text-2xl font-extrabold text-[#161D1A] tracking-tight mb-2">
                Assess your current skill levels
              </h2>
              <p className="text-sm text-[#45524B] mb-6">
                Set your estimated starting band score for each of the 4 IELTS skills.
              </p>

              <div className="space-y-4">
                {/* Listening */}
                <SkillSliderRow
                  icon={<Headphones size={18} />}
                  label="Listening"
                  value={profile.listeningBand}
                  onChange={(val) => setProfile({ ...profile, listeningBand: val })}
                />
                {/* Reading */}
                <SkillSliderRow
                  icon={<BookOpen size={18} />}
                  label="Reading"
                  value={profile.readingBand}
                  onChange={(val) => setProfile({ ...profile, readingBand: val })}
                />
                {/* Writing */}
                <SkillSliderRow
                  icon={<PenTool size={18} />}
                  label="Writing"
                  value={profile.writingBand}
                  onChange={(val) => setProfile({ ...profile, writingBand: val })}
                />
                {/* Speaking */}
                <SkillSliderRow
                  icon={<Mic size={18} />}
                  label="Speaking"
                  value={profile.speakingBand}
                  onChange={(val) => setProfile({ ...profile, speakingBand: val })}
                />
              </div>

              {/* Live Baseline Preview */}
              <div className="mt-5 p-3.5 rounded-2xl bg-[#F0F5F1] border border-[#CBD7CF] flex items-center justify-between text-xs font-semibold">
                <span className="text-[#45524B]">Calculated Baseline:</span>
                <span className="text-sm font-extrabold text-[#00543C]">
                  Overall Band {calculateStartingBand(profile).toFixed(1)}
                </span>
              </div>
            </div>
          )}
        </div>

        {/* CTA Button */}
        <div className="mt-8 pt-4 border-t border-[#CBD7CF]">
          <button
            type="button"
            onClick={handleNext}
            className="w-full py-3.5 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-sm sm:text-base flex items-center justify-center gap-2 transition-all shadow-sm"
          >
            {step === 2 ? 'Complete & Start Learning' : 'Continue'}
            <ArrowRight size={17} />
          </button>
        </div>
      </div>
    </div>
  );
};

interface SkillSliderRowProps {
  icon: React.ReactNode;
  label: string;
  value: number;
  onChange: (val: number) => void;
}

const SkillSliderRow: React.FC<SkillSliderRowProps> = ({
  icon,
  label,
  value,
  onChange
}) => {
  return (
    <div className="p-3.5 rounded-2xl border border-[#CBD7CF] bg-white">
      <div className="flex items-center justify-between mb-2">
        <div className="flex items-center gap-2.5">
          <div className="p-1.5 rounded-lg bg-[#BFF1D8] text-[#00543C]">
            {icon}
          </div>
          <span className="font-bold text-sm text-[#161D1A]">{label}</span>
        </div>
        <span className="text-xs font-extrabold px-2.5 py-1 rounded-lg bg-[#FFDFBD] text-[#3A2500]">
          Band {value.toFixed(1)}
        </span>
      </div>
      <input
        type="range"
        min={1.0}
        max={9.0}
        step={0.5}
        value={value}
        onChange={(e) => onChange(parseFloat(e.target.value))}
        className="w-full accent-[#00543C] cursor-pointer"
      />
    </div>
  );
};
