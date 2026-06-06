import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const Footer = () => {
    return (

        <footer className="bg-forest-green pt-5 pb-3 mt-auto">
            <Container>
                <Row>
                    <Col md={4} className="mb-4">
                        <h5 className="text-white mb-3 fw-bold">
                            <i className="bi bi-book-half me-2"></i>E-Learning Nhóm 11
                        </h5>
                        <p className="small lh-lg">
                            Nền tảng học trực tuyến hàng đầu, cung cấp các khóa học chất lượng cao giúp bạn phát triển kỹ năng và thăng tiến trong sự nghiệp nhanh chóng.
                        </p>
                    </Col>
                    
                    <Col md={4} className="mb-4">
                        <h5 className="text-white mb-3 fw-bold">Liên kết nhanh</h5>
                        <ul className="list-unstyled small lh-lg">
                            <li className="mb-2">
                                <Link to="/" className="text-decoration-none text-light hover-opacity">Trang chủ</Link>
                            </li>
                            <li className="mb-2">
                                <Link to="/courses" className="text-decoration-none text-light hover-opacity">Khóa học</Link>
                            </li>
                            <li className="mb-2">
                                <Link to="/instructors" className="text-decoration-none text-light hover-opacity">Đội ngũ giảng viên</Link>
                            </li>
                            <li className="mb-2">
                                <Link to="/compare" className="text-decoration-none text-light hover-opacity">So sánh khóa học</Link>
                            </li>
                        </ul>
                    </Col>
                    
                    <Col md={4} className="mb-4">
                        <h5 className="text-white mb-3 fw-bold">Thông tin liên hệ</h5>
                        <ul className="list-unstyled small lh-lg">
                            <li className="mb-2">
                                <i className="bi bi-geo-alt me-2"></i> TP. Hồ Chí Minh, Việt Nam
                            </li>
                            <li className="mb-2">
                                <i className="bi bi-envelope me-2"></i> contact@elearning.edu.vn
                            </li>
                            <li className="mb-2">
                                <i className="bi bi-telephone me-2"></i> 0123 456 789
                            </li>
                        </ul>
                    </Col>
                </Row>
                
                <hr className="border-light opacity-25" />
                
                <Row>
                    <Col className="text-center small">
                        &copy; {new Date().getFullYear()} E-Learning Nhóm 11. Đã đăng ký bản quyền.
                    </Col>
                </Row>
            </Container>
        </footer>
    );
}

export default Footer;