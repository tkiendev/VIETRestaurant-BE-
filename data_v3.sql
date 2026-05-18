-- Insert data mẫu cho các quyền
INSERT INTO Role (RoleName, Description) VALUES
('admin', 'Quản trị viên toàn hệ thống'),
('bếp', 'Nhân viên khu vực bếp'),
('thu ngân', 'Nhân viên thu ngân'),
('order', 'Nhân viên phục vụ/lễ tân');

-- Tạo tài khoản Admin mặc định (mật khẩu mặc định: 123456)
INSERT INTO Account (Username, Password, RoleID, IsActive) VALUES
('admin', '123456', 1, TRUE);

-- Tạo User tương ứng với Account trên
INSERT INTO User (AccountID, UserType, FullName, Phone, Email, Address) VALUES
(1, 'Employee', 'Quản trị viên', '0123456789', 'admin@restaurant.com', 'Hà Nội');

-- Sample floors and areas
INSERT INTO Floor (FloorName, Description) VALUES
('Tầng trệt', 'Khu vực chính, gần cửa vào'),
('Tầng 1', 'Khu vực trong nhà, gần quầy bar'),
('Tầng 2', 'Phòng VIP và khu yên tĩnh');

-- 10 Area records
INSERT INTO Area (FloorID, AreaName, Description) VALUES
(1, 'Sảnh chính', 'Khu sảnh lớn, gần lối vào'),
(1, 'Ngoài trời', 'Khu bàn ngoài trời, thoáng mát'),
(1, 'Phòng VIP 1', 'Phòng VIP nhỏ, riêng tư'),
(2, 'Quầy Bar', 'Khu gần quầy bar, có ghế cao'),
(2, 'Phòng VIP 2', 'Phòng VIP lớn'),
(2, 'Khu gia đình', 'Bàn lớn cho gia đình'),
(3, 'Phòng họp', 'Phòng cho đoàn họp, riêng tư'),
(3, 'Sân thượng', 'Khu trên sân thượng, view đẹp'),
(3, 'Khu yên tĩnh', 'Dành cho khách muốn yên tĩnh'),
(3, 'Khu Private', 'Khu nhỏ kín cho sự kiện');
