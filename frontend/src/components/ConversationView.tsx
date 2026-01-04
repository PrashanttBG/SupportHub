import { useEffect, useRef, useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { format } from 'date-fns';
import { Send, Zap, CheckCircle } from 'lucide-react';
import { useStore } from '../store/useStore';
import { conversationApi, messageApi } from '../services/api';
import CustomerInfoPanel from './CustomerInfoPanel';
import CannedMessagePicker from './CannedMessagePicker';
import UrgencyBadge from './UrgencyBadge';
import type { Message } from '../types';

/**
 * Main conversation view - shows messages and reply input
 * Reply input is fixed at the bottom of the viewport
 */
export default function ConversationView() {
  const { selectedConversation, setSelectedConversation, currentAgent } = useStore();
  const [replyContent, setReplyContent] = useState('');
  const [showCannedPicker, setShowCannedPicker] = useState(false);
  const messagesContainerRef = useRef<HTMLDivElement>(null);
  const queryClient = useQueryClient();

  // Fetch conversation with messages
  const { data: conversationData, isLoading, refetch } = useQuery({
    queryKey: ['conversation', selectedConversation?.id],
    queryFn: () => conversationApi.getWithMessages(selectedConversation!.id),
    enabled: !!selectedConversation?.id,
  });

  // Get messages from the fetched data (not global state)
  const messages: Message[] = conversationData?.messages || [];

  // Scroll messages container to bottom when conversation changes or messages load
  useEffect(() => {
    if (messagesContainerRef.current && !isLoading) {
      // Scroll to bottom of messages container
      const scrollToBottom = () => {
        if (messagesContainerRef.current) {
          messagesContainerRef.current.scrollTop = messagesContainerRef.current.scrollHeight;
        }
      };
      
      // Use setTimeout to ensure DOM has updated
      setTimeout(scrollToBottom, 100);
    }
  }, [messages.length, selectedConversation?.id, isLoading]);

  // Send reply mutation
  const sendReply = useMutation({
    mutationFn: (content: string) =>
      messageApi.reply(selectedConversation!.id, {
        agentId: currentAgent!.id,
        content,
      }),
    onSuccess: () => {
      setReplyContent('');
      // Refetch the conversation to get new message
      refetch().then(() => {
        // Scroll to bottom after new message is loaded
        setTimeout(() => {
          if (messagesContainerRef.current) {
            messagesContainerRef.current.scrollTop = messagesContainerRef.current.scrollHeight;
          }
        }, 200);
      });
      // Refresh sidebar
      queryClient.invalidateQueries({ queryKey: ['conversations'] });
    },
    onError: (error) => {
      console.error('Failed to send reply:', error);
      alert('Failed to send reply. Please try again.');
    },
  });

  // Resolve conversation mutation
  const resolveConversation = useMutation({
    mutationFn: () =>
      conversationApi.updateStatus(selectedConversation!.id, 'RESOLVED'),
    onSuccess: (updatedConversation) => {
      // Update the selected conversation with new status
      setSelectedConversation(updatedConversation);
      // Refresh the sidebar and current conversation
      queryClient.invalidateQueries({ queryKey: ['conversations'] });
      queryClient.invalidateQueries({ queryKey: ['conversation', selectedConversation?.id] });
    },
    onError: (error) => {
      console.error('Failed to resolve conversation:', error);
      alert('Failed to resolve conversation. Please try again.');
    },
  });

  // Handle send button click
  const handleSend = () => {
    if (replyContent.trim() && currentAgent && !sendReply.isPending) {
      sendReply.mutate(replyContent.trim());
    }
  };

  // Handle Enter key press
  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  // Handle canned message selection
  const handleCannedSelect = (content: string) => {
    setReplyContent(content);
    setShowCannedPicker(false);
  };

  if (!selectedConversation) return null;

  // Use conversationData for status if available (more up to date)
  const currentStatus = conversationData?.status || selectedConversation.status;

  return (
    <div className="flex-1 flex h-full">
      {/* Main chat area - full height flex column */}
      <div className="flex-1 flex flex-col h-full">
        {/* Header - fixed at top */}
        <div className="h-16 px-4 flex items-center justify-between border-b border-slate-700 bg-slate-800 flex-shrink-0">
          <div>
            <h2 className="font-semibold text-white flex items-center gap-2">
              {selectedConversation.customer.name}
              <UrgencyBadge level={selectedConversation.urgencyLevel} />
            </h2>
            <p className="text-sm text-gray-400">{selectedConversation.subject}</p>
          </div>

          {/* Resolve button - only show if not resolved */}
          {currentStatus !== 'RESOLVED' && (
            <button
              onClick={() => resolveConversation.mutate()}
              disabled={resolveConversation.isPending}
              className="flex items-center gap-2 px-3 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg text-sm disabled:opacity-50"
            >
              <CheckCircle className="w-4 h-4" />
              {resolveConversation.isPending ? 'Resolving...' : 'Resolve'}
            </button>
          )}
          
          {/* Show resolved badge */}
          {currentStatus === 'RESOLVED' && (
            <span className="flex items-center gap-2 px-3 py-2 bg-green-600/20 text-green-400 rounded-lg text-sm">
              <CheckCircle className="w-4 h-4" />
              Resolved
            </span>
          )}
        </div>

        {/* Messages list - scrollable area, takes remaining space */}
        <div 
          ref={messagesContainerRef}
          className="flex-1 overflow-y-auto p-4 space-y-3 bg-slate-900 min-h-0"
        >
          {isLoading ? (
            <div className="text-center text-gray-400">Loading messages...</div>
          ) : messages.length === 0 ? (
            <div className="text-center text-gray-400">No messages yet</div>
          ) : (
            <>
              {messages.map((message) => (
                <MessageBubble key={message.id} message={message} />
              ))}
            </>
          )}
        </div>

        {/* Reply input - fixed at bottom of viewport */}
        <div className="p-4 border-t border-slate-700 bg-slate-800 flex-shrink-0">
          <div className="flex items-end gap-2">
            {/* Canned messages button */}
            <button
              onClick={() => setShowCannedPicker(true)}
              className="p-3 bg-slate-700 hover:bg-slate-600 text-gray-300 rounded-lg"
              title="Quick Replies"
            >
              <Zap className="w-5 h-5" />
            </button>

            {/* Text input */}
            <textarea
              value={replyContent}
              onChange={(e) => setReplyContent(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Type your reply..."
              rows={1}
              className="flex-1 px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg
                       text-white placeholder-gray-500 focus:outline-none focus:border-blue-500"
            />

            {/* Send button */}
            <button
              onClick={handleSend}
              disabled={!replyContent.trim() || sendReply.isPending}
              className="p-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg disabled:opacity-50"
            >
              <Send className="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>

      {/* Customer info panel */}
      <CustomerInfoPanel customer={selectedConversation.customer} />

      {/* Canned message picker modal */}
      {showCannedPicker && (
        <CannedMessagePicker
          onSelect={handleCannedSelect}
          onClose={() => setShowCannedPicker(false)}
        />
      )}
    </div>
  );
}

/**
 * Single message bubble component
 */
interface MessageBubbleProps {
  message: Message;
}

function MessageBubble({ message }: MessageBubbleProps) {
  const isCustomer = message.senderType === 'CUSTOMER';
  const time = format(new Date(message.createdAt), 'h:mm a');

  return (
    <div className={`flex ${isCustomer ? 'justify-start' : 'justify-end'}`}>
      <div
        className={`max-w-[70%] px-4 py-3 rounded-2xl ${
          isCustomer
            ? 'bg-slate-700 rounded-bl-none'
            : 'bg-blue-600 rounded-br-none'
        }`}
      >
        {/* Sender name */}
        <div className={`text-xs font-medium mb-1 ${isCustomer ? 'text-blue-400' : 'text-blue-200'}`}>
          {message.senderName || (isCustomer ? 'Customer' : 'Agent')}
        </div>

        {/* Message content - full content, no truncation */}
        <p className="text-white whitespace-pre-wrap break-words">{message.content}</p>

        {/* Timestamp */}
        <div className="text-xs text-gray-400 mt-1">{time}</div>
      </div>
    </div>
  );
}
