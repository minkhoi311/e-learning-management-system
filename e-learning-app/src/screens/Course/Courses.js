import React, { useState, useEffect, useContext } from 'react';
import { Container, Row, Col, Card, Button, Alert, Badge } from 'react-bootstrap';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import cookies from 'react-cookies';
import Apis, { endpoints } from '../../configs/Apis';
import { MyCartContext, MyUserContext } from '../../configs/Contexts'; // CHÚ Ý: Đã import MyUserContext
import MySpinner from '../../components/MySpinner';

const Courses = () => {
    const [courses, setCourses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    
    const [compareList, setCompareList] = useState([]); 
    
    const [searchParams] = useSearchParams();
    const nav = useNavigate();
    
    // Lấy context của Giỏ hàng và User
    const [, cartDispatch] = useContext(MyCartContext);
    const [user] = useContext(MyUserContext); // CHÚ Ý: Khai báo user từ Context

    const loadCourses = async () => {
        setLoading(true);
        setError(null);
        try {
            let url = `${endpoints['courses']}?page=${page}`;
            
            let kw = searchParams.get('kw');
            if (kw) url += `&kw=${kw}`;
            
            let cateId = searchParams.get('cateId');
            if (cateId) url += `&cateId=${cateId}`;

            let res = await Apis.get(url);
            
            setCourses(res.data.courses);
            setTotalPages(res.data.totalPages);
            
        } catch (ex) {
            console.error("Lỗi tải khóa học:", ex);
            setError("Không thể tải danh sách khóa học. Vui lòng thử lại sau.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadCourses();
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [page, searchParams]);

    const addToCart = (course) => {
        // 1. Chặn nếu chưa đăng nhập
        if (!user) {
            alert("Vui lòng đăng nhập để thêm khóa học vào giỏ!");
            nav('/login?next=/courses');
            return;
        }

        // 2. Xác định tên cookie riêng của user hiện tại
        const cartCookieName = `cart_${user.username}`;
        
        // 3. Lấy giỏ hiện tại lên
        let cart = cookies.load(cartCookieName) || {};
        
        if (course.id in cart) {
            alert("Khóa học này đã có trong giỏ hàng!");
            return;
        }

        // 4. Thêm khóa học mới vào giỏ
        cart[course.id] = {
            id: course.id,
            subject: course.subject,
            price: course.price,
            image: course.image,
            quantity: 1
        };
        
        // 5. Lưu lại vào cookie CỦA USER ĐÓ
        cookies.save(cartCookieName, cart, { path: '/', maxAge: 7 * 24 * 60 * 60 });
        
        // 6. Cập nhật qua Reducer (BẮT BUỘC có payload là giỏ hàng mới)
        cartDispatch({ type: 'UPDATE', payload: cart });
        
        alert("Đã thêm khóa học vào giỏ hàng thành công!");
    };

    const toggleCompare = (course) => {
        const isExist = compareList.find(c => c.id === course.id);
        if (isExist) {
            setCompareList(compareList.filter(c => c.id !== course.id));
        } else {
            if (compareList.length >= 3) {
                alert("Bạn chỉ có thể so sánh tối đa 3 khóa học cùng lúc!");
                return;
            }
            setCompareList([...compareList, course]);
        }
    };

    if (loading && page === 1) return (
        <Container className="text-center mt-5">
            <MySpinner /> 
        </Container>
    );

    return (
        <Container className="mt-4 mb-5 pb-5"> 
            <h2 className="text-primary mb-4" style={{ color: 'var(--primary-dark-blue)' }}>
                <i className="bi bi-journal-richtext me-2"></i> 
                Danh sách khóa học
            </h2>

            {error && <Alert variant="danger">{error}</Alert>}

            {!loading && courses.length === 0 && !error && (
                <Alert variant="info">Không tìm thấy khóa học nào phù hợp với yêu cầu của bạn.</Alert>
            )}

            <Row className="g-4">
                {courses.map(c => {
                    const isComparing = compareList.some(item => item.id === c.id);

                    return (
                        <Col key={c.id} xs={12} sm={6} md={4} lg={3}>
                            <Card className={`h-100 hover-lift shadow-sm ${isComparing ? 'border-primary border-2' : 'border-0'}`}>
                                
                                <Link to={`/courses/${c.id}`} style={{ textDecoration: 'none' }}>
                                    <Card.Img 
                                        variant="top" 
                                        className="card-img-custom"
                                        src={c.image || 'https://via.placeholder.com/300x180?text=No+Image'} 
                                    />
                                </Link>

                                <Card.Body className="d-flex flex-column">
                                    <Link to={`/courses/${c.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
                                        <Card.Title className="text-truncate" title={c.subject}>{c.subject}</Card.Title>
                                    </Link>
                                    
                                    <Card.Text className="text-muted small mb-3 flex-grow-1 text-clamp-3">
                                        {c.description || "Chưa có mô tả cho khóa học này."}
                                    </Card.Text>

                                    <div className="d-flex justify-content-between align-items-center mb-3">
                                        <Badge bg="success" className="fs-6">
                                            {c.price === 0 ? "Miễn phí" : `${c.price.toLocaleString("vi-VN")} đ`}
                                        </Badge>
                                        <small className="text-muted">
                                            <i className="bi bi-clock me-1"></i> 
                                            {c.durationHours ? `${c.durationHours} giờ` : 'N/A'}
                                        </small>
                                    </div>

                                    <div className="d-grid gap-2 mt-auto">
                                        <Button variant="primary" size="sm" onClick={() => nav(`/courses/${c.id}`)}>
                                            <i className="bi bi-eye me-1"></i> Xem chi tiết
                                        </Button>
                                        <Button variant="outline-success" size="sm" onClick={() => addToCart(c)}>
                                            <i className="bi bi-cart-plus me-1"></i> Thêm giỏ hàng
                                        </Button>
                                        <Button 
                                            variant={isComparing ? "secondary" : "outline-info"} 
                                            size="sm" 
                                            onClick={() => toggleCompare(c)}
                                        >
                                            <i className="bi bi-arrow-left-right me-1"></i> 
                                            {isComparing ? "Bỏ so sánh" : "Thêm vào so sánh"}
                                        </Button>
                                    </div>
                                </Card.Body>
                            </Card>
                        </Col>
                    );
                })}
            </Row>

            {page < totalPages && (
                <div className="text-center mt-4">
                    <Button 
                        variant="outline-primary" 
                        onClick={() => setPage(page + 1)} 
                        disabled={loading}>
                        {loading ? 'Đang tải...' : 'Xem thêm khóa học'}
                    </Button>
                </div>
            )}

            {compareList.length > 0 && (
                <div className="fixed-bottom bg-white border-top shadow-lg p-3 d-flex justify-content-between align-items-center" style={{ zIndex: 1000 }}>
                    <div className="d-flex align-items-center">
                        <span className="me-3">Bạn đang chọn <strong>{compareList.length}/3</strong> khóa học để so sánh:</span>
                        <div className="d-none d-md-flex gap-2">
                            {compareList.map(item => (
                                <Badge bg="info" text="dark" key={item.id} className="p-2 text-truncate" style={{ maxWidth: '150px' }}>
                                    {item.subject}
                                </Badge>
                            ))}
                        </div>
                    </div>
                    <div>
                        <Button variant="outline-danger" className="me-2" onClick={() => setCompareList([])}>
                            Xóa tất cả
                        </Button>
                        <Button variant="primary" onClick={() => nav(`/compare?ids=${compareList.map(c => c.id).join(',')}`)}>
                            Tiến hành so sánh
                        </Button>
                    </div>
                </div>
            )}
        </Container>
    );
}

export default Courses;