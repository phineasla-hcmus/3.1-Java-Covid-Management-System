create database covid_management;

use covid_management;

create table User
(
	userID varchar(12),
    pwd char(50),
    roleID tinyint,
    primary key (userID)
);

create table Log
(
	logID int AUTO_INCREMENT,
    userID varchar(12),
    logMsg nvarchar(500),
    logTime datetime,
    primary key (logID)
);

create table Ban
(
	userID varchar(12),
    expiredDate datetime,
    primary key (userID)
);

create table Role
(
	roleID tinyint,
    roleName nvarchar(20),
    primary key (roleID)
);

create table ManagedUser
(
	idCard varchar(12),
	fullName nvarchar(50),
	yob smallint,
	relatedPerson varchar(12),
    debt int,
	address nvarchar(100),
	state tinyint,
	primary key (idCard)
);

-- NewUser dùng để check xem ManagedUser có phải là đăng nhập lần đầu, nếu có thì bắt đổi password
create table NewUser
(
	userID varchar(12),
	primary key (userID),
	foreign key (userID) references ManagedUser(idCard)
);

create table StateHistory
(
	userID varchar(12),
	time datetime,
	state tinyint,
	primary key (userID, time)
);

create table TreatmentPlaceHistory
(
	userID varchar(12),
	time datetime,
	treatID int,
	primary key (userID, time)
);

create table TreatmentPlace
(
	treatID int AUTO_INCREMENT,
	name nvarchar(30),
	address nvarchar(100),
	capacity int,
	currentReception int,
	primary key (treatID)
);

create table District
(
	districtID char(3),
	districtName nvarchar(20),
	cityID char(2),
	primary key (districtID)
);

create table Ward
(
	wardID char(5),
	wardName nvarchar(20),
	districtID char(3),
	primary key (wardID)
);

create table City
(
	cityID char(2),
	cityName nvarchar(30),
	primary key (cityID)
);

create table Package
(
	packageID int AUTO_INCREMENT,
	name nvarchar(100),
	limitPerPerson tinyint,
	dayCooldown tinyint,
	price decimal(10,3),
	primary key (packageID)
);

-- Flow: 
-- 1.	Thêm vào giỏ hàng --> Insert vào CartItem
-- 1.1	Nếu ManagedUser.debt vượt quá mức quy định thì không cho thêm vào giỏ hàng nữa
-- 2.	Trong view Cart, có nút Buy --> Chuyển data từ CartItem sang OrderHistory, OrderItem, và cộng vào ManagedUser.debt
-- 3.	User dùng payment screen để trả cho ManagedUser.debt --> thành công --> ManagedUser.debt = 0
--		NOTE: Trả hết ManagedUser.debt, không hỗ trợ trả góp
create table CartItem
(
	userID varchar(12),
	packageID int,
	quantity tinyint,
	price decimal(10,3),
	primary key (userID, packageID),
    foreign key (userID) references User(userID),
    foreign key (packageID) references Package(packageID)
);

create table OrderHistory
(
	orderID bigint AUTO_INCREMENT,
	userID varchar(12),
	timeOrder datetime,
	totalOrderMoney decimal(10,3),
	primary key (orderID)
);

create table OrderItem
(
	orderID bigint,
	packageID int,
	orderItemQuantity tinyint,
	orderItemPrice decimal(10,3),
	primary key (orderID, packageID),
	foreign key (orderID) references OrderHistory(orderID)
);

-- Là bản copy của TransactionHistory cho client, dùng để đối soát
create table PaymentHistory
(
	transactionID bigint,
	userID varchar(12),
	paymentTime datetime,
	totalMoney decimal(10,3),
	primary key (transactionID)
);

-- BỎ DO KHÔNG CÓ THỜI GIAN
-- Log của những order chưa được trả tiền
create table PendingPayment
(
	orderID bigint,
    userID varchar(12),
    primary key (orderID),
	foreign key (orderID) references OrderHistory(orderID)
);

-- SCHEMA CỦA PAYMENT SERVER
create table TransactionAccount
(
	userID varchar(12),
	balance decimal(10,3),
	primary key (userID)
);

create table TransactionAdmin
(
	userID varchar(12),
    primary key (userID)
);
create table TransactionHistory
(
	transactionID bigint AUTO_INCREMENT,
	fromID varchar(12),
	toID varchar(12),
	paymentTime datetime,
	totalMoney decimal(10,3),
	primary key (transactionID),
	foreign key (fromID) references TransactionAccount(userID),
	foreign key (toID) references TransactionAccount(userID)
);
-- SCHEMA CỦA PAYMENT SERVER

alter table User
add
constraint FK_User_Role foreign key (roleID) references Role(roleID);

alter table Log
add
constraint FK_Log_User foreign key (userID) references User(userID);

alter table Ban
add
constraint FK_Ban_User foreign key (userID) references User(userID);

alter table ManagedUser
add
constraint FK_ManagedUser_User foreign key (idCard) references User(userID);

-- Tích hợp vô chung address nvarchar nên không cần nữa
-- alter table ManagedUser
-- add
-- constraint FK_User_Ward foreign key (wardID) references Ward(wardID);

alter table StateHistory
add constraint FK_StateHistory_ManagedUser foreign key (userID) references ManagedUser(idCard);

alter table TreatmentPlaceHistory
add
constraint FK_TreatmentPlaceHistory_ManagedUser foreign key (userID) references ManagedUser(idCard);

alter table TreatmentPlaceHistory
add
constraint FK_TreatmentPlaceHistory_TreatmentPlace foreign key (treatID) references TreatmentPlace(treatID);

-- Tích hợp vô chung address nvarchar nên không cần nữa
-- alter table TreatmentPlace
-- add constraint FK_TreatmentPlace_Ward foreign key (wardID) references Ward(wardID);

alter table OrderHistory
add constraint FK_OrderHistory_ManagedUser foreign key (userID) references ManagedUser(idCard);

alter table OrderItem
add
constraint FK_OrderItem_ConsumptionHistory foreign key (orderID) references OrderHistory(orderID);

ALTER TABLE OrderItem
ADD CONSTRAINT FK_OrderItem_Package FOREIGN KEY(packageID)
REFERENCES Package(packageID) ON DELETE CASCADE;

alter table District
add constraint FK_District_City foreign key (cityID) references City(cityID);

alter table Ward
add constraint FK_Ward_District foreign key (districtID) references District(districtID);

ALTER TABLE manageduser ADD FULLTEXT(fullName);

ALTER TABLE package ADD FULLTEXT(name);