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
DROP TABLE IF EXISTS MenuItemIngredient;
DROP TABLE IF EXISTS IngredientStock;
DROP TABLE IF EXISTS PurchaseOrderDetail;
DROP TABLE IF EXISTS PurchaseOrder;
DROP TABLE IF EXISTS Ingredient;
DROP TABLE IF EXISTS Supplier;

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

-- -------------------------------------------------------------------------------------
-- PHẦN V: QUẢN LÝ NGUYÊN LIỆU, NHẬP HÀNG VÀ CÔNG THỨC (COST & PROFIT)
-- -------------------------------------------------------------------------------------

-- Nhà cung cấp
CREATE TABLE Supplier (
    SupplierID INT PRIMARY KEY AUTO_INCREMENT,
    SupplierName VARCHAR(200) NOT NULL,
    ContactInfo VARCHAR(255)
);

-- Nguyên liệu
CREATE TABLE Ingredient (
    IngredientID INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(200) NOT NULL,
    Unit VARCHAR(50) NOT NULL,            -- Ví dụ: kg, g, l, ml, pcs
    DefaultUnitCost DECIMAL(18,4) DEFAULT 0, -- Giá đơn vị mặc định (dùng nếu chưa có nhập hàng)
    Notes VARCHAR(255)
);

-- Đơn hàng mua nguyên liệu (nhập kho)
CREATE TABLE PurchaseOrder (
    PurchaseOrderID INT PRIMARY KEY AUTO_INCREMENT,
    SupplierID INT,
    PurchaseDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    TotalAmount DECIMAL(18,4) DEFAULT 0,
    Remark VARCHAR(255),
    FOREIGN KEY (SupplierID) REFERENCES Supplier(SupplierID) ON DELETE SET NULL
);

-- Chi tiết đơn hàng mua (đơn vị, giá nhập tại thời điểm đó)
CREATE TABLE PurchaseOrderDetail (
    PODetailID INT PRIMARY KEY AUTO_INCREMENT,
    PurchaseOrderID INT,
    IngredientID INT NULL,
    Quantity DECIMAL(18,4) NOT NULL,
    UnitCost DECIMAL(18,4) NOT NULL,
    LineTotal DECIMAL(18,4) GENERATED ALWAYS AS (Quantity * UnitCost) STORED,
    FOREIGN KEY (PurchaseOrderID) REFERENCES PurchaseOrder(PurchaseOrderID) ON DELETE CASCADE,
    FOREIGN KEY (IngredientID) REFERENCES Ingredient(IngredientID) ON DELETE SET NULL
);

-- Tồn kho nguyên liệu (dùng để nhanh tra cứu số lượng hiện có)
CREATE TABLE IngredientStock (
    IngredientID INT PRIMARY KEY,
    QuantityOnHand DECIMAL(18,4) DEFAULT 0,
    ReorderLevel DECIMAL(18,4) DEFAULT 0,
    LastUpdated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (IngredientID) REFERENCES Ingredient(IngredientID) ON DELETE CASCADE
);

-- Bảng liên kết công thức: món ăn sử dụng nguyên liệu nào và bao nhiêu cho 1 đơn vị món
CREATE TABLE MenuItemIngredient (
    MenuItemIngredientID INT PRIMARY KEY AUTO_INCREMENT,
    MenuItemID INT,
    IngredientID INT,
    Quantity DECIMAL(18,4) NOT NULL, -- Số lượng nguyên liệu cần cho 1 món (theo đơn vị Ingredient.Unit)
    FOREIGN KEY (MenuItemID) REFERENCES MenuItem(MenuItemID) ON DELETE CASCADE,
    FOREIGN KEY (IngredientID) REFERENCES Ingredient(IngredientID) ON DELETE RESTRICT
);

-- View: Lấy giá nhập gần nhất cho mỗi nguyên liệu (dùng giá mới nhất nếu có)
CREATE VIEW IngredientLatestCost AS
SELECT pod.IngredientID, pod.UnitCost
FROM PurchaseOrderDetail pod
JOIN (
    SELECT IngredientID, MAX(PurchaseOrderID) AS MaxPO
    FROM PurchaseOrderDetail
    GROUP BY IngredientID
) x ON pod.IngredientID = x.IngredientID AND pod.PurchaseOrderID = x.MaxPO;

-- View: Chi phí gốc để chế biến 1 món (tổng chi phí nguyên liệu theo công thức)
CREATE VIEW MenuItemCost AS
SELECT m.MenuItemID,
       m.ItemName,
       COALESCE(SUM(mii.Quantity * COALESCE(ilc.UnitCost, i.DefaultUnitCost)), 0) AS Cost
FROM MenuItem m
LEFT JOIN MenuItemIngredient mii ON m.MenuItemID = mii.MenuItemID
LEFT JOIN Ingredient i ON i.IngredientID = mii.IngredientID
LEFT JOIN IngredientLatestCost ilc ON ilc.IngredientID = mii.IngredientID
GROUP BY m.MenuItemID, m.ItemName;

-- View: Lợi nhuận và tỷ lệ lợi nhuận cho mỗi món
CREATE VIEW MenuItemProfit AS
SELECT mc.MenuItemID,
       mc.ItemName,
       mc.Cost AS CostPrice,
       mi.Price AS SalePrice,
       (mi.Price - mc.Cost) AS Profit,
       CASE WHEN mi.Price > 0 THEN ROUND((mi.Price - mc.Cost) / mi.Price * 100, 2) ELSE NULL END AS ProfitMarginPercent
FROM MenuItemCost mc
JOIN MenuItem mi ON mi.MenuItemID = mc.MenuItemID;

-- Gợi ý: Truy vấn hiển thị dashboard tổng quan (ví dụ tổng lợi nhuận theo ngày / món)
-- SELECT mi.MenuItemID, mi.ItemName, SUM(bd.Quantity) AS SoldQty,
--        SUM(bd.Quantity * (bd.UnitPrice - COALESCE(mc.Cost,0))) AS TotalProfit
-- FROM BillDetail bd
-- JOIN MenuItem mi ON mi.MenuItemID = bd.MenuItemID
-- LEFT JOIN MenuItemCost mc ON mc.MenuItemID = mi.MenuItemID
-- GROUP BY mi.MenuItemID, mi.ItemName;

