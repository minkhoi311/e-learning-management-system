import { useContext, useState, useEffect } from "react";
import { Alert, Button, Card, Col, Container, Row, Table, Form } from "react-bootstrap";
import cookies from "react-cookies";
import { Link, useNavigate } from "react-router-dom";
import { MyCartContext, MyUserContext } from "../../configs/Contexts";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../../components/MySpinner";


const Cart = () => {
    const [, cartDispatch] = useContext(MyCartContext);
    const [user] = useContext(MyUserContext);
    
    const cartCookieName = user ? `cart_${user.username}` : '';

    const [cart, setCart] = useState({});
    const nav = useNavigate();
    const [loading, setLoading] = useState(false);
    const [paymentMethod, setPaymentMethod] = useState("CASH");

    useEffect(() => {
        if (!user) {
            nav('/login?next=/cart');
            return;
        }

        setCart(cookies.load(cartCookieName) || {});
    }, [user, cartCookieName, nav]);

    const deleteItem = (item) => {
        if (window.confirm(`Bạn chắc chắn muốn xóa "${item.subject}" khỏi giỏ hàng?`)) {
            let c = { ...cart };
            delete c[item.id];
            setCart(c);
            
            cookies.save(cartCookieName, c, { path: '/', maxAge: 7 * 24 * 60 * 60 });
            cartDispatch({ 
                type: 'UPDATE', 
                payload: c
            });
        }
    };

    const pay = async () => {
        if (!user) {
            alert("Vui lòng đăng nhập để thanh toán!");
            nav('/login?next=/cart');
            return;
        }
        if (Object.keys(cart).length === 0) {
            alert("Giỏ hàng đang trống!");
            return;
        }

        setLoading(true);
        try {
            for (let c of Object.values(cart)) {
                let enrollRes = await authApis().post(endpoints['enroll'](c.id));
                
                if (enrollRes.status === 201) {
                    let newEnrollmentId = enrollRes.data.id;
                    let payRes = await authApis().post(endpoints['pay'](newEnrollmentId), {
                        "method": paymentMethod
                    });

                    if (paymentMethod === "MOMO" && payRes.status === 200 && payRes.data.payment_url) {
                        cookies.remove(cartCookieName, { path: '/' });
                        cartDispatch({ type: 'PAID', payload: 0 });
                        setCart({});
                        window.location.href = payRes.data.payment_url;
                        return; 
                    }
                }
            }

            if (paymentMethod === "CASH") {
                cookies.remove(cartCookieName, { path: '/' });
                cartDispatch({ type: 'PAID', payload: 0 });
                setCart({});
                alert("Đăng ký thành công! Vui lòng đến trung tâm thanh toán tiền mặt để kích hoạt khóa học.");
                nav('/my-enrollments');
            }
            
        } catch (ex) {
            console.error(ex);
            alert("Có lỗi xảy ra! Có thể bạn đã ghi danh khóa học này từ trước.");
        } finally {
            setLoading(false);
        }
    };

    const totalAmount = Object.values(cart).reduce((sum, item) => sum + (item.price * item.quantity), 0);

    if (Object.keys(cart).length === 0) {
        return (
            <Container className="mt-5 text-center mb-5">
                <Alert variant="info" className="p-5 shadow-sm">
                    <i className="bi bi-cart-x display-1 text-muted mb-3"></i>
                    <h4>Giỏ hàng của bạn đang trống!</h4>
                    <p className="text-muted">Hãy tìm thêm các khóa học hấp dẫn để nâng cao kỹ năng nhé.</p>
                    <Link to="/courses" className="btn btn-primary mt-3 px-4 rounded-pill">
                        Khám phá khóa học ngay
                    </Link>
                </Alert>
            </Container>
        );
    }

    return (
        <Container className="mt-4 mb-5">
            <h2 className="text-primary mb-4" style={{ color: 'var(--primary-dark-blue)' }}>
                <i className="bi bi-cart3 me-2"></i> Giỏ hàng của bạn
            </h2>

            <Row className="g-4">
                <Col lg={8}>
                    <div className="table-responsive shadow-sm bg-white rounded border-0">
                        <Table hover className="mb-0 align-middle text-center">
                            <thead className="table-light">
                                <tr>
                                    <th width="15%">Ảnh</th>
                                    <th width="45%" className="text-start">Tên khóa học</th>
                                    <th width="20%">Học phí</th>
                                    <th width="20%">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                {Object.values(cart).map(c => (
                                    <tr key={c.id}>
                                        <td>
                                            <img 
                                                src={c.image || 'https://via.placeholder.com/150x100?text=Course'} 
                                                alt={c.subject} 
                                                className="img-fluid rounded border" 
                                                style={{ width: '80px', height: '50px', objectFit: 'cover' }} 
                                            />
                                        </td>
                                        <td className="text-start fw-bold text-primary">{c.subject}</td>
                                        <td className="text-success fw-bold">
                                            {c.price === 0 ? "Miễn phí" : `${c.price.toLocaleString("vi-VN")} đ`}
                                        </td>
                                        <td>
                                            <Button variant="outline-danger" size="sm" onClick={() => deleteItem(c)}>
                                                <i className="bi bi-trash me-1"></i> Xóa
                                            </Button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    </div>
                </Col>

                <Col lg={4}>
                    <Card className="shadow-sm border-0">
                        <Card.Header className="bg-white fw-bold fs-5 text-primary border-bottom py-3">
                            <i className="bi bi-receipt me-2"></i> Tóm tắt đơn hàng
                        </Card.Header>
                        <Card.Body className="p-4">
                            <div className="d-flex justify-content-between mb-3">
                                <span className="text-muted">Tổng số lượng:</span>
                                <strong>{Object.keys(cart).length} khóa học</strong>
                            </div>
                            <div className="d-flex justify-content-between mb-4">
                                <span className="text-muted">Tổng thanh toán:</span>
                                <h4 className="text-success fw-bold mb-0">
                                    {totalAmount === 0 ? "0 đ" : `${totalAmount.toLocaleString("vi-VN")} đ`}
                                </h4>
                            </div>
                            
                            <hr className="mb-3" />
                            
                            <h6 className="fw-bold mb-3">Phương thức thanh toán:</h6>
                            <Form>
                                <Form.Check 
                                    type="radio"
                                    id="pay-cash"
                                    label="Thanh toán Tiền mặt tại Trung tâm"
                                    name="paymentMethod"
                                    value="CASH"
                                    checked={paymentMethod === "CASH"}
                                    onChange={(e) => setPaymentMethod(e.target.value)}
                                    className="mb-2"
                                />
                                <Form.Check 
                                    type="radio"
                                    id="pay-momo"
                                    label="Thanh toán qua Ví MoMo"
                                    name="paymentMethod"
                                    value="MOMO"
                                    checked={paymentMethod === "MOMO"}
                                    onChange={(e) => setPaymentMethod(e.target.value)}
                                    className="mb-4"
                                />
                            </Form>
                            
                            <div className="d-grid">
                                {loading ? (
                                    <div className="text-center"><MySpinner /></div>
                                ) : (
                                    <Button variant="success" size="lg" onClick={pay}>
                                        <i className="bi bi-credit-card me-2"></i> Tiến hành thanh toán
                                    </Button>
                                )}
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Cart;