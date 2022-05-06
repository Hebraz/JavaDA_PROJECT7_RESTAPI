-- create users
insert into Users(fullname, username, password, role) values("Administrator", "admin", "$2a$10$pBV8ILO/s/nao4wVnGLrh.sa/rnr5pDpbeC4E.KNzQWoy8obFZdaa", "ROLE_ADMIN");
insert into Users(fullname, username, password, role) values("User", "user", "$2a$10$pBV8ILO/s/nao4wVnGLrh.sa/rnr5pDpbeC4E.KNzQWoy8obFZdaa", "ROLE_USER");
insert into Users(fullname, username, password, role) values("John Boyd", "john.boyd@gmail.com", "$2a$10$ep//kwapcPwllF/btNb0kezT9u3P.7rd4qvS1ZDkhVyS8hXSgFvp.", "ROLE_USER");

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

-- create ratings
insert into rating   (moodysRating, sandPRating, fitchRating, orderNumber)
values
("Moodys rating 001", "SandP rating 001", "Fitch rating 001", 1),
("Moodys rating 002", "SandP rating 002", "Fitch rating 002", 2),
("Moodys rating 003", "SandP rating 003", "Fitch rating 003", 3),
("Moodys rating 004", "SandP rating 004", "Fitch rating 004", 4);

-- create ruleName
insert into ruleName (name, description, json, template, sqlStr, sqlPart)
values
("Rule name 001", "Description rule name 001", "{Json:001}", "Template 001", "Select * from RuleName_001", "Sqlpart 001"),
("Rule name 002", "Description rule name 002", "{Json:002}", "Template 002", "Select * from RuleName_002", "Sqlpart 002"),
("Rule name 003", "Description rule name 003", "{Json:003}", "Template 003", "Select * from RuleName_003", "Sqlpart 003"),
("Rule name 004", "Description rule name 004", "{Json:004}", "Template 004", "Select * from RuleName_004", "Sqlpart 004");

-- create trade
insert into trade (account, type, buyQuantity)
values
("Account 001", "Type 001", 100.01),
("Account 002", "Type 002", 200.02),
("Account 003", "Type 003", 300.03),
("Account 004", "Type 004", 400.04);