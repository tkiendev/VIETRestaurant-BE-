-- Sample data seed for demo database
-- Run after schema is applied

-- Accounts and Users
INSERT INTO Account (Username, Password, RoleID, IsActive) VALUES
('admin','adminpass', 1, TRUE),
('cashier','cashierpass', 2, TRUE),
('kitchen','kitchenpass', 3, TRUE),
('customer1','custpass', NULL, TRUE);

INSERT INTO Role (RoleName) VALUES ('admin'), ('cashier'), ('kitchen');

INSERT INTO `User` (AccountID, UserType, FullName, Phone, Email, Address, RewardPoints, CustomerTier)
VALUES
((SELECT AccountID FROM Account WHERE Username='admin'), 'Admin', 'Admin User', NULL, NULL, NULL, 0, NULL),
((SELECT AccountID FROM Account WHERE Username='cashier'), 'Staff', 'Cashier User', NULL, NULL, NULL, 0, NULL),
((SELECT AccountID FROM Account WHERE Username='kitchen'), 'Staff', 'Kitchen User', NULL, NULL, NULL, 0, NULL),
((SELECT AccountID FROM Account WHERE Username='customer1'), 'Customer', 'Customer One', '0911000111', 'cust1@example.com', NULL, 0, 'Thường');

-- Suppliers
INSERT INTO Supplier (SupplierName, ContactInfo) VALUES
('Nha cung cap A', '0912000111'),
('Nha cung cap B', '0912000222');

-- Categories
INSERT INTO Category (CategoryName) VALUES ('Món chính'), ('Khai vị'), ('Tráng miệng');

-- Ingredients
INSERT INTO Ingredient (Name, Unit, DefaultUnitCost) VALUES
('Thịt bò', 'kg', 120000),
('Hành tây', 'kg', 15000),
('Ớt', 'kg', 30000),
('Gạo', 'kg', 20000),
('Dầu ăn', 'lit', 25000);

-- Initial stock entries
INSERT INTO IngredientStock (IngredientID, QuantityOnHand) VALUES
(1, 10),
(2, 20),
(3, 5),
(4, 50),
(5, 30);

-- Purchase Orders and details (to set latest costs)
INSERT INTO PurchaseOrder (SupplierID, TotalAmount) VALUES
(1, 600000),
(2, 300000);

INSERT INTO PurchaseOrderDetail (PurchaseOrderID, IngredientID, Quantity, UnitCost) VALUES
((SELECT PurchaseOrderID FROM PurchaseOrder LIMIT 1), 1, 5, 120000),
((SELECT PurchaseOrderID FROM PurchaseOrder LIMIT 1), 2, 10, 15000),
((SELECT PurchaseOrderID FROM PurchaseOrder LIMIT 2 OFFSET 1), 3, 5, 30000);

-- Menu Items
INSERT INTO MenuItem (ItemName, CategoryID, Price) VALUES
('Bò xào', (SELECT CategoryID FROM Category WHERE CategoryName='Món chính'), 150000),
('Cơm trắng', (SELECT CategoryID FROM Category WHERE CategoryName='Món chính'), 30000),
('Gỏi hành tây', (SELECT CategoryID FROM Category WHERE CategoryName='Khai vị'), 50000);

-- Recipes (MenuItemIngredient)
INSERT INTO MenuItemIngredient (MenuItemID, IngredientID, Quantity) VALUES
((SELECT MenuItemID FROM MenuItem WHERE ItemName='Bò xào'), 1, 0.2),
((SELECT MenuItemID FROM MenuItem WHERE ItemName='Bò xào'), 2, 0.05),
((SELECT MenuItemID FROM MenuItem WHERE ItemName='Cơm trắng'), 4, 0.2),
((SELECT MenuItemID FROM MenuItem WHERE ItemName='Gỏi hành tây'), 2, 0.1),
((SELECT MenuItemID FROM MenuItem WHERE ItemName='Gỏi hành tây'), 3, 0.02);

-- Bills and BillDetails (some sales)
INSERT INTO Bill (TableID, CustomerID, CashierID, TotalAmount, Discount, TimeIn, Status) VALUES
(1, (SELECT UserID FROM `User` WHERE FullName='Customer One'), (SELECT UserID FROM `User` WHERE FullName='Cashier User'), 150000, 0, NOW(), 'Chưa thanh toán');

INSERT INTO BillDetail (BillID, MenuItemID, Quantity, UnitPrice) VALUES
((SELECT BillID FROM Bill LIMIT 1), (SELECT MenuItemID FROM MenuItem WHERE ItemName='Bò xào'), 1, 150000);

-- Update IngredientStock after sales (decrement for 1 Bò xào)
UPDATE IngredientStock SET QuantityOnHand = QuantityOnHand - 0.2 WHERE IngredientID = 1;
UPDATE IngredientStock SET QuantityOnHand = QuantityOnHand - 0.05 WHERE IngredientID = 2;

-- End of sample data
