import { Routes, Route, Navigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import AgentPortal from './pages/AgentPortal';
import CustomerPortal from './pages/CustomerPortal';
import AgentSelector from './components/AgentSelector';
import { useStore } from './store/useStore';
import { agentApi } from './services/api';

/**
 * Main App component with routing
 */
function App() {
  const { currentAgent, setCurrentAgent } = useStore();

  // Fetch agents from API
  const { data: agents, isLoading } = useQuery({
    queryKey: ['agents'],
    queryFn: agentApi.getAll,
  });

  // Auto-select first online agent
  useEffect(() => {
    if (!currentAgent && agents && agents.length > 0) {
      const onlineAgent = agents.find(a => a.status === 'ONLINE');
      if (onlineAgent) {
        setCurrentAgent(onlineAgent);
      }
    }
  }, [agents, currentAgent, setCurrentAgent]);

  // Show loading while fetching agents
  if (isLoading) {
    return (
      <div className="h-screen flex items-center justify-center">
        <p className="text-gray-400">Loading...</p>
      </div>
    );
  }

  return (
    <div className="h-screen overflow-hidden">
      <Routes>
        {/* Redirect root to agent portal */}
        <Route path="/" element={<Navigate to="/agent" replace />} />
        
        {/* Agent Portal - requires agent selection */}
        <Route
          path="/agent"
          element={
            currentAgent ? (
              <AgentPortal />
            ) : (
              <AgentSelector agents={agents || []} onSelect={setCurrentAgent} />
            )
          }
        />
        
        {/* Customer Portal - public */}
        <Route path="/customer" element={<CustomerPortal />} />
      </Routes>
    </div>
  );
}

export default App;
