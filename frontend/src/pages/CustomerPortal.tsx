import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { Send, MessageSquare, ArrowLeft } from 'lucide-react';
import { messageApi } from '../services/api';

/**
 * Customer portal for sending new messages
 */
export default function CustomerPortal() {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState('');
  const [submitted, setSubmitted] = useState(false);

  // Send message mutation
  const sendMessage = useMutation({
    mutationFn: () =>
      messageApi.create({
        customerName: name,
        customerEmail: email,
        content: message,
      }),
    onSuccess: () => {
      setSubmitted(true);
      setName('');
      setEmail('');
      setMessage('');
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (name && message) {
      sendMessage.mutate();
    }
  };

  return (
    <div className="min-h-screen bg-slate-900 flex items-center justify-center p-8">
      <div className="max-w-md w-full">
        {/* Header */}
        <div className="text-center mb-8">
          <div className="w-16 h-16 rounded-xl bg-blue-600 flex items-center justify-center mx-auto mb-4">
            <MessageSquare className="w-8 h-8 text-white" />
          </div>
          <h1 className="text-2xl font-bold text-white mb-2">Contact Support</h1>
          <p className="text-gray-400">Send us a message and we'll respond soon</p>
        </div>

        {submitted ? (
          // Success message
          <div className="bg-slate-800 rounded-lg p-8 text-center">
            <div className="w-12 h-12 rounded-full bg-green-600 flex items-center justify-center mx-auto mb-4">
              <Send className="w-6 h-6 text-white" />
            </div>
            <h2 className="text-xl font-semibold text-white mb-2">Message Sent!</h2>
            <p className="text-gray-400 mb-6">
              Thank you for contacting us. Our team will review your message and respond shortly.
            </p>
            <button
              onClick={() => setSubmitted(false)}
              className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg"
            >
              Send Another Message
            </button>
          </div>
        ) : (
          // Contact form
          <form onSubmit={handleSubmit} className="bg-slate-800 rounded-lg p-6 space-y-4">
            {/* Name */}
            <div>
              <label className="block text-sm text-gray-400 mb-1">Name *</label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Your name"
                required
                className="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg
                         text-white placeholder-gray-500 focus:outline-none focus:border-blue-500"
              />
            </div>

            {/* Email */}
            <div>
              <label className="block text-sm text-gray-400 mb-1">Email</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="your@email.com"
                className="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg
                         text-white placeholder-gray-500 focus:outline-none focus:border-blue-500"
              />
            </div>

            {/* Message */}
            <div>
              <label className="block text-sm text-gray-400 mb-1">Message *</label>
              <textarea
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                placeholder="How can we help you?"
                required
                rows={4}
                className="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg
                         text-white placeholder-gray-500 focus:outline-none focus:border-blue-500 resize-none"
              />
            </div>

            {/* Submit */}
            <button
              type="submit"
              disabled={sendMessage.isPending}
              className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg
                       flex items-center justify-center gap-2 disabled:opacity-50"
            >
              {sendMessage.isPending ? (
                'Sending...'
              ) : (
                <>
                  <Send className="w-4 h-4" />
                  Send Message
                </>
              )}
            </button>
          </form>
        )}

        {/* Back to agent portal */}
        <div className="mt-6 text-center">
          <a
            href="/agent"
            className="text-gray-400 hover:text-blue-400 text-sm flex items-center justify-center gap-1"
          >
            <ArrowLeft className="w-4 h-4" />
            Agent Portal
          </a>
        </div>
      </div>
    </div>
  );
}
