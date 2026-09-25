import React, { useState, useEffect } from 'react';
import { ArrowLeft, ArrowRight, ShieldCheck } from 'lucide-react';

interface OtpVerificationScreenProps {
  phoneNumber: string;
  onVerified: () => void;
  onBack: () => void;
}

export const OtpVerificationScreen: React.FC<OtpVerificationScreenProps> = ({
  phoneNumber,
  onVerified,
  onBack
}) => {
  const [otp, setOtp] = useState(['', '', '', '', '', '']);
  const [timer, setTimer] = useState(30);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (timer <= 0) return;
    const interval = setInterval(() => {
      setTimer((prev) => prev - 1);
    }, 1000);
    return () => clearInterval(interval);
  }, [timer]);

  const handleOtpChange = (index: number, val: string) => {
    if (!/^\d*$/.test(val)) return;
    const newOtp = [...otp];
    newOtp[index] = val.slice(-1);
    setOtp(newOtp);

    // Auto-focus next input
    if (val && index < 5) {
      const nextInput = document.getElementById(`otp-input-${index + 1}`);
      nextInput?.focus();
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Backspace' && !otp[index] && index > 0) {
      const prevInput = document.getElementById(`otp-input-${index - 1}`);
      prevInput?.focus();
    }
  };

  const handleAutofillDemoOtp = () => {
    setOtp(['1', '2', '3', '4', '5', '6']);
  };

  const handleVerify = () => {
    const fullOtp = otp.join('');
    if (fullOtp.length < 6) {
      setError('Please enter the complete 6-digit verification code.');
      return;
    }
    setError(null);
    setLoading(true);

    setTimeout(() => {
      setLoading(false);
      onVerified();
    }, 600);
  };

  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] flex flex-col items-center justify-center p-4 sm:p-6">
      <div className="w-full max-w-md bg-white rounded-3xl shadow-sm border border-[#CBD7CF] p-6 sm:p-8">
        <button
          type="button"
          onClick={onBack}
          className="flex items-center gap-1.5 text-xs font-semibold text-[#45524B] hover:text-[#161D1A] mb-4"
        >
          <ArrowLeft size={16} />
          Change Number
        </button>

        <div className="w-12 h-12 bg-[#BFF1D8] text-[#00543C] rounded-2xl flex items-center justify-center mb-4">
          <ShieldCheck size={26} />
        </div>

        <h1 className="text-2xl sm:text-3xl font-extrabold text-[#161D1A] tracking-tight">
          Verify Phone Number
        </h1>
        <p className="text-sm text-[#45524B] mt-2 mb-6">
          We sent a 6-digit SMS verification code to{' '}
          <span className="font-bold text-[#161D1A]">{phoneNumber || '+880 1700000000'}</span>.
        </p>

        {/* 6-box input */}
        <div className="flex justify-between gap-2 mb-4">
          {otp.map((digit, idx) => (
            <input
              key={idx}
              id={`otp-input-${idx}`}
              type="text"
              inputMode="numeric"
              maxLength={1}
              value={digit}
              onChange={(e) => handleOtpChange(idx, e.target.value)}
              onKeyDown={(e) => handleKeyDown(idx, e)}
              className="w-11 h-13 sm:w-12 sm:h-14 text-center text-xl font-bold bg-[#F0F5F1] border border-[#CBD7CF] rounded-xl focus:border-[#00543C] focus:bg-white focus:outline-none transition-all"
            />
          ))}
        </div>

        {/* Autofill Demo Chip */}
        <button
          type="button"
          onClick={handleAutofillDemoOtp}
          className="mb-4 w-full py-1.5 bg-[#EAF7EE] text-[#00543C] rounded-xl text-xs font-semibold hover:bg-[#DDF2E3] transition-colors"
        >
          Demo mode: Autofill OTP (123456)
        </button>

        {error && (
          <p className="mb-4 text-xs text-red-600 font-medium text-center">{error}</p>
        )}

        <button
          type="button"
          onClick={handleVerify}
          disabled={loading || otp.join('').length < 6}
          className="w-full py-3.5 bg-[#00543C] hover:bg-[#00785A] disabled:opacity-50 text-white rounded-full font-bold text-sm sm:text-base flex items-center justify-center gap-2 transition-all shadow-sm mb-4"
        >
          {loading ? (
            <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
          ) : (
            <>
              Verify & Proceed
              <ArrowRight size={17} />
            </>
          )}
        </button>

        {/* Resend Timer */}
        <div className="text-center text-xs text-[#45524B]">
          {timer > 0 ? (
            <span>Resend code in <strong className="text-[#00543C]">{timer}s</strong></span>
          ) : (
            <button
              type="button"
              onClick={() => setTimer(30)}
              className="font-bold text-[#00543C] hover:underline"
            >
              Resend OTP
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
