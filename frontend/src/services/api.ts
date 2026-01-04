import axios from 'axios';
import type {
  Agent,
  CannedMessage,
  Conversation,
  ConversationStatus,
  CreateMessageRequest,
  Customer,
  Message,
  ReplyMessageRequest,
  SearchResult,
} from '../types';

/**
 * Axios instance configured for API calls
 */
const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Conversation API endpoints
 */
export const conversationApi = {
  // Get all open conversations
  getOpen: async (): Promise<Conversation[]> => {
    const response = await api.get('/conversations/open');
    return response.data;
  },

  // Get conversation with all messages
  getWithMessages: async (id: string): Promise<Conversation> => {
    const response = await api.get(`/conversations/${id}/full`);
    return response.data;
  },

  // Get conversations by customer ID
  getByCustomer: async (customerId: string): Promise<Conversation[]> => {
    const response = await api.get(`/conversations/customer/${customerId}`);
    return response.data;
  },

  // Assign agent to conversation
  assignAgent: async (conversationId: string, agentId: string): Promise<Conversation> => {
    const response = await api.put(`/conversations/${conversationId}/assign`, null, {
      params: { agentId },
    });
    return response.data;
  },

  // Update conversation status
  updateStatus: async (conversationId: string, status: ConversationStatus): Promise<Conversation> => {
    const response = await api.put(`/conversations/${conversationId}/status`, null, {
      params: { status },
    });
    return response.data;
  },

  // Mark conversation as read
  markAsRead: async (conversationId: string): Promise<void> => {
    await api.post(`/conversations/${conversationId}/read`);
  },
};

/**
 * Message API endpoints
 */
export const messageApi = {
  // Create new message (starts new conversation)
  create: async (request: CreateMessageRequest): Promise<Message> => {
    const response = await api.post('/messages', request);
    return response.data;
  },

  // Reply to conversation as agent
  reply: async (conversationId: string, request: ReplyMessageRequest): Promise<Message> => {
    const response = await api.post(`/messages/conversation/${conversationId}/reply`, request);
    return response.data;
  },

  // Get messages for a conversation
  getByConversation: async (conversationId: string): Promise<Message[]> => {
    const response = await api.get(`/messages/conversation/${conversationId}`);
    return response.data;
  },
};

/**
 * Customer API endpoints
 */
export const customerApi = {
  // Get all customers
  getAll: async (): Promise<Customer[]> => {
    const response = await api.get('/customers');
    return response.data;
  },

  // Search customers
  search: async (query: string): Promise<Customer[]> => {
    const response = await api.get('/customers/search', { params: { query } });
    return response.data;
  },
};

/**
 * Agent API endpoints
 */
export const agentApi = {
  // Get all agents
  getAll: async (): Promise<Agent[]> => {
    const response = await api.get('/agents');
    return response.data;
  },

  // Update agent status
  updateStatus: async (agentId: string, status: string): Promise<Agent> => {
    const response = await api.put(`/agents/${agentId}/status`, null, {
      params: { status },
    });
    return response.data;
  },
};

/**
 * Canned message API endpoints
 */
export const cannedMessageApi = {
  // Get all canned messages
  getAll: async (): Promise<CannedMessage[]> => {
    const response = await api.get('/canned-messages');
    return response.data;
  },

  // Get all categories
  getCategories: async (): Promise<string[]> => {
    const response = await api.get('/canned-messages/categories');
    return response.data;
  },
};

/**
 * Search API endpoints
 */
export const searchApi = {
  // Global search
  global: async (query: string): Promise<SearchResult> => {
    const response = await api.get('/search', { params: { query } });
    return response.data;
  },
};

export default api;
