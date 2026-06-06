import React, { useContext, useEffect, useState } from 'react';
import { Container, Row, Col, Card, Alert, Nav } from 'react-bootstrap';
import { useNavigate, Link } from 'react-router-dom';
import { authApis, endpoints } from '../../configs/Apis';
import { MyUserContext } from '../../configs/Contexts';
import MySpinner from '../../components/MySpinner';

const InstructorDashboard = () => {
    const [user] = useContext(MyUserContext);
    const nav = useNavigate();
    
    const [stats, setStats] = useState({});
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {

        if (!user || user.role !== 'INSTRUCTOR') {
            nav('/');
            return;
        }

        const fetchStats = async () => {
            try {
                setLoading(true);
                let res = await authApis().get(endpoints['instructor-stats']);

                setStats(res.data || {});
            } catch (err) {
                console.error("Lỗi khi tải thống kê: ", err);
                setError("Chưa thể tải dữ liệu thống kê lúc này. Vui lòng thử lại sau.");
            } finally {
                setLoading(false);
            }
        };

        fetchStats();
    }, [user, nav]);

    if (loading) return <Container className="mt-5 text-center"><MySpinner /></Container>;

    return (
        <Container className="mt-4 mb-5">
            <h2 className="text-primary mb-4 fw-bold" style={{ color: 'var(--primary-dark-blue)' }}>
                <i className="bi bi-speedometer2 me-2"></i> Dashboard Giảng Viên
            </h2>
            
            {/* Lời chào */}
            <Alert variant="info" className="shadow-sm border-0 border-start border-info border-5">
                <h5 className="mb-1">Chào mừng trở lại, Giảng viên <strong>{user.username}</strong>!</h5>
                <p className="mb-0 text-muted small">Dưới đây là tổng quan về hoạt động giảng dạy của bạn trên hệ thống E-Learning.</p>
            </Alert>

            {error && <Alert variant="danger">{error}</Alert>}


            <Row className="g-4 mb-5 mt-2">
                <Col md={4}>
                    <Card className="shadow-sm border-0 text-center text-white bg-primary h-100" style={{ borderRadius: '15px' }}>
                        <Card.Body className="py-4">
                            <i className="bi bi-journal-bookmark display-4 mb-3 opacity-75"></i>
                            <h2 className="fw-bold">{stats.totalCourses || 0}</h2>
                            <h6 className="text-uppercase tracking-wide opacity-75">Khóa học của tôi</h6>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card className="shadow-sm border-0 text-center text-white bg-success h-100" style={{ borderRadius: '15px' }}>
                        <Card.Body className="py-4">
                            <i className="bi bi-people display-4 mb-3 opacity-75"></i>
                            <h2 className="fw-bold">{stats.totalEnrollments || 0}</h2>
                            <h6 className="text-uppercase tracking-wide opacity-75">Học viên theo học</h6>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card className="shadow-sm border-0 text-center text-white bg-warning h-100" style={{ borderRadius: '15px' }}>
                        <Card.Body className="py-4">
                            <i className="bi bi-cash-coin display-4 mb-3 opacity-75"></i>
                            <h2 className="fw-bold text-dark">
                                {stats.totalRevenue ? stats.totalRevenue.toLocaleString("vi-VN") : 0} đ
                            </h2>
                            <h6 className="text-uppercase tracking-wide text-dark opacity-75">Doanh thu</h6>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default InstructorDashboard;