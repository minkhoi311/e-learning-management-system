import React, { useState, useEffect, useContext } from 'react';
import { Container, Form, Button, Card, Row, Col, Alert } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import { authApis, endpoints } from '../../configs/Apis';
import { MyUserContext } from '../../configs/Contexts';
import MySpinner from '../../components/MySpinner';

const CourseForm = () => {
    const { courseId } = useParams();
    const isEditMode = !!courseId;
    
    const [courseData, setCourseData] = useState({
        subject: '', description: '', price: 0, durationHours: '', videoUrl: '', categoryId: ''
    });
    const [categories, setCategories] = useState([]);
    const [imageFile, setImageFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    
    const [user] = useContext(MyUserContext);
    const nav = useNavigate();

    useEffect(() => {
        if (!user || user.role !== 'INSTRUCTOR') {
            nav('/');
            return;
        }

        const loadCategories = async () => {
            let res = await authApis().get(endpoints['categories']);
            setCategories(res.data);
        };
        
        const loadCourseDetail = async () => {
            if (isEditMode) {
                try {
                    // CHUẨN HÓA LẠI API CALL
                    let res = await authApis().get(endpoints['course-details'](courseId));
                    setCourseData({
                        subject: res.data.subject || '',
                        description: res.data.description || '',
                        price: res.data.price || 0,
                        durationHours: res.data.durationHours || '',
                        videoUrl: res.data.videoUrl || '',
                        categoryId: res.data.categoryId?.id || ''
                    });
                } catch (err) {
                    setError("Không thể tải thông tin khóa học cũ.");
                }
            }
        };

        loadCategories();
        loadCourseDetail();
    }, [courseId, isEditMode]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        const formData = new FormData();
        formData.append("subject", courseData.subject);
        formData.append("description", courseData.description);
        formData.append("price", courseData.price);
        formData.append("durationHours", courseData.durationHours);
        formData.append("videoUrl", courseData.videoUrl);
        formData.append("categoryId", courseData.categoryId);
        if (imageFile) {
            formData.append("file", imageFile);
        }

        try {
            if (isEditMode) {

                await authApis().patch(endpoints['update-course'](courseId), formData, {
                    headers: { 'Content-Type': 'multipart/form-data' }
                });
                alert("Cập nhật thông tin khóa học thành công!");
            } else {

                await authApis().post(endpoints['add-course'], formData, {
                    headers: { 'Content-Type': 'multipart/form-data' }
                });
                alert("Đăng tải khóa học thành công!");
            }
            nav('/instructor/courses'); 
        } catch (err) {
            console.error(err);
            setError("Xảy ra lỗi trong quá trình xử lý dữ liệu. Vui lòng kiểm tra lại các trường thông tin.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="mt-4 mb-5" style={{ maxWidth: '700px' }}>
            <Card className="shadow-sm border-0">
                <Card.Body className="p-4">
                    <h3 className="fw-bold text-center text-primary mb-4">
                        {isEditMode ? "CẬP NHẬT KHÓA HỌC" : "TẠO KHÓA HỌC MỚI"}
                    </h3>

                    {error && <Alert variant="danger">{error}</Alert>}

                    <Form onSubmit={handleSubmit}>
                        <Form.Group className="mb-3">
                            <Form.Label className="fw-bold small">Tên khóa học *</Form.Label>
                            <Form.Control type="text" required value={courseData.subject} onChange={e => setCourseData({...courseData, subject: e.target.value})} placeholder="Ví dụ: Lập trình Java Spring Boot nâng cao" />
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label className="fw-bold small">Danh mục khóa học *</Form.Label>
                            <Form.Select required value={courseData.categoryId} onChange={e => setCourseData({...courseData, categoryId: e.target.value})}>
                                <option value="">-- Chọn danh mục học --</option>
                                {categories.map(cat => <option key={cat.id} value={cat.id}>{cat.name}</option>)}
                            </Form.Select>
                        </Form.Group>

                        <Row className="g-3 mb-3">
                            <Col md={6}>
                                <Form.Group>
                                    <Form.Label className="fw-bold small">Học phí (đ) *</Form.Label>
                                    <Form.Control type="number" min="0" required value={courseData.price} onChange={e => setCourseData({...courseData, price: e.target.value})} />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group>
                                    <Form.Label className="fw-bold small">Thời lượng giảng dạy (Giờ) *</Form.Label>
                                    <Form.Control type="number" min="1" required value={courseData.durationHours} onChange={e => setCourseData({...courseData, durationHours: e.target.value})} placeholder="Ví dụ: 45" />
                                </Form.Group>
                            </Col>
                        </Row>

                        <Form.Group className="mb-3">
                            <Form.Label className="fw-bold small">Hình ảnh minh họa</Form.Label>
                            <Form.Control type="file" accept="image/*" onChange={e => setImageFile(e.target.files[0])} />
                            <Form.Text className="text-muted">Định dạng hỗ trợ JPG, PNG. Dung lượng tối đa 5MB.</Form.Text>
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label className="fw-bold small">Link Video giới thiệu (Nếu có)</Form.Label>
                            <Form.Control type="text" value={courseData.videoUrl} onChange={e => setCourseData({...courseData, videoUrl: e.target.value})} placeholder="Nhập link YouTube hoặc Google Drive giới thiệu" />
                        </Form.Group>

                        <Form.Group className="mb-4">
                            <Form.Label className="fw-bold small">Mô tả chi tiết khóa học *</Form.Label>
                            <Form.Control as="textarea" rows={5} required value={courseData.description} onChange={e => setCourseData({...courseData, description: e.target.value})} placeholder="Mô tả nội dung, mục tiêu đầu ra và kiến thức đạt được sau khóa học..." />
                        </Form.Group>

                        <div className="d-flex gap-2 justify-content-end">
                            <Button variant="secondary" onClick={() => nav('/instructor/courses')} disabled={loading}>Hủy bỏ</Button>
                            <Button variant="primary" type="submit" disabled={loading}>
                                {loading ? <MySpinner /> : "Xác nhận lưu cấu trúc"}
                            </Button>
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default CourseForm;