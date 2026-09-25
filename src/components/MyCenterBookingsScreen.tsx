import React from 'react';
import {
  ArrowLeft,
  Calendar,
  MapPin,
  CheckCircle2,
  Ticket,
  QrCode,
  ShieldCheck
} from 'lucide-react';
import { CenterBooking } from '../types';

interface MyCenterBookingsScreenProps {
  bookings: CenterBooking[];
  onBack: () => void;
  onExploreCenters: () => void;
}

export const MyCenterBookingsScreen: React.FC<MyCenterBookingsScreenProps> = ({
  bookings,
  onBack,
  onExploreCenters
}) => {
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
              CONFIRMED TICKETS
            </div>
            <h1 className="text-xl sm:text-2xl font-extrabold text-[#161D1A]">
              My Center Bookings
            </h1>
          </div>
        </div>

        {bookings.length === 0 ? (
          <div className="bg-white rounded-3xl border border-[#CBD7CF] p-8 text-center shadow-2xs my-8">
            <div className="w-14 h-14 rounded-full bg-[#BFF1D8] text-[#00543C] flex items-center justify-center mx-auto mb-3">
              <Ticket size={28} />
            </div>
            <h2 className="text-lg font-bold text-[#161D1A] mb-1">
              No Bookings Found
            </h2>
            <p className="text-xs text-[#45524B] mb-6">
              You haven't booked any in-person mock exam or coaching slots yet.
            </p>
            <button
              type="button"
              onClick={onExploreCenters}
              className="py-3 px-6 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-xs sm:text-sm shadow-xs transition-colors"
            >
              Explore Partner Practice Centers
            </button>
          </div>
        ) : (
          <div className="space-y-5">
            {bookings.map((booking) => (
              <div
                key={booking.bookingId}
                className="bg-white rounded-3xl border border-[#CBD7CF] overflow-hidden shadow-sm"
              >
                {/* Ticket Top Strip */}
                <div className="bg-[#00543C] text-white p-4 flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <ShieldCheck size={18} className="text-[#6FD6AC]" />
                    <span className="font-bold text-xs uppercase tracking-wider">
                      CONFIRMED PRACTICE PASS
                    </span>
                  </div>
                  <span className="text-xs font-black text-[#FFB84D]">
                    ৳{booking.priceBdt} BDT Paid
                  </span>
                </div>

                {/* Ticket Body */}
                <div className="p-5">
                  <h3 className="font-extrabold text-base text-[#161D1A] mb-1">
                    {booking.slotTitle}
                  </h3>
                  <div className="text-xs font-semibold text-[#00543C] mb-3">
                    {booking.centerName}
                  </div>

                  <div className="space-y-1.5 text-xs text-[#45524B] pb-4 border-b border-[#CBD7CF] mb-4">
                    <div className="flex items-center gap-2">
                      <Calendar size={13} className="text-[#00543C] shrink-0" />
                      <span>{booking.slotDate} ({booking.slotTime})</span>
                    </div>
                    <div className="flex items-start gap-2">
                      <MapPin size={13} className="text-[#00543C] shrink-0 mt-0.5" />
                      <span>{booking.centerAddress}</span>
                    </div>
                  </div>

                  {/* Candidate & Transaction Footer */}
                  <div className="flex items-center justify-between text-2xs text-[#45524B]">
                    <div>
                      <div>Candidate: <strong className="text-[#161D1A]">{booking.userName}</strong></div>
                      <div>Phone: <strong className="text-[#161D1A]">{booking.userPhone}</strong></div>
                      <div className="mt-1 font-mono text-gray-400">Txn: {booking.paymentTranId}</div>
                    </div>

                    {/* QR Code representation */}
                    <div className="p-2 rounded-xl bg-[#F0F5F1] text-[#00543C] border border-[#CBD7CF] text-center">
                      <QrCode size={34} />
                      <span className="text-3xs block font-bold mt-0.5 uppercase">PASS ID</span>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
