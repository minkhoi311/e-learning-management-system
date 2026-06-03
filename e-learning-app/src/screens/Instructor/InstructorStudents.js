import React, { useEffect, useState } from 'react';
import { Container, Table, ProgressBar, Alert, Badge, Image, Button } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import { authApis, endpoints } from '../../configs/Apis';
import MySpinner from '../../components/MySpinner';
import ChatModal from '../../components/ChatModal';

const InstructorStudents = () => {
    const { courseId } = useParams();
    const [students, setStudents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const nav = useNavigate();

    const [showChat, setShowChat] = useState(false);
    const [currentRoomId, setCurrentRoomId] = useState(null);
    const [chatTargetName, setChatTargetName] = useState("");

    useEffect(() => {
        const loadStudentsProgress = async () => {
            try {
                setLoading(true);
                let res = await authApis().get(endpoints['instructor-students'](courseId));
                setStudents(res.data || []);
            } catch (err) {
                console.error(err);
                setError("Không thể lấy dữ liệu tiến độ sinh viên của khóa học này.");
            } finally {
                setLoading(false);
            }
        };

        loadStudentsProgress();
    }, [courseId]);

    const openChat = async (studentId, studentUsername) => {
        try {
            // Gọi Spring Boot để tạo/lấy mã phòng (vd: room_2_5)
            let res = await authApis().post(endpoints['get-chat-room'], {
                target_id: studentId
            });
            
            // Lấy được mã phòng thì mở Popup Firebase
            setCurrentRoomId(res.data.firebase_room);
            setChatTargetName(studentUsername);
            setShowChat(true);
        } catch (err) {
            alert("Lỗi không thể tạo phòng chat lúc này!");
            console.error(err);
        }
    };

    if (loading) return <Container className="mt-5 text-center"><MySpinner /></Container>;

    return (
        <Container className="mt-4 mb-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h3 className="fw-bold text-primary">
                    <i className="bi bi-people-fill me-2"></i> Tiến độ học tập của Sinh viên
                </h3>
                <Button variant="outline-secondary" onClick={() => nav('/instructor/courses')}>
                    <i className="bi bi-arrow-left"></i> Quay lại
                </Button>
            </div>

            {error && <Alert variant="danger">{error}</Alert>}

            {students.length === 0 ? (
                <Alert variant="warning" className="text-center py-4">Chưa có sinh viên nào đăng ký tham gia học lớp này.</Alert>
            ) : (
                <div className="table-responsive bg-white rounded shadow-sm">
                    <Table hover className="align-middle mb-0 text-center">
                        <thead className="table-light">
                            <tr>
                                <th width="10%">Ảnh</th>
                                <th className="text-start" width="25%">Họ và tên sinh viên</th>
                                <th width="20%">Ngày tham gia</th>
                                <th width="30%">Tiến độ bài học</th>
                                <th width="15%">Hóa đơn</th>
                                <th width="10%">Liên hệ</th>
                            </tr>
                        </thead>
                        <tbody>
                            {students.map(e => (
                                <tr key={e.id}>
                                    <td>
                                        <Image src={e.studentId?.avatar || 'https://via.placeholder.com/40'} roundedCircle style={{ width: '40px', height: '40px', objectFit: 'cover' }} />
                                    </td>
                                    <td className="text-start">
                                        <div className="fw-bold">{e.studentId?.username}</div>
                                        <small className="text-muted">{e.studentId?.email || "Chưa cập nhật email"}</small>
                                    </td>
                                    <td>{e.enrolledTime ? new Date(e.enrolledTime).toLocaleDateString('vi-VN') : 'N/A'}</td>
                                    <td>
                                        <div className="d-flex justify-content-between align-items-center mb-1 small">
                                            <span className="fw-bold text-success">{e.progressPercent || 0}% hoàn thành</span>
                                        </div>
                                        <ProgressBar variant={e.progressPercent === 100 ? "success" : "info"} now={e.progressPercent || 0} style={{ height: '10px' }} animated />
                                    </td>
                                    <td>
                                        {e.payment?.status === 'SUCCESS' ? (
                                            <Badge bg="success">Đã thu tiền</Badge>
                                        ) : (
                                            <Badge bg="warning" text="dark">Đang chờ duyệt</Badge>
                                        )}
                                    </td>
                                    <td>
                                    {/* 🔥 Nút bấm để Chat */}
                                    <Button 
                                        variant="outline-primary" 
                                        size="sm" 
                                        className="rounded-circle"
                                        onClick={() => openChat(e.studentId.id, e.studentId.username)}
                                    >
                                        <i className="bi bi-chat-dots"></i>
                                    </Button>
                                </td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                </div>
            )}
            <ChatModal 
                show={showChat} 
                handleClose={() => setShowChat(false)} 
                roomId={currentRoomId} 
                targetName={chatTargetName} 
            />
        </Container>
    );
};

export default InstructorStudents;