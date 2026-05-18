LƯU Ý DÀNH CHO MÔI TRƯỜNG PHÁT TRIỂN/TESTING

- Mật khẩu đang được lưu dưới dạng plaintext trong database cho mục đích thử nghiệm.
- Thư viện `jbcrypt` đã bị xóa khỏi `pom.xml` để tránh xử lý băm không mong muốn.
- TUYỆT ĐỐI KHÔNG ĐƯA MÃ NÀY LÊN PRODUCTION.
- Trước khi deploy lên production, khôi phục lại hashing (ví dụ dùng `BCryptPasswordEncoder`), và kiểm tra lại logic đăng ký/đăng nhập.
