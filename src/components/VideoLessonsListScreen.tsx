import React, { useState } from 'react';
import {
  ArrowLeft,
  Play,
  Download,
  CheckCircle,
  Video,
  Clock,
  BookOpen,
  Filter
} from 'lucide-react';
import { VideoLesson } from '../types';
import { storage } from '../services/storage';

interface VideoLessonsListScreenProps {
  lessons: VideoLesson[];
  onSelectLesson: (lesson: VideoLesson) => void;
  onBack: () => void;
}

export const VideoLessonsListScreen: React.FC<VideoLessonsListScreenProps> = ({
  lessons,
  onSelectLesson,
  onBack
}) => {
  const [selectedFilter, setSelectedFilter] = useState<string>('All');
  const [downloadedIds, setDownloadedIds] = useState<string[]>(
    storage.getDownloadedLessons()
  );
  const [downloadingId, setDownloadingId] = useState<string | null>(null);

  const filters = ['All', 'Listening', 'Reading', 'Writing', 'Speaking'];

  const filteredLessons = selectedFilter === 'All'
    ? lessons
    : lessons.filter((l) => l.skillCategory === selectedFilter);

  const handleToggleDownload = (e: React.MouseEvent, lessonId: string) => {
    e.stopPropagation();
    if (downloadedIds.includes(lessonId)) {
      const isStillDownloaded = storage.toggleDownloadLesson(lessonId);
      if (!isStillDownloaded) {
        setDownloadedIds((prev) => prev.filter((id) => id !== lessonId));
      }
    } else {
      setDownloadingId(lessonId);
      setTimeout(() => {
        storage.toggleDownloadLesson(lessonId);
        setDownloadedIds((prev) => [...prev, lessonId]);
        setDownloadingId(null);
      }, 800);
    }
  };

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
              STUDY RESOURCES
            </div>
            <h1 className="text-xl sm:text-2xl font-extrabold text-[#161D1A]">
              Video Lessons Library
            </h1>
          </div>
        </div>

        {/* Filter Chips */}
        <div className="flex items-center gap-2 overflow-x-auto pb-2 mb-4 scrollbar-none">
          {filters.map((f) => {
            const isSelected = selectedFilter === f;
            return (
              <button
                key={f}
                type="button"
                onClick={() => setSelectedFilter(f)}
                className={`px-3.5 py-1.5 rounded-full text-xs font-bold transition-all whitespace-nowrap ${
                  isSelected
                    ? 'bg-[#00543C] text-white shadow-xs'
                    : 'bg-white border border-[#CBD7CF] text-[#45524B] hover:border-gray-400'
                }`}
              >
                {f}
              </button>
            );
          })}
        </div>

        {/* Lesson Cards List */}
        <div className="space-y-4">
          {filteredLessons.map((lesson) => {
            const isDownloaded = downloadedIds.includes(lesson.id);
            const isDownloading = downloadingId === lesson.id;

            return (
              <div
                key={lesson.id}
                onClick={() => onSelectLesson(lesson)}
                className="cursor-pointer bg-white hover:border-[#00543C] rounded-3xl border border-[#CBD7CF] overflow-hidden shadow-2xs transition-all"
              >
                {/* Thumbnail container */}
                <div className="relative aspect-video w-full bg-[#151D19] overflow-hidden">
                  <img
                    src={lesson.thumbnailUrl}
                    alt={lesson.title}
                    className="w-full h-full object-cover opacity-90 hover:opacity-100 transition-opacity"
                  />
                  <div className="absolute inset-0 bg-black/20 flex items-center justify-center">
                    <div className="w-12 h-12 rounded-full bg-[#00543C]/90 text-white flex items-center justify-center shadow-md">
                      <Play size={20} fill="currentColor" className="ml-0.5" />
                    </div>
                  </div>

                  {/* Duration Badge */}
                  <div className="absolute bottom-2.5 right-2.5 bg-black/70 text-white px-2 py-0.5 rounded-md text-xs font-mono font-semibold flex items-center gap-1">
                    <Clock size={11} /> {lesson.duration}
                  </div>

                  {/* Category Pill */}
                  <div className="absolute top-2.5 left-2.5 bg-[#BFF1D8] text-[#00543C] px-2.5 py-0.5 rounded-md text-2xs font-extrabold uppercase tracking-wide">
                    {lesson.skillCategory}
                  </div>
                </div>

                {/* Lesson Info */}
                <div className="p-4 sm:p-5">
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <div className="text-xs font-semibold text-[#00543C] mb-0.5">
                        {lesson.topic}
                      </div>
                      <h3 className="font-bold text-sm sm:text-base text-[#161D1A] line-clamp-1">
                        {lesson.title}
                      </h3>
                      <p className="text-xs text-[#45524B] mt-1 line-clamp-2">
                        {lesson.description}
                      </p>
                    </div>

                    {/* Download Toggle */}
                    <button
                      type="button"
                      onClick={(e) => handleToggleDownload(e, lesson.id)}
                      className={`p-2.5 rounded-xl border shrink-0 transition-colors ${
                        isDownloaded
                          ? 'bg-[#BFF1D8] text-[#00543C] border-[#96E2BD]'
                          : 'bg-[#F0F5F1] text-[#45524B] border-[#CBD7CF] hover:text-[#161D1A]'
                      }`}
                      title={isDownloaded ? 'Downloaded for Offline' : 'Download for Offline'}
                    >
                      {isDownloading ? (
                        <div className="w-4 h-4 border-2 border-[#00543C] border-t-transparent rounded-full animate-spin" />
                      ) : isDownloaded ? (
                        <CheckCircle size={16} />
                      ) : (
                        <Download size={16} />
                      )}
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

interface VideoLessonPlayerScreenProps {
  lesson: VideoLesson;
  onBack: () => void;
}

export const VideoLessonPlayerScreen: React.FC<VideoLessonPlayerScreenProps> = ({
  lesson,
  onBack
}) => {
  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] pb-12">
      <div className="max-w-2xl mx-auto px-4 sm:px-6 pt-5">
        {/* Header */}
        <div className="flex items-center gap-3 py-2 mb-4">
          <button
            type="button"
            onClick={onBack}
            className="w-10 h-10 rounded-full bg-white border border-[#CBD7CF] flex items-center justify-center text-[#45524B] hover:text-[#161D1A] transition-colors shadow-2xs"
          >
            <ArrowLeft size={18} />
          </button>
          <div className="flex-1 truncate">
            <div className="text-xs font-bold text-[#00543C] uppercase tracking-wide">
              {lesson.skillCategory} Strategy Lesson
            </div>
            <h1 className="text-base sm:text-lg font-extrabold text-[#161D1A] truncate">
              {lesson.title}
            </h1>
          </div>
        </div>

        {/* Video Player */}
        <div className="bg-black rounded-3xl overflow-hidden shadow-lg mb-6 aspect-video">
          <video
            src={lesson.videoUrl}
            controls
            autoPlay
            className="w-full h-full object-contain"
          />
        </div>

        {/* Lesson Details */}
        <div className="bg-white rounded-3xl border border-[#CBD7CF] p-6 shadow-2xs mb-6">
          <div className="flex items-center justify-between pb-3 border-b border-[#CBD7CF] mb-3">
            <span className="text-xs font-bold px-2.5 py-1 rounded-lg bg-[#BFF1D8] text-[#00543C]">
              {lesson.skillCategory}
            </span>
            <span className="text-xs text-[#45524B] font-mono">
              Duration: {lesson.duration}
            </span>
          </div>

          <h2 className="text-xl font-bold text-[#161D1A] mb-2">
            {lesson.title}
          </h2>
          <p className="text-sm text-[#45524B] leading-relaxed mb-4">
            {lesson.description}
          </p>

          <h3 className="font-bold text-sm text-[#161D1A] mb-2 uppercase tracking-wide">
            Key Strategy Takeaways
          </h3>
          <ul className="text-xs sm:text-sm text-[#45524B] space-y-2 list-disc pl-5">
            <li>Identify keywords and paraphrases before the speaker begins or before reading the passage.</li>
            <li>Maintain steady pacing to ensure all questions receive a response within allotted time.</li>
            <li>Apply elimination strategies to differentiate between true contradictions and absent details.</li>
          </ul>
        </div>
      </div>
    </div>
  );
};
