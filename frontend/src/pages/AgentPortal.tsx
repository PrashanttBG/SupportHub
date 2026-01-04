import { useEffect, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import Header from '../components/Header';
import Sidebar from '../components/Sidebar';
import ConversationView from '../components/ConversationView';
import EmptyState from '../components/EmptyState';
import { useStore } from '../store/useStore';
import { conversationApi } from '../services/api';

/**
 * Main Agent Portal page
 * Shows sidebar with conversations and main area for viewing messages
 */
export default function AgentPortal() {
  const { selectedConversation, setConversations } = useStore();
  const [sidebarOpen] = useState(true);

  // Fetch open conversations from API
  const { data: conversations, isLoading, refetch } = useQuery({
    queryKey: ['conversations', 'open'],
    queryFn: conversationApi.getOpen,
    refetchInterval: 30000, // Auto refresh every 30 seconds
  });

  // Update store when conversations are loaded
  useEffect(() => {
    if (conversations) {
      setConversations(conversations);
    }
  }, [conversations, setConversations]);

  return (
    <div className="h-screen flex flex-col bg-slate-900 overflow-hidden">
      {/* Header - fixed at top */}
      <Header onRefresh={refetch} />
      
      {/* Main content area - takes remaining space */}
      <div className="flex-1 flex overflow-hidden min-h-0">
        {/* Sidebar with conversation list */}
        {sidebarOpen && (
          <div className="w-80 border-r border-slate-700 overflow-hidden flex-shrink-0">
            <Sidebar isLoading={isLoading} />
          </div>
        )}

        {/* Main content area - full height */}
        <div className="flex-1 flex overflow-hidden h-full">
          {selectedConversation ? (
            <ConversationView />
          ) : (
            <EmptyState />
          )}
        </div>
      </div>
    </div>
  );
}
