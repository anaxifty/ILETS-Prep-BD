import React, { useState } from 'react';
import {
  Shield,
  X,
  Smartphone,
  CreditCard,
  Lock,
  CheckCircle2
} from 'lucide-react';
import { PartnerCenter, PartnerSlot } from '../types';

interface SslCommerzPaymentSheetProps {
  center: PartnerCenter;
  slot: PartnerSlot;
  studentName: string;
  studentPhone: string;
  tranId: string;
  onPaymentSuccess: (tranId: string) => void;
  onPaymentFailed: (error: string) => void;
  onDismiss: () => void;
}

export const SslCommerzPaymentSheet: React.FC<SslCommerzPaymentSheetProps> = ({
  center,
  slot,
  studentName,
  studentPhone,
  tranId,
  onPaymentSuccess,
  onPaymentFailed,
  onDismiss
}) => {
  const [selectedMethod, setSelectedMethod] = useState<'bKash' | 'Nagad' | 'Cards'>('bKash');
  const [isProcessing, setIsProcessing] = useState(false);

  const handlePay = () => {
    setIsProcessing(true);
    setTimeout(() => {
      onPaymentSuccess(tranId);
    }, 2000);
  };

  return (
    <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-end sm:items-center justify-center p-0 sm:p-4 animate-in fade-in duration-200">
      <div className="bg-white w-full max-w-lg rounded-t-3xl sm:rounded-3xl border border-[#CBD7CF] p-6 shadow-2xl max-h-[90vh] overflow-y-auto">
        {/* Header Bar */}
        <div className="flex items-center justify-between pb-3 border-b border-[#CBD7CF] mb-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-[#00658F]/10 flex items-center justify-center text-[#00658F]">
              <Shield size={20} />
            </div>
            <div>
              <h2 className="font-extrabold text-base text-[#00543C]">
                SSLCommerz Secured Payment
              </h2>
              <p className="text-2xs text-[#45524B]">
                Secure Bangladesh Payment Gateway
              </p>
            </div>
          </div>

          <button
            type="button"
            disabled={isProcessing}
            onClick={onDismiss}
            className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-500 hover:text-gray-800 disabled:opacity-50"
          >
            <X size={18} />
          </button>
        </div>

        {/* Transaction Summary Card */}
        <div className="bg-[#EFF4F0] rounded-2xl p-4 mb-4 border border-[#CBD7CF]/50">
          <div className="flex items-start justify-between mb-2">
            <div className="font-bold text-sm text-[#161D1A]">
              {slot.title}
            </div>
            <div className="text-lg font-black text-[#00543C]">
              ৳{slot.priceBdt} BDT
            </div>
          </div>

          <div className="text-xs text-[#45524B] space-y-0.5">
            <div>Center: <strong className="text-[#161D1A]">{center.name}</strong></div>
            <div>Slot Date: <strong className="text-[#161D1A]">{slot.date} ({slot.time})</strong></div>
            <div>Student: <strong className="text-[#161D1A]">{studentName || 'Student'} ({studentPhone})</strong></div>
          </div>

          <div className="mt-3 pt-2 border-t border-[#CBD7CF] flex items-center justify-between text-2xs text-[#45524B]">
            <span>Transaction ID:</span>
            <span className="font-mono font-bold text-[#161D1A]">{tranId}</span>
          </div>
        </div>

        {/* Payment Method Selector */}
        <div className="mb-4">
          <label className="block text-xs font-bold text-[#00543C] uppercase tracking-wide mb-2">
            Select Payment Method
          </label>
          <div className="grid grid-cols-3 gap-2">
            {/* bKash */}
            <button
              type="button"
              onClick={() => setSelectedMethod('bKash')}
              className={`p-3 rounded-2xl border text-center transition-all flex flex-col items-center justify-center gap-1 ${
                selectedMethod === 'bKash'
                  ? 'border-[#00543C] bg-[#BFF1D8]/40 ring-1 ring-[#00543C]'
                  : 'border-[#CBD7CF] bg-white text-[#45524B]'
              }`}
            >
              <Smartphone size={20} className={selectedMethod === 'bKash' ? 'text-[#00543C]' : 'text-[#45524B]'} />
              <span className={`text-xs font-bold ${selectedMethod === 'bKash' ? 'text-[#00543C]' : 'text-[#161D1A]'}`}>
                bKash
              </span>
            </button>

            {/* Nagad */}
            <button
              type="button"
              onClick={() => setSelectedMethod('Nagad')}
              className={`p-3 rounded-2xl border text-center transition-all flex flex-col items-center justify-center gap-1 ${
                selectedMethod === 'Nagad'
                  ? 'border-[#00543C] bg-[#BFF1D8]/40 ring-1 ring-[#00543C]'
                  : 'border-[#CBD7CF] bg-white text-[#45524B]'
              }`}
            >
              <Smartphone size={20} className={selectedMethod === 'Nagad' ? 'text-[#00543C]' : 'text-[#45524B]'} />
              <span className={`text-xs font-bold ${selectedMethod === 'Nagad' ? 'text-[#00543C]' : 'text-[#161D1A]'}`}>
                Nagad
              </span>
            </button>

            {/* Cards / Bank */}
            <button
              type="button"
              onClick={() => setSelectedMethod('Cards')}
              className={`p-3 rounded-2xl border text-center transition-all flex flex-col items-center justify-center gap-1 ${
                selectedMethod === 'Cards'
                  ? 'border-[#00543C] bg-[#BFF1D8]/40 ring-1 ring-[#00543C]'
                  : 'border-[#CBD7CF] bg-white text-[#45524B]'
              }`}
            >
              <CreditCard size={20} className={selectedMethod === 'Cards' ? 'text-[#00543C]' : 'text-[#45524B]'} />
              <span className={`text-xs font-bold ${selectedMethod === 'Cards' ? 'text-[#00543C]' : 'text-[#161D1A]'}`}>
                Cards / Bank
              </span>
            </button>
          </div>
        </div>

        {/* Disclaimer Banner (Hard Requirement) */}
        <div className="bg-[#FFE5B0] border border-[#F6C697] rounded-2xl p-3 flex items-start gap-2.5 mb-5 text-xs text-[#3A2500]">
          <Lock size={16} className="text-[#B26A00] shrink-0 mt-0.5" />
          <p className="leading-snug">
            Booking is for a practice mock test / coaching sitting at partner center. Does NOT register for official IELTS exam.
          </p>
        </div>

        {/* CTA */}
        {isProcessing ? (
          <div className="py-4 text-center">
            <div className="w-8 h-8 border-3 border-[#00543C] border-t-transparent rounded-full animate-spin mx-auto mb-2" />
            <div className="text-sm font-bold text-[#00543C]">
              Connecting SSLCommerz Gateway ({selectedMethod})...
            </div>
            <div className="text-xs text-[#45524B] mt-0.5">
              Please do not close app during transaction processing
            </div>
          </div>
        ) : (
          <div>
            <button
              type="button"
              onClick={handlePay}
              className="w-full py-3.5 bg-[#00543C] hover:bg-[#00785A] text-white rounded-full font-bold text-sm sm:text-base flex items-center justify-center gap-2 shadow-sm transition-all"
            >
              <Lock size={16} />
              Pay ৳{slot.priceBdt} BDT via SSLCommerz
            </button>

            <button
              type="button"
              onClick={() => onPaymentFailed('Payment cancelled by user.')}
              className="w-full mt-2 py-2 text-xs font-semibold text-gray-500 hover:text-gray-800"
            >
              Cancel Transaction
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
