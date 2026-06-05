import React, { useEffect, useState, useContext } from 'react';
import { Container, ListGroup, Badge, Button, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { authApis, endpoints } from '../../configs/Apis';
import { MyUserContext } from '../../configs/Contexts';
import MySpinner from '../../components/MySpinner';

const InstructorChats = () => {
    const [sessions, setSessions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [user] = useContext(MyUserContext);
    const nav = useNavigate();

    useEffect(() => {
        if (!user || user.role !== 'INSTRUCTOR') {
            nav('/');
            return;
        }

        const loadSessions = async () => {
            try {
                let res = await authApis().get(endpoints['my-chat-sessions']);
                setSessions(res.data || []);
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        loadSessions();
    }, [user, nav]);

    if (loading) return <Container className="mt-5 text-center"><MySpinner /></Container>;

    return (
        <Container className="mt-4 mb-5" style={{ maxWidth: '700px' }}>
            <h3 className="fw-bold text-primary mb-4">
                <i className="bi bi-chat-dots-fill me-2"></i> Hộp thư nhắn tin
            </h3>

            {sessions.length === 0 ? (
                <Alert variant="info">Chưa có học viên nào nhắn tin với bạn.</Alert>
            ) : (
                <ListGroup className="shadow-sm">
                    {sessions.map(s => (
                        <ListGroup.Item
                            key={s.id}
                            className="d-flex justify-content-between align-items-center p-3 hover-lift"
                        >
                            <div className="d-flex align-items-center gap-3">
                                <img
                                    src={s.studentId?.avatar || 'https://via.placeholder.com/40'}
                                    alt="avatar"
                                    className="rounded-circle border"
                                    style={{ width: '45px', height: '45px', objectFit: 'cover' }}
                                />
                                <div>
                                    <div className="fw-bold">{s.studentId?.username}</div>
                                    <small className="text-muted">{s.studentId?.email}</small>
                                </div>
                            </div>

                            <Button
                                variant="primary"
                                size="sm"
                                onClick={() => nav('/chat', {
                                    state: {
                                        roomId: s.firebaseRoom,
                                        targetName: s.studentId?.username
                                    }
                                })}
                            >
                                <i className="bi bi-chat-fill me-1"></i> Mở chat
                            </Button>
                        </ListGroup.Item>
                    ))}
                </ListGroup>
            )}
        </Container>
    );
};

export default InstructorChats;