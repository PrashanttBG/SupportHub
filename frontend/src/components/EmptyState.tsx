import { MessageSquare } from 'lucide-react';

/**
 * Empty state shown when no conversation is selected
 */
export default function EmptyState() {
  return (
    <div className="flex-1 flex flex-col items-center justify-center bg-slate-900 text-center p-8">
      <div className="w-16 h-16 rounded-full bg-slate-800 flex items-center justify-center mb-4">
        <MessageSquare className="w-8 h-8 text-gray-500" />
      </div>
      <h2 className="text-xl font-semibold text-white mb-2">
        No conversation selected
      </h2>
      <p className="text-gray-400 max-w-md">
        Select a conversation from the sidebar to view messages and start helping customers.
      </p>
    </div>
  );
}
