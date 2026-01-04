/**
 * Customer entity
 */
export interface Customer {
  id: string;
  name: string;
  email: string | null;
  phone: string | null;
  accountStatus: string | null;
  loanStatus: string | null;
  totalConversations: number;
  createdAt: string;
}

/**
 * Agent entity
 */
export interface Agent {
  id: string;
  name: string;
  email: string;
  avatarUrl: string | null;
  status: AgentStatus;
  activeConversations: number;
  totalResolved: number;
  lastActive: string;
  createdAt: string;
}

export type AgentStatus = 'ONLINE' | 'AWAY' | 'BUSY' | 'OFFLINE';

/**
 * Conversation entity
 */
export interface Conversation {
  id: string;
  customer: Customer;
  assignedAgent: Agent | null;
  subject: string;
  status: ConversationStatus;
  urgencyScore: number;
  urgencyReason: string | null;
  urgencyLevel: UrgencyLevel;
  unreadCount: number;
  lastMessageAt: string;
  lastMessagePreview: string | null;
  createdAt: string;
  updatedAt: string;
  messages?: Message[];
}

export type ConversationStatus = 'OPEN' | 'IN_PROGRESS' | 'WAITING' | 'RESOLVED' | 'CLOSED';
export type UrgencyLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

/**
 * Message entity
 */
export interface Message {
  id: string;
  conversationId: string;
  senderId: string | null;
  senderType: SenderType;
  senderName: string | null;
  content: string;
  isRead: boolean;
  isCannedResponse: boolean;
  createdAt: string;
}

export type SenderType = 'CUSTOMER' | 'AGENT' | 'SYSTEM';

/**
 * Canned message template
 */
export interface CannedMessage {
  id: string;
  title: string;
  category: string;
  content: string;
  shortcut: string | null;
  usageCount: number;
  isActive: boolean;
  createdAt: string;
}

/**
 * Request types
 */
export interface CreateMessageRequest {
  customerName: string;
  customerEmail?: string;
  customerPhone?: string;
  content: string;
  subject?: string;
}

export interface ReplyMessageRequest {
  agentId: string;
  content: string;
  cannedMessageId?: string;
}

/**
 * Search result
 */
export interface SearchResult {
  messages: Message[];
  messagesTotal: number;
  customers: Customer[];
  customersTotal: number;
}
