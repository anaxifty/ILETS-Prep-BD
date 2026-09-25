import React, { useState } from 'react';
import {
  ArrowLeft,
  MapPin,
  Star,
  Phone,
  Mail,
  Calendar,
  Users,
  CheckCircle2,
  Lock,
  Ticket
} from 'lucide-react';
import { PartnerCenter, PartnerSlot } from '../types';
import { SslCommerzPaymentSheet } from './SslCommerzPaymentSheet';

interface CenterDetailScreenProps {
  center: PartnerCenter;
  userPhone: string;
  onBookSlot: (slot: PartnerSlot, studentName: string, studentPhone: string, tranId: string) => void;
  onBack: () => void;
}

export const CenterDetailScreen: React.FC<CenterDetailScreenProps> = ({
  center,
  userPhone,
  onBookSlot,
  onBack
}) => {
  const [selectedSlot, setSelectedSlot] = useState<PartnerSlot | null>(center.slots[0] || null);
  const [studentName, setStudentName] = useState('IELTS Candidate');
  const [studentPhone, setStudentPhone] = useState(userPhone || '01700000000');
  const [showPaymentSheet, setShowPaymentSheet] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const tranId = `SSL_MOCK_${Date.now().toString().slice(-8)}`;

  const handleStartBooking = () => {
    if (!selectedSlot) {
      setError('Please choose a practice mock slot.');
      return;
    }
    if (selectedSlot.seatsRemaining <= 0) {
      setError('Sorry, this slot is already fully booked.');
      return;
    }
    if (!studentName.trim() || !studentPhone.trim()) {
      setError('Please enter your full name and contact number.');
      return;
    }
    setError(null);
    setShowPaymentSheet(true);
  };

  const handlePaymentSuccess = (transactionId: string) => {
    setShowPaymentSheet(false);
    if (selectedSlot) {
      onBookSlot(selectedSlot, studentName, studentPhone, transactionId);
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
              CENTER DETAILS & BOOKING
            </div>
            <h1 className="text-xl sm:text-2xl font-extrabold text-[#161D1A]">
              {center.name}
            </h1>
          </div>
        </div>

        {/* Center Summary Card */}
        <div className="bg-white rounded-3xl border border-[#CBD7CF] p-5 sm:p-6 mb-6 shadow-2xs">
          <div className="flex items-center justify-between mb-3">
            <span className="text-2xs font-extrabold uppercase px-2.5 py-1 rounded-full bg-[#BFF1D8] text-[#00543C]">
              {center.city} Center
            </span>
            <span className="text-xs font-bold text-[#00543C] flex items-center gap-1">
              <Star size={14} fill="#00543C" /> {center.rating.toFixed(1)} / 5.0 Rating
            </span>
          </div>

          <div className="flex items-start gap-2 text-xs text-[#45524B] mb-2">
            <MapPin size={15} className="text-[#00543C] shrink-0 mt-0.5" />
            <span>{center.address}</span>
          </div>

          <div className="flex items-center gap-4 text-xs text-[#45524B] mb-4">
            <div className="flex items-center gap-1">
              <Phone size={13} className="text-[#00543C]" />
              <span>{center.contactPhone}</span>
            </div>
            <div className="flex items-center gap-1">
              <Mail size={13} className="text-[#00543C]" />
              <span>{center.contactEmail}</span>
            </div>
          </div>

          <p className="text-xs sm:text-sm text-[#45524B] leading-relaxed pt-3 border-t border-[#CBD7CF]">
            {center.description}
          </p>
        </div>

        {/* Slots Selection */}
        <h2 className="text-base font-bold text-[#161D1A] mb-3">
          Select Practice Mock Test / Coaching Slot
        </h2>

        <div className="space-y-3 mb-6">
          {center.slots.map((slot) => {
            const isSelected = selectedSlot?.slotId === slot.slotId;
            const isSoldOut = slot.seatsRemaining <= 0;

            return (
              <div
                key={slot.slotId}
                onClick={() => !isSoldOut && setSelectedSlot(slot)}
                className={`p-4 rounded-3xl border transition-all cursor-pointer ${
                  isSelected
                    ? 'border-[#00543C] bg-white ring-2 ring-[#00543C]'
                    : isSoldOut
                    ? 'border-[#CBD7CF] bg-gray-50 opacity-60 cursor-not-allowed'
                    : 'border-[#CBD7CF] bg-white hover:border-gray-400'
                }`}
              >
                <div className="flex items-start justify-between mb-2">
                  <div>
                    <span className="text-2xs font-extrabold px-2 py-0.5 rounded-md bg-[#FFDFBD] text-[#3A2500] uppercase mr-2">
                      {slot.slotType === 'PRACTICE_MOCK' ? 'Mock Exam' : 'Coaching'}
                    </span>
                    <h3 className="font-bold text-sm sm:text-base text-[#161D1A] mt-1">
                      {slot.title}
                    </h3>
                  </div>
                  <div className="text-right">
                    <span className="text-base font-black text-[#00543C]">
                      ৳{slot.priceBdt}
                    </span>
                    <span className="text-2xs block text-[#45524B]">BDT</span>
                  </div>
                </div>

                <div className="flex items-center justify-between text-xs text-[#45524B] pt-2 border-t border-[#CBD7CF]">
                  <div className="flex items-center gap-1.5">
                    <Calendar size={13} />
                    <span>{slot.date} • {slot.time}</span>
                  </div>

                  <div className="flex items-center gap-1 font-semibold">
                    <Users size={13} />
                    <span className={isSoldOut ? 'text-red-600 font-bold' : slot.seatsRemaining <= 5 ? 'text-[#B26A00]' : 'text-[#00543C]'}>
                      {isSoldOut ? 'Sold Out' : `${slot.seatsRemaining} seats left`}
                    </span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        {/* Student Details Form */}
        <div className="bg-white rounded-3xl border border-[#CBD7CF] p-5 mb-6 shadow-2xs">
          <h3 className="font-bold text-sm text-[#161D1A] mb-3 uppercase tracking-wide">
            Candidate Booking Details
          </h3>

          <div className="space-y-3">
            <div>
              <label className="block text-xs font-semibold text-[#45524B] mb-1">
                Candidate Full Name
              </label>
              <input
                type="text"
                value={studentName}
                onChange={(e) => setStudentName(e.target.value)}
                placeholder="Full Name as in Passport"
                className="w-full p-3 bg-[#F8FAF9] rounded-xl border border-[#CBD7CF] text-xs font-semibold outline-none focus:border-[#00543C]"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-[#45524B] mb-1">
                Mobile Number for Booking SMS
              </label>
              <input
                type="tel"
                value={studentPhone}
                onChange={(e) => setStudentPhone(e.target.value)}
                placeholder="017XXXXXXXX"
                className="w-full p-3 bg-[#F8FAF9] rounded-xl border border-[#CBD7CF] text-xs font-semibold outline-none focus:border-[#00543C]"
              />
            </div>
          </div>

          {error && (
            <p className="mt-3 text-xs text-red-600 font-semibold text-center">{error}</p>
          )}
        </div>

        {/* Disclaimer note */}
        <p className="text-2xs text-[#45524B] text-center mb-4">
          Note: This booking provides practice sitting and expert feedback at {center.name}.
        </p>

        {/* CTA */}
        <button
          type="button"
          onClick={handleStartBooking}
          disabled={!selectedSlot || selectedSlot.seatsRemaining <= 0}
          className="w-full py-4 bg-[#00543C] hover:bg-[#00785A] disabled:opacity-50 text-white rounded-full font-bold text-base shadow-sm transition-all text-center flex items-center justify-center gap-2"
        >
          <Lock size={16} />
          Book Slot & Pay ৳{selectedSlot?.priceBdt || 1500} via SSLCommerz
        </button>

        {/* Modal Payment Sheet */}
        {showPaymentSheet && selectedSlot && (
          <SslCommerzPaymentSheet
            center={center}
            slot={selectedSlot}
            studentName={studentName}
            studentPhone={studentPhone}
            tranId={tranId}
            onPaymentSuccess={handlePaymentSuccess}
            onPaymentFailed={(err) => {
              setShowPaymentSheet(false);
              setError(err);
            }}
            onDismiss={() => setShowPaymentSheet(false)}
          />
        )}
      </div>
    </div>
  );
};
