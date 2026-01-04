import { Search, RefreshCw, LogOut } from 'lucide-react';
import { useState } from 'react';
import { useStore } from '../store/useStore';
import SearchModal from './SearchModal';

interface HeaderProps {
  onRefresh?: () => void;
}

/**
 * Header component with logo, search, and agent info
 */
export default function Header({ onRefresh }: HeaderProps) {
  const { currentAgent, setCurrentAgent } = useStore();
  const [showSearch, setShowSearch] = useState(false);

  return (
    <>
      <header className="h-16 bg-slate-800 border-b border-slate-700 flex items-center px-4 gap-4">
        {/* Logo */}
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center">
            <span className="text-white font-bold text-sm">SP</span>
          </div>
          <span className="font-semibold text-white">Support Portal</span>
        </div>

        {/* Spacer */}
        <div className="flex-1" />

        {/* Refresh button */}
        {onRefresh && (
          <button
            onClick={onRefresh}
            className="p-2 text-gray-400 hover:text-white hover:bg-slate-700 rounded-lg"
            title="Refresh conversations"
          >
            <RefreshCw className="w-5 h-5" />
          </button>
        )}

        {/* Search button */}
        <button
          onClick={() => setShowSearch(true)}
          className="flex items-center gap-2 px-3 py-2 text-gray-400 hover:text-white hover:bg-slate-700 rounded-lg"
        >
          <Search className="w-4 h-4" />
          <span className="text-sm">Search</span>
        </button>

        {/* Current agent info */}
        <div className="flex items-center gap-3 px-3 py-2 bg-slate-700/50 rounded-lg">
          <div className="w-8 h-8 rounded-full bg-blue-600 flex items-center justify-center text-white text-sm font-medium">
            {currentAgent?.name.charAt(0)}
          </div>
          <div className="hidden md:block">
            <div className="text-sm font-medium text-white">{currentAgent?.name}</div>
            <div className="text-xs text-gray-400">{currentAgent?.status}</div>
          </div>
        </div>

        {/* Logout/Switch agent */}
        <button
          onClick={() => setCurrentAgent(null)}
          className="p-2 text-gray-400 hover:text-red-400 hover:bg-slate-700 rounded-lg"
          title="Switch Agent"
        >
          <LogOut className="w-5 h-5" />
        </button>
      </header>

      {/* Search Modal */}
      {showSearch && <SearchModal onClose={() => setShowSearch(false)} />}
    </>
  );
}
