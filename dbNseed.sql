-- Tạo Database (Nếu chưa có)
CREATE DATABASE IF NOT EXISTS elearningdb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE elearningdb;

-- ========================================================
-- PHẦN 1: TẠO BẢNG (TABLES)
-- ========================================================

-- 1. Bảng Category (Danh mục khóa học)
CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- 2. Bảng User (Đã thêm username)
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NULL,
    avatar VARCHAR(500) NULL,
    role ENUM('ADMIN', 'INSTRUCTOR', 'STUDENT') NOT NULL,
    is_instructor BOOLEAN DEFAULT FALSE,
    is_admin BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    auth_provider ENUM('LOCAL', 'GOOGLE') DEFAULT 'LOCAL',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Bảng Course (Khóa học)
CREATE TABLE course (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject VARCHAR(255) NOT NULL,
    description TEXT,
    image VARCHAR(500),
    price DECIMAL(10,2) DEFAULT 0.00,
    duration_hours DOUBLE,
    category_id INT NULL,
    instructor_id INT NOT NULL,
    video_url VARCHAR(500) NULL,
    level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') DEFAULT 'BEGINNER',
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_course_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL,
    CONSTRAINT fk_course_instructor FOREIGN KEY (instructor_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 4. Bảng Lesson (Bài học)
CREATE TABLE lesson (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject VARCHAR(255) NOT NULL,
    content TEXT,
    image VARCHAR(500),
    course_id INT NOT NULL,
    video_url VARCHAR(500) NULL,
    order_index INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_lesson_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);

-- 5. Bảng Lesson Comment (Bình luận bài học)
CREATE TABLE lesson_comment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    lesson_id INT NOT NULL,
    user_id INT NOT NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_lesson FOREIGN KEY (lesson_id) REFERENCES lesson(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 6. Bảng Enrollment (Đăng ký học)
CREATE TABLE enrollment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    progress_percent DOUBLE DEFAULT 0.0,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    enrolled_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    CONSTRAINT uq_student_course UNIQUE (student_id, course_id)
);

-- 7. Bảng Lesson_Progress (Tiến độ chi tiết từng bài học)
CREATE TABLE lesson_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    is_completed BOOLEAN DEFAULT FALSE,
    enrollment_id INT NOT NULL,
    lesson_id INT NOT NULL,
    completed_time TIMESTAMP NULL,
    CONSTRAINT fk_progress_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollment(id) ON DELETE CASCADE,
    CONSTRAINT fk_progress_lesson FOREIGN KEY (lesson_id) REFERENCES lesson(id) ON DELETE CASCADE,
    CONSTRAINT uq_enrollment_lesson UNIQUE (enrollment_id, lesson_id)
);

-- 8. Bảng Payment (Thanh toán)
CREATE TABLE payment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('CASH', 'MOMO', 'ZALOPAY') NOT NULL,
    status ENUM('PENDING', 'SUCCESS', 'FAILED') DEFAULT 'PENDING',
    transaction_reference VARCHAR(255) NULL,
    enrollment_id INT NOT NULL UNIQUE,
    paid_time TIMESTAMP NULL,
    CONSTRAINT fk_payment_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollment(id) ON DELETE CASCADE
);

-- ========================================================
-- PHẦN 2: CHÈN DỮ LIỆU MẪU
-- ========================================================

-- 1. Thêm danh mục
INSERT INTO category (id, name) VALUES
(1, 'Lập trình & CNTT'),
(2, 'Thiết kế Đồ họa'),
(3, 'Kinh doanh & Marketing'),
(4, 'Ngoại ngữ'),
(5, 'Kỹ năng mềm');

-- 2. Thêm User
INSERT INTO user (id, username, first_name, last_name, full_name, email, password, phone, avatar, role, is_instructor, is_admin, is_active, auth_provider) VALUES
(1, 'admin_system', 'Quản Trị', 'Hệ Thống', 'Hệ Thống Quản Trị', 'admin@eduspace.vn', '$2a$10$dummyHash123', '0901234567', 'admin_avatar.png', 'ADMIN', FALSE, TRUE, TRUE, 'LOCAL'),
(2, 'hoang_tech', 'Công Nghệ', 'Hoàng', 'Hoàng Công Nghệ', 'tech.lead@gmail.com', '$2a$10$dummyHash123', '0987654321', 'gv_tech.png', 'INSTRUCTOR', TRUE, FALSE, TRUE, 'LOCAL'),
(3, 'le_design', 'Mỹ Thuật', 'Lê', 'Lê Mỹ Thuật', 'designer.pro@gmail.com', '$2a$10$dummyHash123', '0912345678', 'gv_design.png', 'INSTRUCTOR', TRUE, FALSE, TRUE, 'LOCAL'),
(4, 'tran_ceo', 'Doanh Nhân', 'Trần', NULL, 'ceo.startup@gmail.com', '$2a$10$dummyHash123', '0933445566', 'gv_biz.png', 'INSTRUCTOR', TRUE, FALSE, TRUE, 'LOCAL'),
(5, 'nguyen_active', 'Học Chăm', 'Nguyễn', 'Nguyễn Học Chăm', 'student.active@gmail.com', '$2a$10$dummyHash123', '0966778899', 'sv_active.png', 'STUDENT', FALSE, FALSE, TRUE, 'LOCAL'),
(6, 'pham_casual', 'Giải Trí', 'Phạm', 'Phạm Giải Trí', 'student.casual@yahoo.com', '$2a$10$dummyOAuthHash', NULL, 'sv_casual.png', 'STUDENT', FALSE, FALSE, TRUE, 'GOOGLE');

-- 3. Thêm Khóa học
INSERT INTO course (id, subject, description, image, price, duration_hours, category_id, instructor_id, is_active) VALUES
(1, 'Python & Data Analysis toàn tập', 'Học cách phân tích dữ liệu kinh doanh với Pandas và NumPy.', 'python_data.jpg', 1200000, 35.5, 1, 2, TRUE),
(2, 'Làm chủ Figma & UI/UX Design', 'Khóa học thiết kế giao diện ứng dụng chuẩn quốc tế từ con số 0.', 'figma_uiux.jpg', 850000, 20.0, 2, 3, TRUE),
(3, 'Digital Marketing Thực Chiến 2026', 'Chạy quảng cáo Facebook, Google Ads và tối ưu SEO.', 'marketing.jpg', 1500000, 40.0, 3, 4, TRUE),
(4, 'Kỹ năng Quản Lý Thời Gian & Hiệu Suất', 'Phương pháp Pomodoro, ma trận Eisenhower.', 'time_manage.jpg', 0, 5.0, 5, 4, TRUE);

-- 4. Thêm Bài học
INSERT INTO lesson (id, subject, content, image, course_id, is_active) VALUES
(1, 'Cài đặt môi trường Python & Jupyter', '<p>Hướng dẫn chi tiết cách cài đặt Anaconda và thư viện cần thiết.</p>', 'lesson_py1.jpg', 1, TRUE),
(2, 'Giới thiệu về thư viện Pandas', '<p>Cách đọc file CSV, Excel và xử lý dữ liệu thiếu.</p>', 'lesson_py2.jpg', 1, TRUE),
(3, 'Nguyên lý phối màu trong UI', '<p>Sử dụng bánh xe màu sắc và tỷ lệ 60-30-10 trong thiết kế.</p>', 'lesson_ui1.jpg', 2, TRUE),
(4, 'Ma trận Eisenhower là gì?', '<p>Phân biệt giữa việc Quan trọng và việc Khẩn cấp.</p>', 'lesson_soft1.jpg', 4, TRUE);

-- 5. Thêm Bình luận
INSERT INTO lesson_comment (id, content, lesson_id, user_id) VALUES
(1, 'Bài này giảng rất dễ hiểu, đặc biệt là phần phân tích data.', 2, 5),
(2, 'Cho mình hỏi tỷ lệ 60-30-10 áp dụng cho app Dark Mode như thế nào?', 3, 6);

-- 6. Thêm Enrollment
INSERT INTO enrollment (id, progress_percent, student_id, course_id) VALUES
(1, 50.0, 5, 1),
(2, 0.0, 6, 2),
(3, 100.0, 6, 4);

-- 7. Thêm Lesson_Progress
INSERT INTO lesson_progress (id, is_completed, enrollment_id, lesson_id, completed_time) VALUES
(1, TRUE, 1, 1, '2026-05-01 09:00:00'),
(2, FALSE, 1, 2, NULL),
(3, FALSE, 2, 3, NULL),
(4, TRUE, 3, 4, '2026-05-10 15:30:00');

-- 8. Thêm Payment
--    Đã sửa dòng thứ 2 từ STRIPE thành MOMO để khớp với enum cho phép
INSERT INTO payment (id, amount, payment_method, status, transaction_reference, enrollment_id, paid_time) VALUES
(1, 1200000, 'ZALOPAY', 'SUCCESS', 'ZLP_987654321', 1, '2026-04-20 10:15:00'),
(2, 850000, 'MOMO', 'PENDING', 'MOMO_CH_112233', 2, NULL),
(3, 0, 'CASH', 'SUCCESS', 'FREE_COURSE_PROMO', 3, '2026-05-05 08:00:00');