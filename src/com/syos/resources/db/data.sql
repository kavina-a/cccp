INSERT INTO Employee (EmployeeID, Email, Username, Password, Role, Status, CreatedAt)
VALUES
    (1, 'manager1@syos.com', 'manager1', 'hashed_password_1', 'MANAGER', 'ACTIVE', CURRENT_TIMESTAMP),
    (2, 'cashier1@syos.com', 'cashier1', 'hashed_password_2', 'CASHIER', 'ACTIVE', CURRENT_TIMESTAMP),
    (3, 'cashier2@syos.com', 'cashier2', 'hashed_password_3', 'CASHIER', 'ACTIVE', CURRENT_TIMESTAMP);


INSERT INTO Product (ItemCode, ItemName, Price, IsActive, IsDeleted, UpdatedDateTime, UpdatedBy, Status, ReorderLevel)
VALUES
    ('BEV-ICF-250ML-1101', 'Iced Coffee - Vanilla Blend 250ml', 650.00, 1, 0, CURRENT_TIMESTAMP, 1, 'ACTIVE', 20),
    ('SNK-ALM-100G-9002', 'Almond Energy Bites 100g', 890.00, 1, 0, CURRENT_TIMESTAMP, 2, 'ACTIVE', 15),
    ('BEV-GRN-500ML-1023', 'Cold Brew Green Tea 500ml', 750.00, 1, 0, CURRENT_TIMESTAMP, 3, 'ACTIVE', 10);


INSERT INTO StoreStock (ItemCode, BatchCode, InitialStock, CurrentStock, ExpiryDate, ReceivedDate, Status, UpdatedBy, UpdatedDateTime)
VALUES
    ('BEV-ICF-250ML-1101', 'BCH-202406A', 100, 80, DATE_ADD(CURRENT_DATE, INTERVAL 90 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), 'ACTIVE', 1, CURRENT_TIMESTAMP),
    ('SNK-ALM-100G-9002', 'BCH-202406B', 100, 80, DATE_ADD(CURRENT_DATE, INTERVAL 90 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), 'ACTIVE', 2, CURRENT_TIMESTAMP),
    ('BEV-GRN-500ML-1023', 'BCH-202406C', 100, 80, DATE_ADD(CURRENT_DATE, INTERVAL 90 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY), 'ACTIVE', 3, CURRENT_TIMESTAMP);


INSERT INTO Shelf (ShelfID, ItemCode, QuantityOnShelf, Location, Status, UpdatedBy, UpdatedAt)
VALUES
    ('SHELF-A1', 'BEV-ICF-250ML-1101', 20, 'Front Display A1', 'ACTIVE', 1, CURRENT_TIMESTAMP),
    ('SHELF-B2', 'SNK-ALM-100G-9002', 20, 'Front Display B2', 'ACTIVE', 2, CURRENT_TIMESTAMP),
    ('SHELF-C3', 'BEV-GRN-500ML-1023', 20, 'Front Display C3', 'ACTIVE', 3, CURRENT_TIMESTAMP);


INSERT INTO ShelfInventory (ItemCode, BatchCode, ShelfID, QuantityTransferred, QuantityRemaining, MovedDate, UpdatedBy, Status)
VALUES
    ('BEV-ICF-250ML-1101', 'BCH-202406A', 'SHELF-A1', 20, 18, CURRENT_TIMESTAMP, 1, 'ACTIVE'),
    ('SNK-ALM-100G-9002', 'BCH-202406B', 'SHELF-B2', 20, 18, CURRENT_TIMESTAMP, 2, 'ACTIVE'),
    ('BEV-GRN-500ML-1023', 'BCH-202406C', 'SHELF-C3', 20, 18, CURRENT_TIMESTAMP, 3, 'ACTIVE');

INSERT INTO WebProduct (itemCode, itemName, price, status, lastUpdated)
VALUES
    ('BEV-ICF-250ML-1101', 'Iced Coffee - Vanilla Blend 250ml', 650.00, 'ACTIVE', CURRENT_TIMESTAMP),
    ('SNK-ALM-100G-9002', 'Almond Energy Bites 100g', 890.00, 'ACTIVE', CURRENT_TIMESTAMP),
    ('BEV-GRN-500ML-1023', 'Cold Brew Green Tea 500ml', 750.00, 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO WebInventory (itemCode, batchCode, quantityRemaining, quantityTransferred, transferredDate, status, updatedDateTime, lastUpdatedBy)
VALUES
    ('BEV-ICF-250ML-1101', 'BCH-202406A', 30, 30, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, 1),
    ('SNK-ALM-100G-9002', 'BCH-202406B', 30, 30, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, 2),
    ('BEV-GRN-500ML-1023', 'BCH-202406C', 30, 30, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, 3);

INSERT INTO Customer (Name, Email, Username, Address, Password)
VALUES
    ('Olivia Carter', 'olivia@syos.com', 'oliviaC', '23 Greenway Blvd, NY', 'hashed_pass_1'),
    ('Ethan Ross', 'ethan@syos.com', 'ethanR', '77 Central Ave, LA', 'hashed_pass_2'),
    ('Walk-In', 'walkin@syos.com', 'walkin', 'N/A', 'walkin123');