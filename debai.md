Tài liệu Đặc tả API - Đề tài Quản lý Thực tập (Internship Management System)
1. Giới thiệu
   Hệ thống API Quản lý Thực tập hỗ trợ các chức năng:
   Quản lý giai đoạn thực tập (Internship Phase)
   Quản lý bảng tiêu chí đánh giá
   Quản lý đợt đánh giá (Assessment Round)
   Chức năng giáo viên hướng dẫn đánh giá sinh viên
   Sinh viên xem kết quả thực tập theo giai đoạn / đợt
   Hệ thống có phân quyền người dùng gồm các vai trò:
   ADMIN: Tạo/sửa/xóa giai đoạn, đợt, bảng đánh giá
   MENTOR: Đánh giá sinh viên theo đợt thực tập được phân công
   STUDENT: Xem kết quả đánh giá thực tập theo giai đoạn
   Tổng số API: 20
   Tổng điểm: 100 điểm, trong đó chức năng bắt buộc chiếm 60 điểm

2. Chuẩn Response API
   Tất cả response trả về theo format chuẩn:
   {
   "success": true,
   "message": "Thao tác thành công",
   "data": [],
   "errors": null,
   "timestamp": "2025-07-30T09:45:00"
   }


hoặc có phân trang :
{
"success": true,
"message": "Lấy danh sách thành công",
"data": {
"items": [
{ "id": 1, "name": "Item 1" },
{ "id": 2, "name": "Item 2" }
],
"pagination": {
"currentPage": 1,
"pageSize": 10,
"totalPages": 5,
"totalItems": 50
}
},
"errors": null,
"timestamp": "2025-07-30T09:45:00"
}

Trường thông tin:
success: true/false
message: Thông báo
data: Kết quả trả về (object hoặc array)
errors: Danh sách lỗi validation (nếu có)
timestamp: Thời gian server trả về
Response lỗi validation:
{
"success": false,
"message": "Dữ liệu không hợp lệ",
"data": null,
"errors": [
{ "field": "email", "message": "Email không hợp lệ" },
{ "field": "password", "message": "Mật khẩu phải tối thiểu 6 ký tự" }
],
"timestamp": "2025-07-30T09:45:00"
}

Mã trạng thái HTTP:
HTTP Status
Ý nghĩa
Tình huống sử dụng
200 OK
Thành công
GET, PUT, DELETE thành công
201 Created
Tạo mới thành công
POST thành công (ví dụ: tạo tài khoản, đợt thực tập)
400 Bad Request
Dữ liệu đầu vào không hợp lệ
Sai định dạng, thiếu trường, validation lỗi
401 Unauthorized
Thiếu token hoặc token sai
Không đăng nhập hoặc JWT không hợp lệ
403 Forbidden
Không có quyền truy cập
Truy cập sai vai trò
404 Not Found
Không tìm thấy tài nguyên
ID không tồn tại
409 Conflict
Xung đột logic nghiệp vụ
Đăng ký trùng, tên bị trùng
500 Internal Server Error
Lỗi hệ thống bất ngờ
Null pointer, exception không xử lý



3. Yêu cầu Test Case
   Mỗi API bắt buộc phải viết tối thiểu 3 test case:
   Loại test case
   Mô tả
   Happy path
   Truyền đúng input, kỳ vọng kết quả trả về đúng
   Invalid input
   Bỏ trường, input sai format, ID không tồn tại
   Unauthorized / Role
   Thiếu token(401), hoặc sai vai trò truy cập (403)
   Conflict / Business
   Trùng đăng ký, trái ràng buộc logic (đã ghi danh không đăng ký lại)



4. Cấu trúc Dự án (Spring Boot)
   src/main/java/com/example/intershipms
   ├── config                # JWT, Security, Exception Handler
   ├── controller           # API endpoints
   ├── dto                    # Request/Response models
   ├── entity                # JPA Entity
   ├── repository         # Spring Data JPA Repos
   ├── service              # Business logic (interface + impl)
   ├── mapper             # Map DTO <-> Entity
   └── util                     # Helper classes (Validation, Constants)

5. Yêu cầu Clean Code
   ✅ Tên hàm/tên biến rõ ràng, camelCase
   ✅ Tuân thủ kiến trúc phân tầng, tránh lặp code
   ✅ Mỗi lớp service tách rõ interface và implement
   ✅ Dùng @Valid, DTO và Mapper để tách logic entity
   ✅ Không trả trực tiếp Entity ra ngoài (dùng DTO)
   ✅ Tách config JWT/Exception thành package riêng
   ✅ Dùng enum thay cho chuỗi cứng (status, role...)
   ✅ Tuân thủ chuẩn REST

6. Yêu cầu Công nghệ Sử dụng
   Thành phần
   Công nghệ đề xuất
   Ngôn ngữ backend
   Java 17 trở lên
   Framework chính
   Spring Boot 3.x
   ORM
   Spring Data JPA + Hibernate
   CSDL
   PostGreSQL
   Bảo mật
   Spring Security + JWT
   Build tool
   Gradle
   Test
   Postman
   Triển khai
   Tomcat Server


    