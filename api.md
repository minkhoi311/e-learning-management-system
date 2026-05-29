# Tài liệu Thiết kế API Endpoints - Hệ thống E-Learning

Tài liệu này mô tả các API Endpoint theo chuẩn RESTful, được thiết kế tối ưu, không dư thừa và tích hợp chặt chẽ với Database, Spring Security và frontend ReactJS. Hệ thống sử dụng luồng **OAuth2** để quản lý Access Token và Refresh Token.

## 1. Phân hệ Authentication & Current User

Tuân thủ cấu trúc đường dẫn quy định, tích hợp trực tiếp với bộ lọc của Spring Security và luồng OAuth2.

| Method | Endpoint | Mô tả | Role (Phân quyền) |
| :--- | :--- | :--- | :--- |
| `POST` | `/user/` | Đăng nhập hệ thống (Sinh ra OAuth2 token). | `PUBLIC` |
| `POST` | `/register/` | Đăng ký tài khoản (Tài khoản `INSTRUCTOR` mặc định `is_approved=false`). | `PUBLIC` |
| `GET` | `/user/current-user/` | Lấy toàn bộ thông tin profile của user đang đăng nhập. | `ADMIN`, `INSTRUCTOR`, `STUDENT` |
| `PATCH` | `/user/current-user/` | Cập nhật thông tin cá nhân (full_name, avatar_url). | `ADMIN`, `INSTRUCTOR`, `STUDENT` |
| `PATCH` | `/user/current-user/password` | Thay đổi mật khẩu. | `ADMIN`, `INSTRUCTOR`, `STUDENT` |

## 2. Phân hệ Quản lý Người dùng (Admin)

| Method | Endpoint | Mô tả | Role (Phân quyền) |
| :--- | :--- | :--- | :--- |
| `GET` | `/users/` | Lấy danh sách người dùng (Hỗ trợ phân trang). | `ADMIN` |
| `PATCH` | `/users/{instructor-id}/approve` | Duyệt tài khoản giảng viên (`is_approved=true`). | `ADMIN` |

## 3. Phân hệ Danh mục (Category)

| Method | Endpoint | Mô tả | Role (Phân quyền) |
| :--- | :--- | :--- | :--- |
| `GET` | `/categories/` | Lấy danh sách tất cả danh mục. | `PUBLIC` |

## 4. Phân hệ Khóa học (Course)

Hỗ trợ phân trang (tối đa 20/trang) và so sánh khóa học.

| Method | Endpoint | Mô tả | Role (Phân quyền) |
| :--- | :--- | :--- | :--- |
| `GET` | `/courses/` | Lấy danh sách khóa học (Query: `?search=..&sort=price&page=0&size=20`). | `PUBLIC` |
| `GET` | `/courses/compare` | So sánh khóa học (Query: `?ids=1,2,3`). | `PUBLIC` |
| `GET` | `/courses/{id}` | Lấy chi tiết khóa học (Bao gồm ds bài học nếu đã enroll). | `PUBLIC` |
| `POST` | `/courses/` | Tạo khóa học mới. | `INSTRUCTOR` |
| `PATCH` | `/courses/{id}` | Sửa thông tin khóa học. | `INSTRUCTOR` (Sở hữu), `ADMIN` |
| `DELETE` | `/courses/{id}` | Xóa khóa học. | `INSTRUCTOR` (Sở hữu), `ADMIN` |

## 5. Phân hệ Bài học & Bình luận (Lesson & Comment)

| Method | Endpoint | Mô tả | Role (Phân quyền) |
| :--- | :--- | :--- | :--- |
| `GET` | `/courses/{courseId}/lessons` | Lấy danh sách bài học của một khóa. | `STUDENT` (Đã enroll), `INSTRUCTOR`, `ADMIN` |
| `POST` | `/courses/{courseId}/lessons`| Thêm bài học mới vào khóa học. | `INSTRUCTOR` (Sở hữu) |
| `PUT` | `/lessons/{id}` | Sửa thông tin bài học. | `INSTRUCTOR` (Sở hữu) |
| `DELETE` | `/lessons/{id}` | Xóa bài học. | `INSTRUCTOR` (Sở hữu) |
| `POST` | `/lessons/{lessonId}/comments`| Đăng bình luận (hoặc phản hồi nếu truyền `parent_comment_id`). | `STUDENT` (Đã enroll), `INSTRUCTOR` |
| `DELETE` | `/comments/{id}` | Xóa bình luận. | User sở hữu, `ADMIN` |

## 6. Phân hệ Đăng ký, Tiến độ & Thanh toán (Enrollment, Progress & Payment)

| Method | Endpoint | Mô tả | Role (Phân quyền) |
| :--- | :--- | :--- | :--- |
| `POST` | `/courses/{courseId}/enroll` | Ghi danh vào khóa học (Tạo bản ghi `Enrollment`). | `STUDENT` |
| `GET` | `/enrollments/` | Xem danh sách các khóa học sinh viên đã đăng ký. | `STUDENT` |
| `POST` | `/enrollments/{enrollmentId}/pay` | Tạo phiên thanh toán, trả về URL (Momo/PayPal/Stripe). | `STUDENT` (Sở hữu) |
| `POST` | `/payments/webhook` | Nhận callback IPN từ cổng thanh toán để cập nhật trạng thái. | `PUBLIC` (S2S) |

## 7. Phân hệ Thống kê & Báo cáo (Statistics)

| Method | Endpoint | Mô tả | Role (Phân quyền) |
| :--- | :--- | :--- | :--- |
| `GET` | `/stats/overview` | Báo cáo doanh thu, lượt đăng ký theo khóa học/tháng.  | `INSTRUCTOR` |
| `GET` | `/stats/admin/overview` | Báo cáo tổng thể toàn hệ thống (doanh thu, khóa học, user). | `ADMIN` |

---

## Lưu ý kỹ thuật:
1. **Chat Realtime:** Sử dụng Firebase Realtime Database. Client ReactJS kết nối trực tiếp qua SDK. Backend Spring không cần xử lý endpoint cho tính năng này để tối ưu hiệu suất server.
2. **Webhook Thanh toán:** Endpoint `/payments/webhook` bỏ qua xác thực Spring Security thông thường. Thay vào đó, xác thực tính hợp lệ của request dựa trên chữ ký (Signature/Checksum) từ các đối tác cổng thanh toán.