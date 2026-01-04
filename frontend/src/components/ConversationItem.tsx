import { formatDistanceToNow } from 'date-fns';
import { User, Clock } from 'lucide-react';
import { useStore } from '../store/useStore';
import type { Conversation } from '../types';
import UrgencyBadge from './UrgencyBadge';

interface ConversationItemProps {
  conversation: Conversation;
}

/**
 * Single conversation item in the sidebar list
 */
export default function ConversationItem({ conversation }: ConversationItemProps) {
  const { selectedConversation, setSelectedConversation } = useStore();
  const isSelected = selectedConversation?.id === conversation.id;

  // Format time ago
  const timeAgo = conversation.lastMessageAt
    ? formatDistanceToNow(new Date(conversation.lastMessageAt), { addSuffix: true })
    : 'Just now';

  return (
    <button
      onClick={() => setSelectedConversation(conversation)}
      className={`w-full p-3 rounded-lg text-left transition-colors ${
        isSelected
          ? 'bg-blue-600/20 border border-blue-500/30'
          : 'bg-slate-700/50 hover:bg-slate-700 border border-transparent'
      }`}
    >
      {/* Header: Customer name and urgency */}
      <div className="flex items-center justify-between mb-2">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-full bg-slate-600 flex items-center justify-center">
            <User className="w-4 h-4 text-gray-400" />
          </div>
          <div>
            <h4 className="font-medium text-white text-sm">
              {conversation.customer.name}
            </h4>
          </div>
        </div>
        <UrgencyBadge level={conversation.urgencyLevel} />
      </div>

      {/* Message preview */}
      <p className="text-sm text-gray-400 line-clamp-2 mb-2">
        {conversation.lastMessagePreview || conversation.subject}
      </p>

      {/* Footer: Time and status */}
      <div className="flex items-center justify-between text-xs">
        <div className="flex items-center gap-1 text-gray-500">
          <Clock className="w-3 h-3" />
          {timeAgo}
        </div>

        <div className="flex items-center gap-2">
          {/* Status badge */}
          <span className={`px-2 py-0.5 rounded-full text-xs ${
            conversation.status === 'OPEN' ? 'bg-blue-500/20 text-blue-400' :
            conversation.status === 'IN_PROGRESS' ? 'bg-yellow-500/20 text-yellow-400' :
            'bg-green-500/20 text-green-400'
          }`}>
            {conversation.status.replace('_', ' ')}
          </span>

          {/* Unread count */}
          {conversation.unreadCount > 0 && (
            <span className="w-5 h-5 flex items-center justify-center bg-blue-600 text-white text-xs rounded-full">
              {conversation.unreadCount}
            </span>
          )}
        </div>
      </div>
    </button>
  );
}
