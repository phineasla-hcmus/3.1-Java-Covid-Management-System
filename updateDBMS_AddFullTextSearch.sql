USE covid_management;
ALTER TABLE manageduser ADD FULLTEXT(fullName);
ALTER TABLE package ADD FULLTEXT(name);