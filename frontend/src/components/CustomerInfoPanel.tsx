import { User, Mail, Phone } from 'lucide-react';
import type { Customer } from '../types';

interface CustomerInfoPanelProps {
  customer: Customer;
}

/**
 * Panel showing customer information
 */
export default function CustomerInfoPanel({ customer }: CustomerInfoPanelProps) {
  return (
    <div className="w-72 border-l border-slate-700 bg-slate-800/50 overflow-y-auto">
      <div className="p-4">
        {/* Customer avatar and name */}
        <div className="text-center mb-6">
          <div className="w-16 h-16 rounded-full bg-blue-600 flex items-center justify-center mx-auto mb-3">
            <User className="w-8 h-8 text-white" />
          </div>
          <h3 className="text-lg font-semibold text-white">{customer.name}</h3>
          <p className="text-sm text-gray-400">Customer</p>
        </div>

        {/* Contact info */}
        <div className="space-y-3 mb-6">
          {customer.email && (
            <div className="p-3 bg-slate-700/50 rounded-lg">
              <div className="flex items-center gap-2 mb-1">
                <Mail className="w-4 h-4 text-gray-400" />
                <span className="text-xs text-gray-500">Email</span>
              </div>
              <p className="text-sm text-white">{customer.email}</p>
            </div>
          )}

          {customer.phone && (
            <div className="p-3 bg-slate-700/50 rounded-lg">
              <div className="flex items-center gap-2 mb-1">
                <Phone className="w-4 h-4 text-gray-400" />
                <span className="text-xs text-gray-500">Phone</span>
              </div>
              <p className="text-sm text-white">{customer.phone}</p>
            </div>
          )}
        </div>

        {/* Account status */}
        <div className="mb-6">
          <h4 className="text-xs text-gray-500 uppercase mb-3">Account Info</h4>
          <div className="space-y-2">
            <div className="flex justify-between p-3 bg-slate-700/50 rounded-lg">
              <span className="text-sm text-gray-400">Account Status</span>
              <span className={`text-sm ${
                customer.accountStatus === 'ACTIVE' ? 'text-green-400' : 'text-gray-400'
              }`}>
                {customer.accountStatus || 'Unknown'}
              </span>
            </div>
            <div className="flex justify-between p-3 bg-slate-700/50 rounded-lg">
              <span className="text-sm text-gray-400">Loan Status</span>
              <span className={`text-sm ${
                customer.loanStatus === 'APPROVED' ? 'text-green-400' :
                customer.loanStatus === 'PENDING' ? 'text-yellow-400' :
                'text-gray-400'
              }`}>
                {customer.loanStatus || 'N/A'}
              </span>
            </div>
          </div>
        </div>

        {/* Stats */}
        <div className="p-3 bg-slate-700/50 rounded-lg text-center">
          <p className="text-2xl font-bold text-white">{customer.totalConversations}</p>
          <p className="text-xs text-gray-500">Total Conversations</p>
        </div>
      </div>
    </div>
  );
}
