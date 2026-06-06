import { useRef, useState } from "react";
import { Alert, Button, Form, Container, Card, Row, Col } from "react-bootstrap";
import { useNavigate, Link } from "react-router-dom";
import MySpinner from "../../components/MySpinner";
import Apis, { endpoints } from "../../configs/Apis";

const Register = () => {
    const userInfo = [
        { field: "lastName", title: "Họ và chữ lót", type: "text" },
        { field: "firstName", title: "Tên", type: "text" },
        { field: "email", title: "Email", type: "email" },
        { field: "phone", title: "Số điện thoại", type: "tel" },
        { field: "username", title: "Tên đăng nhập", type: "text" },
        { field: "password", title: "Mật khẩu", type: "password" },
        { field: "confirm", title: "Xác nhận mật khẩu", type: "password" }
    ];

    const [user, setUser] = useState({});
    const [err, setErr] = useState();
    const avatar = useRef();
    const [loading, setLoading] = useState(false);
    const nav = useNavigate();

    const validate = () => {
        for (let u of userInfo) {
            if (!(u.field in user) || !user[u.field]) {
                setErr(`Vui lòng nhập ${u.title}!`);
                return false;
            }
        }

        if (user.password !== user.confirm) {
            setErr('Mật khẩu xác nhận KHÔNG khớp!');
            return false;
        }
        return true;
    }

    const register = async (e) => {
        e.preventDefault();

        if (validate() === true) {
            setErr(""); 
            let form = new FormData();
            
            for (let key of Object.keys(user)) {
                if (key !== 'confirm') {
                    form.append(key, user[key]);
                }
            }


            if (avatar.current.files.length > 0) {
                form.append('avatar', avatar.current.files[0]);
            } else {
                setErr("Vui lòng chọn ảnh đại diện!");
                return;
            }

            try {
                setLoading(true);
                let res = await Apis.post(endpoints['register'], form, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });

                if (res.status === 201) {
                    alert("Đăng ký tài khoản thành công!");
                    nav('/login');
                } else {
                    setErr("Hệ thống bị lỗi! Vui lòng thử lại.");
                }
            } catch (ex) {
                console.error(ex);
                setErr("Tên đăng nhập hoặc Email đã tồn tại trong hệ thống!");
            } finally {
                setLoading(false);
            }
        }
    }

    return (
        <Container className="d-flex justify-content-center align-items-center mt-5 mb-5">
            <Card className="shadow-sm border-0" style={{ width: '100%', maxWidth: '700px' }}>
                <Card.Body className="p-4 p-md-5">
                    <div className="text-center mb-4">
                        <h3 className="fw-bold" style={{ color: 'var(--primary-dark-blue)' }}>ĐĂNG KÝ TÀI KHOẢN</h3>
                        <p className="text-muted small">Tạo tài khoản để tham gia các khóa học E-Learning</p>
                    </div>

                    {err && <Alert variant="danger" className="small py-2 text-center">{err}</Alert>}

                    <Form onSubmit={register}>
                        <Row>
                            {userInfo.map((u, index) => (
                                <Col md={(u.field === 'username') ? 12 : 6} key={u.field}>
                                    <Form.Group className="mb-3" controlId={u.field}>
                                        <Form.Label className="fw-bold small">{u.title} <span className="text-danger">*</span></Form.Label>
                                        <Form.Control 
                                            type={u.type} 
                                            placeholder={`Nhập ${u.title.toLowerCase()}`} 
                                            value={user[u.field] || ''} 
                                            onChange={e => setUser({...user, [u.field]: e.target.value})} 
                                        />
                                    </Form.Group>
                                </Col>
                            ))}
                        </Row>

                        <Form.Group className="mb-4" controlId="avatar">
                            <Form.Label className="fw-bold small">Ảnh đại diện <span className="text-danger">*</span></Form.Label>
                            <Form.Control type="file" accept="image/*" ref={avatar} />
                        </Form.Group>

                        <div className="d-grid mt-4 mb-3">
                            {loading ? (
                                <div className="text-center"><MySpinner /></div>
                            ) : (
                                <Button variant="success" type="submit" size="lg">
                                    Đăng ký ngay
                                </Button>
                            )}
                        </div>

                        <div className="text-center mt-4 small">
                            Đã có tài khoản? <Link to="/login" className="text-decoration-none fw-bold">Đăng nhập</Link>
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
}

export default Register;