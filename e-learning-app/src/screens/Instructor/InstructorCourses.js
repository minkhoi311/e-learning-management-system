import React, { useEffect, useState, useContext } from 'react';
import { Container, Table, Button, Alert, Image } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { authApis, endpoints } from '../../configs/Apis';
import { MyUserContext } from '../../configs/Contexts';
import MySpinner from '../../components/MySpinner';

const InstructorCourses = () => {
    const [courses, setCourses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [user] = useContext(MyUserContext);
    const nav = useNavigate();

    const loadInstructorCourses = async () => {
        try {
            setLoading(true);
            let res = await authApis().get(`${endpoints['courses']}?username=${user.username}`);
            setCourses(res.data.courses || []);
        } catch (err) {
            console.error(err);
            setError("Không thể tải danh sách khóa học.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (!user || user.role !== 'INSTRUCTOR') {
            nav('/');
            return;
        }
        loadInstructorCourses();
    }, [user, nav]);

    const handleDelete = async (courseId, subject) => {
        if (window.confirm(`Bạn có chắc chắn muốn xóa khóa học "${subject}"? Thao tác này không thể hoàn tác.`)) {
            try {
                // CHUẨN HÓA: Dùng endpoints đã khai báo
                await authApis().delete(endpoints['delete-course'](courseId));
                alert("Xóa khóa học thành công!");
                loadInstructorCourses(); // Tải lại danh sách
            } catch (err) {
                console.error(err);
                alert("Xóa thất bại! Khóa học này có thể đã có sinh viên đăng ký học.");
            }
        }
    };

    if (loading) return <Container className="mt-5 text-center"><MySpinner /></Container>;

    return (
        <Container className="mt-4 mb-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h3 className="fw-bold text-primary"><i className="bi bi-journal-text me-2"></i> Khóa học của tôi</h3>
                <Button variant="success" onClick={() => nav('/instructor/courses/add')}>
                    <i className="bi bi-plus-circle me-1"></i> Tạo khóa học mới
                </Button>
            </div>

            {error && <Alert variant="danger">{error}</Alert>}

            {courses.length === 0 ? (
                <Alert variant="info" className="text-center py-4">Bạn chưa đăng tải khóa học nào lên hệ thống.</Alert>
            ) : (
                <div className="table-responsive bg-white rounded shadow-sm">
                    <Table hover className="align-middle mb-0 text-center">
                        <thead className="table-light">
                            <tr>
                                <th>Hình ảnh</th>
                                <th className="text-start">Tên khóa học</th>
                                <th>Học phí</th>
                                <th>Thời lượng</th>
                                <th>Chức năng quản lý</th>
                            </tr>
                        </thead>
                        <tbody>
                            {courses.map(c => (
                                <tr key={c.id}>
                                    <td>
                                        <Image src={c.image || 'https://via.placeholder.com/80x50'} rounded style={{ width: '80px', height: '45px', objectFit: 'cover' }} />
                                    </td>
                                    <td className="text-start fw-bold text-dark">{c.subject}</td>
                                    <td className="text-success fw-bold">
                                        {c.price === 0 ? "Miễn phí" : `${c.price.toLocaleString("vi-VN")} đ`}
                                    </td>
                                    <td>{c.duration || "N/A"} giờ</td>
                                    <td>
                                        <Button variant="outline-info" size="sm" className="me-2" onClick={() => nav(`/instructor/courses/${c.id}/students`)}>
                                            <i className="bi bi-people-fill me-1"></i> Sinh viên
                                        </Button>
                                        <Button variant="outline-warning" size="sm" className="me-2" onClick={() => nav(`/instructor/courses/edit/${c.id}`)}>
                                            <i className="bi bi-pencil-square"></i> Sửa
                                        </Button>
                                        <Button variant="outline-danger" size="sm" onClick={() => handleDelete(c.id, c.subject)}>
                                            <i className="bi bi-trash-fill"></i> Xóa
                                        </Button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                </div>
            )}
        </Container>
    );
};

export default InstructorCourses;