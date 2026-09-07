import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Send, LogOut, Users, MessageCircle } from 'lucide-react';
import api from '../api/client';
import { useAuth } from '../context/AuthContext';
import { ChatMessage, PresenceUpdate } from '../types';

export default function ChatRoom() {
  const { username, token, logout } = useAuth();
  const navigate = useNavigate();
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [messageInput, setMessageInput] = useState('');
  const [onlineUsers, setOnlineUsers] = useState<string[]>([]);
  const [connected, setConnected] = useState(false);
  const clientRef = useRef<Client | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!token) { navigate('/login'); return; }

    // Load message history
    api.get('/messages/room/general').then(({ data }) => {
      setMessages(data.map((m: any) => ({
        sender: m.sender, content: m.content, roomId: m.roomId, type: m.type, createdAt: m.createdAt
      })));
    }).catch(() => {});

    // Set up STOMP WebSocket client
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: { Authorization: `Bearer ${token}` },
      debug: () => {},
      onConnect: () => {
        setConnected(true);

        // Subscribe to general chat room
        client.subscribe('/topic/room/general', (msg) => {
          const chatMsg: ChatMessage = JSON.parse(msg.body);
          setMessages((prev) => [...prev, chatMsg]);
        });

        // Subscribe to presence updates
        client.subscribe('/topic/presence', (msg) => {
          const presence: PresenceUpdate = JSON.parse(msg.body);
          setOnlineUsers(presence.onlineUsers || []);
        });

        // Announce user joining
        client.publish({
          destination: '/app/chat.addUser',
          body: JSON.stringify({ sender: username, content: '', roomId: 'general', type: 'JOIN' }),
        });
      },
      onDisconnect: () => setConnected(false),
      onStompError: () => setConnected(false),
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [token, username, navigate]);

  // Fetch online users via REST as fallback
  useEffect(() => {
    api.get('/users/online').then(({ data }) => {
      setOnlineUsers(data.onlineUsers || []);
    }).catch(() => {});
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = () => {
    if (!messageInput.trim() || !clientRef.current?.connected) return;
    clientRef.current.publish({
      destination: '/app/chat.sendMessage',
      body: JSON.stringify({
        sender: username, content: messageInput, roomId: 'general', type: 'CHAT'
      }),
    });
    setMessageInput('');
  };

  const handleLogout = () => {
    clientRef.current?.deactivate();
    logout();
    navigate('/login');
  };

  return (
    <div className="h-screen flex">
      {/* Sidebar - Online users */}
      <div className="w-64 bg-gray-900 text-white flex flex-col">
        <div className="p-4 border-b border-gray-700">
          <h2 className="font-bold text-lg flex items-center gap-2"><MessageCircle size={20} /> Chat App</h2>
        </div>
        <div className="p-4 border-b border-gray-700">
          <div className="flex items-center gap-2 text-sm text-gray-400 mb-3">
            <Users size={16} /> Online ({onlineUsers.length})
          </div>
          <ul className="space-y-2">
            {onlineUsers.map((u) => (
              <li key={u} className="flex items-center gap-2 text-sm">
                <span className="w-2 h-2 bg-green-500 rounded-full"></span> {u}
              </li>
            ))}
            {onlineUsers.length === 0 && <li className="text-gray-500 text-sm">No users online</li>}
          </ul>
        </div>
        <div className="mt-auto p-4 border-t border-gray-700">
          <div className="text-sm text-gray-400 mb-2">Logged in as <strong className="text-white">{username}</strong></div>
          <div className="text-xs mb-3">
            <span className={connected ? 'text-green-400' : 'text-red-400'}>
              {connected ? '● Connected' : '● Disconnected'}
            </span>
          </div>
          <button onClick={handleLogout} className="flex items-center gap-2 text-sm text-gray-400 hover:text-white">
            <LogOut size={16} /> Logout
          </button>
        </div>
      </div>

      {/* Chat area */}
      <div className="flex-1 flex flex-col">
        <div className="p-4 bg-white border-b shadow-sm">
          <h1 className="font-bold text-gray-800"># general</h1>
        </div>
        <div className="flex-1 overflow-y-auto p-4 space-y-3 bg-gray-50">
          {messages.map((msg, i) => (
            <div key={i} className={msg.type === 'JOIN' || msg.type === 'LEAVE' ? 'text-center' : ''}>
              {msg.type === 'JOIN' || msg.type === 'LEAVE' ? (
                <span className="text-xs text-gray-500 bg-gray-200 px-3 py-1 rounded-full">{msg.content}</span>
              ) : (
                <div className={msg.sender === username ? 'text-right' : ''}>
                  <div className={msg.sender === username
                    ? 'inline-block bg-indigo-600 text-white px-4 py-2 rounded-2xl rounded-br-sm max-w-md'
                    : 'inline-block bg-white text-gray-800 px-4 py-2 rounded-2xl rounded-bl-sm max-w-md shadow-sm border'}>
                    {msg.sender !== username && <div className="text-xs font-bold text-indigo-600 mb-1">{msg.sender}</div>}
                    <div className="text-sm">{msg.content}</div>
                  </div>
                </div>
              )}
            </div>
          ))}
          <div ref={messagesEndRef} />
        </div>
        <div className="p-4 bg-white border-t">
          <div className="flex gap-2">
            <input
              type="text"
              value={messageInput}
              onChange={(e) => setMessageInput(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSend()}
              placeholder="Type a message..."
              className="flex-1 px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-500"
            />
            <button onClick={handleSend} className="bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 flex items-center gap-1">
              <Send size={18} /> Send
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}