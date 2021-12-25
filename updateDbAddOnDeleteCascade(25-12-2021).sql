use covid_management;

ALTER TABLE OrderItem
DROP FOREIGN KEY FK_OrderItem_Package;

ALTER TABLE OrderItem
ADD CONSTRAINT FK_OrderItem_Package FOREIGN KEY(packageID)
REFERENCES Package(packageID) ON DELETE CASCADE;