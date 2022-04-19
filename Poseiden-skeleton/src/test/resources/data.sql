-- create users
insert into Users(fullname, username, password, role) values("Administrator", "admin", "$2a$10$pBV8ILO/s/nao4wVnGLrh.sa/rnr5pDpbeC4E.KNzQWoy8obFZdaa", "ADMIN");
insert into Users(fullname, username, password, role) values("User", "user", "$2a$10$pBV8ILO/s/nao4wVnGLrh.sa/rnr5pDpbeC4E.KNzQWoy8obFZdaa", "USER");
insert into Users(fullname, username, password, role) values("John Boyd", "john.boyd@gmail.com", "$2a$10$ep//kwapcPwllF/btNb0kezT9u3P.7rd4qvS1ZDkhVyS8hXSgFvp.", "USER");

-- create bdlists
insert into BidList (account, type, bidQuantity)
values
("Account_001", "BID_TYPE_001", "1.11"),
("Account_002", "BID_TYPE_002", "2.22"),
("Account_003", "BID_TYPE_003", "3.33"),
("Account_004", "BID_TYPE_004", "4.44");


-- create curves point
insert into curvepoint (curveId, term, value)
values
(11, 1.01, 1000.0001),
(22, 2.02, 2000.0002),
(33, 3.03, 3000.0003),
(44, 4.04, 4000.0004);
