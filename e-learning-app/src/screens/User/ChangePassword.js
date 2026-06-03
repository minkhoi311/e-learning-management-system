import React, { useState } from 'react';
import { Container, Card, Form, Button, Alert } from 'react-bootstrap';
import { authApis, endpoints } from '../../configs/Apis';
import { useNavigate } from 'react-router-dom';
import MySpinner from '../../components/MySpinner';

const ChangePassword = () => {
    const [passwords, setPasswords] = useState({
        old_password: '', 
        new_password: '', 
        confirm_password: ''
    });
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState(null);
    const nav = useNavigate();

    const changePassword = async (e) => {
        e.preventDefault();
        if (passwords.new_password !== passwords.confirm_password) {
            setMsg({ type: 'danger', text: 'Mật khẩu xác nhận KHÔNG khớp!' });
            return;
        }
        if (passwords.new_password.length < 3) {
            setMsg({ type: 'warning', text: 'Mật khẩu mới phải có ít nhất 3 ký tự!' });
            return;
        }

        setLoading(true);
        setMsg(null);
        
        try {
            let res = await authApis().post(endpoints['change-password'], {
                old_password: passwords.old_password,
                new_password: passwords.new_password
            });
            
            if (res.status === 200) {
                setMsg({ type: 'success', text: 'Đổi mật khẩu thành công!' });
                setPasswords({ old_password: '', new_password: '', confirm_password: '' });
                setTimeout(() => nav('/profile'), 2000);
            }
        } catch (ex) {
            console.error(ex);
            setMsg({ 
                type: 'danger', 
                text: 'Mật khẩu cũ không đúng hoặc có lỗi xảy ra!' 
            });
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="mt-4 mb-5 d-flex justify-content-center">
            <Card className="shadow-sm border-0 w-100" style={{ maxWidth: '500px' }}>
                <Card.Header className="bg-white text-danger border-bottom py-3">
                    <h4 className="mb-0 fw-bold"><i className="bi bi-shield-lock me-2"></i> ĐỔI MẬT KHẨU</h4>
                </Card.Header>
                <Card.Body className="p-4">
                    {msg && <Alert variant={msg.type}>{msg.text}</Alert>}

                    <Form onSubmit={changePassword}>
                        <Form.Group className="mb-3" controlId="old_password">
                            <Form.Label className="fw-bold small">Mật khẩu hiện tại</Form.Label>
                            <Form.Control 
                                type="password" 
                                value={passwords.old_password} 
                                onChange={e => setPasswords({...passwords, old_password: e.target.value})} 
                                required 
                            />
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="new_password">
                            <Form.Label className="fw-bold small">Mật khẩu mới</Form.Label>
                            <Form.Control 
                                type="password" 
                                value={passwords.new_password} 
                                onChange={e => setPasswords({...passwords, new_password: e.target.value})} 
                                required 
                            />
                        </Form.Group>

                        <Form.Group className="mb-4" controlId="confirm_password">
                            <Form.Label className="fw-bold small">Xác nhận mật khẩu mới</Form.Label>
                            <Form.Control 
                                type="password" 
                                value={passwords.confirm_password} 
                                onChange={e => setPasswords({...passwords, confirm_password: e.target.value})} 
                                required 
                            />
                        </Form.Group>

                        <div className="d-grid gap-2">
                            {loading ? <MySpinner /> : (
                                <>
                                    <Button variant="danger" type="submit" size="lg">Xác nhận đổi</Button>
                                    <Button variant="light" onClick={() => nav('/profile')}>Hủy và quay lại</Button>
                                </>
                            )}
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default ChangePassword;