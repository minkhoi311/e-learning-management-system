USE elearningdb;

-- --------------------------------------------------------
-- 1. Bảng Category (Đã bỏ cột description)
-- --------------------------------------------------------
INSERT INTO category (id, name) VALUES
(1, 'Lập trình & CNTT'),
(2, 'Thiết kế Đồ họa'),
(3, 'Kinh doanh & Marketing'),
(4, 'Ngoại ngữ'),
(5, 'Kỹ năng mềm');

-- --------------------------------------------------------
-- 2. Bảng User
-- Chú ý: password đang NOT NULL nên tài khoản Google cũng phải có chuỗi dummy
-- --------------------------------------------------------
INSERT INTO user (id, email, password, full_name, avatar, role, is_staff, is_active, auth_provider) VALUES
(1, 'admin@eduspace.vn', '$2a$10$dummyHash123', 'Hệ Thống Quản Trị', 'admin_avatar.png', 'ADMIN', TRUE, TRUE, 'LOCAL'),
(2, 'tech.lead@gmail.com', '$2a$10$dummyHash123', 'Hoàng Công Nghệ', 'gv_tech.png', 'INSTRUCTOR', FALSE, TRUE, 'LOCAL'),
(3, 'designer.pro@gmail.com', '$2a$10$dummyHash123', 'Lê Mỹ Thuật', 'gv_design.png', 'INSTRUCTOR', FALSE, TRUE, 'LOCAL'),
(4, 'ceo.startup@gmail.com', '$2a$10$dummyHash123', 'Trần Doanh Nhân', 'gv_biz.png', 'INSTRUCTOR', FALSE, TRUE, 'LOCAL'),
(5, 'student.active@gmail.com', '$2a$10$dummyHash123', 'Nguyễn Học Chăm', 'sv_active.png', 'STUDENT', FALSE, TRUE, 'LOCAL'),
(6, 'student.casual@yahoo.com', '$2a$10$dummyOAuthHash', 'Phạm Giải Trí', 'sv_casual.png', 'STUDENT', FALSE, TRUE, 'GOOGLE');

-- --------------------------------------------------------
-- 3. Bảng Course (Sử dụng 'subject' thay vì 'title')
-- --------------------------------------------------------
INSERT INTO course (id, subject, description, image, price, duration_hours, category_id, instructor_id, is_active) VALUES
(1, 'Python & Data Analysis toàn tập', 'Học cách phân tích dữ liệu kinh doanh với Pandas và NumPy.', 'python_data.jpg', 1200000, 35.5, 1, 2, TRUE),
(2, 'Làm chủ Figma & UI/UX Design', 'Khóa học thiết kế giao diện ứng dụng chuẩn quốc tế từ con số 0.', 'figma_uiux.jpg', 850000, 20.0, 2, 3, TRUE),
(3, 'Digital Marketing Thực Chiến 2026', 'Chạy quảng cáo Facebook, Google Ads và tối ưu SEO.', 'marketing.jpg', 1500000, 40.0, 3, 4, TRUE),
(4, 'Kỹ năng Quản Lý Thời Gian & Hiệu Suất', 'Phương pháp Pomodoro, ma trận Eisenhower.', 'time_manage.jpg', 0, 5.0, 5, 4, TRUE);

-- --------------------------------------------------------
-- 4. Bảng Lesson (Sử dụng 'subject' thay vì 'title')
-- --------------------------------------------------------
INSERT INTO lesson (id, subject, content, image, course_id, is_active) VALUES
(1, 'Cài đặt môi trường Python & Jupyter', '<p>Hướng dẫn chi tiết cách cài đặt Anaconda và thư viện cần thiết.</p>', 'lesson_py1.jpg', 1, TRUE),
(2, 'Giới thiệu về thư viện Pandas', '<p>Cách đọc file CSV, Excel và xử lý dữ liệu thiếu.</p>', 'lesson_py2.jpg', 1, TRUE),
(3, 'Nguyên lý phối màu trong UI', '<p>Sử dụng bánh xe màu sắc và tỷ lệ 60-30-10 trong thiết kế.</p>', 'lesson_ui1.jpg', 2, TRUE),
(4, 'Ma trận Eisenhower là gì?', '<p>Phân biệt giữa việc Quan trọng và việc Khẩn cấp.</p>', 'lesson_soft1.jpg', 4, TRUE);

-- --------------------------------------------------------
-- 5. Bảng lesson_comment (Đã đổi tên bảng)
-- --------------------------------------------------------
INSERT INTO lesson_comment (id, content, lesson_id, user_id) VALUES
(1, 'Bài này giảng rất dễ hiểu, đặc biệt là phần phân tích data.', 2, 5),
(2, 'Cho mình hỏi tỷ lệ 60-30-10 áp dụng cho app Dark Mode như thế nào?', 3, 6);

-- --------------------------------------------------------
-- 6. Bảng Course_Like
-- --------------------------------------------------------
INSERT INTO course_like (id, user_id, course_id) VALUES
(1, 5, 1),
(2, 5, 3),
(3, 6, 2),
(4, 6, 4);

-- --------------------------------------------------------
-- 7. Bảng Enrollment
-- --------------------------------------------------------
INSERT INTO enrollment (id, progress_percent, student_id, course_id) VALUES
(1, 50.0, 5, 1),
(2, 0.0, 6, 2),
(3, 100.0, 6, 4);

-- --------------------------------------------------------
-- 8. Bảng Lesson_Progress
-- --------------------------------------------------------
INSERT INTO lesson_progress (id, is_completed, enrollment_id, lesson_id, completed_time) VALUES
(1, TRUE, 1, 1, '2026-05-01 09:00:00'),
(2, FALSE, 1, 2, NULL),
(3, FALSE, 2, 3, NULL),
(4, TRUE, 3, 4, '2026-05-10 15:30:00');

-- --------------------------------------------------------
-- 9. Bảng Payment
-- --------------------------------------------------------
INSERT INTO payment (id, amount, payment_method, status, transaction_reference, enrollment_id, paid_time) VALUES
(1, 1200000, 'ZALOPAY', 'SUCCESS', 'ZLP_987654321', 1, '2026-04-20 10:15:00'),
(2, 850000, 'STRIPE', 'PENDING', 'STRIPE_CH_112233', 2, NULL),
(3, 0, 'CASH', 'SUCCESS', 'FREE_COURSE_PROMO', 3, '2026-05-05 08:00:00');