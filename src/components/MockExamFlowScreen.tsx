import React, { useState, useEffect, useRef } from 'react';
import {
  Clock,
  AlertTriangle,
  Play,
  Pause,
  Headphones,
  BookOpen,
  PenTool,
  Mic,
  ArrowRight,
  CheckCircle2
} from 'lucide-react';
import {
  MockExam,
  MockExamAttempt,
  ListeningTest,
  ReadingTest,
  WritingTask,
  SpeakingTask,
  IeltsScoreCalculator
} from '../types';

interface MockExamFlowScreenProps {
  exam: MockExam;
  listeningTest: ListeningTest;
  readingTest: ReadingTest;
  writingTask: WritingTask;
  speakingTask: SpeakingTask;
  onExamCompleted: (attempt: MockExamAttempt) => void;
  onExit: () => void;
}

export const MockExamFlowScreen: React.FC<MockExamFlowScreenProps> = ({
  exam,
  listeningTest,
  readingTest,
  writingTask,
  speakingTask,
  onExamCompleted,
  onExit
}) => {
  const [section, setSection] = useState<'LISTENING' | 'READING' | 'WRITING' | 'SPEAKING'>('LISTENING');
  const [elapsedSeconds, setElapsedSeconds] = useState(0);
  const totalExamSeconds = exam.totalDurationMinutes * 60;
  const gracePeriodSeconds = 180; // 3-minute grace period buffer

  // Answers state
  const [listeningAnswers, setListeningAnswers] = useState<Record<number, string>>({});
  const [readingAnswers, setReadingAnswers] = useState<Record<number, string>>({});
  const [writingEssay, setWritingEssay] = useState('');
  const [speakingRecorded, setSpeakingRecorded] = useState(false);

  // Audio player for listening section
  const [isAudioPlaying, setIsAudioPlaying] = useState(false);
  const audioRef = useRef<HTMLAudioElement | null>(null);

  // Continuous timer
  useEffect(() => {
    const timer = setInterval(() => {
      setElapsedSeconds((prev) => prev + 1);
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const remainingSeconds = Math.max(0, totalExamSeconds - elapsedSeconds);
  const isOfficialTimeExpired = elapsedSeconds >= totalExamSeconds;
  const isGracePeriodExpired = elapsedSeconds >= totalExamSeconds + gracePeriodSeconds;

  // Auto-submit when grace expires
  useEffect(() => {
    if (isGracePeriodExpired) {
      handleFinalSubmission();
    }
  }, [isGracePeriodExpired]);

  const formatTime = (secs: number) => {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  const handleFinalSubmission = () => {
    // 1. Calculate Listening Band
    let lCorrect = 0;
    listeningTest.questions.forEach((q) => {
      if ((listeningAnswers[q.id] || '').trim().toLowerCase() === q.correctAnswer.trim().toLowerCase()) {
        lCorrect++;
      }
    });
    const lRatio = listeningTest.questions.length > 0 ? lCorrect / listeningTest.questions.length : 0;
    const lBand = lRatio >= 0.8 ? 8.0 : lRatio >= 0.6 ? 7.0 : lRatio >= 0.4 ? 6.0 : 5.5;

    // 2. Calculate Reading Band
    let rCorrect = 0;
    readingTest.questions.forEach((q) => {
      if ((readingAnswers[q.id] || '').trim().toLowerCase() === q.correctAnswer.trim().toLowerCase()) {
        rCorrect++;
      }
    });
    const rRatio = readingTest.questions.length > 0 ? rCorrect / readingTest.questions.length : 0;
    const rBand = rRatio >= 0.8 ? 8.0 : rRatio >= 0.6 ? 7.0 : rRatio >= 0.4 ? 6.0 : 5.5;

    // 3. Calculate Writing Band
    const wordCount = writingEssay.trim().split(/\s+/).filter(Boolean).length;
    const wBand = wordCount >= 250 ? 7.5 : wordCount >= 180 ? 6.5 : wordCount >= 100 ? 6.0 : 5.5;

    // 4. Calculate Speaking Band
    const sBand = speakingRecorded ? 7.0 : 6.0;

    // Overall official rounding
    const overall = IeltsScoreCalculator.calculateOverallBand(lBand, rBand, wBand, sBand);

    const now = Date.now();
    const attempt: MockExamAttempt = {
      attemptId: `mock_att_${now}`,
      mockExamId: exam.id,
      userId: 'default_user',
      examTitle: exam.title,
      examType: exam.examType,
      startTimeMillis: now - (elapsedSeconds * 1000),
      endTimeMillis: now,
      status: 'COMPLETED',
      currentSection: 'COMPLETED',
      listeningBand: lBand,
      readingBand: rBand,
      writingBand: wBand,
      speakingBand: sBand,
      overallBand: overall,
      listeningAnswersJson: JSON.stringify(listeningAnswers),
      readingAnswersJson: JSON.stringify(readingAnswers),
      writingEssayText: writingEssay,
      speakingAudioUri: speakingRecorded ? 'recorded_exam_speech' : '',
      officialTimeUpTimeMillis: now + (remainingSeconds * 1000),
      actualSubmissionTimeMillis: now
    };

    onExamCompleted(attempt);
  };

  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] flex flex-col pb-12">
      {/* Listening Audio player */}
      <audio
        ref={audioRef}
        src={listeningTest.audioUrl}
        preload="auto"
        onPlay={() => setIsAudioPlaying(true)}
        onPause={() => setIsAudioPlaying(false)}
      />

      {/* Persistent Exam Bar with Timer and Sections */}
      <header className="sticky top-0 z-30 bg-[#00543C] text-white px-4 py-3 shadow-md flex items-center justify-between">
        <div>
          <div className="text-2xs font-extrabold uppercase text-white/80 tracking-wider">
            MOCK EXAM IN PROGRESS • {exam.title}
          </div>
          <div className="text-xs sm:text-sm font-bold flex items-center gap-2 mt-0.5">
            <span>Section: {section}</span>
          </div>
        </div>

        <div className="flex items-center gap-3">
          {/* Live Continuous Timer */}
          <div
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs sm:text-sm font-mono font-bold ${
              isOfficialTimeExpired ? 'bg-red-600 text-white animate-pulse' : 'bg-black/30 text-white'
            }`}
          >
            {isOfficialTimeExpired ? <AlertTriangle size={15} /> : <Clock size={15} />}
            <span>
              {isOfficialTimeExpired
                ? `Grace Buffer: ${formatTime(totalExamSeconds + gracePeriodSeconds - elapsedSeconds)}`
                : formatTime(remainingSeconds)}
            </span>
          </div>

          <button
            type="button"
            onClick={onExit}
            className="text-xs text-white/80 hover:text-white px-2 py-1 rounded border border-white/20"
          >
            Exit Exam
          </button>
        </div>
      </header>

      {/* Section Progress Stepper */}
      <div className="bg-white border-b border-[#CBD7CF] px-4 py-2">
        <div className="max-w-2xl mx-auto flex items-center justify-between text-xs font-bold">
          <button
            type="button"
            onClick={() => setSection('LISTENING')}
            className={`flex items-center gap-1.5 py-1 px-3 rounded-full ${
              section === 'LISTENING' ? 'bg-[#00543C] text-white' : 'text-[#45524B]'
            }`}
          >
            <Headphones size={13} /> 1. Listening
          </button>
          <button
            type="button"
            onClick={() => setSection('READING')}
            className={`flex items-center gap-1.5 py-1 px-3 rounded-full ${
              section === 'READING' ? 'bg-[#00543C] text-white' : 'text-[#45524B]'
            }`}
          >
            <BookOpen size={13} /> 2. Reading
          </button>
          <button
            type="button"
            onClick={() => setSection('WRITING')}
            className={`flex items-center gap-1.5 py-1 px-3 rounded-full ${
              section === 'WRITING' ? 'bg-[#00543C] text-white' : 'text-[#45524B]'
            }`}
          >
            <PenTool size={13} /> 3. Writing
          </button>
          <button
            type="button"
            onClick={() => setSection('SPEAKING')}
            className={`flex items-center gap-1.5 py-1 px-3 rounded-full ${
              section === 'SPEAKING' ? 'bg-[#00543C] text-white' : 'text-[#45524B]'
            }`}
          >
            <Mic size={13} /> 4. Speaking
          </button>
        </div>
      </div>

      {/* Main Section Content */}
      <main className="flex-1 max-w-3xl mx-auto w-full p-4 sm:p-6 space-y-6">
        {/* SECTION 1: LISTENING */}
        {section === 'LISTENING' && (
          <div className="space-y-6">
            <div className="bg-white rounded-3xl border border-[#CBD7CF] p-5 sm:p-6 shadow-2xs">
              <h2 className="text-lg font-bold text-[#161D1A] mb-2 flex items-center gap-2">
                <Headphones size={20} className="text-[#00543C]" />
                Section 1: Listening Comprehension
              </h2>
              <p className="text-xs text-[#45524B] mb-4">
                Play the audio stream and answer the questions simultaneously.
              </p>

              <button
                type="button"
                onClick={() => {
                  if (audioRef.current) {
                    if (isAudioPlaying) {
                      audioRef.current.pause();
                      setIsAudioPlaying(false);
                    } else {
                      audioRef.current.play();
                      setIsAudioPlaying(true);
                    }
                  }
                }}
                className="w-full py-3 bg-[#BFF1D8] text-[#00543C] font-bold rounded-2xl flex items-center justify-center gap-2 mb-6"
              >
                {isAudioPlaying ? <Pause size={18} /> : <Play size={18} fill="currentColor" />}
                {isAudioPlaying ? 'Pause Audio Stream' : 'Play Listening Audio Track'}
              </button>

              <div className="space-y-4">
                {listeningTest.questions.map((q, idx) => (
                  <div key={q.id} className="p-4 bg-[#F0F5F1] rounded-2xl border border-[#CBD7CF]">
                    <div className="font-bold text-xs sm:text-sm text-[#161D1A] mb-2">
                      {idx + 1}. {q.questionText}
                    </div>
                    {q.options && (
                      <div className="space-y-1.5">
                        {q.options.map((opt) => {
                          const letter = opt.trim().charAt(0);
                          const isSel = listeningAnswers[q.id] === letter;
                          return (
                            <button
                              key={opt}
                              type="button"
                              onClick={() => setListeningAnswers({ ...listeningAnswers, [q.id]: letter })}
                              className={`w-full text-left p-2.5 rounded-xl border text-xs font-medium transition-all ${
                                isSel ? 'bg-[#00543C] text-white font-bold' : 'bg-white text-[#161D1A]'
                              }`}
                            >
                              {opt}
                            </button>
                          );
                        })}
                      </div>
                    )}
                    {q.type === 'FORM_COMPLETION' && (
                      <input
                        type="text"
                        value={listeningAnswers[q.id] || ''}
                        onChange={(e) => setListeningAnswers({ ...listeningAnswers, [q.id]: e.target.value })}
                        placeholder="Write your answer..."
                        className="w-full p-2.5 rounded-xl bg-white border border-[#CBD7CF] text-xs font-semibold outline-none"
                      />
                    )}
                  </div>
                ))}
              </div>

              <button
                type="button"
                onClick={() => setSection('READING')}
                className="mt-6 w-full py-3.5 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-sm flex items-center justify-center gap-2"
              >
                Next Section: Academic Reading <ArrowRight size={16} />
              </button>
            </div>
          </div>
        )}

        {/* SECTION 2: READING */}
        {section === 'READING' && (
          <div className="space-y-6">
            <div className="bg-white rounded-3xl border border-[#CBD7CF] p-5 sm:p-6 shadow-2xs">
              <h2 className="text-lg font-bold text-[#161D1A] mb-2 flex items-center gap-2">
                <BookOpen size={20} className="text-[#00543C]" />
                Section 2: Academic Reading Passage
              </h2>

              <div className="p-4 bg-[#F8FAF9] rounded-2xl border border-[#CBD7CF] text-xs leading-relaxed max-h-72 overflow-y-auto mb-6 whitespace-pre-line">
                <strong className="block text-sm text-[#161D1A] mb-2">{readingTest.title}</strong>
                {readingTest.passageText}
              </div>

              <div className="space-y-4">
                {readingTest.questions.map((q, idx) => (
                  <div key={q.id} className="p-4 bg-[#F0F5F1] rounded-2xl border border-[#CBD7CF]">
                    <div className="font-bold text-xs sm:text-sm text-[#161D1A] mb-2">
                      {idx + 1}. {q.questionText}
                    </div>
                    {q.options && (
                      <div className="space-y-1.5">
                        {q.options.map((opt) => {
                          const letter = opt.trim().charAt(0);
                          const isSel = readingAnswers[q.id] === letter;
                          return (
                            <button
                              key={opt}
                              type="button"
                              onClick={() => setReadingAnswers({ ...readingAnswers, [q.id]: letter })}
                              className={`w-full text-left p-2.5 rounded-xl border text-xs font-medium transition-all ${
                                isSel ? 'bg-[#00543C] text-white font-bold' : 'bg-white text-[#161D1A]'
                              }`}
                            >
                              {opt}
                            </button>
                          );
                        })}
                      </div>
                    )}
                    {q.type === 'TRUE_FALSE_NOT_GIVEN' && (
                      <div className="grid grid-cols-3 gap-2">
                        {['TRUE', 'FALSE', 'NOT GIVEN'].map((c) => (
                          <button
                            key={c}
                            type="button"
                            onClick={() => setReadingAnswers({ ...readingAnswers, [q.id]: c })}
                            className={`py-2 rounded-xl text-xs font-bold border transition-all ${
                              readingAnswers[q.id] === c ? 'bg-[#00543C] text-white' : 'bg-white text-[#161D1A]'
                            }`}
                          >
                            {c}
                          </button>
                        ))}
                      </div>
                    )}
                    {q.type === 'FILL_IN_BLANK' && (
                      <input
                        type="text"
                        value={readingAnswers[q.id] || ''}
                        onChange={(e) => setReadingAnswers({ ...readingAnswers, [q.id]: e.target.value })}
                        placeholder="One-word answer..."
                        className="w-full p-2.5 rounded-xl bg-white border border-[#CBD7CF] text-xs font-semibold outline-none"
                      />
                    )}
                  </div>
                ))}
              </div>

              <div className="flex gap-3 mt-6">
                <button
                  type="button"
                  onClick={() => setSection('LISTENING')}
                  className="flex-1 py-3.5 bg-white border border-[#CBD7CF] text-[#45524B] rounded-full font-bold text-sm"
                >
                  Previous Section
                </button>
                <button
                  type="button"
                  onClick={() => setSection('WRITING')}
                  className="flex-1 py-3.5 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-sm flex items-center justify-center gap-2"
                >
                  Next: Writing <ArrowRight size={16} />
                </button>
              </div>
            </div>
          </div>
        )}

        {/* SECTION 3: WRITING */}
        {section === 'WRITING' && (
          <div className="space-y-6">
            <div className="bg-white rounded-3xl border border-[#CBD7CF] p-5 sm:p-6 shadow-2xs">
              <h2 className="text-lg font-bold text-[#161D1A] mb-2 flex items-center gap-2">
                <PenTool size={20} className="text-[#00543C]" />
                Section 3: Essay Writing Task
              </h2>

              <div className="p-4 bg-[#F0F5F1] rounded-2xl border border-[#CBD7CF] text-xs mb-4">
                <strong className="block text-sm text-[#161D1A] mb-1">{writingTask.title}</strong>
                {writingTask.prompt}
              </div>

              <textarea
                value={writingEssay}
                onChange={(e) => setWritingEssay(e.target.value)}
                placeholder="Write your complete essay response here (target: 250+ words)..."
                rows={12}
                className="w-full p-4 bg-[#F8FAF9] rounded-2xl border border-[#CBD7CF] text-sm leading-relaxed outline-none focus:border-[#00543C]"
              />

              <div className="text-xs text-[#45524B] mt-2 mb-6">
                Word Count: <strong>{writingEssay.trim().split(/\s+/).filter(Boolean).length} words</strong>
              </div>

              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => setSection('READING')}
                  className="flex-1 py-3.5 bg-white border border-[#CBD7CF] text-[#45524B] rounded-full font-bold text-sm"
                >
                  Previous Section
                </button>
                <button
                  type="button"
                  onClick={() => setSection('SPEAKING')}
                  className="flex-1 py-3.5 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-sm flex items-center justify-center gap-2"
                >
                  Next: Speaking <ArrowRight size={16} />
                </button>
              </div>
            </div>
          </div>
        )}

        {/* SECTION 4: SPEAKING */}
        {section === 'SPEAKING' && (
          <div className="space-y-6">
            <div className="bg-white rounded-3xl border border-[#CBD7CF] p-5 sm:p-6 shadow-2xs">
              <h2 className="text-lg font-bold text-[#161D1A] mb-2 flex items-center gap-2">
                <Mic size={20} className="text-[#00543C]" />
                Section 4: Speaking Interview
              </h2>

              <div className="p-4 bg-[#F0F5F1] rounded-2xl border border-[#CBD7CF] text-xs mb-6">
                <strong className="block text-sm text-[#161D1A] mb-1">{speakingTask.title}</strong>
                <p>Respond to the cue card or prompt questions aloud.</p>
                {speakingTask.part2CueCard && (
                  <p className="font-semibold text-sm text-[#00543C] mt-2">
                    "{speakingTask.part2CueCard}"
                  </p>
                )}
              </div>

              <div className="text-center p-6 bg-[#F8FAF9] rounded-2xl border border-[#CBD7CF] mb-6">
                <button
                  type="button"
                  onClick={() => setSpeakingRecorded(!speakingRecorded)}
                  className={`px-6 py-3 rounded-full font-bold text-sm flex items-center justify-center gap-2 mx-auto transition-all ${
                    speakingRecorded
                      ? 'bg-[#BFF1D8] text-[#00543C]'
                      : 'bg-[#00543C] text-white hover:bg-[#00785A]'
                  }`}
                >
                  <Mic size={18} />
                  {speakingRecorded ? 'Spoken Response Recorded ✓' : 'Record Speaking Response'}
                </button>
                <p className="text-xs text-[#45524B] mt-2">
                  {speakingRecorded ? 'Audio response ready for official scoring' : 'Click to record your response'}
                </p>
              </div>

              {/* Final Exam Completion CTA */}
              <button
                type="button"
                onClick={handleFinalSubmission}
                className="w-full py-4 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-extrabold text-base shadow-sm transition-all text-center flex items-center justify-center gap-2"
              >
                Submit Entire Mock Exam & Calculate Band Score
                <CheckCircle2 size={18} />
              </button>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};
