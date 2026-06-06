import React, { useState, useEffect, useContext } from 'react';
import { Container, Row, Col, Card, Button, Alert, Badge, Form } from 'react-bootstrap';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import cookies from 'react-cookies';
import Apis, { endpoints } from '../../configs/Apis';
import { MyCartContext, MyUserContext } from '../../configs/Contexts'; 
import MySpinner from '../../components/MySpinner';

const Courses = () => {
    const [courses, setCourses] = useState([]);
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(1);
    
    const [compareList, setCompareList] = useState([]); 
    const [searchParams, setSearchParams] = useSearchParams();
    const nav = useNavigate();
    
    const [, cartDispatch] = useContext(MyCartContext);
    const [user] = useContext(MyUserContext); 

    const [filterKw, setFilterKw] = useState(searchParams.get('kw') || "");
    const [filterFromPrice, setFilterFromPrice] = useState(searchParams.get('fromPrice') || "");
    const [filterToPrice, setFilterToPrice] = useState(searchParams.get('toPrice') || "");
    const [filterSort, setFilterSort] = useState(searchParams.get('sort') || "newest");

    const loadCourses = async () => {
        try {
            setLoading(true);
            
            let url = `${endpoints['courses']}?page=${page}`;

            const kw = searchParams.get('kw');
            if (kw) url += `&kw=${kw}`;

            const cateId = searchParams.get('cateId');
            if (cateId) url += `&cateId=${cateId}`;

            const fromPrice = searchParams.get('fromPrice');
            if (fromPrice) url += `&fromPrice=${fromPrice}`;

            const toPrice = searchParams.get('toPrice');
            if (toPrice) url += `&toPrice=${toPrice}`;

            const sort = searchParams.get('sort');
            if (sort) url += `&sort=${sort}`;

            let res = await Apis.get(url);
            let data = res.data || [];

            if (data.length === 0) {
                setPage(0);
            }
            if (page === 1) {
                setCourses(data);
            } else if (page > 1) {
                setCourses([...courses, ...data]);
            }
            
        } catch (ex) {
            console.error("Lỗi tải khóa học:", ex);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (page > 0) {
            loadCourses();
        }
    }, [page, searchParams]);


    useEffect(() => {
        setPage(1);
        setFilterKw(searchParams.get('kw') || "");
        setFilterFromPrice(searchParams.get('fromPrice') || "");
        setFilterToPrice(searchParams.get('toPrice') || "");
        setFilterSort(searchParams.get('sort') || "newest");
    }, [searchParams]);


    const loadMore = () => {
        if (page > 0 && !loading) {
            setPage(page + 1);
        }
    };


    const handleFilterSubmit = (e) => {
        e.preventDefault();
        const newParams = {};
        if (filterKw.trim()) newParams.kw = filterKw.trim();
        if (searchParams.get('cateId')) newParams.cateId = searchParams.get('cateId');
        if (filterFromPrice) newParams.fromPrice = filterFromPrice;
        if (filterToPrice) newParams.toPrice = filterToPrice;
        if (filterSort) newParams.sort = filterSort;

        setSearchParams(newParams);
    };


    const addToCart = (course) => {
        if (!user) {
            alert("Vui lòng đăng nhập để thêm khóa học vào giỏ!");
            nav('/login?next=/courses');
            return;
        }
        const cartCookieName = `cart_${user.username}`;
        let cart = cookies.load(cartCookieName) || {};
        
        if (course.id in cart) {
            alert("Khóa học này đã có trong giỏ hàng!");
            return;
        }
        cart[course.id] = {
            id: course.id, subject: course.subject, price: course.price, image: course.image, quantity: 1
        };
        cookies.save(cartCookieName, cart, { path: '/', maxAge: 7 * 24 * 60 * 60 });
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

    return (
        <Container className="mt-4 mb-5 pb-5"> 
            <h2 className="text-primary mb-4" style={{ color: 'var(--primary-dark-blue)' }}>
                <i className="bi bi-journal-richtext me-2"></i> 
                Danh sách khóa học
            </h2>

            <Card className="p-3 mb-4 shadow-sm border-0 bg-white">
                <Form onSubmit={handleFilterSubmit} className="row g-2 align-items-end">
                    <Col md={3}>
                        <Form.Label className="text-muted small fw-bold">Tên khóa học</Form.Label>
                        <Form.Control 
                            type="text" 
                            placeholder="Nhập từ khóa..." 
                            value={filterKw}
                            onChange={e => setFilterKw(e.target.value)}
                        />
                    </Col>
                    <Col md={2}>
                        <Form.Label className="text-muted small fw-bold">Giá từ (VNĐ)</Form.Label>
                        <Form.Control 
                            type="number" 
                            placeholder="Ví dụ: 200000" 
                            value={filterFromPrice}
                            onChange={e => setFilterFromPrice(e.target.value)}
                        />
                    </Col>
                    <Col md={2}>
                        <Form.Label className="text-muted small fw-bold">Đến giá (VNĐ)</Form.Label>
                        <Form.Control 
                            type="number" 
                            placeholder="Ví dụ: 1500000" 
                            value={filterToPrice}
                            onChange={e => setFilterToPrice(e.target.value)}
                        />
                    </Col>
                    <Col md={3}>
                        <Form.Label className="text-muted small fw-bold">Sắp xếp theo</Form.Label>
                        <Form.Select value={filterSort} onChange={e => setFilterSort(e.target.value)}>
                            <option value="newest">Khóa học mới nhất</option>
                            <option value="name_asc">Tên khóa học (A - Z)</option>
                            <option value="price_asc">Giá tăng dần</option>
                            <option value="price_desc">Giá giảm dần</option>
                        </Form.Select>
                    </Col>
                    <Col md={2}>
                        <Button type="submit" variant="primary" className="w-100">
                            <i className="bi bi-funnel-fill me-1"></i> Lọc kết quả
                        </Button>
                    </Col>
                </Form>
            </Card>

            {/* LIST KHÓA HỌC */}
            {!loading && courses.length === 0 && (
                <Alert variant="info" className="mt-2">Không tìm thấy khóa học nào phù hợp với tiêu chí tìm kiếm của bạn.</Alert>
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

            {/* NÚT XEM THÊM (Chỉ hiện khi page > 0) */}
            {page > 0 && !loading && (
                <div className="text-center mt-4">
                    <Button 
                        variant="outline-primary" 
                        size="lg"
                        className="px-5 rounded-pill"
                        onClick={loadMore} 
                    >
                        <span><i className="bi bi-arrow-down-circle me-2"></i> Xem thêm khóa học</span>
                    </Button>
                </div>
            )}
            
            {loading && <div className="text-center mt-3"><MySpinner /></div>}

            {/* THANH SO SÁNH */}
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