import React, { useState, useEffect, useContext, useCallback } from 'react';
import { Container, Row, Col, Card, Button, Alert, ProgressBar, Badge } from 'react-bootstrap';
import { useNavigate, Link, useSearchParams } from 'react-router-dom';
import { authApis, endpoints } from '../../configs/Apis';
import { MyUserContext } from '../../configs/Contexts';
import MySpinner from '../../components/MySpinner';

const MyEnrollments = () => {
    const [enrollments, setEnrollments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    // ĐÃ ĐỔI TÊN: Dùng tên chung cho tất cả các cổng thanh toán sau này
    const [paymentMsg, setPaymentMsg] = useState(null); 

    const [user] = useContext(MyUserContext);
    const nav = useNavigate();
    const [searchParams] = useSearchParams();

    const fetchEnrollments = useCallback(async () => {
        if (!user) {
            nav('/login?next=/my-enrollments');
            return;
        }

        setLoading(true);
        setError(null);
        try {
            // Gọi endpoint: GET /api/secure/enrollments
            let res = await authApis().get(endpoints['my-enrollments']);
            setEnrollments(Array.isArray(res.data) ? res.data : []); 
        } catch (err) {
            console.error("Lỗi khi lấy danh sách khóa học:", err);
            setError("Không thể tải danh sách khóa học. Vui lòng thử lại sau.");
            setEnrollments([]); 
        } finally {
            setLoading(false);
        }
    }, [user, nav]);

    useEffect(() => {
        const resultCode = searchParams.get('resultCode'); 

        if (resultCode !== null) {
            if (resultCode === '0') {
                setPaymentMsg({ 
                    type: 'success', 
                    text: '🎉 Giao dịch thành công! Khóa học của bạn đã được kích hoạt hệ thống.' 
                });
            } else {
                setPaymentMsg({ 
                    type: 'danger', 
                    text: '⚠️ Thanh toán thất bại hoặc phiên giao dịch đã bị hủy.' 
                });
            }
            window.history.replaceState(null, '', '/my-enrollments');
        }

        fetchEnrollments();
    }, [fetchEnrollments, searchParams]);

    if (loading) return <Container className="text-center mt-5"><MySpinner /></Container>;

    return (
        <Container className="mt-4 mb-5">
            <h2 className="text-primary mb-4" style={{ color: 'var(--primary-dark-blue)' }}>
                <i className="bi bi-mortarboard-fill me-2"></i> 
                Khóa học của tôi
            </h2>

            {/* Hiển thị thông báo thanh toán tổng quát */}
            {paymentMsg && (
                <Alert variant={paymentMsg.type} className="fw-bold shadow-sm" onClose={() => setPaymentMsg(null)} dismissible>
                    {paymentMsg.text}
                </Alert>
            )}

            {error && <Alert variant="danger">{error}</Alert>}

            {!loading && enrollments.length === 0 && !error && (
                <Alert variant="info" className="text-center p-5 shadow-sm">
                    <i className="bi bi-journal-x display-1 text-muted mb-3"></i>
                    <h4>Bạn chưa đăng ký khóa học nào!</h4>
                    <p className="text-muted">Hãy tìm kiếm khóa học để bắt đầu lộ trình học tập của mình.</p>
                    <Button variant="primary" className="mt-2 rounded-pill px-4" onClick={() => nav('/courses')}>
                        Xem danh sách khóa học
                    </Button>
                </Alert>
            )}

            <Row className="g-4">
                {enrollments.map(e => {
                    if (!e || !e.courseId) return null; 
                    
                    // Khai báo an toàn thông tin hóa đơn từ bảng Payment đi kèm sang
                    const paymentInfo = e.payment; 

                    return (
                        <Col key={e.id} xs={12} sm={6} md={4} lg={3}>
                            <Card className="h-100 hover-lift shadow-sm border-0">
                                
                                <Link to={`/courses/${e.courseId.id}`} style={{ textDecoration: 'none' }}>
                                    <Card.Img 
                                        variant="top" 
                                        className="card-img-custom"
                                        src={e.courseId.image || 'https://via.placeholder.com/300x180?text=Course'} 
                                    />
                                </Link>

                                <Card.Body className="d-flex flex-column">
                                    <Link to={`/courses/${e.courseId.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
                                        <Card.Title className="text-truncate" title={e.courseId.subject}>
                                            {e.courseId.subject}
                                        </Card.Title>
                                    </Link>

                                    <div className="mt-2 mb-2 flex-grow-1">
                                        <div className="d-flex justify-content-between mb-1 small text-muted">
                                            <span>Tiến độ học</span>
                                            <span className="fw-bold text-success">{e.progressPercent || 0}%</span>
                                        </div>
                                        <ProgressBar 
                                            variant="success" 
                                            now={e.progressPercent || 0} 
                                            style={{ height: '8px' }} 
                                        />
                                    </div>

                                    {/* PHẦN HIỂN THỊ TRẠNG THÁI THANH TOÁN (HỢP NHẤT LOGIC MỚI) */}
                                    <div className="mb-3 d-flex justify-content-between align-items-center small">
                                        <span className="text-muted">Trạng thái:</span>
                                        {paymentInfo && paymentInfo.status === 'SUCCESS' ? (
                                            <Badge bg="success">Đã mở khóa</Badge>
                                        ) : paymentInfo && paymentInfo.paymentMethod === 'CASH' ? (
                                            <Badge bg="warning" text="dark">Chờ thu tiền mặt</Badge>
                                        ) : (
                                            <Badge bg="secondary">Chưa thanh toán</Badge>
                                        )}
                                    </div>
                                    
                                    <div className="text-muted small mb-3">
                                        <i className="bi bi-calendar-check me-1"></i>
                                        Đăng ký: {e.enrolledTime ? new Date(e.enrolledTime).toLocaleDateString('vi-VN') : 'N/A'}
                                    </div>

                                    <div className="d-grid mt-auto">
                                        {/* Kiểm tra đúng trường status của đối tượng Payment bên trong Enrollment */}
                                        {paymentInfo && paymentInfo.status === 'SUCCESS' ? (
                                            <Button variant="primary" onClick={() => nav(`/courses/${e.courseId.id}`)}>
                                                <i className="bi bi-play-circle me-1"></i> Vào học tiếp
                                            </Button>
                                        ) : (
                                            <Button variant="light" disabled className="text-muted border">
                                                <i className="bi bi-lock-fill me-1"></i> Khóa học đang đóng
                                            </Button>
                                        )}
                                    </div>
                                </Card.Body>
                            </Card>
                        </Col>
                    );
                })}
            </Row>
        </Container>
    );
};

export default MyEnrollments;