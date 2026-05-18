-- v3
-- =====================================================================================
-- 1. XÓA BẢNG NẾU ĐÃ TỒN TẠI (Tắt kiểm tra khóa ngoại để xóa an toàn)
-- =====================================================================================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS KitchenOrder;
DROP TABLE IF EXISTS Payment;
DROP TABLE IF EXISTS BillDetail;
DROP TABLE IF EXISTS Bill;
DROP TABLE IF EXISTS Reservation;
DROP TABLE IF EXISTS DiningTable;
DROP TABLE IF EXISTS Area;
DROP TABLE IF EXISTS Floor;
DROP TABLE IF EXISTS MenuItem;
DROP TABLE IF EXISTS Category;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Account;
DROP TABLE IF EXISTS Role_Permission;
DROP TABLE IF EXISTS Permission;
DROP TABLE IF EXISTS Role;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================================
-- 2. TẠO CẤU TRÚC CÁC BẢNG (CREATE TABLES)
-- =====================================================================================

-- -------------------------------------------------------------------------------------
-- PHẦN I: HỆ THỐNG TÀI KHOẢN VÀ PHÂN QUYỀN
-- -------------------------------------------------------------------------------------

-- Bảng Nhóm Quyền (VD: Quản lý, Thu ngân, Bếp, Phục vụ)
CREATE TABLE Role (
    RoleID INT PRIMARY KEY AUTO_INCREMENT,
    RoleName VARCHAR(50) NOT NULL UNIQUE,
    Description VARCHAR(255)
);

-- Bảng Quyền hạn chi tiết (VD: VIEW_REPORT, CREATE_BILL, EDIT_MENU...)
CREATE TABLE Permission (
    PermissionID INT PRIMARY KEY AUTO_INCREMENT,
    PermissionCode VARCHAR(50) NOT NULL UNIQUE, 
    PermissionName VARCHAR(100) NOT NULL,
    Description VARCHAR(255)
);

-- Bảng Trung gian: Gán quyền chi tiết cho từng Nhóm quyền
CREATE TABLE Role_Permission (
    RoleID INT,
    PermissionID INT,
    PRIMARY KEY (RoleID, PermissionID),
    FOREIGN KEY (RoleID) REFERENCES Role(RoleID) ON DELETE CASCADE,
    FOREIGN KEY (PermissionID) REFERENCES Permission(PermissionID) ON DELETE CASCADE
);

-- Bảng Tài khoản (Chỉ dành cho nhân viên)
CREATE TABLE Account (
    AccountID INT PRIMARY KEY AUTO_INCREMENT,
    Username VARCHAR(50) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    RoleID INT,
    IsActive BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (RoleID) REFERENCES Role(RoleID) ON DELETE SET NULL
);

-- -------------------------------------------------------------------------------------
-- PHẦN II: QUẢN LÝ NHÂN VIÊN VÀ KHÁCH HÀNG (Tích hợp chung 1 bảng)
-- -------------------------------------------------------------------------------------

-- Bảng Thông tin người dùng (Nhân viên sẽ có AccountID, Khách hàng thì AccountID = NULL)
CREATE TABLE User (
    UserID INT PRIMARY KEY AUTO_INCREMENT,
    AccountID INT UNIQUE NULL,                     -- NULL nếu là Khách hàng
    UserType ENUM('Employee', 'Customer') NOT NULL DEFAULT 'Customer',
    FullName VARCHAR(100) NOT NULL,
    Phone VARCHAR(15),
    Email VARCHAR(100),
    Address VARCHAR(255),
    
    -- Các trường dành riêng cho Chăm sóc khách hàng
    RewardPoints INT DEFAULT 0,                    -- Điểm tích lũy cho khách
    CustomerTier VARCHAR(50) DEFAULT 'Thường',     -- Hạng khách (Thường, Bạc, Vàng, Kim Cương...)
    Notes VARCHAR(255),                            -- Ghi chú (VD: Khách VIP, Dị ứng hải sản...)
    
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID) ON DELETE CASCADE
);

-- -------------------------------------------------------------------------------------
-- PHẦN III: QUẢN LÝ THỰC ĐƠN, KHÔNG GIAN VÀ BÀN ĂN
-- -------------------------------------------------------------------------------------

-- Bảng Danh mục món ăn
CREATE TABLE Category (
    CategoryID INT PRIMARY KEY AUTO_INCREMENT,
    CategoryName VARCHAR(100) NOT NULL
);

-- Bảng Món ăn
CREATE TABLE MenuItem (
    MenuItemID INT PRIMARY KEY AUTO_INCREMENT,
    CategoryID INT,
    ItemName VARCHAR(100) NOT NULL,
    Price DECIMAL(18, 2) NOT NULL,
    ImageURL VARCHAR(255),
    IsAvailable BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID) ON DELETE SET NULL
);

-- Bảng Tầng
CREATE TABLE Floor (
    FloorID INT PRIMARY KEY AUTO_INCREMENT,
    FloorName VARCHAR(50) NOT NULL,
    Description VARCHAR(255)
);

-- Bảng Khu vực (Nằm trong Tầng, VD: Trong nhà, Ngoài trời, Phòng VIP)
CREATE TABLE Area (
    AreaID INT PRIMARY KEY AUTO_INCREMENT,
    FloorID INT NOT NULL,
    AreaName VARCHAR(100) NOT NULL,
    Description VARCHAR(255),
    FOREIGN KEY (FloorID) REFERENCES Floor(FloorID) ON DELETE CASCADE
);

-- Bảng Bàn ăn (Nằm trong Khu vực)
CREATE TABLE DiningTable (
    TableID INT PRIMARY KEY AUTO_INCREMENT,
    AreaID INT NOT NULL,
    TableName VARCHAR(50) NOT NULL,
    Capacity INT DEFAULT 4,
    Status VARCHAR(50) DEFAULT 'Trống', -- Trống, Có khách, Đã đặt, Đang dọn
    FOREIGN KEY (AreaID) REFERENCES Area(AreaID) ON DELETE CASCADE
);

-- -------------------------------------------------------------------------------------
-- PHẦN IV: QUY TRÌNH PHỤC VỤ (ĐẶT BÀN, ORDER, BẾP, THANH TOÁN)
-- -------------------------------------------------------------------------------------

-- Bảng Đặt bàn
CREATE TABLE Reservation (
    ReservationID INT PRIMARY KEY AUTO_INCREMENT,
    CustomerID INT,
    TableID INT,
    ReservationTime DATETIME NOT NULL,
    GuestCount INT,
    Status VARCHAR(50) DEFAULT 'Chờ xác nhận', -- Chờ xác nhận, Đã xác nhận, Đã đến, Đã hủy
    FOREIGN KEY (CustomerID) REFERENCES User(UserID) ON DELETE SET NULL,
    FOREIGN KEY (TableID) REFERENCES DiningTable(TableID) ON DELETE SET NULL
);

-- Bảng Hóa đơn
CREATE TABLE Bill (
    BillID INT PRIMARY KEY AUTO_INCREMENT,
    TableID INT,
    CustomerID INT NULL,                           -- Có thể lưu khách vãng lai (NULL) hoặc khách có trong hệ thống
    CashierID INT NULL,                            -- Nhân viên thu ngân xử lý hóa đơn
    TotalAmount DECIMAL(18, 2) DEFAULT 0,
    Discount DECIMAL(18, 2) DEFAULT 0,             -- Thêm cột giảm giá (dựa trên hạng khách/điểm)
    TimeIn DATETIME DEFAULT CURRENT_TIMESTAMP,
    TimeOut DATETIME NULL,
    Status VARCHAR(50) DEFAULT 'Chưa thanh toán',  -- Chưa thanh toán, Đã thanh toán, Đã hủy
    FOREIGN KEY (TableID) REFERENCES DiningTable(TableID) ON DELETE SET NULL,
    FOREIGN KEY (CustomerID) REFERENCES User(UserID) ON DELETE SET NULL,
    FOREIGN KEY (CashierID) REFERENCES User(UserID) ON DELETE SET NULL
);

-- Bảng Chi tiết Hóa đơn (Order món)
CREATE TABLE BillDetail (
    BillDetailID INT PRIMARY KEY AUTO_INCREMENT,
    BillID INT,
    MenuItemID INT,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(18, 2) NOT NULL,
    FOREIGN KEY (BillID) REFERENCES Bill(BillID) ON DELETE CASCADE,
    FOREIGN KEY (MenuItemID) REFERENCES MenuItem(MenuItemID) ON DELETE SET NULL
);

-- Bảng Quản lý Bếp (Điều phối món ăn)
CREATE TABLE KitchenOrder (
    KitchenOrderID INT PRIMARY KEY AUTO_INCREMENT,
    BillDetailID INT NOT NULL UNIQUE,
    Status VARCHAR(50) DEFAULT 'Chờ chế biến', -- Chờ chế biến, Đang nấu, Đã xong, Hết món
    SpecialNote VARCHAR(255),                  -- VD: Không hành, Ít cay...
    ReceivedTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    CompletedTime DATETIME NULL,
    FOREIGN KEY (BillDetailID) REFERENCES BillDetail(BillDetailID) ON DELETE CASCADE
);

-- Bảng Thanh toán
CREATE TABLE Payment (
    PaymentID INT PRIMARY KEY AUTO_INCREMENT,
    BillID INT UNIQUE,
    PaymentMethod VARCHAR(50),                 -- Tiền mặt, Thẻ tín dụng, Chuyển khoản, Ví điện tử
    AmountPaid DECIMAL(18, 2) NOT NULL,
    PaymentTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (BillID) REFERENCES Bill(BillID) ON DELETE CASCADE
);
