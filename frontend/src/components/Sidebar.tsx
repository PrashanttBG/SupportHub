import { Inbox } from 'lucide-react';
import { useStore } from '../store/useStore';
import ConversationItem from './ConversationItem';

interface SidebarProps {
  isLoading: boolean;
}

/**
 * Sidebar showing list of conversations grouped by urgency
 */
export default function Sidebar({ isLoading }: SidebarProps) {
  const { conversations } = useStore();

  // Group conversations by urgency level
  const criticalConversations = conversations.filter(c => c.urgencyLevel === 'CRITICAL');
  const highConversations = conversations.filter(c => c.urgencyLevel === 'HIGH');
  const mediumConversations = conversations.filter(c => c.urgencyLevel === 'MEDIUM');
  const lowConversations = conversations.filter(c => c.urgencyLevel === 'LOW');

  // Loading state
  if (isLoading) {
    return (
      <div className="h-full bg-slate-800/50 p-4">
        <div className="text-gray-400">Loading conversations...</div>
      </div>
    );
  }

  // Empty state
  if (conversations.length === 0) {
    return (
      <div className="h-full bg-slate-800/50 flex flex-col items-center justify-center p-4">
        <Inbox className="w-12 h-12 text-gray-600 mb-3" />
        <p className="text-gray-400">No conversations</p>
      </div>
    );
  }

  return (
    <div className="h-full bg-slate-800/50 flex flex-col">
      {/* Header */}
      <div className="p-4 border-b border-slate-700">
        <h2 className="font-semibold text-white text-lg">
          Conversations ({conversations.length})
        </h2>
      </div>

      {/* Conversation list */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {/* Critical Priority */}
        {criticalConversations.length > 0 && (
          <ConversationGroup
            title="Critical"
            color="red"
            conversations={criticalConversations}
          />
        )}

        {/* High Priority */}
        {highConversations.length > 0 && (
          <ConversationGroup
            title="High"
            color="orange"
            conversations={highConversations}
          />
        )}

        {/* Medium Priority */}
        {mediumConversations.length > 0 && (
          <ConversationGroup
            title="Medium"
            color="yellow"
            conversations={mediumConversations}
          />
        )}

        {/* Low Priority */}
        {lowConversations.length > 0 && (
          <ConversationGroup
            title="Low"
            color="green"
            conversations={lowConversations}
          />
        )}
      </div>
    </div>
  );
}

/**
 * Group of conversations with same priority
 */
interface ConversationGroupProps {
  title: string;
  color: string;
  conversations: ReturnType<typeof useStore>['conversations'];
}

function ConversationGroup({ title, color, conversations }: ConversationGroupProps) {
  // Color mapping for badges
  const colorClasses: Record<string, string> = {
    red: 'bg-red-500/20 text-red-400',
    orange: 'bg-orange-500/20 text-orange-400',
    yellow: 'bg-yellow-500/20 text-yellow-400',
    green: 'bg-green-500/20 text-green-400',
  };

  return (
    <div className="mb-4">
      {/* Group header */}
      <div className="flex items-center gap-2 mb-2">
        <span className={`px-2 py-1 rounded text-xs font-medium ${colorClasses[color]}`}>
          {title} ({conversations.length})
        </span>
      </div>

      {/* Conversation items */}
      <div className="space-y-2">
        {conversations.map((conversation) => (
          <ConversationItem key={conversation.id} conversation={conversation} />
        ))}
      </div>
    </div>
  );
}
