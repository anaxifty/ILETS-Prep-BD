import React, { useState, useRef, useEffect } from 'react';
import {
  ArrowLeft,
  Play,
  Pause,
  RotateCcw,
  Volume2,
  VolumeX,
  Clock,
  CheckCircle2,
  AlertCircle,
  HelpCircle
} from 'lucide-react';
import { ListeningTest, ListeningAttempt } from '../types';

interface ListeningPracticeScreenProps {
  test: ListeningTest;
  onSubmit: (attempt: ListeningAttempt) => void;
  onBack: () => void;
}

export const ListeningPracticeScreen: React.FC<ListeningPracticeScreenProps> = ({
  test,
  onSubmit,
  onBack
}) => {
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(test.audioDurationSeconds || 180);
  const [isMuted, setIsMuted] = useState(false);
  const [transferSeconds, setTransferSeconds] = useState(test.transferTimeSeconds || 600);
  const [isAudioFinished, setIsAudioFinished] = useState(false);
  const [startTime] = useState(Date.now());

  const audioRef = useRef<HTMLAudioElement | null>(null);

  useEffect(() => {
    const audio = audioRef.current;
    if (!audio) return;

    const handleTimeUpdate = () => {
      setCurrentTime(audio.currentTime);
      if (audio.duration && !isNaN(audio.duration)) {
        setDuration(audio.duration);
      }
    };

    const handleEnded = () => {
      setIsPlaying(false);
      setIsAudioFinished(true);
    };

    audio.addEventListener('timeupdate', handleTimeUpdate);
    audio.addEventListener('ended', handleEnded);

    return () => {
      audio.removeEventListener('timeupdate', handleTimeUpdate);
      audio.removeEventListener('ended', handleEnded);
    };
  }, []);

  // Transfer countdown timer
  useEffect(() => {
    const interval = setInterval(() => {
      setTransferSeconds((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  const togglePlay = () => {
    const audio = audioRef.current;
    if (!audio) return;
    if (isPlaying) {
      audio.pause();
      setIsPlaying(false);
    } else {
      audio.play().then(() => setIsPlaying(true)).catch(() => {
        // Fallback simulated player if remote audio fails CORS or blocked
        setIsPlaying(true);
      });
    }
  };

  const handleSeek = (e: React.ChangeEvent<HTMLInputElement>) => {
    const target = parseFloat(e.target.value);
    setCurrentTime(target);
    if (audioRef.current) {
      audioRef.current.currentTime = target;
    }
  };

  const handleAnswerChange = (questionId: number, val: string) => {
    setAnswers((prev) => ({ ...prev, [questionId]: val }));
  };

  const formatTime = (secs: number) => {
    const m = Math.floor(secs / 60);
    const s = Math.floor(secs % 60);
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  const handleSubmit = () => {
    let score = 0;
    test.questions.forEach((q) => {
      const userAns = (answers[q.id] || '').trim().toLowerCase();
      const actualAns = q.correctAnswer.trim().toLowerCase();
      if (userAns === actualAns) {
        score++;
      }
    });

    const total = test.questions.length;
    let band = 5.0;
    const ratio = total > 0 ? score / total : 0;
    if (ratio >= 0.9) band = 8.5;
    else if (ratio >= 0.75) band = 7.5;
    else if (ratio >= 0.6) band = 6.5;
    else if (ratio >= 0.4) band = 6.0;
    else if (ratio >= 0.2) band = 5.5;

    const timeTaken = Math.floor((Date.now() - startTime) / 1000);

    const attempt: ListeningAttempt = {
      id: `listening_att_${Date.now()}`,
      testId: test.id,
      testTitle: test.title,
      userId: 'default_user',
      userAnswers: Object.fromEntries(
        Object.entries(answers).map(([k, v]) => [k.toString(), v])
      ),
      score,
      totalQuestions: total,
      bandScore: band,
      timeTakenSeconds: timeTaken,
      timestamp: Date.now()
    };

    onSubmit(attempt);
  };

  const answeredCount = Object.keys(answers).length;

  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] pb-12 flex flex-col">
      {/* Audio Element */}
      <audio
        ref={audioRef}
        src={test.audioUrl}
        preload="auto"
        onPlay={() => setIsPlaying(true)}
        onPause={() => setIsPlaying(false)}
      />

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
              IELTS Listening Section
            </div>
            <h1 className="text-sm sm:text-base font-bold truncate max-w-[200px] sm:max-w-md">
              {test.title}
            </h1>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <div className="flex items-center gap-1.5 bg-black/25 px-3 py-1.5 rounded-full text-xs sm:text-sm font-mono font-bold">
            <Clock size={15} />
            <span>Transfer: {formatTime(transferSeconds)}</span>
          </div>

          <button
            type="button"
            onClick={handleSubmit}
            className="px-4 py-1.5 bg-[#FFDFBD] text-[#3A2500] hover:bg-[#FAD1A5] text-xs sm:text-sm font-bold rounded-full transition-all shadow-xs"
          >
            Submit ({answeredCount}/{test.questions.length})
          </button>
        </div>
      </header>

      {/* Fixed Sticky Audio Control Deck */}
      <div className="bg-white border-b border-[#CBD7CF] px-4 py-3 shadow-xs sticky top-[57px] z-20">
        <div className="max-w-2xl mx-auto flex items-center gap-3 sm:gap-4">
          <button
            type="button"
            onClick={togglePlay}
            className="w-11 h-11 rounded-full bg-[#00543C] text-white flex items-center justify-center hover:bg-[#00785A] transition-colors shrink-0 shadow-sm"
          >
            {isPlaying ? <Pause size={20} /> : <Play size={20} className="ml-0.5" fill="currentColor" />}
          </button>

          <div className="flex-1">
            <div className="flex items-center justify-between text-xs text-[#45524B] mb-1 font-mono">
              <span>{formatTime(currentTime)}</span>
              <span>{formatTime(duration)}</span>
            </div>
            <input
              type="range"
              min={0}
              max={duration || 100}
              value={currentTime}
              onChange={handleSeek}
              className="w-full accent-[#00543C] cursor-pointer"
            />
          </div>

          <button
            type="button"
            onClick={() => {
              if (audioRef.current) {
                audioRef.current.muted = !isMuted;
                setIsMuted(!isMuted);
              }
            }}
            className="p-2 text-[#45524B] hover:text-[#161D1A]"
          >
            {isMuted ? <VolumeX size={18} /> : <Volume2 size={18} />}
          </button>
        </div>
      </div>

      {/* Main Form & Questions */}
      <main className="flex-1 max-w-2xl mx-auto w-full p-4 sm:p-6 space-y-6">
        <div className="bg-[#BFF1D8]/50 border border-[#96E2BD] p-4 rounded-2xl flex items-center justify-between text-xs">
          <span className="font-semibold text-[#00543C]">
            Listen to the recording and answer Questions 1 to {test.questions.length} as you listen.
          </span>
          <span className="font-bold text-[#00543C] shrink-0 ml-2">
            Band {test.targetBand.toFixed(1)}
          </span>
        </div>

        {test.questions.map((q, idx) => (
          <div
            key={q.id}
            className="bg-white rounded-3xl border border-[#CBD7CF] p-5 shadow-2xs"
          >
            <div className="flex items-start gap-3 mb-3">
              <span className="w-6 h-6 rounded-full bg-[#00543C] text-white text-xs font-bold flex items-center justify-center shrink-0 mt-0.5">
                {idx + 1}
              </span>
              <div>
                <span className="text-2xs font-extrabold uppercase tracking-wider text-[#00543C] block mb-0.5">
                  Section {q.sectionNumber} • {q.type.replace('_', ' ')}
                </span>
                <h3 className="font-bold text-sm sm:text-base text-[#161D1A]">
                  {q.questionText}
                </h3>
              </div>
            </div>

            {/* Form Context snippet */}
            {q.formContext && (
              <div className="bg-[#F0F5F1] p-3 rounded-2xl font-mono text-xs text-[#161D1A] mb-3 whitespace-pre-line border border-[#CBD7CF]">
                {q.formContext}
              </div>
            )}

            {/* FORM_COMPLETION: text box */}
            {q.type === 'FORM_COMPLETION' && (
              <div className="mt-2">
                <input
                  type="text"
                  value={answers[q.id] || ''}
                  onChange={(e) => handleAnswerChange(q.id, e.target.value)}
                  placeholder="Write NO MORE THAN TWO WORDS AND/OR A NUMBER..."
                  className="w-full p-3 bg-[#F8FAF9] rounded-xl border border-[#CBD7CF] text-sm font-semibold outline-none focus:border-[#00543C] focus:bg-white"
                />
              </div>
            )}

            {/* MULTIPLE_CHOICE */}
            {q.type === 'MULTIPLE_CHOICE' && q.options && (
              <div className="space-y-2 mt-2">
                {q.options.map((opt) => {
                  const letter = opt.trim().charAt(0);
                  const isSelected = answers[q.id] === letter;
                  return (
                    <button
                      key={opt}
                      type="button"
                      onClick={() => handleAnswerChange(q.id, letter)}
                      className={`w-full text-left p-3 rounded-xl border text-xs sm:text-sm font-medium transition-all flex items-center justify-between ${
                        isSelected
                          ? 'border-[#00543C] bg-[#BFF1D8] text-[#00543C] font-bold'
                          : 'border-[#CBD7CF] bg-white text-[#161D1A] hover:bg-gray-50'
                      }`}
                    >
                      <span>{opt}</span>
                      {isSelected && <CheckCircle2 size={16} />}
                    </button>
                  );
                })}
              </div>
            )}

            {/* MATCHING */}
            {q.type === 'MATCHING' && q.matchingOptions && (
              <div className="space-y-2 mt-2">
                {q.matchingOptions.map((opt) => {
                  const letter = opt.trim().charAt(0);
                  const isSelected = answers[q.id] === letter;
                  return (
                    <button
                      key={opt}
                      type="button"
                      onClick={() => handleAnswerChange(q.id, letter)}
                      className={`w-full text-left p-3 rounded-xl border text-xs sm:text-sm font-medium transition-all flex items-center justify-between ${
                        isSelected
                          ? 'border-[#00543C] bg-[#BFF1D8] text-[#00543C] font-bold'
                          : 'border-[#CBD7CF] bg-white text-[#161D1A] hover:bg-gray-50'
                      }`}
                    >
                      <span>{opt}</span>
                      {isSelected && <CheckCircle2 size={16} />}
                    </button>
                  );
                })}
              </div>
            )}
          </div>
        ))}

        <div className="pt-4">
          <button
            type="button"
            onClick={handleSubmit}
            className="w-full py-4 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-base shadow-sm transition-all text-center"
          >
            Submit Listening Answers & View Results
          </button>
        </div>
      </main>
    </div>
  );
};

interface ListeningResultsScreenProps {
  test: ListeningTest;
  attempt: ListeningAttempt;
  onDone: () => void;
}

export const ListeningResultsScreen: React.FC<ListeningResultsScreenProps> = ({
  test,
  attempt,
  onDone
}) => {
  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] pb-12">
      <div className="max-w-2xl mx-auto px-4 sm:px-6 pt-6">
        <div className="text-center mb-6">
          <div className="inline-block bg-[#BFF1D8] text-[#00543C] text-xs font-bold px-3 py-1 rounded-full mb-3 tracking-wider">
            LISTENING TEST COMPLETED
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-[#161D1A]">
            Performance Review
          </h1>
          <p className="text-sm text-[#45524B] mt-1">{test.title}</p>
        </div>

        {/* Hero Score Card */}
        <div className="bg-gradient-to-br from-[#00543C] to-[#00785A] text-white rounded-3xl p-6 sm:p-8 shadow-sm mb-6 text-center">
          <div className="text-xs uppercase tracking-wider font-semibold text-white/80">
            Calculated Listening Band
          </div>
          <div className="text-5xl sm:text-6xl font-black mt-2 mb-2 text-[#FFB84D]">
            Band {attempt.bandScore.toFixed(1)}
          </div>
          <p className="text-sm text-white/90">
            {attempt.score} out of {attempt.totalQuestions} questions correct
          </p>
        </div>

        <h2 className="text-lg font-bold text-[#161D1A] mb-3">
          Answer Key & Explanations
        </h2>

        <div className="space-y-4 mb-8">
          {test.questions.map((q, idx) => {
            const userAns = (attempt.userAnswers[q.id.toString()] || '').trim();
            const correctAns = q.correctAnswer.trim();
            const isCorrect = userAns.toLowerCase() === correctAns.toLowerCase();

            return (
              <div
                key={q.id}
                className={`p-5 rounded-2xl bg-white border ${
                  isCorrect ? 'border-[#96E2BD]' : 'border-red-200'
                } shadow-2xs`}
              >
                <div className="flex items-start justify-between gap-3 mb-2">
                  <div className="flex items-start gap-2">
                    <span className="w-6 h-6 rounded-full bg-[#F0F5F1] text-[#161D1A] text-xs font-bold flex items-center justify-center shrink-0 mt-0.5">
                      {idx + 1}
                    </span>
                    <h3 className="font-bold text-sm text-[#161D1A]">
                      {q.questionText}
                    </h3>
                  </div>
                  {isCorrect ? (
                    <span className="flex items-center gap-1 text-xs font-bold text-[#00543C] bg-[#BFF1D8] px-2.5 py-1 rounded-full shrink-0">
                      <CheckCircle2 size={14} /> Correct
                    </span>
                  ) : (
                    <span className="flex items-center gap-1 text-xs font-bold text-red-700 bg-red-100 px-2.5 py-1 rounded-full shrink-0">
                      <AlertCircle size={14} /> Incorrect
                    </span>
                  )}
                </div>

                <div className="grid grid-cols-2 gap-2 my-3 text-xs">
                  <div className="p-2.5 rounded-xl bg-[#F0F5F1]">
                    <span className="text-[#45524B] block mb-0.5">Your Answer:</span>
                    <strong className="text-sm font-bold text-[#161D1A]">
                      {userAns || '(No Answer)'}
                    </strong>
                  </div>
                  <div className="p-2.5 rounded-xl bg-[#BFF1D8]/40 border border-[#BFF1D8]">
                    <span className="text-[#00543C] block mb-0.5">Correct Answer:</span>
                    <strong className="text-sm font-bold text-[#00543C]">
                      {correctAns}
                    </strong>
                  </div>
                </div>

                {q.explanation && (
                  <div className="text-xs text-[#45524B] bg-gray-50 p-3 rounded-xl border border-gray-100 flex items-start gap-2">
                    <HelpCircle size={15} className="text-[#00543C] shrink-0 mt-0.5" />
                    <span><strong>Audio Reference:</strong> {q.explanation}</span>
                  </div>
                )}
              </div>
            );
          })}
        </div>

        <button
          type="button"
          onClick={onDone}
          className="w-full py-4 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-base shadow-sm transition-all text-center"
        >
          Done & Return to Practice Hub
        </button>
      </div>
    </div>
  );
};
