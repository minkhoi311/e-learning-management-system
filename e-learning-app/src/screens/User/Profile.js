import React, { useState, useEffect, useContext, useRef } from 'react';
import { Container, Card, Form, Button, Row, Col, Alert, Image } from 'react-bootstrap';
import { MyUserContext } from '../../configs/Contexts';
import { authApis, endpoints } from '../../configs/Apis';
import { useNavigate } from 'react-router-dom';
import MySpinner from '../../components/MySpinner';

const Profile = () => {
    const [userContext, dispatch] = useContext(MyUserContext);
    const [profile, setProfile] = useState(null);
    const avatarRef = useRef();
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState(null);
    const nav = useNavigate();


    useEffect(() => {
        const loadProfile = async () => {
            if (!userContext) {
                nav('/login?next=/profile');
                return;
            }
            try {
                setLoading(true);
                let res = await authApis().get(endpoints['profile']);
                setProfile(res.data);
            } catch (ex) {
                console.error(ex);
                setMsg({ type: 'danger', text: 'Không thể tải thông tin. Vui lòng thử lại!' });
            } finally {
                setLoading(false);
            }
        };
        loadProfile();
    }, [userContext, nav]);


    const saveProfile = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMsg(null);
        
        try {
            let form = new FormData();
            form.append('firstName', profile.firstName);
            form.append('lastName', profile.lastName);
            form.append('email', profile.email);
            form.append('phone', profile.phone || '');
            
            if (avatarRef.current.files.length > 0) {
                form.append('file', avatarRef.current.files[0]);
            }

            let res = await authApis().patch(endpoints['profile'], form, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });

            if (res.status === 200) {
                dispatch({ type: 'LOGIN', payload: res.data });
                setProfile(res.data);
                setMsg({ type: 'success', text: 'Cập nhật thông tin thành công!' });
            }
        } catch (ex) {
            console.error(ex);
            setMsg({ type: 'danger', text: 'Cập nhật thất bại. Vui lòng kiểm tra lại!' });
        } finally {
            setLoading(false);
        }
    };

    if (!profile) return <Container className="mt-5 text-center"><MySpinner /></Container>;

    return (
        <Container className="mt-4 mb-5 d-flex justify-content-center">
            <Card className="shadow-sm border-0 w-100" style={{ maxWidth: '800px' }}>
                <Card.Header className="bg-white text-primary border-bottom py-3">
                    <h3 className="mb-0 fw-bold"><i className="bi bi-person-lines-fill me-2"></i> THÔNG TIN CÁ NHÂN</h3>
                </Card.Header>
                <Card.Body className="p-4">
                    {msg && <Alert variant={msg.type}>{msg.text}</Alert>}

                    <Form onSubmit={saveProfile}>
                        <Row>
                            <Col md={4} className="text-center mb-4">
                                <Image 
                                    src={profile.avatar || 'https://via.placeholder.com/150'} 
                                    roundedCircle 
                                    fluid 
                                    className="mb-3 border shadow-sm"
                                    style={{ width: '150px', height: '150px', objectFit: 'cover' }}
                                />
                                <Form.Group controlId="avatar">
                                    <Form.Control type="file" size="sm" accept="image/*" ref={avatarRef} />
                                    <Form.Text className="text-muted">Định dạng .jpg, .png</Form.Text>
                                </Form.Group>
                            </Col>

                            <Col md={8}>
                                <Row>
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="lastName">
                                            <Form.Label className="fw-bold small">Họ và chữ lót</Form.Label>
                                            <Form.Control 
                                                type="text" 
                                                value={profile.lastName} 
                                                onChange={e => setProfile({...profile, lastName: e.target.value})} 
                                                required 
                                            />
                                        </Form.Group>
                                    </Col>
                                    <Col md={6}>
                                        <Form.Group className="mb-3" controlId="firstName">
                                            <Form.Label className="fw-bold small">Tên</Form.Label>
                                            <Form.Control 
                                                type="text" 
                                                value={profile.firstName} 
                                                onChange={e => setProfile({...profile, firstName: e.target.value})} 
                                                required 
                                            />
                                        </Form.Group>
                                    </Col>
                                </Row>

                                <Form.Group className="mb-3" controlId="email">
                                    <Form.Label className="fw-bold small">Email</Form.Label>
                                    <Form.Control 
                                        type="email" 
                                        value={profile.email} 
                                        onChange={e => setProfile({...profile, email: e.target.value})} 
                                        required 
                                    />
                                </Form.Group>

                                <Form.Group className="mb-4" controlId="phone">
                                    <Form.Label className="fw-bold small">Số điện thoại</Form.Label>
                                    <Form.Control 
                                        type="tel" 
                                        value={profile.phone || ''} 
                                        onChange={e => setProfile({...profile, phone: e.target.value})} 
                                    />
                                </Form.Group>

                                <div className="d-flex justify-content-between align-items-center">
                                    <Button variant="outline-danger" onClick={() => nav('/change-password')}>
                                        <i className="bi bi-shield-lock me-2"></i>Đổi mật khẩu
                                    </Button>
                                    
                                    {loading ? <MySpinner /> : (
                                        <Button variant="success" type="submit" className="px-4">
                                            <i className="bi bi-save me-2"></i>Lưu thay đổi
                                        </Button>
                                    )}
                                </div>
                            </Col>
                        </Row>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default Profile;