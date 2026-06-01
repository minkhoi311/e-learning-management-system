import React, { useState, useEffect, useContext } from 'react';
import { Container, Row, Col, Card, Button, Alert, ListGroup, Badge } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import cookies from 'react-cookies';
import Apis, { endpoints } from '../../configs/Apis';
import { MyCartContext } from '../../configs/Contexts';
import MySpinner from '../../components/MySpinner';

const CourseDetail = () => {
    const [course, setCourse] = useState(null);
    const [lessons, setLessons] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const { courseId } = useParams(); 
    const nav = useNavigate();
    const [, cartDispatch] = useContext(MyCartContext);

    useEffect(() => {
        const loadCourseDetails = async () => {
            setLoading(true);
            try {
                let courseRes = await Apis.get(endpoints['course-details'](courseId));
                setCourse(courseRes.data);

                let lessonRes = await Apis.get(endpoints['lessons'](courseId));
                setLessons(lessonRes.data);
            } catch (err) {
                console.error("Lỗi khi tải chi tiết khóa học:", err);
                setError("Không thể tải dữ liệu. Khóa học có thể không tồn tại hoặc đã bị xóa.");
            } finally {
                setLoading(false);
            }
        };

        if (courseId) {
            loadCourseDetails();
        }
    }, [courseId]);

    const addToCart = () => {
        if (!course) return;

        let cart = cookies.load('cart') || {};
        if (course.id in cart) {
            alert("Khóa học này đã có trong giỏ hàng!");
            return;
        }

        cart[course.id] = {
            id: course.id,
            subject: course.subject,
            price: course.price,
            image: course.image,
            quantity: 1
        };

        cookies.save('cart', cart, { path: '/', maxAge: 7 * 24 * 60 * 60 });
        cartDispatch({ type: 'UPDATE' });
        alert("Đã thêm khóa học vào giỏ hàng thành công!");
    };

    if (loading) return <Container className="text-center mt-5"><MySpinner /></Container>;

    if (error || !course) {
        return (
            <Container className="mt-5">
                <Alert variant="danger" className="text-center">
                    <h4>{error || "Khóa học không tồn tại!"}</h4>
                    <Button variant="primary" className="mt-3" onClick={() => nav('/courses')}>
                        Quay lại danh sách
                    </Button>
                </Alert>
            </Container>
        );
    }

    return (
        <Container className="mt-4 mb-5">
            {/* 1. Phần Thông tin Khóa học (Giữ nguyên) */}
            <Card className="shadow-sm border-0 mb-5">
                <Row className="g-0">
                    <Col md={5}>
                        <Card.Img 
                            src={course.image || 'https://via.placeholder.com/500x300?text=No+Image'} 
                            alt={course.subject}
                            style={{ height: '100%', minHeight: '300px', objectFit: 'cover' }}
                            className="rounded-start"
                        />
                    </Col>
                    <Col md={7}>
                        <Card.Body className="d-flex flex-column h-100 p-4">
                            <Card.Title className="fs-3 fw-bold text-primary mb-3">
                                {course.subject}
                            </Card.Title>
                            
                            <div className="d-flex gap-3 mb-3 text-muted">
                                <span><i className="bi bi-person-video3 me-1"></i> Giảng viên: <strong>{course.instructorId?.lastName} {course.instructorId?.firstName}</strong></span>
                                <span><i className="bi bi-tags me-1"></i> {course.categoryId?.name}</span>
                                <span><i className="bi bi-clock me-1"></i> {course.durationHours ? `${course.durationHours} giờ` : 'N/A'}</span>
                            </div>

                            <Card.Text className="text-muted lh-lg flex-grow-1">
                                {course.description || "Khóa học này chưa có mô tả chi tiết."}
                            </Card.Text>

                            <div className="mt-auto pt-3 border-top d-flex justify-content-between align-items-center">
                                <h3 className="text-success mb-0 fw-bold">
                                    {course.price === 0 ? "MIỄN PHÍ" : `${course.price.toLocaleString("vi-VN")} VNĐ`}
                                </h3>
                                <div className="d-flex gap-2">
                                    <Button variant="outline-secondary" onClick={() => nav('/courses')}>
                                        Trở về
                                    </Button>
                                    <Button variant="primary" size="lg" onClick={addToCart}>
                                        <i className="bi bi-cart-plus me-2"></i>Thêm vào giỏ
                                    </Button>
                                </div>
                            </div>
                        </Card.Body>
                    </Col>
                </Row>
            </Card>

            {/* 2. Phần Danh sách Bài học - Đã sửa thành List ngang */}
            <h4 className="text-primary mb-3" style={{ color: 'var(--primary-dark-blue)' }}>
                <i className="bi bi-list-ul me-2"></i>
                Nội dung khóa học ({lessons.length} bài học)
            </h4>

            {lessons.length === 0 ? (
                <Alert variant="info">Khóa học này hiện chưa có bài học nào được cập nhật.</Alert>
            ) : (
                <ListGroup variant="flush" className="shadow-sm rounded border">
                    {lessons.map((lesson, index) => (
                        <ListGroup.Item key={lesson.id} className="p-3 hover-lift border-bottom">
                            <Row className="align-items-center">
                                {/* Cột Ảnh */}
                                <Col xs={4} md={2} className="text-center">
                                    <img 
                                        src={lesson.image || 'https://via.placeholder.com/150x100?text=Lesson'} 
                                        alt={lesson.subject} 
                                        className="img-fluid rounded" 
                                        style={{ height: '80px', width: '100%', objectFit: 'cover' }} 
                                    />
                                </Col>

                                {/* Cột Tiêu đề & Mô tả */}
                                <Col xs={8} md={8}>
                                    <div className="d-flex align-items-center mb-1">
                                        <Badge bg="secondary" className="me-2">Bài {index + 1}</Badge>
                                        <h6 className="mb-0 text-dark fw-bold">{lesson.subject}</h6>
                                    </div>
                                    <p className="text-muted small mb-0 text-clamp-3">
                                        {lesson.content || "Chưa có mô tả chi tiết cho bài học này."}
                                    </p>
                                </Col>

                                {/* Cột Nút Action */}
                                <Col xs={12} md={2} className="text-md-end text-center mt-3 mt-md-0">
                                    <Button 
                                        variant="outline-primary" 
                                        size="sm" 
                                        className="w-100"
                                        onClick={() => nav(`/lessons/${lesson.id}`, { state: { lesson: lesson } })}
                                    >
                                        <i className="bi bi-play-circle me-1"></i> Chi tiết
                                    </Button>
                                </Col>
                            </Row>
                        </ListGroup.Item>
                    ))}
                </ListGroup>
            )}
        </Container>
    );
};

export default CourseDetail;