# Customer Support Messaging System

A full-stack messaging application for customer support agents to manage and respond to customer inquiries.

## Features

- **Agent Portal**: View and respond to customer conversations
- **Urgency Detection**: Automatic prioritization of messages (Critical, High, Medium, Low)
- **Canned Responses**: Quick reply templates for common questions
- **Search**: Search across messages and customers
- **Customer Portal**: Customers can send new messages

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.2
- Spring Data JPA
- H2 Database (in-memory for development)
- OpenCSV (for CSV import)

### Frontend
- React 18
- TypeScript
- Tailwind CSS
- React Query
- Zustand (state management)

## Prerequisites

1. **Java 17** - Install via Homebrew: `brew install openjdk@17`
2. **Maven** - Install via Homebrew: `brew install maven`
3. **Node.js 18+** - Install via Homebrew: `brew install node`

## Setup & Run Instructions

### 1. Start the Backend

```bash
cd backend

# Set Java 17 (if needed)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Run the application
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Backend will start at: http://localhost:8080

### 2. Start the Frontend

Open a new terminal:

```bash
cd frontend

# Install dependencies
npm install

# Run development server
npm run dev
```

Frontend will start at: http://localhost:5173

## Using the Application

1. Open http://localhost:5173 in your browser
2. Select an agent from the list (Prashant Baghel, Sneha Singh, or Rajesh Kumar)
3. View conversations in the sidebar, sorted by urgency
4. Click a conversation to view messages and reply
5. Use the Canned Responses (lightning bolt icon) for quick replies
6. Use Search (magnifying glass icon) to find messages or customers

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/conversations/open` | GET | Get all open conversations |
| `/api/conversations/{id}/full` | GET | Get conversation with messages |
| `/api/messages` | POST | Create new message (new conversation) |
| `/api/messages/conversation/{id}/reply` | POST | Reply to conversation |
| `/api/agents` | GET | Get all agents |
| `/api/canned-messages` | GET | Get canned response templates |
| `/api/search?query=` | GET | Search messages and customers |

## Project Structure

```
Chat/
├── backend/
│   └── src/main/java/com/support/
│       ├── controller/     # REST API controllers
│       ├── service/        # Business logic
│       ├── repository/     # Database access
│       ├── model/          # JPA entities
│       └── dto/            # Data transfer objects
├── frontend/
│   └── src/
│       ├── components/     # React components
│       ├── pages/          # Page components
│       ├── services/       # API calls
│       ├── store/          # Zustand state
│       └── types/          # TypeScript types
└── README.md
```

## Key Code to Review

### Backend
1. **UrgencyDetectionService.java** - Simple keyword matching for urgency detection
2. **CsvImportService.java** - CSV file import and data initialization
3. **MessageService.java** - Message creation and reply logic
4. **ConversationService.java** - Conversation management

### Frontend
1. **AgentPortal.tsx** - Main page with sidebar and conversation view
2. **ConversationView.tsx** - Message display and reply interface
3. **Sidebar.tsx** - Conversation list grouped by urgency
4. **api.ts** - All API calls to backend

## Data Flow

1. Customer sends message → Creates new conversation with urgency analysis
2. Conversation appears in agent sidebar, sorted by urgency level
3. Agent clicks conversation → Messages are loaded
4. Agent replies → Message saved, conversation status updated
5. Agent can resolve conversation when issue is fixed

