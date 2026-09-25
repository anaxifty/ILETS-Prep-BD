import React, { useState } from 'react';
import { Phone, Mail, Lock, Shield, User, ArrowRight } from 'lucide-react';

interface PhoneAuthScreenProps {
  onOtpSent: (phone: string) => void;
  onAuthenticated: (phone: string) => void;
}

export const PhoneAuthScreen: React.FC<PhoneAuthScreenProps> = ({
  onOtpSent,
  onAuthenticated
}) => {
  const [authMode, setAuthMode] = useState<'PHONE' | 'EMAIL'>('PHONE');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSendOtp = () => {
    if (phone.length < 10) {
      setError('Please enter a valid Bangladeshi mobile number (e.g., 01712345678)');
      return;
    }
    setError(null);
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      onOtpSent(phone);
    }, 600);
  };

  const handleEmailSignIn = () => {
    if (!email || !password) {
      setError('Please enter both email and password');
      return;
    }
    setError(null);
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      onAuthenticated(email);
    }, 600);
  };

  const handleGoogleSignIn = () => {
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      onAuthenticated('student@gmail.com');
    }, 600);
  };

  const handleGuestSignIn = () => {
    onAuthenticated('Guest Candidate');
  };

  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] flex flex-col items-center justify-center p-4 sm:p-6">
      <div className="w-full max-w-md bg-white rounded-3xl shadow-sm border border-[#CBD7CF] p-6 sm:p-8">
        {/* Header Tag */}
        <div className="inline-block bg-[#BFF1D8] text-[#00543C] text-xs font-bold px-3 py-1.5 rounded-full mb-4 tracking-wider">
          🇧🇩 BANGLADESH EDITION
        </div>

        <h1 className="text-2xl sm:text-3xl font-extrabold text-[#161D1A] tracking-tight">
          Start Your IELTS Journey
        </h1>
        <p className="text-sm text-[#45524B] mt-2 mb-6">
          Sign in to personalize your study plan, track band progress, and access full mock exams.
        </p>

        {/* Tab Toggle */}
        <div className="grid grid-cols-2 bg-[#F0F5F1] p-1 rounded-full mb-6">
          <button
            type="button"
            onClick={() => setAuthMode('PHONE')}
            className={`flex items-center justify-center gap-2 py-2 text-xs sm:text-sm font-bold rounded-full transition-all ${
              authMode === 'PHONE'
                ? 'bg-[#00543C] text-white shadow-sm'
                : 'text-[#45524B] hover:text-[#161D1A]'
            }`}
          >
            <Phone size={15} />
            Phone OTP
          </button>
          <button
            type="button"
            onClick={() => setAuthMode('EMAIL')}
            className={`flex items-center justify-center gap-2 py-2 text-xs sm:text-sm font-bold rounded-full transition-all ${
              authMode === 'EMAIL'
                ? 'bg-[#00543C] text-white shadow-sm'
                : 'text-[#45524B] hover:text-[#161D1A]'
            }`}
          >
            <Mail size={15} />
            Email Sign In
          </button>
        </div>

        {authMode === 'PHONE' ? (
          <div>
            <label className="block text-xs font-bold tracking-wider text-[#45524B] uppercase mb-2">
              Mobile Number
            </label>
            <div className="flex items-center border border-[#CBD7CF] rounded-2xl bg-[#F0F5F1] px-3 py-2.5 focus-within:border-[#00543C] focus-within:ring-1 focus-within:ring-[#00543C]">
              <span className="font-semibold text-sm mr-2 flex items-center gap-1 border-r border-[#CBD7CF] pr-2">
                🇧🇩 +880
              </span>
              <input
                type="tel"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="017XXXXXXXX"
                className="w-full bg-transparent text-sm font-medium outline-none text-[#161D1A] placeholder:text-gray-400"
              />
              <Phone size={16} className="text-[#45524B]" />
            </div>

            {/* Quick Autofill Chip */}
            <button
              type="button"
              onClick={() => setPhone('01700000000')}
              className="mt-2.5 w-full flex items-center justify-center gap-1.5 py-1.5 px-3 bg-[#EAF7EE] text-[#00543C] rounded-xl text-xs font-semibold hover:bg-[#DDF2E3] transition-colors"
            >
              <Shield size={13} />
              Autofill demo number (01700000000)
            </button>

            {error && (
              <p className="mt-3 text-xs text-red-600 font-medium text-center">{error}</p>
            )}

            <button
              type="button"
              onClick={handleSendOtp}
              disabled={loading || phone.length < 10}
              className="mt-5 w-full py-3.5 bg-[#00543C] hover:bg-[#00785A] disabled:opacity-50 text-white rounded-full font-bold text-sm sm:text-base flex items-center justify-center gap-2 transition-all shadow-sm"
            >
              {loading ? (
                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
              ) : (
                <>
                  Get Verification OTP
                  <ArrowRight size={17} />
                </>
              )}
            </button>
          </div>
        ) : (
          <div>
            <label className="block text-xs font-bold tracking-wider text-[#45524B] uppercase mb-1">
              Email Address
            </label>
            <div className="flex items-center border border-[#CBD7CF] rounded-2xl bg-[#F0F5F1] px-3 py-2.5 mb-3 focus-within:border-[#00543C]">
              <Mail size={16} className="text-[#45524B] mr-2" />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="student@example.com"
                className="w-full bg-transparent text-sm font-medium outline-none text-[#161D1A]"
              />
            </div>

            <label className="block text-xs font-bold tracking-wider text-[#45524B] uppercase mb-1">
              Password
            </label>
            <div className="flex items-center border border-[#CBD7CF] rounded-2xl bg-[#F0F5F1] px-3 py-2.5 mb-4 focus-within:border-[#00543C]">
              <Lock size={16} className="text-[#45524B] mr-2" />
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full bg-transparent text-sm font-medium outline-none text-[#161D1A]"
              />
            </div>

            {error && (
              <p className="mb-3 text-xs text-red-600 font-medium text-center">{error}</p>
            )}

            <button
              type="button"
              onClick={handleEmailSignIn}
              disabled={loading || !email || !password}
              className="w-full py-3.5 bg-[#00543C] hover:bg-[#00785A] disabled:opacity-50 text-white rounded-full font-bold text-sm sm:text-base flex items-center justify-center transition-all shadow-sm"
            >
              {loading ? (
                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
              ) : (
                'Sign In with Email'
              )}
            </button>
          </div>
        )}

        {/* OR Divider */}
        <div className="relative my-6 text-center">
          <div className="absolute inset-0 flex items-center">
            <div className="w-full border-t border-[#CBD7CF]"></div>
          </div>
          <span className="relative bg-white px-3 text-xs uppercase font-bold text-[#45524B]">
            OR CONTINUE WITH
          </span>
        </div>

        {/* Google Sign In */}
        <button
          type="button"
          onClick={handleGoogleSignIn}
          className="w-full py-3 border border-[#CBD7CF] rounded-full font-semibold text-sm text-[#161D1A] flex items-center justify-center gap-2 hover:bg-[#F0F5F1] transition-colors mb-3"
        >
          <span className="font-black text-lg text-[#00543C]">G</span>
          Sign in with Google
        </button>

        {/* Guest Skip */}
        <button
          type="button"
          onClick={handleGuestSignIn}
          className="w-full py-3 bg-[#EBF3EF] hover:bg-[#DFEDE5] border border-[#CBD7CF] rounded-full font-semibold text-sm text-[#00543C] flex items-center justify-center gap-2 transition-colors"
        >
          <User size={16} />
          Continue as Guest / Quick Skip
        </button>

        <p className="mt-6 text-xs text-center text-[#45524B]">
          By continuing, you agree to our terms of service and privacy policy.
        </p>
      </div>
    </div>
  );
};
