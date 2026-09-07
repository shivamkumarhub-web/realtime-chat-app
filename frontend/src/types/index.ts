export interface JwtResponse {
  token: string;
  type: string;
  username: string;
  roles: string[];
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface ChatMessage {
  sender: string;
  content: string;
  roomId: string;
  type: string;
  createdAt?: string;
}

export interface PresenceUpdate {
  onlineUsers: string[];
}