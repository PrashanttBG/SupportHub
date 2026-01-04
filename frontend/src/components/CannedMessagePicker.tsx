import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { X, Zap } from 'lucide-react';
import { cannedMessageApi } from '../services/api';

interface CannedMessagePickerProps {
  onSelect: (content: string) => void;
  onClose: () => void;
}

/**
 * Modal for selecting canned/template messages
 */
export default function CannedMessagePicker({ onSelect, onClose }: CannedMessagePickerProps) {
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);

  // Fetch canned messages
  const { data: cannedMessages, isLoading } = useQuery({
    queryKey: ['cannedMessages'],
    queryFn: cannedMessageApi.getAll,
  });

  // Fetch categories
  const { data: categories } = useQuery({
    queryKey: ['cannedMessageCategories'],
    queryFn: cannedMessageApi.getCategories,
  });

  // Filter by category
  const filteredMessages = selectedCategory
    ? cannedMessages?.filter(msg => msg.category === selectedCategory)
    : cannedMessages;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="w-full max-w-lg bg-slate-800 rounded-xl max-h-[80vh] flex flex-col">
        {/* Header */}
        <div className="p-4 border-b border-slate-700 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Zap className="w-5 h-5 text-yellow-400" />
            <h2 className="text-lg font-semibold text-white">Quick Replies</h2>
          </div>
          <button
            onClick={onClose}
            className="p-2 hover:bg-slate-700 rounded-lg text-gray-400"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Category tabs */}
        {categories && categories.length > 0 && (
          <div className="p-3 border-b border-slate-700 flex flex-wrap gap-2">
            <button
              onClick={() => setSelectedCategory(null)}
              className={`px-3 py-1.5 rounded-lg text-sm ${
                !selectedCategory
                  ? 'bg-blue-600 text-white'
                  : 'bg-slate-700 text-gray-300 hover:bg-slate-600'
              }`}
            >
              All
            </button>
            {categories.map((category) => (
              <button
                key={category}
                onClick={() => setSelectedCategory(category)}
                className={`px-3 py-1.5 rounded-lg text-sm ${
                  selectedCategory === category
                    ? 'bg-blue-600 text-white'
                    : 'bg-slate-700 text-gray-300 hover:bg-slate-600'
                }`}
              >
                {category}
              </button>
            ))}
          </div>
        )}

        {/* Messages list */}
        <div className="flex-1 overflow-y-auto p-4">
          {isLoading ? (
            <div className="text-center text-gray-400">Loading...</div>
          ) : !filteredMessages || filteredMessages.length === 0 ? (
            <div className="text-center text-gray-400">No quick replies found</div>
          ) : (
            <div className="space-y-2">
              {filteredMessages.map((msg) => (
                <button
                  key={msg.id}
                  onClick={() => onSelect(msg.content)}
                  className="w-full p-3 bg-slate-700/50 hover:bg-slate-700 rounded-lg text-left"
                >
                  <div className="flex items-center justify-between mb-1">
                    <h4 className="font-medium text-white">{msg.title}</h4>
                    {msg.shortcut && (
                      <code className="text-xs text-gray-500 bg-slate-800 px-1.5 py-0.5 rounded">
                        {msg.shortcut}
                      </code>
                    )}
                  </div>
                  <p className="text-sm text-gray-400 line-clamp-2">{msg.content}</p>
                </button>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
