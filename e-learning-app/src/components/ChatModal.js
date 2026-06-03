import React, { useState, useEffect, useContext, useRef } from 'react';
import { Modal, Form, Button, ListGroup } from 'react-bootstrap';
import { database } from '../configs/firebase'; // File chứa cục firebaseConfig của bạn
import { ref, push, onValue, serverTimestamp } from 'firebase/database';
import { MyUserContext } from '../configs/Contexts';

const ChatModal = ({ show, handleClose, roomId, targetName }) => {
    const [user] = useContext(MyUserContext);
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState("");
    const messagesEndRef = useRef(null);

    // 1. Tự động tải và lắng nghe tin nhắn từ Firebase
    useEffect(() => {
        if (!roomId || !show) return;

        const chatRef = ref(database, `chats/${roomId}/messages`);
        const unsubscribe = onValue(chatRef, (snapshot) => {
            const data = snapshot.val();
            if (data) {
                // Biến object thành array và sắp xếp theo thời gian
                const loadedMessages = Object.entries(data)
                    .map(([key, val]) => ({ id: key, ...val }))
                    .sort((a, b) => a.timestamp - b.timestamp);
                setMessages(loadedMessages);
            } else {
                setMessages([]);
            }
        });

        return () => unsubscribe();
    }, [roomId, show]);

    // 2. Tự động cuộn xuống tin nhắn mới nhất
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    // 3. Hàm gửi tin nhắn
    const sendMessage = async (e) => {
        e.preventDefault();
        if (newMessage.trim() === "") return;

        const chatRef = ref(database, `chats/${roomId}/messages`);
        await push(chatRef, {
            text: newMessage,
            senderId: user.id,
            senderName: user.username,
            timestamp: serverTimestamp()
        });
        setNewMessage("");
    };

    return (
        <Modal show={show} onHide={handleClose} centered>
            <Modal.Header closeButton className="bg-primary text-white">
                <Modal.Title className="fs-5">
                    <i className="bi bi-chat-dots-fill me-2"></i> Chat với {targetName}
                </Modal.Title>
            </Modal.Header>

            <Modal.Body className="bg-light" style={{ height: '400px', overflowY: 'auto' }}>
                <ListGroup variant="flush">
                    {messages.length === 0 ? (
                        <div className="text-center text-muted mt-5 small">Hãy gửi lời chào đầu tiên!</div>
                    ) : (
                        messages.map((msg) => (
                            <ListGroup.Item 
                                key={msg.id} 
                                className={`border-0 bg-transparent d-flex flex-column ${msg.senderId === user.id ? 'align-items-end' : 'align-items-start'}`}
                            >
                                <small className="text-muted mb-1" style={{ fontSize: '0.7rem' }}>{msg.senderName}</small>
                                <div 
                                    className={`p-2 rounded-3 text-white shadow-sm ${msg.senderId === user.id ? 'bg-primary' : 'bg-secondary'}`}
                                    style={{ maxWidth: '80%', wordWrap: 'break-word' }}
                                >
                                    {msg.text}
                                </div>
                            </ListGroup.Item>
                        ))
                    )}
                    <div ref={messagesEndRef} />
                </ListGroup>
            </Modal.Body>

            <Modal.Footer className="p-2 bg-white">
                <Form onSubmit={sendMessage} className="d-flex w-100 gap-2 mb-0">
                    <Form.Control 
                        type="text" 
                        placeholder="Nhập tin nhắn..." 
                        value={newMessage}
                        onChange={(e) => setNewMessage(e.target.value)}
                        autoComplete="off"
                    />
                    <Button variant="primary" type="submit" disabled={!newMessage.trim()}>
                        <i className="bi bi-send-fill"></i>
                    </Button>
                </Form>
            </Modal.Footer>
        </Modal>
    );
};

export default ChatModal;