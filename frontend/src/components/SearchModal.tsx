import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Search, X, MessageSquare, User, AlertCircle } from 'lucide-react';
import { searchApi, conversationApi } from '../services/api';
import { useStore } from '../store/useStore';
import type { Customer, Message } from '../types';

interface SearchModalProps {
  onClose: () => void;
}

/**
 * Search modal for finding messages and customers
 */
export default function SearchModal({ onClose }: SearchModalProps) {
  const [query, setQuery] = useState('');
  const [isNavigating, setIsNavigating] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { setSelectedConversation } = useStore();

  // Search when query changes
  const { data: results, isLoading } = useQuery({
    queryKey: ['search', query],
    queryFn: () => searchApi.global(query),
    enabled: query.length >= 2,
  });

  // Handle customer click - load their conversation
  const handleCustomerClick = async (customer: Customer) => {
    setIsNavigating(true);
    setError(null);
    try {
      const conversations = await conversationApi.getByCustomer(customer.id);
      if (conversations.length > 0) {
        // Select the most recent conversation
        setSelectedConversation(conversations[0]);
        onClose();
      } else {
        setError(`No conversations found for ${customer.name}`);
      }
    } catch (err) {
      setError('Failed to load conversation');
    } finally {
      setIsNavigating(false);
    }
  };

  // Handle message click - load the conversation containing this message
  const handleMessageClick = async (message: Message) => {
    setIsNavigating(true);
    setError(null);
    try {
      if (message.conversationId) {
        const conversation = await conversationApi.getWithMessages(message.conversationId);
        setSelectedConversation(conversation);
        onClose();
      } else {
        setError('Message conversation not found');
      }
    } catch (err) {
      setError('Failed to load conversation');
    } finally {
      setIsNavigating(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-start justify-center pt-20 z-50 p-4">
      <div className="w-full max-w-lg bg-slate-800 rounded-xl">
        {/* Search input */}
        <div className="p-4 border-b border-slate-700 flex items-center gap-3">
          <Search className="w-5 h-5 text-gray-400" />
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search messages and customers..."
            className="flex-1 bg-transparent text-white placeholder-gray-500 focus:outline-none"
            autoFocus
          />
          <button onClick={onClose} className="p-1 hover:bg-slate-700 rounded">
            <X className="w-5 h-5 text-gray-400" />
          </button>
        </div>

        {/* Error message */}
        {error && (
          <div className="px-4 py-2 bg-red-500/20 text-red-400 flex items-center gap-2">
            <AlertCircle className="w-4 h-4" />
            {error}
          </div>
        )}

        {/* Loading overlay when navigating */}
        {isNavigating && (
          <div className="p-4 text-center text-gray-400">
            Loading conversation...
          </div>
        )}

        {/* Results */}
        {!isNavigating && (
          <div className="max-h-96 overflow-y-auto p-4">
            {query.length < 2 ? (
              <p className="text-center text-gray-500 py-8">
                Type at least 2 characters to search
              </p>
            ) : isLoading ? (
              <p className="text-center text-gray-400 py-8">Searching...</p>
            ) : !results || (results.messages.length === 0 && results.customers.length === 0) ? (
              <p className="text-center text-gray-400 py-8">No results found</p>
            ) : (
              <div className="space-y-4">
                {/* Customers */}
                {results.customers.length > 0 && (
                  <div>
                    <h3 className="text-xs text-gray-500 uppercase mb-2">
                      Customers ({results.customersTotal})
                    </h3>
                    <div className="space-y-2">
                      {results.customers.slice(0, 5).map((customer) => (
                        <button
                          key={customer.id}
                          onClick={() => handleCustomerClick(customer)}
                          className="w-full p-3 bg-slate-700/50 hover:bg-slate-700 rounded-lg text-left flex items-center gap-3"
                        >
                          <div className="w-10 h-10 rounded-full bg-blue-600 flex items-center justify-center">
                            <User className="w-5 h-5 text-white" />
                          </div>
                          <div className="flex-1">
                            <p className="text-sm text-white font-medium">{customer.name}</p>
                            <p className="text-xs text-gray-500">{customer.email}</p>
                          </div>
                          <span className="text-xs text-gray-400">Click to view</span>
                        </button>
                      ))}
                    </div>
                  </div>
                )}

                {/* Messages */}
                {results.messages.length > 0 && (
                  <div>
                    <h3 className="text-xs text-gray-500 uppercase mb-2">
                      Messages ({results.messagesTotal})
                    </h3>
                    <div className="space-y-2">
                      {results.messages.slice(0, 5).map((msg) => (
                        <button
                          key={msg.id}
                          onClick={() => handleMessageClick(msg)}
                          className="w-full p-3 bg-slate-700/50 hover:bg-slate-700 rounded-lg text-left flex items-start gap-3"
                        >
                          <MessageSquare className="w-4 h-4 text-gray-400 mt-1 flex-shrink-0" />
                          <div className="flex-1 min-w-0">
                            <p className="text-sm text-white line-clamp-2">{msg.content}</p>
                            <p className="text-xs text-gray-500 mt-1">
                              From: {msg.senderName || 'Unknown'}
                            </p>
                          </div>
                          <span className="text-xs text-gray-400 flex-shrink-0">Click to view</span>
                        </button>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        )}

        {/* Help text */}
        <div className="p-3 border-t border-slate-700 text-center">
          <p className="text-xs text-gray-500">
            Click on a customer or message to view the conversation
          </p>
        </div>
      </div>
    </div>
  );
}
