import React from 'react';
import { Container, Row, Col, Card, Button, Badge } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const Home = () => {
    return (
        <div className="home-page">
            <section className="hero-section bg-white py-5 shadow-sm mb-5">
                <Container>
                    <Row className="align-items-center min-vh-50">
                        <Col lg={6} className="mb-4 mb-lg-0 pe-lg-5">
                            <Badge bg="info" className="text-dark mb-3 px-3 py-2 rounded-pill shadow-sm">
                                <i className="bi bi-star-fill text-warning me-1"></i> Nền tảng học tập trực tuyến
                            </Badge>
                            <h1 className="display-4 fw-bold mb-4" style={{ color: 'var(--primary-dark-blue)' }}>
                                Khơi dậy tiềm năng cùng <span className="text-primary">E-Learning</span>
                            </h1>
                            <p className="lead text-muted mb-4 lh-lg">
                                Nâng cao kiến thức và kỹ năng của bạn mọi lúc, mọi nơi với hệ thống khóa học đa dạng, 
                                được giảng dạy bởi đội ngũ chuyên gia giàu kinh nghiệm.
                            </p>
                            <div className="d-flex gap-3">
                                <Link to="/courses" className="btn btn-primary btn-lg rounded-pill px-4 shadow-sm">
                                    <i className="bi bi-search me-2"></i> Khám phá khóa học
                                </Link>
                            </div>
                        </Col>
                        <Col lg={6} className="text-center">
                            {/* Bạn có thể thay thế link ảnh này bằng ảnh thật trong dự án */}
                            <img 
                                src="https://images.unsplash.com/photo-1522202176988-66273c2fd55f?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80" 
                                alt="E-Learning Education" 
                                className="img-fluid rounded-4 shadow-lg"
                                style={{ objectFit: 'cover', height: '400px', width: '100%' }}
                            />
                        </Col>
                    </Row>
                </Container>
            </section>

            {/* 2. Features Section - Khối tính năng nổi bật */}
            <section className="features-section py-5 mb-5 bg-light">
                <Container>
                    <div className="text-center mb-5">
                        <h2 className="fw-bold" style={{ color: 'var(--primary-dark-blue)' }}>Tại sao chọn chúng tôi?</h2>
                        <p className="text-muted">Mang đến trải nghiệm học tập hiện đại và hiệu quả nhất</p>
                    </div>
                    
                    <Row className="g-4">
                        {/* Feature 1 */}
                        <Col md={4}>
                            <Card className="h-100 border-0 shadow-sm text-center p-4 hover-lift">
                                <div className="mb-3">
                                    <i className="bi bi-laptop display-3 text-primary"></i>
                                </div>
                                <Card.Body>
                                    <Card.Title className="fw-bold mb-3">Học tập linh hoạt</Card.Title>
                                    <Card.Text className="text-muted">
                                        Không giới hạn không gian và thời gian. Bạn hoàn toàn chủ động sắp xếp lịch học phù hợp với bản thân.
                                    </Card.Text>
                                </Card.Body>
                            </Card>
                        </Col>

                        {/* Feature 2 */}
                        <Col md={4}>
                            <Card className="h-100 border-0 shadow-sm text-center p-4 hover-lift">
                                <div className="mb-3">
                                    <i className="bi bi-person-workspace display-3 text-success"></i>
                                </div>
                                <Card.Body>
                                    <Card.Title className="fw-bold mb-3">Chuyên gia hàng đầu</Card.Title>
                                    <Card.Text className="text-muted">
                                        Đội ngũ giảng viên được tuyển chọn kỹ lưỡng, mang đến những kiến thức thực tế và bám sát nhu cầu công việc.
                                    </Card.Text>
                                </Card.Body>
                            </Card>
                        </Col>

                        {/* Feature 3 */}
                        <Col md={4}>
                            <Card className="h-100 border-0 shadow-sm text-center p-4 hover-lift">
                                <div className="mb-3">
                                    <i className="bi bi-award display-3 text-warning"></i>
                                </div>
                                <Card.Body>
                                    <Card.Title className="fw-bold mb-3">Chất lượng đảm bảo</Card.Title>
                                    <Card.Text className="text-muted">
                                        Nội dung khóa học liên tục được cập nhật. Cấp chứng nhận uy tín ngay sau khi hoàn thành lộ trình.
                                    </Card.Text>
                                </Card.Body>
                            </Card>
                        </Col>
                    </Row>
                </Container>
            </section>

            <section className="cta-section bg-forest-green py-5 mt-5">
                <Container className="text-center text-white py-4">
                    <h2 className="fw-bold mb-4">Sẵn sàng để bắt đầu hành trình của bạn?</h2>
                    <p className="lead mb-4 opacity-75">
                        Hàng ngàn học viên đã tham gia và nâng tầm sự nghiệp. Đừng bỏ lỡ cơ hội!
                    </p>
                    <Link to="/courses">
                        <Button variant="light" size="lg" className="rounded-pill px-5 fw-bold text-success shadow">
                            Vào lớp học ngay <i className="bi bi-arrow-right ms-2"></i>
                        </Button>
                    </Link>
                </Container>
            </section>
        </div>
    );
};

export default Home;