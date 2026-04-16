-- Tạo Database (Nếu chưa có)
CREATE DATABASE IF NOT EXISTS elearningdb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE elearningdb;

-- 1. Bảng Category (Danh mục khóa học)
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- 2. Bảng User
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, 
    full_name VARCHAR(255) NOT NULL,
    avatar VARCHAR(500) NULL,
    role ENUM('ADMIN', 'INSTRUCTOR', 'STUDENT') NOT NULL,
    is_staff BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    auth_provider ENUM('LOCAL', 'GOOGLE', 'FACEBOOK') DEFAULT 'LOCAL',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Bảng Course (Khóa học)
CREATE TABLE course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject VARCHAR(255) NOT NULL,
    description TEXT,
    image VARCHAR(500),
    price DECIMAL(10,2) DEFAULT 0.00,
    duration_hours DOUBLE,
    category_id BIGINT NULL,
    instructor_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE, -- Thêm trường active
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_course_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL,
    CONSTRAINT fk_course_instructor FOREIGN KEY (instructor_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 4. Bảng Lesson (Bài học)
CREATE TABLE lesson (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject VARCHAR(255) NOT NULL,
    content TEXT, -- Thêm trường content
    image VARCHAR(500),
    course_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE, -- Thêm trường active
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_lesson_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);

-- 5. Bảng Comment (Bình luận & Phản hồi)
CREATE TABLE lesson_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    lesson_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_lesson FOREIGN KEY (lesson_id) REFERENCES lesson(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Bảng Course_Like (Quản lý Like theo cách chuẩn hóa)
CREATE TABLE course_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_like_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_course_like UNIQUE (user_id, course_id) -- Đảm bảo không like trùng
);

-- 6. Bảng Enrollment (Đăng ký học)
CREATE TABLE enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    progress_percent DOUBLE DEFAULT 0.0,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    enrolled_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    CONSTRAINT uq_student_course UNIQUE (student_id, course_id) -- Chống đăng ký trùng
);

-- 7. Bảng Lesson_Progress (Tiến độ chi tiết từng bài học)
CREATE TABLE lesson_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    is_completed BOOLEAN DEFAULT FALSE,
    enrollment_id BIGINT NOT NULL,
    lesson_id BIGINT NOT NULL,
    completed_time TIMESTAMP NULL,
    CONSTRAINT fk_progress_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollment(id) ON DELETE CASCADE,
    CONSTRAINT fk_progress_lesson FOREIGN KEY (lesson_id) REFERENCES lesson(id) ON DELETE CASCADE,
    CONSTRAINT uq_enrollment_lesson UNIQUE (enrollment_id, lesson_id) 
);

-- 8. Bảng Payment (Thanh toán)
CREATE TABLE payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('CASH', 'PAYPAL', 'STRIPE', 'MOMO', 'ZALOPAY') NOT NULL,
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    transaction_reference VARCHAR(255) NULL,
    enrollment_id BIGINT NOT NULL UNIQUE, 
    paid_time TIMESTAMP NULL,
    CONSTRAINT fk_payment_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollment(id) ON DELETE CASCADE
);