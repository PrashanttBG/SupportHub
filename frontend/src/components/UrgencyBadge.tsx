import type { UrgencyLevel } from '../types';

interface UrgencyBadgeProps {
  level: UrgencyLevel;
}

/**
 * Badge showing urgency level with color coding
 */
export default function UrgencyBadge({ level }: UrgencyBadgeProps) {
  // Color mapping for different urgency levels
  const colors: Record<UrgencyLevel, string> = {
    CRITICAL: 'bg-red-500/20 text-red-400 border-red-500/30',
    HIGH: 'bg-orange-500/20 text-orange-400 border-orange-500/30',
    MEDIUM: 'bg-yellow-500/20 text-yellow-400 border-yellow-500/30',
    LOW: 'bg-green-500/20 text-green-400 border-green-500/30',
  };

  return (
    <span className={`px-2 py-0.5 text-xs font-medium rounded border ${colors[level]}`}>
      {level}
    </span>
  );
}
