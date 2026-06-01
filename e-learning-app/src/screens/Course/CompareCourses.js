import React, { useState, useEffect, useContext } from 'react';
import { Container, Table, Button, Alert, Badge } from 'react-bootstrap';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import cookies from 'react-cookies';
import Apis, { endpoints } from '../../configs/Apis';
import { MyCartContext } from '../../configs/Contexts';
import MySpinner from '../../components/MySpinner';

const CompareCourses = () => {
    const [courses, setCourses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [searchParams] = useSearchParams();
    const ids = searchParams.get('ids'); // Lấy chuỗi id, VD: "1,2,3"
    
    const nav = useNavigate();
    const [, cartDispatch] = useContext(MyCartContext);

    // Xử lý thêm vào giỏ hàng (giống hệt bên trang Courses)
    const addToCart = (course) => {
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

    useEffect(() => {
        const fetchCompareData = async () => {
            if (!ids) {
                setLoading(false);
                return;
            }
            try {
                // Gọi API lấy các khóa học dựa trên chuỗi ids
                let res = await Apis.get(`${endpoints['compare']}?ids=${ids}`);
                setCourses(res.data);
            } catch (err) {
                console.error(err);
                setError("Có lỗi xảy ra khi tải dữ liệu so sánh.");
            } finally {
                setLoading(false);
            }
        };

        fetchCompareData();
    }, [ids]);

    if (loading) return <Container className="text-center mt-5"><MySpinner /></Container>;

    if (!ids || courses.length === 0) {
        return (
            <Container className="mt-5">
                <Alert variant="warning" className="text-center">
                    <h4>Bạn chưa chọn khóa học nào để so sánh!</h4>
                    <Button variant="primary" className="mt-3" onClick={() => nav('/courses')}>
                        Quay lại danh sách khóa học
                    </Button>
                </Alert>
            </Container>
        );
    }

    return (
        <Container className="mt-4 mb-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2 style={{ color: 'var(--primary-dark-blue)' }}>
                    <i className="bi bi-layout-split me-2"></i> 
                    So sánh khóa học
                </h2>
                <Button variant="outline-secondary" onClick={() => nav('/courses')}>
                    <i className="bi bi-arrow-left me-1"></i> Quay lại
                </Button>
            </div>

            {error && <Alert variant="danger">{error}</Alert>}

            <div className="table-responsive shadow-sm rounded bg-white">
                <Table bordered hover className="mb-0 text-center align-middle">
                    <thead className="table-light">
                        <tr>
                            <th width="15%" className="bg-light text-muted">Tiêu chí</th>
                            {/* In ra các cột tiêu đề dựa trên số lượng khóa học */}
                            {courses.map((c, index) => (
                                <th key={c.id} style={{ width: `${85 / courses.length}%` }}>
                                    Khóa học {index + 1}
                                </th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {/* Hàng 1: Hình ảnh */}
                        <tr>
                            <td className="fw-bold text-muted">Hình ảnh</td>
                            {courses.map(c => (
                                <td key={c.id}>
                                    <img 
                                        src={c.image || 'https://via.placeholder.com/300x180?text=No+Image'} 
                                        alt={c.subject} 
                                        className="img-fluid rounded" 
                                        style={{ height: '120px', objectFit: 'cover', width: '100%' }} 
                                    />
                                </td>
                            ))}
                        </tr>

                        {/* Hàng 2: Tên khóa học */}
                        <tr>
                            <td className="fw-bold text-muted">Tên khóa học</td>
                            {courses.map(c => (
                                <td key={c.id}>
                                    <h5 className="text-primary mb-0">{c.subject}</h5>
                                </td>
                            ))}
                        </tr>

                        {/* Hàng 3: Học phí */}
                        <tr>
                            <td className="fw-bold text-muted">Học phí</td>
                            {courses.map(c => (
                                <td key={c.id}>
                                    <Badge bg="success" className="fs-6">
                                        {c.price === 0 ? "Miễn phí" : `${c.price.toLocaleString("vi-VN")} đ`}
                                    </Badge>
                                </td>
                            ))}
                        </tr>

                        {/* Hàng 4: Danh mục */}
                        <tr>
                            <td className="fw-bold text-muted">Danh mục</td>
                            {courses.map(c => (
                                <td key={c.id}>{c.categoryId ? c.categoryId.name : "N/A"}</td>
                            ))}
                        </tr>

                        {/* Hàng 5: Thời lượng */}
                        <tr>
                            <td className="fw-bold text-muted">Thời lượng</td>
                            {courses.map(c => (
                                <td key={c.id}>{c.durationHours ? `${c.durationHours} giờ` : 'Chưa cập nhật'}</td>
                            ))}
                        </tr>

                        {/* Hàng 6: Giảng viên */}
                        <tr>
                            <td className="fw-bold text-muted">Giảng viên</td>
                            {courses.map(c => (
                                <td key={c.id}>
                                    {c.instructorId ? `${c.instructorId.lastName} ${c.instructorId.firstName}` : "N/A"}
                                </td>
                            ))}
                        </tr>

                        {/* Hàng 7: Mô tả */}
                        <tr>
                            <td className="fw-bold text-muted">Mô tả tóm tắt</td>
                            {courses.map(c => (
                                <td key={c.id} className="text-start">
                                    <div className="text-clamp-3 text-muted small">
                                        {c.description || "Chưa có mô tả."}
                                    </div>
                                </td>
                            ))}
                        </tr>

                        {/* Hàng 8: Hành động */}
                        <tr className="table-light">
                            <td className="fw-bold text-muted">Thao tác</td>
                            {courses.map(c => (
                                <td key={c.id}>
                                    <div className="d-flex flex-column gap-2 px-3">
                                        <Button variant="primary" size="sm" onClick={() => nav(`/courses/${c.id}`)}>
                                            Xem chi tiết
                                        </Button>
                                        <Button variant="outline-success" size="sm" onClick={() => addToCart(c)}>
                                            Thêm giỏ hàng
                                        </Button>
                                    </div>
                                </td>
                            ))}
                        </tr>
                    </tbody>
                </Table>
            </div>
        </Container>
    );
};

export default CompareCourses;