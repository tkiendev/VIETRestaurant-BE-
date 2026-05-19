-- Dataset mẫu cho schema v4
-- Các lệnh INSERT này cung cấp dữ liệu demo ban đầu cho ứng dụng nhà hàng.

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM Role_Permission;
DELETE FROM Permission;
DELETE FROM ImportDetail;
DELETE FROM ImportReceipt;
DELETE FROM KitchenOrder;
DELETE FROM Payment;
DELETE FROM BillDetail;
DELETE FROM Bill;
DELETE FROM Reservation;
DELETE FROM DiningTable;
DELETE FROM Area;
DELETE FROM Floor;
DELETE FROM MenuItem;
DELETE FROM Category;
DELETE FROM `User`;
DELETE FROM Account;
DELETE FROM Role;
ALTER TABLE Role_Permission AUTO_INCREMENT = 1;
ALTER TABLE Permission AUTO_INCREMENT = 1;
ALTER TABLE ImportDetail AUTO_INCREMENT = 1;
ALTER TABLE ImportReceipt AUTO_INCREMENT = 1;
ALTER TABLE KitchenOrder AUTO_INCREMENT = 1;
ALTER TABLE Payment AUTO_INCREMENT = 1;
ALTER TABLE BillDetail AUTO_INCREMENT = 1;
ALTER TABLE Bill AUTO_INCREMENT = 1;
ALTER TABLE Reservation AUTO_INCREMENT = 1;
ALTER TABLE DiningTable AUTO_INCREMENT = 1;
ALTER TABLE Area AUTO_INCREMENT = 1;
ALTER TABLE Floor AUTO_INCREMENT = 1;
ALTER TABLE MenuItem AUTO_INCREMENT = 1;
ALTER TABLE Category AUTO_INCREMENT = 1;
ALTER TABLE `User` AUTO_INCREMENT = 1;
ALTER TABLE Account AUTO_INCREMENT = 1;
ALTER TABLE Role AUTO_INCREMENT = 1;
SET FOREIGN_KEY_CHECKS = 1;

-- Roles
INSERT INTO Role (RoleName, Description) VALUES
('Quản trị viên', 'Toàn quyền hệ thống'),
('Thu ngân', 'Thanh toán và quản lý hóa đơn'),
('Bếp', 'Quản lý đơn hàng bếp'),
('Phục vụ', 'Tiếp nhận order và phục vụ khách');

-- Permissions
INSERT INTO Permission (PermissionCode, PermissionName, Description) VALUES
('VIEW_MENU', 'Xem thực đơn', 'Cho phép xem danh sách món ăn'),
('CREATE_BILL', 'Tạo hóa đơn', 'Cho phép tạo và cập nhật hóa đơn'),
('PROCESS_PAYMENT', 'Xử lý thanh toán', 'Cho phép lưu thông tin thanh toán'),
('MANAGE_STOCK', 'Quản lý kho', 'Cho phép tạo phiếu nhập hàng'),
('VIEW_REPORT', 'Xem báo cáo', 'Cho phép xem báo cáo doanh thu và xuất nhập');

-- Role_Permission
INSERT INTO Role_Permission (RoleID, PermissionID) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(2, 2),
(2, 3),
(3, 4),
(4, 1),
(4, 2);

-- Accounts
INSERT INTO Account (Username, `Password`, RoleID, IsActive) VALUES
('admin', '123456', 1, TRUE),
('cashier01', '123456', 2, TRUE),
('chef01', '123456', 3, TRUE),
('staff01', '123456', 4, TRUE);

-- Users
INSERT INTO `User` (AccountID, UserType, FullName, Phone, Email, Address, RewardPoints, CustomerTier, Notes) VALUES
(1, 'Employee', 'Nguyễn Văn A', '0901111222', 'admin@demo.com', 'Hà Nội', 0, 'Kim Cương', 'Quản trị hệ thống'),
(2, 'Employee', 'Trần Thị B', '0902222333', 'cashier@demo.com', 'Hà Nội', 0, 'Thường', 'Thu ngân ca sáng'),
(3, 'Employee', 'Lê Văn C', '0903333444', 'chef@demo.com', 'Hà Nội', 0, 'Thường', 'Bếp chính'),
(4, 'Employee', 'Phạm Thị D', '0904444555', 'staff@demo.com', 'Hà Nội', 0, 'Thường', 'Phục vụ'),
(NULL, 'Customer', 'Nguyễn Thị E', '0911111222', 'customer1@mail.com', 'Hà Nội', 150, 'Vàng', 'Khách VIP'),
(NULL, 'Customer', 'Hoàng Văn F', '0912222333', 'customer2@mail.com', 'Hà Nội', 45, 'Bạc', 'Khách thân thiết'),
(NULL, 'Customer', 'Trần Văn G', '0913333444', 'customer3@mail.com', 'Hà Nội', 0, 'Thường', 'Khách vãng lai');

-- Danh mục
INSERT INTO Category (CategoryName) VALUES
('Khai vị'),
('Món chính'),
('Tráng miệng'),
('Đồ uống');

-- Món ăn
INSERT INTO MenuItem (CategoryID, ItemName, Price, CostPrice, ImageURL, IsAvailable) VALUES
(1, 'Gỏi cuốn', 45000.00, 22000.00, 'images/goi-cuon.jpg', TRUE),
(1, 'Súp hải sản', 59000.00, 28000.00, 'images/sup-hai-san.jpg', TRUE),
(2, 'Phở bò', 75000.00, 32000.00, 'images/pho-bo.jpg', TRUE),
(2, 'Cơm sườn', 85000.00, 40000.00, 'images/com-suon.jpg', TRUE),
(2, 'Bún chả', 78000.00, 35000.00, 'images/bun-cha.jpg', TRUE),
(2, 'Cá hồi nướng', 195000.00, 95000.00, 'images/ca-hoi-nuong.jpg', TRUE),
(3, 'Kem matcha', 52000.00, 18000.00, 'images/kem-matcha.jpg', TRUE),
(3, 'Chè thập cẩm', 43000.00, 15000.00, 'images/che-thap-cam.jpg', TRUE),
(4, 'Trà đá', 10000.00, 1000.00, 'images/tra-da.jpg', TRUE),
(4, 'Cà phê sữa đá', 29000.00, 7000.00, 'images/ca-phe-sua-da.jpg', TRUE);

-- Tầng
INSERT INTO Floor (FloorName, Description) VALUES
('Tầng trệt', 'Khu vực chính, gần lối ra vào'),
('Tầng 1', 'Khu vực trong nhà, yên tĩnh');

-- Khu vực
INSERT INTO Area (FloorID, AreaName, Description) VALUES
(1, 'Sảnh chính', 'Bàn nhỏ và trung bình'),
(1, 'Ngoài trời', 'Bàn ngoài trời thoáng mát'),
(2, 'Phòng VIP', 'Phòng riêng, phù hợp nhóm khách VIP');

-- Bàn ăn
INSERT INTO DiningTable (AreaID, TableName, Capacity, Status) VALUES
(1, 'Bàn 1', 4, 'Trống'),
(1, 'Bàn 2', 4, 'Có khách'),
(1, 'Bàn 3', 2, 'Trống'),
(2, 'Bàn 4', 6, 'Đang dọn'),
(2, 'Bàn 5', 4, 'Trống'),
(3, 'VIP 1', 8, 'Có khách');

-- Đặt bàn
INSERT INTO Reservation (CustomerID, TableID, ReservationTime, GuestCount, Status) VALUES
(5, 5, '2026-05-20 18:30:00', 4, 'Chờ xác nhận'),
(6, 6, '2026-05-21 12:00:00', 2, 'Đã xác nhận');

-- Hóa đơn
INSERT INTO Bill (TableID, CustomerID, CashierID, TotalAmount, Discount, TimeIn, TimeOut, Status) VALUES
(2, 5, 2, 320000.00, 20000.00, '2026-05-18 11:20:00', NULL, 'Chưa thanh toán'),
(6, 6, 2, 450000.00, 50000.00, '2026-05-17 19:10:00', '2026-05-17 20:05:00', 'Đã thanh toán');

-- Chi tiết hóa đơn
INSERT INTO BillDetail (BillID, MenuItemID, Quantity, UnitPrice, CostPrice) VALUES
(1, 3, 2, 75000.00, 32000.00),
(1, 9, 4, 10000.00, 1000.00),
(2, 4, 1, 85000.00, 40000.00),
(2, 10, 3, 29000.00, 7000.00),
(2, 7, 2, 52000.00, 18000.00);

-- Đơn bếp
INSERT INTO KitchenOrder (BillDetailID, Status, SpecialNote, ReceivedTime, CompletedTime) VALUES
(1, 'Đang nấu', 'Ít hành', '2026-05-18 11:21:00', NULL),
(2, 'Đang nấu', NULL, '2026-05-18 11:21:30', NULL),
(3, 'Đã xong', 'Nhiệt độ cao', '2026-05-17 19:12:00', '2026-05-17 19:30:00');

-- Thanh toán
INSERT INTO Payment (BillID, PaymentMethod, AmountPaid, PaymentTime) VALUES
(2, 'Tiền mặt', 400000.00, '2026-05-17 20:06:00');

-- Phiếu nhập hàng
INSERT INTO ImportReceipt (UserID, SupplierName, TotalCost, ImportTime, Notes) VALUES
(3, 'Công ty thực phẩm X', 1200000.00, '2026-05-16 08:30:00', 'Nhập nguyên liệu đầu tuần'),
(3, 'Nhà cung cấp Y', 750000.00, '2026-05-17 08:45:00', 'Nhập thực phẩm cho cuối tuần');

-- Chi tiết nhập hàng
INSERT INTO ImportDetail (ImportReceiptID, MenuItemID, Quantity, UnitCost) VALUES
(1, 3, 20, 28000.00),
(1, 4, 10, 38000.00),
(2, 7, 30, 16000.00),
(2, 9, 50, 900.00);
