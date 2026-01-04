import { Headphones } from 'lucide-react';
import type { Agent } from '../types';

interface AgentSelectorProps {
  agents: Agent[];
  onSelect: (agent: Agent) => void;
}

/**
 * Agent selection screen shown before entering the portal
 */
export default function AgentSelector({ agents, onSelect }: AgentSelectorProps) {
  return (
    <div className="min-h-screen bg-slate-900 flex items-center justify-center p-8">
      <div className="max-w-md w-full">
        {/* Header */}
        <div className="text-center mb-8">
          <div className="w-16 h-16 rounded-xl bg-blue-600 flex items-center justify-center mx-auto mb-4">
            <Headphones className="w-8 h-8 text-white" />
          </div>
          <h1 className="text-2xl font-bold text-white mb-2">Support Portal</h1>
          <p className="text-gray-400">Select an agent to continue</p>
        </div>

        {/* Agent list */}
        <div className="space-y-3">
          {agents.length === 0 ? (
            <div className="text-center p-8 bg-slate-800 rounded-lg">
              <p className="text-gray-400">No agents available</p>
            </div>
          ) : (
            agents.map((agent) => (
              <button
                key={agent.id}
                onClick={() => onSelect(agent)}
                className="w-full p-4 bg-slate-800 hover:bg-slate-700 rounded-lg text-left flex items-center gap-4 transition-colors"
              >
                {/* Avatar */}
                <div className="w-12 h-12 rounded-full bg-blue-600 flex items-center justify-center text-white font-semibold">
                  {agent.name.charAt(0)}
                </div>

                {/* Info */}
                <div className="flex-1">
                  <h3 className="font-medium text-white">{agent.name}</h3>
                  <p className="text-sm text-gray-400">{agent.email}</p>
                </div>

                {/* Status indicator */}
                <div className="flex items-center gap-2">
                  <div className={`w-2 h-2 rounded-full ${
                    agent.status === 'ONLINE' ? 'bg-green-500' :
                    agent.status === 'AWAY' ? 'bg-yellow-500' :
                    'bg-gray-500'
                  }`} />
                  <span className="text-xs text-gray-400 capitalize">
                    {agent.status.toLowerCase()}
                  </span>
                </div>
              </button>
            ))
          )}
        </div>

        {/* Customer portal link */}
        <div className="mt-8 text-center">
          <a href="/customer" className="text-gray-400 hover:text-blue-400 text-sm">
            Are you a customer? Send us a message →
          </a>
        </div>
      </div>
    </div>
  );
}
