import React, { useState, useEffect } from 'react';
import { ArrowLeft, Clock, CheckCircle2, AlertCircle, Type, HelpCircle } from 'lucide-react';
import { ReadingTest, ReadingAttempt } from '../types';

interface ReadingPracticeScreenProps {
  test: ReadingTest;
  onSubmit: (attempt: ReadingAttempt) => void;
  onBack: () => void;
}

export const ReadingPracticeScreen: React.FC<ReadingPracticeScreenProps> = ({
  test,
  onSubmit,
  onBack
}) => {
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [secondsRemaining, setSecondsRemaining] = useState(test.timeLimitMinutes * 60);
  const [fontSize, setFontSize] = useState<'normal' | 'large'>('normal');
  const [activeTab, setActiveTab] = useState<'passage' | 'questions'>('passage');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const timer = setInterval(() => {
      setSecondsRemaining((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          handleSubmit();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const formatTime = (secs: number) => {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  const handleAnswer = (questionId: number, val: string) => {
    setAnswers((prev) => ({ ...prev, [questionId]: val }));
  };

  const handleSubmit = () => {
    setIsSubmitting(true);
    let correctCount = 0;
    test.questions.forEach((q) => {
      const userAns = (answers[q.id] || '').trim().toLowerCase();
      const actualAns = q.correctAnswer.trim().toLowerCase();
      if (userAns === actualAns) {
        correctCount++;
      }
    });

    const total = test.questions.length;
    // IELTS Band score conversion
    let band = 5.0;
    const ratio = total > 0 ? correctCount / total : 0;
    if (ratio >= 0.9) band = 8.5;
    else if (ratio >= 0.75) band = 7.5;
    else if (ratio >= 0.6) band = 6.5;
    else if (ratio >= 0.4) band = 6.0;
    else if (ratio >= 0.2) band = 5.5;

    const timeSpent = test.timeLimitMinutes * 60 - secondsRemaining;

    const attempt: ReadingAttempt = {
      id: `reading_att_${Date.now()}`,
      testId: test.id,
      testTitle: test.title,
      userId: 'default_user',
      userAnswers: Object.fromEntries(
        Object.entries(answers).map(([k, v]) => [k.toString(), v])
      ),
      score: correctCount,
      totalQuestions: total,
      bandScore: band,
      timeTakenSeconds: timeSpent,
      timestamp: Date.now()
    };

    setTimeout(() => {
      onSubmit(attempt);
    }, 400);
  };

  const answeredCount = Object.keys(answers).length;

  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] flex flex-col">
      {/* Top Exam Navigation Bar */}
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
              IELTS Academic Reading
            </div>
            <h1 className="text-sm sm:text-base font-bold truncate max-w-[200px] sm:max-w-md">
              {test.title}
            </h1>
          </div>
        </div>

        <div className="flex items-center gap-3">
          {/* Timer */}
          <div className="flex items-center gap-1.5 bg-black/25 px-3 py-1.5 rounded-full text-xs sm:text-sm font-mono font-bold">
            <Clock size={15} />
            <span>{formatTime(secondsRemaining)}</span>
          </div>

          {/* Font toggle */}
          <button
            type="button"
            onClick={() => setFontSize(fontSize === 'normal' ? 'large' : 'normal')}
            className="p-1.5 bg-white/10 hover:bg-white/20 rounded-lg text-white"
            title="Toggle Font Size"
          >
            <Type size={16} />
          </button>

          {/* Submit CTA */}
          <button
            type="button"
            onClick={handleSubmit}
            disabled={isSubmitting}
            className="px-4 py-1.5 bg-[#FFDFBD] text-[#3A2500] hover:bg-[#FAD1A5] text-xs sm:text-sm font-bold rounded-full transition-all shadow-xs"
          >
            Submit ({answeredCount}/{test.questions.length})
          </button>
        </div>
      </header>

      {/* Mobile Tab Switcher */}
      <div className="md:hidden flex bg-white border-b border-[#CBD7CF]">
        <button
          type="button"
          onClick={() => setActiveTab('passage')}
          className={`flex-1 py-2.5 text-center text-xs font-bold border-b-2 transition-all ${
            activeTab === 'passage'
              ? 'border-[#00543C] text-[#00543C]'
              : 'border-transparent text-[#45524B]'
          }`}
        >
          Passage Text
        </button>
        <button
          type="button"
          onClick={() => setActiveTab('questions')}
          className={`flex-1 py-2.5 text-center text-xs font-bold border-b-2 transition-all ${
            activeTab === 'questions'
              ? 'border-[#00543C] text-[#00543C]'
              : 'border-transparent text-[#45524B]'
          }`}
        >
          Questions ({answeredCount}/{test.questions.length})
        </button>
      </div>

      {/* Main Content: Split View on desktop, tabbed on mobile */}
      <main className="flex-1 max-w-7xl mx-auto w-full p-4 sm:p-6 grid grid-cols-1 md:grid-cols-2 gap-6 overflow-hidden">
        {/* Left: Reading Passage */}
        <section
          className={`bg-white rounded-3xl border border-[#CBD7CF] p-6 shadow-sm overflow-y-auto max-h-[calc(100vh-140px)] ${
            activeTab === 'passage' ? 'block' : 'hidden md:block'
          }`}
        >
          <div className="flex items-center justify-between pb-4 border-b border-[#CBD7CF] mb-4">
            <span className="text-xs font-bold text-[#00543C] uppercase tracking-wide">
              Reading Passage
            </span>
            <span className="text-xs text-[#45524B]">
              Band {test.targetBand.toFixed(1)} Target Level
            </span>
          </div>

          <h2 className="text-xl font-extrabold text-[#161D1A] mb-4 leading-tight">
            {test.title}
          </h2>

          <div
            className={`text-[#161D1A] whitespace-pre-line leading-relaxed ${
              fontSize === 'large' ? 'text-base sm:text-lg' : 'text-sm sm:text-base'
            }`}
          >
            {test.passageText}
          </div>
        </section>

        {/* Right: Question Set */}
        <section
          className={`bg-white rounded-3xl border border-[#CBD7CF] p-6 shadow-sm overflow-y-auto max-h-[calc(100vh-140px)] ${
            activeTab === 'questions' ? 'block' : 'hidden md:block'
          }`}
        >
          <div className="flex items-center justify-between pb-4 border-b border-[#CBD7CF] mb-4">
            <span className="text-xs font-bold text-[#00543C] uppercase tracking-wide">
              Questions 1–{test.questions.length}
            </span>
            <span className="text-xs font-semibold text-[#45524B]">
              {answeredCount} of {test.questions.length} answered
            </span>
          </div>

          <div className="space-y-6">
            {test.questions.map((q, idx) => (
              <div
                key={q.id}
                className="p-4 rounded-2xl bg-[#F0F5F1] border border-[#CBD7CF]"
              >
                <div className="flex items-start gap-2 mb-3">
                  <span className="w-6 h-6 rounded-full bg-[#00543C] text-white text-xs font-bold flex items-center justify-center shrink-0 mt-0.5">
                    {idx + 1}
                  </span>
                  <div className="font-bold text-sm text-[#161D1A]">
                    {q.questionText}
                  </div>
                </div>

                {/* Multiple Choice Options */}
                {q.type === 'MULTIPLE_CHOICE' && q.options && (
                  <div className="space-y-2 mt-2">
                    {q.options.map((opt) => {
                      const letter = opt.trim().charAt(0);
                      const isSelected = answers[q.id] === letter;
                      return (
                        <button
                          key={opt}
                          type="button"
                          onClick={() => handleAnswer(q.id, letter)}
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

                {/* True / False / Not Given Options */}
                {q.type === 'TRUE_FALSE_NOT_GIVEN' && (
                  <div className="grid grid-cols-3 gap-2 mt-2">
                    {['TRUE', 'FALSE', 'NOT GIVEN'].map((choice) => {
                      const isSelected = answers[q.id] === choice;
                      return (
                        <button
                          key={choice}
                          type="button"
                          onClick={() => handleAnswer(q.id, choice)}
                          className={`py-2 px-2 text-center text-xs font-bold rounded-xl border transition-all ${
                            isSelected
                              ? 'border-[#00543C] bg-[#BFF1D8] text-[#00543C]'
                              : 'border-[#CBD7CF] bg-white text-[#161D1A] hover:bg-gray-50'
                          }`}
                        >
                          {choice}
                        </button>
                      );
                    })}
                  </div>
                )}

                {/* Fill In Blank */}
                {q.type === 'FILL_IN_BLANK' && (
                  <div className="mt-2">
                    <input
                      type="text"
                      value={answers[q.id] || ''}
                      onChange={(e) => handleAnswer(q.id, e.target.value)}
                      placeholder="Type your one-word answer..."
                      className="w-full p-3 rounded-xl bg-white border border-[#CBD7CF] text-sm font-medium focus:border-[#00543C] outline-none"
                    />
                  </div>
                )}
              </div>
            ))}
          </div>

          <div className="mt-8 pt-4 border-t border-[#CBD7CF]">
            <button
              type="button"
              onClick={handleSubmit}
              className="w-full py-3.5 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-sm sm:text-base flex items-center justify-center gap-2 shadow-sm transition-all"
            >
              Submit Test & View Results
            </button>
          </div>
        </section>
      </main>
    </div>
  );
};

interface ReadingResultsScreenProps {
  test: ReadingTest;
  attempt: ReadingAttempt;
  onDone: () => void;
}

export const ReadingResultsScreen: React.FC<ReadingResultsScreenProps> = ({
  test,
  attempt,
  onDone
}) => {
  const formatTime = (secs: number) => {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m}m ${s}s`;
  };

  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] pb-12">
      <div className="max-w-2xl mx-auto px-4 sm:px-6 pt-6">
        {/* Header */}
        <div className="text-center mb-6">
          <div className="inline-block bg-[#BFF1D8] text-[#00543C] text-xs font-bold px-3 py-1 rounded-full mb-3 tracking-wider">
            READING TEST COMPLETED
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-[#161D1A]">
            Performance Review
          </h1>
          <p className="text-sm text-[#45524B] mt-1">{test.title}</p>
        </div>

        {/* Hero Score Card */}
        <div className="bg-gradient-to-br from-[#00543C] to-[#00785A] text-white rounded-3xl p-6 sm:p-8 shadow-sm mb-6 text-center">
          <div className="text-xs uppercase tracking-wider font-semibold text-white/80">
            Calculated IELTS Band Score
          </div>
          <div className="text-5xl sm:text-6xl font-black mt-2 mb-2 text-[#FFB84D]">
            Band {attempt.bandScore.toFixed(1)}
          </div>
          <p className="text-sm text-white/90">
            {attempt.score} out of {attempt.totalQuestions} questions correct • Time taken: {formatTime(attempt.timeTakenSeconds)}
          </p>
        </div>

        {/* Detailed Question Review */}
        <h2 className="text-lg font-bold text-[#161D1A] mb-3">
          Detailed Question Explanations
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
                    <span><strong>Explanation:</strong> {q.explanation}</span>
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
