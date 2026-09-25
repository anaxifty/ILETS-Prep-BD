import React, { useState } from 'react';
import {
  ArrowLeft,
  MapPin,
  Star,
  Phone,
  Calendar,
  ChevronRight,
  Ticket,
  Search
} from 'lucide-react';
import { PartnerCenter } from '../types';

interface CenterLocatorScreenProps {
  centers: PartnerCenter[];
  onSelectCenter: (center: PartnerCenter) => void;
  onNavigateToMyBookings: () => void;
  onBack: () => void;
}

export const CenterLocatorScreen: React.FC<CenterLocatorScreenProps> = ({
  centers,
  onSelectCenter,
  onNavigateToMyBookings,
  onBack
}) => {
  const [selectedCity, setSelectedCity] = useState<string>('All');
  const [searchQuery, setSearchQuery] = useState('');

  const cities = ['All', 'Dhaka', 'Chittagong', 'Sylhet'];

  const filteredCenters = centers.filter((center) => {
    const matchesCity = selectedCity === 'All' || center.city === selectedCity;
    const matchesSearch =
      center.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      center.address.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCity && matchesSearch;
  });

  return (
    <div className="min-h-screen bg-[#F5FAF7] text-[#161D1A] pb-12">
      <div className="max-w-xl mx-auto px-4 sm:px-6 pt-5">
        {/* Header */}
        <div className="flex items-center justify-between py-2 mb-4">
          <div className="flex items-center gap-3">
            <button
              type="button"
              onClick={onBack}
              className="w-10 h-10 rounded-full bg-white border border-[#CBD7CF] flex items-center justify-center text-[#45524B] hover:text-[#161D1A] transition-colors shadow-2xs"
            >
              <ArrowLeft size={18} />
            </button>
            <div>
              <div className="text-xs font-bold tracking-wider text-[#00543C] uppercase">
                IN-PERSON SITTINGS
              </div>
              <h1 className="text-xl sm:text-2xl font-extrabold text-[#161D1A]">
                Partner Practice Centers
              </h1>
            </div>
          </div>

          <button
            type="button"
            onClick={onNavigateToMyBookings}
            className="px-3 py-1.5 bg-[#FFDFBD] text-[#3A2500] hover:bg-[#FAD1A5] text-xs font-bold rounded-full flex items-center gap-1.5 transition-colors shadow-2xs"
          >
            <Ticket size={14} />
            My Bookings
          </button>
        </div>

        {/* Search Bar */}
        <div className="flex items-center bg-white border border-[#CBD7CF] rounded-2xl px-3.5 py-2.5 mb-3 shadow-2xs focus-within:border-[#00543C]">
          <Search size={16} className="text-[#45524B] mr-2 shrink-0" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search by area (Dhanmondi, Uttara, Agrabad)..."
            className="w-full text-xs sm:text-sm outline-none text-[#161D1A] bg-transparent"
          />
        </div>

        {/* City Filter Pills */}
        <div className="flex items-center gap-2 overflow-x-auto pb-2 mb-4 scrollbar-none">
          {cities.map((city) => {
            const isSelected = selectedCity === city;
            return (
              <button
                key={city}
                type="button"
                onClick={() => setSelectedCity(city)}
                className={`px-3.5 py-1.5 rounded-full text-xs font-bold transition-all whitespace-nowrap ${
                  isSelected
                    ? 'bg-[#00543C] text-white shadow-xs'
                    : 'bg-white border border-[#CBD7CF] text-[#45524B] hover:border-gray-400'
                }`}
              >
                {city}
              </button>
            );
          })}
        </div>

        {/* Center Cards */}
        <div className="space-y-4">
          {filteredCenters.map((center) => (
            <div
              key={center.id}
              onClick={() => onSelectCenter(center)}
              className="cursor-pointer bg-white hover:border-[#00543C] rounded-3xl border border-[#CBD7CF] p-5 shadow-2xs transition-all"
            >
              <div className="flex items-start justify-between gap-3 mb-2">
                <div>
                  <span className="text-2xs font-extrabold uppercase px-2 py-0.5 rounded-md bg-[#BFF1D8] text-[#00543C] mr-2">
                    {center.city}
                  </span>
                  <span className="text-xs font-bold text-[#00543C] flex items-center gap-1 inline-flex">
                    <Star size={13} fill="#00543C" /> {center.rating.toFixed(1)}
                  </span>
                  <h3 className="font-extrabold text-base text-[#161D1A] mt-1.5">
                    {center.name}
                  </h3>
                </div>
              </div>

              <div className="flex items-start gap-1.5 text-xs text-[#45524B] mb-2">
                <MapPin size={14} className="text-[#00543C] shrink-0 mt-0.5" />
                <span>{center.address}</span>
              </div>

              <p className="text-xs text-[#45524B] line-clamp-2 mb-3">
                {center.description}
              </p>

              {/* Slots Preview */}
              <div className="pt-3 border-t border-[#CBD7CF] flex items-center justify-between text-xs">
                <div className="flex items-center gap-1.5 text-[#00543C] font-semibold">
                  <Calendar size={14} />
                  <span>{center.slots.length} Upcoming Practice Slots Available</span>
                </div>
                <div className="flex items-center font-bold text-[#00543C]">
                  View Slots <ChevronRight size={15} />
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
