import React, { useState, useEffect } from 'react';
import {
  ArrowLeft,
  Clock,
  FileText,
  Camera,
  CheckCircle2,
  Sparkles,
  BarChart2,
  AlertTriangle
} from 'lucide-react';
import { WritingTask, WritingAttempt } from '../types';
import { aiEvaluationService } from '../services/aiEvaluation';

interface WritingPracticeScreenProps {
  task: WritingTask;
  onSubmit: (attempt: WritingAttempt) => void;
  onBack: () => void;
}

export const WritingPracticeScreen: React.FC<WritingPracticeScreenProps> = ({
  task,
  onSubmit,
  onBack
}) => {
  const [inputMethod, setInputMethod] = useState<'TEXT' | 'PHOTO_SCAN'>('TEXT');
  const [essayText, setEssayText] = useState('');
  const [photoPreview, setPhotoPreview] = useState<string | null>(null);
  const [secondsElapsed, setSecondsElapsed] = useState(0);
  const [isEvaluating, setIsEvaluating] = useState(false);

  useEffect(() => {
    const timer = setInterval(() => {
      setSecondsElapsed((prev) => prev + 1);
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const formatTime = (secs: number) => {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  const wordCount = essayText.trim().split(/\s+/).filter(Boolean).length;
  const isWordCountMet = wordCount >= task.targetWordCount;

  const handlePhotoUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setPhotoPreview(reader.result as string);
        if (!essayText) {
          setEssayText("Handwritten submission captured via photo scanner. Ready for multimodal AI examiner evaluation.");
        }
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async () => {
    setIsEvaluating(true);
    try {
      const attempt = await aiEvaluationService.evaluateWritingSubmission(
        task,
        inputMethod,
        essayText,
        photoPreview,
        secondsElapsed
      );
      onSubmit(attempt);
    } catch {
      setIsEvaluating(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] pb-12 flex flex-col">
      {/* Header */}
      <header className="sticky top-0 z-30 bg-[#00543C] text-white px-4 py-3 shadow-md flex items-center justify-between">
        <div className="flex items-center gap-3">
          <button
            type="button"
            onClick={onBack}
            className="w-9 h-9 rounded-full bg-white/10 hover:bg-white/20 flex items-center justify-center transition-colors"
          >
            <ArrowLeft size={18} />
          </button>
          <div>
            <div className="text-xs text-white/80 font-bold uppercase tracking-wide">
              IELTS {task.taskType} Writing
            </div>
            <h1 className="text-sm sm:text-base font-bold truncate max-w-[200px] sm:max-w-md">
              {task.title}
            </h1>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1.5 bg-black/25 px-3 py-1.5 rounded-full text-xs sm:text-sm font-mono font-bold">
            <Clock size={15} />
            <span>{formatTime(secondsElapsed)}</span>
          </div>

          <button
            type="button"
            onClick={handleSubmit}
            disabled={isEvaluating || (!essayText.trim() && !photoPreview)}
            className="px-4 py-1.5 bg-[#FFDFBD] text-[#3A2500] hover:bg-[#FAD1A5] text-xs sm:text-sm font-bold rounded-full transition-all shadow-xs disabled:opacity-50 flex items-center gap-1.5"
          >
            {isEvaluating ? (
              <>
                <Sparkles size={14} className="animate-spin" /> Evaluating...
              </>
            ) : (
              'Submit for Grading'
            )}
          </button>
        </div>
      </header>

      {/* Main Container */}
      <main className="flex-1 max-w-4xl mx-auto w-full p-4 sm:p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Left: Prompt & Image */}
        <section className="bg-white rounded-3xl border border-[#CBD7CF] p-6 shadow-sm overflow-y-auto max-h-[calc(100vh-140px)]">
          <div className="flex items-center justify-between pb-3 border-b border-[#CBD7CF] mb-4">
            <span className="text-xs font-bold text-[#00543C] uppercase tracking-wide">
              {task.taskType} Prompt
            </span>
            <span className="text-xs font-semibold text-[#45524B]">
              Recommended: {task.recommendedTimeMinutes} mins
            </span>
          </div>

          <h2 className="text-lg font-bold text-[#161D1A] mb-3 leading-snug">
            {task.title}
          </h2>

          <div className="bg-[#F0F5F1] p-4 rounded-2xl border border-[#CBD7CF] text-sm text-[#161D1A] leading-relaxed mb-4">
            {task.prompt}
          </div>

          {task.imageUrl && (
            <div className="mt-4 rounded-2xl overflow-hidden border border-[#CBD7CF]">
              <img
                src={task.imageUrl}
                alt="Task Chart / Diagram"
                className="w-full h-auto object-cover max-h-72"
              />
            </div>
          )}

          <div className="mt-4 p-3 bg-[#BFF1D8]/40 border border-[#BFF1D8] rounded-2xl text-xs text-[#00543C]">
            <strong>Target Length:</strong> Minimum {task.targetWordCount} words. Include a clear overview and appropriate paragraph structure.
          </div>
        </section>

        {/* Right: Essay Editor / Photo Scan */}
        <section className="bg-white rounded-3xl border border-[#CBD7CF] p-6 shadow-sm flex flex-col justify-between max-h-[calc(100vh-140px)]">
          <div>
            {/* Input Method Switcher */}
            <div className="grid grid-cols-2 bg-[#F0F5F1] p-1 rounded-full mb-4">
              <button
                type="button"
                onClick={() => setInputMethod('TEXT')}
                className={`flex items-center justify-center gap-1.5 py-1.5 text-xs font-bold rounded-full transition-all ${
                  inputMethod === 'TEXT'
                    ? 'bg-[#00543C] text-white shadow-xs'
                    : 'text-[#45524B]'
                }`}
              >
                <FileText size={14} /> Type Essay
              </button>
              <button
                type="button"
                onClick={() => setInputMethod('PHOTO_SCAN')}
                className={`flex items-center justify-center gap-1.5 py-1.5 text-xs font-bold rounded-full transition-all ${
                  inputMethod === 'PHOTO_SCAN'
                    ? 'bg-[#00543C] text-white shadow-xs'
                    : 'text-[#45524B]'
                }`}
              >
                <Camera size={14} /> Camera / Photo Scan
              </button>
            </div>

            {inputMethod === 'TEXT' ? (
              <div>
                <textarea
                  value={essayText}
                  onChange={(e) => setEssayText(e.target.value)}
                  placeholder={`Write your ${task.taskType} response here...\n\nIntroduction & Overview:\n\nBody Paragraph 1:\n\nBody Paragraph 2:\n\nConclusion:`}
                  rows={14}
                  className="w-full p-4 bg-[#F8FAF9] rounded-2xl border border-[#CBD7CF] text-sm leading-relaxed outline-none focus:border-[#00543C] focus:bg-white resize-none"
                />

                {/* Word Counter Indicator */}
                <div className="flex items-center justify-between mt-3 text-xs">
                  <div className="flex items-center gap-1.5">
                    {isWordCountMet ? (
                      <span className="flex items-center gap-1 text-[#00543C] font-bold">
                        <CheckCircle2 size={15} /> Goal reached ({wordCount} / {task.targetWordCount})
                      </span>
                    ) : (
                      <span className="flex items-center gap-1 text-[#B26A00] font-semibold">
                        <AlertTriangle size={15} /> {wordCount} / {task.targetWordCount} words ({task.targetWordCount - wordCount} more needed)
                      </span>
                    )}
                  </div>
                  <span className="text-[#45524B] font-mono">
                    Time: {formatTime(secondsElapsed)}
                  </span>
                </div>
              </div>
            ) : (
              <div className="space-y-4">
                <div className="border-2 border-dashed border-[#CBD7CF] rounded-2xl p-6 text-center hover:border-[#00543C] transition-colors">
                  <input
                    type="file"
                    accept="image/*"
                    onChange={handlePhotoUpload}
                    id="handwriting-photo-input"
                    className="hidden"
                  />
                  <label
                    htmlFor="handwriting-photo-input"
                    className="cursor-pointer flex flex-col items-center justify-center"
                  >
                    <Camera size={36} className="text-[#00543C] mb-2" />
                    <span className="text-sm font-bold text-[#161D1A]">
                      Upload Handwritten Essay Photo
                    </span>
                    <span className="text-xs text-[#45524B] mt-1">
                      PNG, JPG or WebP (AI Examiner will transcribe & grade)
                    </span>
                  </label>
                </div>

                {photoPreview && (
                  <div className="relative rounded-2xl overflow-hidden border border-[#CBD7CF] max-h-48">
                    <img
                      src={photoPreview}
                      alt="Scanned handwriting"
                      className="w-full h-full object-cover"
                    />
                  </div>
                )}

                <div className="p-3 bg-[#F0F5F1] rounded-2xl text-xs text-[#45524B]">
                  Write your answer on paper under real exam conditions, then photograph it for official AI Examiner evaluation.
                </div>
              </div>
            )}
          </div>

          <div className="pt-4 border-t border-[#CBD7CF] mt-4">
            <button
              type="button"
              onClick={handleSubmit}
              disabled={isEvaluating || (!essayText.trim() && !photoPreview)}
              className="w-full py-3.5 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-sm sm:text-base flex items-center justify-center gap-2 shadow-sm transition-all disabled:opacity-50"
            >
              {isEvaluating ? (
                <>
                  <Sparkles size={18} className="animate-spin" /> AI Examiner Evaluating Essay...
                </>
              ) : (
                <>
                  Submit & Receive Examiner Band Score
                  <Sparkles size={16} />
                </>
              )}
            </button>
          </div>
        </section>
      </main>
    </div>
  );
};

interface WritingResultsScreenProps {
  attempt: WritingAttempt;
  onReturnToHub: () => void;
}

export const WritingResultsScreen: React.FC<WritingResultsScreenProps> = ({
  attempt,
  onReturnToHub
}) => {
  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] pb-12">
      <div className="max-w-2xl mx-auto px-4 sm:px-6 pt-6">
        <div className="text-center mb-6">
          <div className="inline-block bg-[#BFF1D8] text-[#00543C] text-xs font-bold px-3 py-1 rounded-full mb-3 tracking-wider">
            IELTS EXAMINER EVALUATION
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-[#161D1A]">
            Writing Band Breakdown
          </h1>
          <p className="text-sm text-[#45524B] mt-1">{attempt.taskTitle}</p>
        </div>

        {/* Hero Overall Band Card */}
        <div className="bg-gradient-to-br from-[#00543C] to-[#00785A] text-white rounded-3xl p-6 sm:p-8 shadow-sm mb-6 text-center">
          <div className="text-xs uppercase tracking-wider font-semibold text-white/80">
            Overall Band Score
          </div>
          <div className="text-5xl sm:text-6xl font-black mt-2 mb-2 text-[#FFB84D]">
            Band {attempt.overallBand.toFixed(1)}
          </div>
          <p className="text-sm text-white/90">
            Word Count: {attempt.wordCount} words • Input: {attempt.inputMethod === 'PHOTO_SCAN' ? 'Photo Scan' : 'Typed Essay'}
          </p>
        </div>

        {/* General Feedback */}
        <div className="bg-white rounded-3xl border border-[#CBD7CF] p-5 sm:p-6 mb-6 shadow-2xs">
          <div className="flex items-center gap-2 mb-2">
            <Sparkles size={18} className="text-[#00543C]" />
            <h2 className="font-bold text-base text-[#161D1A]">Examiner Summary</h2>
          </div>
          <p className="text-sm text-[#45524B] leading-relaxed">
            {attempt.generalFeedback}
          </p>
        </div>

        {/* 4 Criteria Scoring Rubrics */}
        <h2 className="text-lg font-bold text-[#161D1A] mb-3">
          Official 4 Criteria Breakdown
        </h2>

        <div className="space-y-3.5 mb-8">
          {attempt.criteriaScores.map((c) => (
            <div
              key={c.criterionName}
              className="bg-white rounded-2xl border border-[#CBD7CF] p-4 sm:p-5 shadow-2xs"
            >
              <div className="flex items-center justify-between mb-2">
                <span className="font-bold text-sm sm:text-base text-[#161D1A]">
                  {c.criterionName}
                </span>
                <span className="font-extrabold text-sm px-3 py-1 rounded-full bg-[#BFF1D8] text-[#00543C]">
                  Band {c.score.toFixed(1)}
                </span>
              </div>
              <p className="text-xs sm:text-sm text-[#45524B] leading-relaxed">
                {c.feedbackNote}
              </p>
            </div>
          ))}
        </div>

        <button
          type="button"
          onClick={onReturnToHub}
          className="w-full py-4 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-base shadow-sm transition-all text-center"
        >
          Done & Return to Practice Hub
        </button>
      </div>
    </div>
  );
};
