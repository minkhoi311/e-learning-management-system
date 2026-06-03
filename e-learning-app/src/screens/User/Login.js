import { useContext, useState } from "react";
import { Alert, Button, Form, Container, Card } from "react-bootstrap";
import { useNavigate, useSearchParams, Link } from "react-router-dom";
import cookies from 'react-cookies';
import Apis, { authApis, endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Contexts";
import MySpinner from "../../components/MySpinner";

const Login = () => {
    const userInfo = [
        { field: "username", title: "Tên đăng nhập", type: "text" }, 
        { field: "password", title: "Mật khẩu", type: "password" }
    ];

    const [user, setUser] = useState({});
    const [err, setErr] = useState();
    const [loading, setLoading] = useState(false);
    const nav = useNavigate();
    const [, dispatch] = useContext(MyUserContext);
    const [q] = useSearchParams();

    const validate = () => {
        for (let u of userInfo) {
            if (!(u.field in user) || !user[u.field]) {
                setErr(`Vui lòng nhập ${u.title}!`);
                return false;
            }
        }
        return true;
    }

    const login = async (e) => {
        e.preventDefault();

        if (validate() === true) {
            setErr("");
            try {
                setLoading(true);
                
                let res = await Apis.post(endpoints['login'], {...user});
                cookies.save('token', res.data.token, { path: '/' });
                
                let u = await authApis().get(endpoints['profile']);
                
                cookies.save('user', u.data, { path: '/' });
                

                dispatch({"type": "LOGIN", "payload": u.data});


                let next = q.get('next');
                
                if (next) {
                    nav(next); 
                } else if (u.data.role === 'INSTRUCTOR') {
                    nav('/instructor'); 
                } else {
                    nav('/');
                }

            } catch (ex) {
                console.error(ex);
                setErr("Tên đăng nhập hoặc mật khẩu không chính xác!");
            } finally {
                setLoading(false);
            }
        }
    }


    const handleGoogleLogin = () => {
        alert("Đang chuẩn bị gọi API Google OAuth2...");
    };

    return (
        <Container className="d-flex justify-content-center align-items-center mt-5 mb-5">
            <Card className="shadow-sm border-0" style={{ width: '100%', maxWidth: '400px' }}>
                <Card.Body className="p-4 p-md-5">
                    <div className="text-center mb-4">
                        <h3 className="fw-bold" style={{ color: 'var(--primary-dark-blue)' }}>ĐĂNG NHẬP</h3>
                        <p className="text-muted small">Chào mừng bạn trở lại E-Learning!</p>
                    </div>

                    {err && <Alert variant="danger" className="small py-2 text-center">{err}</Alert>}

                    <Form onSubmit={login}>
                        {userInfo.map(u => (
                            <Form.Group key={u.field} className="mb-3" controlId={u.field}>
                                <Form.Label className="fw-bold small">{u.title}</Form.Label>
                                <Form.Control 
                                    type={u.type} 
                                    placeholder={`Nhập ${u.title.toLowerCase()}`} 
                                    value={user[u.field] || ''} 
                                    onChange={e => setUser({...user, [u.field]: e.target.value})} 
                                />
                            </Form.Group>
                        ))}
                        
                        <div className="d-grid mt-4 mb-3">
                            {loading ? (
                                <div className="text-center"><MySpinner /></div>
                            ) : (
                                <Button variant="primary" type="submit" size="lg">
                                    Đăng nhập
                                </Button>
                            )}
                        </div>
                    </Form>

                    {/* Vạch ngăn cách */}
                    <div className="d-flex align-items-center my-4">
                        <hr className="flex-grow-1 text-muted" />
                        <span className="mx-2 text-muted small">HOẶC</span>
                        <hr className="flex-grow-1 text-muted" />
                    </div>

                    <div className="d-grid">
                        <Button variant="outline-danger" onClick={handleGoogleLogin} className="d-flex align-items-center justify-content-center">
                            <i className="bi bi-google me-2"></i> Tiếp tục với Google
                        </Button>
                    </div>

                    <div className="text-center mt-4 small">
                        Chưa có tài khoản? <Link to="/register" className="text-decoration-none fw-bold">Đăng ký ngay</Link>
                    </div>
                </Card.Body>
            </Card>
        </Container>
    );
}

export default Login;