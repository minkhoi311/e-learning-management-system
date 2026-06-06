import React, { useState, useEffect, useContext, useRef } from 'react';
import { Container, Card, Form, InputGroup, Button } from 'react-bootstrap';
import { useLocation, useNavigate, Navigate } from 'react-router-dom';
import { database } from '../../configs/firebase';
import { ref, onValue, push, set } from 'firebase/database';
import { MyUserContext } from '../../configs/Contexts';

const ChatRoom = () => {
    const [user] = useContext(MyUserContext);
    const location = useLocation();
    const nav = useNavigate();
    

    const { roomId, targetName } = location.state || {};

    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState("");


    useEffect(() => {
        if (roomId) {
            const messagesRef = ref(database, `messages/${roomId}`);
            const unsubscribe = onValue(messagesRef, (snapshot) => {
                const data = snapshot.val();
                if (data) {
                    const parsedMessages = Object.keys(data).map(key => ({
                        id: key,
                        ...data[key]
                    }));
                    setMessages(parsedMessages);
                } else {
                    setMessages([]);
                }
            });
            return () => unsubscribe();
        }
    }, [roomId]);

    const sendMessage = (e) => {
        e.preventDefault();
        if (newMessage.trim() === "") return;

        const messagesRef = ref(database, `messages/${roomId}`);
        const newMsgRef = push(messagesRef);
        
        set(newMsgRef, {
            senderId: user.id,
            senderName: user.username,
            text: newMessage.trim(),
            timestamp: Date.now()
        });
        
        setNewMessage("");
    };

    const formatTime = (timestamp) => {
        if (!timestamp) return "";
        const date = new Date(timestamp);
        return date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    };


    if (!user || !roomId) {
        return <Navigate to="/" replace />;
    }

    return (
        <Container className="mt-4 mb-5 d-flex justify-content-center">
            <Card className="shadow border-0 w-100" style={{ maxWidth: '800px', height: '75vh' }}>
                {/* Header */}
                <Card.Header className="bg-primary text-white d-flex align-items-center justify-content-between py-3">
                    <div className="d-flex align-items-center">
                        <Button variant="link" className="text-white p-0 me-3" onClick={() => nav(-1)}>
                            <i className="bi bi-arrow-left fs-5"></i>
                        </Button>
                        <h5 className="mb-0 fw-bold">
                            <i className="bi bi-person-circle me-2"></i> 
                            Trò chuyện với {targetName}
                        </h5>
                    </div>
                </Card.Header>
                

                <Card.Body className="d-flex flex-column" style={{ backgroundColor: '#f8f9fa', overflowY: 'auto' }}>
                    {messages.length === 0 ? (
                        <div className="text-center text-muted my-auto">
                            <i className="bi bi-chat-dots display-4 opacity-50"></i>
                            <p className="mt-3">Hãy gửi lời chào đến {targetName}!</p>
                        </div>
                    ) : (
                        messages.map((msg) => {
                            const isMe = msg.senderId === user.id;
                            return (
                                <div key={msg.id} className={`d-flex flex-column mb-3 ${isMe ? 'align-items-end' : 'align-items-start'}`}>
                                    <div 
                                        className={`px-3 py-2 rounded-3 shadow-sm`} 
                                        style={{ 
                                            maxWidth: '75%', 
                                            backgroundColor: isMe ? 'var(--primary-dark-blue)' : '#ffffff',
                                            color: isMe ? '#ffffff' : '#333333',
                                            borderBottomRightRadius: isMe ? '4px' : '0.5rem',
                                            borderBottomLeftRadius: !isMe ? '4px' : '0.5rem'
                                        }}
                                    >
                                        <div style={{ wordWrap: 'break-word' }}>{msg.text}</div>
                                        <div className="mt-1" style={{ fontSize: '0.7rem', opacity: 0.7, textAlign: isMe ? 'right' : 'left' }}>
                                            {formatTime(msg.timestamp)}
                                        </div>
                                    </div>
                                </div>
                            );
                        })
                    )}
                </Card.Body>
                
                <Card.Footer className="bg-white p-3 border-top-0">
                    <Form onSubmit={sendMessage} className="m-0">
                        <InputGroup size="lg">
                            <Form.Control
                                type="text"
                                placeholder="Soạn tin nhắn..."
                                value={newMessage}
                                onChange={(e) => setNewMessage(e.target.value)}
                                className="border-secondary bg-light"
                                autoFocus
                            />
                            <Button type="submit" variant="primary" className="px-4" disabled={!newMessage.trim()}>
                                <i className="bi bi-send-fill"></i>
                            </Button>
                        </InputGroup>
                    </Form>
                </Card.Footer>
            </Card>
        </Container>
    );
};

export default ChatRoom;