import React, { useState, useEffect, useContext } from 'react';
import { Container, Row, Col, Card, Button, Alert, Form } from 'react-bootstrap';
import { Link, useNavigate, useParams } from 'react-router-dom';
import Apis, { authApis, endpoints } from '../../configs/Apis';
import { MyUserContext } from '../../configs/Contexts';
import MySpinner from '../../components/MySpinner';

const LessonDetail = () => {
    const { lessonId } = useParams();
    const nav = useNavigate();
    
    const [lesson, setLesson] = useState(null);
    const [comments, setComments] = useState([]);
    const [content, setContent] = useState("");
    const [loading, setLoading] = useState(false);
    const [user] = useContext(MyUserContext);
    const [enrollmentId, setEnrollmentId] = useState(null);

    // Load dữ liệu bài học
    const loadLesson = async () => {
        try {
            setLoading(true);
            let res = await Apis.get(endpoints['lesson-details'](lessonId));
            let lessonData = res.data; 
            
            setLesson(lessonData); 

            if (user && lessonData && lessonData.courseId) {
                try {
                    let enrollRes = await authApis().get(endpoints['check-enrollment'](lessonData.courseId.id));
                    setEnrollmentId(enrollRes.data.id);
                } catch (enrollErr) {
                    console.log("User chưa đăng ký khóa học này hoặc không có quyền.");
                }
            }
        } catch (ex) {
            console.error(ex);
        } finally {
            setLoading(false);
        }
    };


    const loadComments = async () => {
        try {
            let res = await Apis.get(endpoints['comments'](lessonId));
            setComments(res.data);
        } catch (ex) {
            console.error(ex);
        }
    };

    useEffect(() => {
        loadLesson();
        loadComments();
    }, [lessonId]);

    const addComment = async () => {
        try {
            let res = await authApis().post(endpoints['addComment'](lessonId), {
                'content': content
            });
            if (res.status === 201) {
                setComments([res.data, ...comments]);
                setContent("");
            }
        } catch (ex) {
            console.error(ex);
            alert("Bình luận thất bại hoặc nội dung trống!");
        }
    };


    const handleComplete = async () => {
        if (!enrollmentId) {
            alert("Bạn chưa đăng ký khóa học này hoặc chưa đăng nhập!");
            return;
        }

        try {
            setLoading(true);
            let res = await authApis().post(endpoints['complete-lesson'](enrollmentId, lessonId));
            if (res.status === 200) {
                alert("Đánh dấu hoàn thành bài học thành công!");
                nav('/my-enrollments');
            }
        } catch (err) {
            console.error(err);
            alert("Lỗi khi lưu tiến độ!");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="mt-4 mb-5">
            {loading && <MySpinner />}

            {lesson && (
                <Row className="g-4">
                    <Col lg={8}>
                        <div className="ratio ratio-16x9 bg-dark rounded shadow-sm mb-4">
                            <video controls src={lesson.videoUrl} poster={lesson.image} />
                        </div>
                        <Card className="border-0 shadow-sm p-3">
                            <h2>{lesson.subject}</h2>
                            <p className="text-muted">{lesson.content}</p>
                            <Button variant="success" className="mt-2" onClick={handleComplete}>
                                <i className="bi bi-check-circle me-1"></i> Đánh dấu đã học
                            </Button>
                        </Card>
                    </Col>

                    <Col lg={4}>
                        <h4>Thảo luận ({comments.length})</h4>
                        {user ? (
                            <div className="mb-3">
                                <Form.Control 
                                    value={content} 
                                    onChange={e => setContent(e.target.value)} 
                                    placeholder="Viết bình luận..." 
                                />
                                <Button className="mt-2" onClick={addComment}>Gửi</Button>
                            </div>
                        ) : (
                            <Alert variant="warning">Vui lòng <Link to="/login">đăng nhập</Link> để bình luận!</Alert>
                        )}

                        <div className="mt-3">
                            {comments.map(c => (
                                <Card key={c.id} className="mb-2 p-2">
                                    <strong>{c.userId?.username}</strong>
                                    <p>{c.content}</p>
                                </Card>
                            ))}
                        </div>
                    </Col>
                </Row>
            )}
        </Container>
    );
}

export default LessonDetail;