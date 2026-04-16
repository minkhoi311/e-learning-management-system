# Phân tích Thực thể (Entities) - Hệ thống E-Learning (Cập nhật)

## 1. Thực thể `Category` (Danh mục khóa học)
* **id** (PK, Long, Auto Increment)
* **name** (String)
* **description** (Text)

## 2. Thực thể `User` (Người dùng)
* **id** (PK, Long, Auto Increment)
* **email** (String, Unique)
* **password** (String)
* **full_name** (String)
* **avatar_url** (String)
* **role** (Enum: `ADMIN`, `INSTRUCTOR`, `STUDENT`)
* **is_approved** (Boolean)
* **auth_provider** (Enum: `LOCAL`, `GOOGLE`, `FACEBOOK`)
* **created_time** (Timestamp)
* **updated_time** (Timestamp)

## 3. Thực thể `Course` (Khóa học)
* **id** (PK, Long)
* **title** (String)
* **description** (Text)
* **image** (String)
* **price** (Decimal)
* **duration_hours** (Double)
* **category_id** (Long) -> **[Khóa ngoại liên kết tới `Category.id`]**
* **instructor_id** (Long) -> **[Khóa ngoại liên kết tới `User.id`]**
* **created_time** (Timestamp)
* **updated_time** (Timestamp)

## 4. Thực thể `Lesson` (Bài học)
* **id** (PK, Long)
* **title** (String)
* **image** (String)
* **course_id** (Long) -> **[Khóa ngoại liên kết tới `Course.id`]**
* **created_time** (Timestamp)
* **updated_time** (Timestamp)

## 5. Thực thể `Comment`
* **id** (PK, Long)
* **content** (Text)
* **lesson_id** (Long) -> **[Khóa ngoại liên kết tới `Lesson.id`]**
* **user_id** (Long) -> **[Khóa ngoại liên kết tới `User.id`]**
* **created_time** (Timestamp)
* **updated_time** (Timestamp)

## 6. Thực thể `Enrollment` (Đăng ký học)
* **id** (PK, Long)
* **progress_percent** (Double)
* **student_id** (Long) -> **[Khóa ngoại liên kết tới `User.id`]**
* **course_id** (Long) -> **[Khóa ngoại liên kết tới `Course.id`]**
* **enrolled_time** (Timestamp)

## 7. Thực thể `Lesson_Progress` (Tiến độ chi tiết)
* **id** (PK, Long)
* **is_completed** (Boolean)
* **enrollment_id** (Long) -> **[Khóa ngoại liên kết tới `Enrollment.id`]**
* **lesson_id** (Long) -> **[Khóa ngoại liên kết tới `Lesson.id`]**
* **completed_time** (Timestamp, Nullable)

## 8. Thực thể `Payment` (Thanh toán)
* **id** (PK, Long)
* **amount** (Decimal)
* **payment_method** (Enum: `CASH`, `PAYPAL`, `STRIPE`, `MOMO`, `ZALOPAY`)
* **status** (Enum: `PENDING`, `SUCCESS`, `FAILED`, `REFUNDED`)
* **transaction_reference** (String)
* **enrollment_id** (Long, Unique) -> **[Khóa ngoại liên kết tới `Enrollment.id`]**
* **paid_time** (Timestamp, Nullable)