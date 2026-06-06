import React, { useState, useEffect, useContext, useCallback } from 'react';
import { Container, Row, Col, Card, Button, Alert, ProgressBar } from 'react-bootstrap';
import { useNavigate, Link, useSearchParams } from 'react-router-dom';
import { authApis, endpoints } from '../../configs/Apis';
import { MyUserContext } from '../../configs/Contexts';
import MySpinner from '../../components/MySpinner';

const MyEnrollments = () => {
    const [enrollments, setEnrollments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
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
            let res = await authApis().get(endpoints['my-enrollments']);
            const successfulEnrollments = Array.isArray(res.data) 
                ? res.data.filter(e => e.payment && e.payment.status === 'SUCCESS')
                : [];
                
            setEnrollments(successfulEnrollments); 
            
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


    const startChatWithInstructor = async (instructor) => {
        if (!instructor || !instructor.id) {
            alert("Không tìm thấy thông tin giảng viên!");
            return;
        }

        try {
            let res = await authApis().post(endpoints['get-chat-room'], {
                "target_id": instructor.id
            });

            if (res.status === 200 && res.data) {
                nav('/chat', {
                    state: {
                        roomId: res.data.firebaseRoom,
                        targetName: `GV. ${instructor.lastName} ${instructor.firstName}`
                    }
                });
            }
        } catch (err) {
            console.error("Lỗi khi tạo phòng chat:", err);
            alert("Không thể kết nối đến hộp thoại của giảng viên lúc này!");
        }
    };

    if (loading) return <Container className="text-center mt-5"><MySpinner /></Container>;

    return (
        <Container className="mt-4 mb-5">
            <h2 className="text-primary mb-4" style={{ color: 'var(--primary-dark-blue)' }}>
                <i className="bi bi-mortarboard-fill me-2"></i> 
                Khóa học của tôi
            </h2>

            {paymentMsg && (
                <Alert variant={paymentMsg.type} className="fw-bold shadow-sm" onClose={() => setPaymentMsg(null)} dismissible>
                    {paymentMsg.text}
                </Alert>
            )}

            {error && <Alert variant="danger">{error}</Alert>}

            {!loading && enrollments.length === 0 && !error && (
                <Alert variant="info" className="text-center p-5 shadow-sm">
                    <i className="bi bi-journal-x display-1 text-muted mb-3"></i>
                    <h4>Bạn chưa có khóa học nào được kích hoạt!</h4>
                    <p className="text-muted">Bạn chưa đăng ký hoặc chưa hoàn tất thanh toán khóa học nào. Hãy tìm kiếm và mua khóa học để bắt đầu.</p>
                    <Button variant="primary" className="mt-2 rounded-pill px-4" onClick={() => nav('/courses')}>
                        Xem danh sách khóa học
                    </Button>
                </Alert>
            )}

            <Row className="g-4">
                {enrollments.map(e => {
                    if (!e || !e.courseId) return null; 

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
                                            <span className="fw-bold text-success">{Math.round(e.progressPercent || 0)}%</span>
                                        </div>
                                        <ProgressBar 
                                            variant="success" 
                                            now={e.progressPercent || 0} 
                                            style={{ height: '8px' }} 
                                        />
                                    </div>
                                    
                                    <div className="text-muted small mb-3">
                                        <i className="bi bi-calendar-check me-1"></i>
                                        Đăng ký: {e.enrolledTime ? new Date(e.enrolledTime).toLocaleDateString('vi-VN') : 'N/A'}
                                    </div>

                                    <div className="d-grid mt-auto gap-2">
                                        <Button variant="primary" onClick={() => nav(`/courses/${e.courseId.id}`)}>
                                            <i className="bi bi-play-circle me-1"></i> Vào học tiếp
                                        </Button>
                                        
                                        {/* Nút gọi hàm mở Chat mới */}
                                        <Button 
                                            variant="outline-info" 
                                            onClick={() => startChatWithInstructor(e.courseId.instructorId)}
                                        >
                                            <i className="bi bi-chat-dots me-1"></i> Nhắn tin Giảng viên
                                        </Button>
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