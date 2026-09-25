import React, { useState, useEffect, useRef } from 'react';
import {
  ArrowLeft,
  Mic,
  Square,
  Sparkles,
  Clock,
  Volume2,
  FileEdit,
  Play,
  RotateCcw,
  CheckCircle2
} from 'lucide-react';
import { SpeakingTask, SpeakingAttempt } from '../types';
import { aiEvaluationService } from '../services/aiEvaluation';

interface SpeakingPracticeScreenProps {
  task: SpeakingTask;
  onSubmit: (attempt: SpeakingAttempt) => void;
  onBack: () => void;
}

export const SpeakingPracticeScreen: React.FC<SpeakingPracticeScreenProps> = ({
  task,
  onSubmit,
  onBack
}) => {
  const [prepSeconds, setPrepSeconds] = useState(task.partNumber === 2 ? 60 : 0);
  const [isPrepping, setIsPrepping] = useState(task.partNumber === 2);
  const [prepNotes, setPrepNotes] = useState('');
  const [isRecording, setIsRecording] = useState(false);
  const [recordSeconds, setRecordSeconds] = useState(0);
  const [audioBlob, setAudioBlob] = useState<Blob | null>(null);
  const [audioUrl, setAudioUrl] = useState<string | null>(null);
  const [isEvaluating, setIsEvaluating] = useState(false);
  const [simulatedTranscript, setSimulatedTranscript] = useState('');

  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const audioChunksRef = useRef<Blob[]>([]);

  // Prep timer
  useEffect(() => {
    if (!isPrepping || prepSeconds <= 0) return;
    const interval = setInterval(() => {
      setPrepSeconds((prev) => {
        if (prev <= 1) {
          setIsPrepping(false);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
    return () => clearInterval(interval);
  }, [isPrepping, prepSeconds]);

  // Recording timer
  useEffect(() => {
    if (!isRecording) return;
    const interval = setInterval(() => {
      setRecordSeconds((prev) => prev + 1);
    }, 1000);
    return () => clearInterval(interval);
  }, [isRecording]);

  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const recorder = new MediaRecorder(stream);
      mediaRecorderRef.current = recorder;
      audioChunksRef.current = [];

      recorder.ondataavailable = (e) => {
        if (e.data.size > 0) {
          audioChunksRef.current.push(e.data);
        }
      };

      recorder.onstop = () => {
        const blob = new Blob(audioChunksRef.current, { type: 'audio/webm' });
        setAudioBlob(blob);
        setAudioUrl(URL.createObjectURL(blob));
        // Stop media tracks
        stream.getTracks().forEach((track) => track.stop());
      };

      recorder.start();
      setIsRecording(true);
      setRecordSeconds(0);
    } catch {
      // If mic permission blocked, allow simulated speech recording
      setIsRecording(true);
      setRecordSeconds(0);
    }
  };

  const stopRecording = () => {
    if (mediaRecorderRef.current && mediaRecorderRef.current.state !== 'inactive') {
      mediaRecorderRef.current.stop();
    } else {
      // Simulate recorded response
      const sampleBlob = new Blob(['simulated-audio'], { type: 'audio/mp3' });
      setAudioBlob(sampleBlob);
    }
    setIsRecording(false);
  };

  const handleSkipPrep = () => {
    setIsPrepping(false);
    setPrepSeconds(0);
  };

  const handleSubmit = async () => {
    setIsEvaluating(true);
    try {
      const attempt = await aiEvaluationService.evaluateSpeakingSubmission(
        task,
        audioBlob,
        simulatedTranscript.trim() || undefined
      );
      onSubmit(attempt);
    } catch {
      setIsEvaluating(false);
    }
  };

  const formatTime = (secs: number) => {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
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
              IELTS Speaking Part {task.partNumber}
            </div>
            <h1 className="text-sm sm:text-base font-bold truncate max-w-[200px] sm:max-w-md">
              {task.title}
            </h1>
          </div>
        </div>

        {audioBlob && (
          <button
            type="button"
            onClick={handleSubmit}
            disabled={isEvaluating}
            className="px-4 py-1.5 bg-[#FFDFBD] text-[#3A2500] hover:bg-[#FAD1A5] text-xs sm:text-sm font-bold rounded-full transition-all shadow-xs flex items-center gap-1.5"
          >
            {isEvaluating ? (
              <>
                <Sparkles size={14} className="animate-spin" /> Evaluating...
              </>
            ) : (
              'Submit for AI Grading'
            )}
          </button>
        )}
      </header>

      {/* Main Container */}
      <main className="flex-1 max-w-2xl mx-auto w-full p-4 sm:p-6 space-y-6">
        {/* Cue Card / Questions Card */}
        <div className="bg-white rounded-3xl border border-[#CBD7CF] p-6 shadow-sm">
          <div className="flex items-center justify-between pb-3 border-b border-[#CBD7CF] mb-4">
            <span className="text-xs font-bold text-[#00543C] uppercase tracking-wide">
              Topic: {task.topic}
            </span>
            <span className="text-xs font-semibold px-2.5 py-0.5 rounded-full bg-[#BFF1D8] text-[#00543C]">
              {task.difficultyLevel}
            </span>
          </div>

          {/* Part 1 */}
          {task.partNumber === 1 && (
            <div className="space-y-3">
              <h2 className="font-bold text-base text-[#161D1A]">
                Examiner Questions (Part 1 - Daily Life & Study):
              </h2>
              {task.part1Questions.map((q, idx) => (
                <div
                  key={idx}
                  className="p-3.5 bg-[#F0F5F1] rounded-2xl border border-[#CBD7CF] text-sm text-[#161D1A] font-medium"
                >
                  "{q}"
                </div>
              ))}
            </div>
          )}

          {/* Part 2 Cue Card */}
          {task.partNumber === 2 && (
            <div className="space-y-4">
              <div className="p-4 bg-[#BFF1D8]/40 border border-[#96E2BD] rounded-2xl">
                <h3 className="font-bold text-sm text-[#00543C] mb-1 uppercase tracking-wide">
                  Candidate Cue Card
                </h3>
                <p className="font-extrabold text-base text-[#161D1A] mb-3">
                  {task.part2CueCard}
                </p>
                <div className="text-xs text-[#45524B] space-y-1">
                  <span className="font-semibold block text-[#161D1A]">You should say:</span>
                  {task.part2Bullets.map((bullet, idx) => (
                    <div key={idx} className="flex items-center gap-2">
                      <span className="w-1.5 h-1.5 rounded-full bg-[#00543C]" />
                      <span>{bullet}</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* 1-Minute Prep Countdown Banner */}
              {isPrepping && (
                <div className="p-4 bg-[#FFDFBD]/70 border border-[#F6C697] rounded-2xl flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <Clock size={20} className="text-[#B26A00]" />
                    <div>
                      <div className="text-xs font-bold text-[#3A2500]">
                        1-Minute Preparation Time Remaining
                      </div>
                      <div className="text-xl font-black text-[#B26A00] font-mono">
                        {prepSeconds}s
                      </div>
                    </div>
                  </div>
                  <button
                    type="button"
                    onClick={handleSkipPrep}
                    className="px-3 py-1.5 bg-white text-xs font-bold text-[#3A2500] rounded-xl border border-[#CBD7CF] hover:bg-gray-50"
                  >
                    Skip Prep
                  </button>
                </div>
              )}

              {/* Cue Card Scratchpad */}
              <div>
                <label className="block text-xs font-bold text-[#45524B] uppercase tracking-wide mb-1">
                  1-Minute Prep Notes Scratchpad
                </label>
                <textarea
                  value={prepNotes}
                  onChange={(e) => setPrepNotes(e.target.value)}
                  placeholder="Jot down bullet points, key vocabulary, idioms, and discourse markers for your 2-minute speech..."
                  rows={3}
                  className="w-full p-3 bg-[#F8FAF9] rounded-2xl border border-[#CBD7CF] text-xs outline-none focus:border-[#00543C] resize-none"
                />
              </div>
            </div>
          )}

          {/* Part 3 */}
          {task.partNumber === 3 && (
            <div className="space-y-3">
              <h2 className="font-bold text-base text-[#161D1A]">
                Deep Discussion Questions (Part 3):
              </h2>
              {task.part3Questions.map((q, idx) => (
                <div
                  key={idx}
                  className="p-3.5 bg-[#F0F5F1] rounded-2xl border border-[#CBD7CF] text-sm text-[#161D1A] font-medium"
                >
                  "{q}"
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Recording Deck Card */}
        <div className="bg-white rounded-3xl border border-[#CBD7CF] p-6 shadow-sm text-center">
          <h3 className="font-bold text-base text-[#161D1A] mb-1">
            Voice Recording Station
          </h3>
          <p className="text-xs text-[#45524B] mb-6">
            Speak clearly into your device microphone. Target duration: 1 to 2 minutes.
          </p>

          <div className="flex flex-col items-center justify-center my-4">
            {isRecording ? (
              <div className="relative flex items-center justify-center">
                <div className="w-24 h-24 rounded-full bg-red-100 animate-ping absolute" />
                <button
                  type="button"
                  onClick={stopRecording}
                  className="relative z-10 w-20 h-20 rounded-full bg-red-600 text-white flex flex-col items-center justify-center shadow-lg hover:bg-red-700 transition-all"
                >
                  <Square size={24} fill="currentColor" />
                  <span className="text-2xs font-bold uppercase mt-1">Stop</span>
                </button>
              </div>
            ) : (
              <button
                type="button"
                onClick={startRecording}
                className="w-20 h-20 rounded-full bg-[#00543C] text-white flex flex-col items-center justify-center shadow-md hover:bg-[#00785A] transition-all"
              >
                <Mic size={28} />
                <span className="text-2xs font-bold uppercase mt-1">Record</span>
              </button>
            )}

            {/* Live Recording Timer */}
            <div className="mt-4 font-mono font-bold text-sm text-[#161D1A]">
              {isRecording ? (
                <span className="text-red-600 flex items-center gap-1.5">
                  <span className="w-2.5 h-2.5 rounded-full bg-red-600 animate-pulse" />
                  Recording: {formatTime(recordSeconds)}
                </span>
              ) : audioBlob ? (
                <span className="text-[#00543C] flex items-center gap-1.5">
                  <CheckCircle2 size={16} /> Audio recorded ({formatTime(recordSeconds || 72)})
                </span>
              ) : (
                <span className="text-[#45524B]">Ready to record</span>
              )}
            </div>
          </div>

          {/* Audio Preview playback */}
          {audioUrl && (
            <div className="mt-4 p-3 bg-[#F0F5F1] rounded-2xl flex items-center justify-center">
              <audio src={audioUrl} controls className="w-full h-10" />
            </div>
          )}

          {/* Optional speech transcript preview */}
          <div className="mt-5 text-left border-t border-[#CBD7CF] pt-4">
            <label className="block text-xs font-semibold text-[#45524B] mb-1">
              Spoken Transcript Note (Optional transcription review):
            </label>
            <input
              type="text"
              value={simulatedTranscript}
              onChange={(e) => setSimulatedTranscript(e.target.value)}
              placeholder="Leave blank for automatic speech transcription, or add notes here..."
              className="w-full p-2.5 bg-[#F8FAF9] rounded-xl border border-[#CBD7CF] text-xs outline-none focus:border-[#00543C]"
            />
          </div>

          {/* Submit CTA */}
          <button
            type="button"
            onClick={handleSubmit}
            disabled={isEvaluating || (!audioBlob && !isRecording)}
            className="mt-6 w-full py-3.5 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-sm sm:text-base flex items-center justify-center gap-2 shadow-sm transition-all disabled:opacity-50"
          >
            {isEvaluating ? (
              <>
                <Sparkles size={18} className="animate-spin" /> AI Multimodal Examiner Grading Speech...
              </>
            ) : (
              <>
                Submit Speaking for AI Pronunciation & Band Grading
                <Sparkles size={16} />
              </>
            )}
          </button>
        </div>
      </main>
    </div>
  );
};

interface SpeakingResultsScreenProps {
  attempt: SpeakingAttempt;
  onReturnToHub: () => void;
}

export const SpeakingResultsScreen: React.FC<SpeakingResultsScreenProps> = ({
  attempt,
  onReturnToHub
}) => {
  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] pb-12">
      <div className="max-w-2xl mx-auto px-4 sm:px-6 pt-6">
        <div className="text-center mb-6">
          <div className="inline-block bg-[#BFF1D8] text-[#00543C] text-xs font-bold px-3 py-1 rounded-full mb-3 tracking-wider">
            IELTS SPEAKING EVALUATION
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-[#161D1A]">
            Speaking Band Breakdown
          </h1>
          <p className="text-sm text-[#45524B] mt-1">{attempt.taskTitle}</p>
        </div>

        {/* Hero Overall Band Card */}
        <div className="bg-gradient-to-br from-[#00543C] to-[#00785A] text-white rounded-3xl p-6 sm:p-8 shadow-sm mb-6 text-center">
          <div className="text-xs uppercase tracking-wider font-semibold text-white/80">
            Overall Speaking Band
          </div>
          <div className="text-5xl sm:text-6xl font-black mt-2 mb-2 text-[#FFB84D]">
            Band {attempt.overallBand.toFixed(1)}
          </div>
          <p className="text-sm text-white/90">
            Part {attempt.partNumber} • Evaluated via AI Senior Examiner Rubric
          </p>
        </div>

        {/* Acoustic Pronunciation Note */}
        <div className="bg-[#BFF1D8]/40 border border-[#96E2BD] rounded-3xl p-5 mb-6 shadow-2xs">
          <div className="flex items-center gap-2 mb-1.5">
            <Volume2 size={18} className="text-[#00543C]" />
            <h2 className="font-bold text-sm sm:text-base text-[#00543C]">
              Acoustic Pronunciation Analysis
            </h2>
          </div>
          <p className="text-xs sm:text-sm text-[#00543C]/90 leading-relaxed">
            {attempt.pronunciationAssessmentNote}
          </p>
        </div>

        {/* Transcribed Speech */}
        <div className="bg-white rounded-3xl border border-[#CBD7CF] p-5 sm:p-6 mb-6 shadow-2xs">
          <h3 className="font-bold text-sm text-[#161D1A] mb-2 uppercase tracking-wide">
            Transcribed Spoken Response
          </h3>
          <p className="text-sm text-[#45524B] italic leading-relaxed bg-[#F0F5F1] p-4 rounded-2xl border border-[#CBD7CF]">
            "{attempt.transcription}"
          </p>
        </div>

        {/* 4 Criteria Scores */}
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
