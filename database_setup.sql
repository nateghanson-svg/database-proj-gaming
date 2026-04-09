This is the code used to create the tables and insert data in SQL

CREATE TABLE Companies (
    company_id INTEGER PRIMARY KEY,
    name TEXT NOT NULL);

INSERT INTO Companies (company_id, name) VALUES (1, 'Nintendo');
INSERT INTO Companies (company_id, name) VALUES (2, 'Sony');
INSERT INTO Companies (company_id, name) VALUES (3, 'Microsoft');
INSERT INTO Companies (company_id, name) VALUES (4, 'Mojang Studios');
INSERT INTO Companies (company_id, name) VALUES (5, 'Team Cherry');

CREATE TABLE Platforms (
    platform_id INTEGER PRIMARY KEY,
    name TEXT NOT NULL);

INSERT INTO Platforms (platform_id, name) VALUES (1, 'Switch');
INSERT INTO Platforms (platform_id, name) VALUES (2, 'Playstation 5');
INSERT INTO Platforms (platform_id, name) VALUES (3, 'Xbox Series X');
INSERT INTO Platforms (platform_id, name) VALUES (4, 'PC');
INSERT INTO Platforms (platform_id, name) VALUES (5, 'Mobile');

CREATE TABLE Games (
    game_id INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    release_year INTEGER,
    company_id INTEGER,
    FOREIGN KEY (company_id) REFERENCES Companies(company_id));

INSERT INTO Games (game_id, title, release_year, company_id) VALUES (1, 'Zelda: Breath of the Wild', 2017, 1);
INSERT INTO Games (game_id, title, release_year, company_id) VALUES (2, 'Super Mario Odyssey', 2017, 1);
INSERT INTO Games (game_id, title, release_year, company_id) VALUES (3, 'Spider-Man: Miles Morales', 2020, 2);
INSERT INTO Games (game_id, title, release_year, company_id) VALUES (4, 'Halo Infinite', 2021, 3);
INSERT INTO Games (game_id, title, release_year, company_id) VALUES (5, 'God of War Ragnarok', 2022, 2);
INSERT INTO Games (game_id, title, release_year, company_id) VALUES (6, 'Minecraft', 2011, 4);
INSERT INTO Games (game_id, title, release_year, company_id) VALUES (7, 'Hollow Knight: Silksong', 2025, 5);

CREATE TABLE Game_Platform (
    game_id INTEGER,
    platform_id INTEGER,
    copies_sold INTEGER,
    in_stock INTEGER DEFAULT 0,
    PRIMARY KEY (game_id, platform_id),
    FOREIGN KEY (game_id) REFERENCES Games(game_id),
    FOREIGN KEY (platform_id) REFERENCES Platforms(platform_id));

INSERT INTO Game_Platform (game_id, platform_id, copies_sold, in_stock) VALUES (1, 1, 20000000, 0);
INSERT INTO Game_Platform (game_id, platform_id, copies_sold, in_stock) VALUES (2, 1, 18000000, 0);
INSERT INTO Game_Platform (game_id, platform_id, copies_sold, in_stock) VALUES (3, 2, 8000000, 0);
INSERT INTO Game_Platform (game_id, platform_id, copies_sold, in_stock) VALUES (4, 3, 10000000, 0);
INSERT INTO Game_Platform (game_id, platform_id, copies_sold, in_stock) VALUES (5, 2, 12000000, 0);
INSERT INTO Game_Platform (game_id, platform_id, copies_sold, in_stock) VALUES (7, 1, 1500000, 0);
INSERT INTO Game_Platform (game_id, platform_id, copies_sold, in_stock) VALUES (7, 4, 1500000, 0);
INSERT INTO Game_Platform (game_id, platform_id, copies_sold, in_stock) VALUES (5, 4, 5000000, 0);
INSERT INTO Game_Platform (game_id, platform_id, copies_sold, in_stock) VALUES (6, 1, 35000000, 0);
INSERT INTO Game_Platform (game_id, platform_id, copies_sold, in_stock) VALUES (6, 2, 60000000, 0);
INSERT INTO Game_Platform (game_id, platform_id, copies_sold, in_stock) VALUES (6, 3, 55000000, 0);
INSERT INTO Game_Platform (game_id, platform_id, copies_sold, in_stock) VALUES (6, 4, 90000000, 0);
INSERT INTO Game_Platform (game_id, platform_id, copies_sold, in_stock) VALUES (6, 5, 110000000, 0);

CREATE TABLE Transactions (
    transaction_id INTEGER PRIMARY KEY,
    game_id INTEGER,
    platform_id INTEGER,
    client_id INTEGER,
    quantity INTEGER,
    transaction_type TEXT,
    transaction_date TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (game_id) REFERENCES Games(game_id),
    FOREIGN KEY (platform_id) REFERENCES Platforms(platform_id));
