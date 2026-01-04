import { create } from 'zustand';
import type { Agent, Conversation } from '../types';

/**
 * Global state store using Zustand
 * Manages current agent and selected conversation
 * Messages are managed by React Query, not global state
 */
interface AppState {
  // Current logged in agent
  currentAgent: Agent | null;
  setCurrentAgent: (agent: Agent | null) => void;

  // Currently selected conversation
  selectedConversation: Conversation | null;
  setSelectedConversation: (conversation: Conversation | null) => void;

  // List of all conversations (for sidebar)
  conversations: Conversation[];
  setConversations: (conversations: Conversation[]) => void;
}

export const useStore = create<AppState>((set) => ({
  // Agent state
  currentAgent: null,
  setCurrentAgent: (agent) => set({ currentAgent: agent }),

  // Conversation state
  selectedConversation: null,
  setSelectedConversation: (conversation) => set({ selectedConversation: conversation }),

  // Conversations list
  conversations: [],
  setConversations: (conversations) => set({ conversations }),
}));
