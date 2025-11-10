TRUNCATE TABLE PLAYER;
DELETE FROM CLUB WHERE true;
DELETE FROM STADIUM WHERE true;
DELETE FROM CITY WHERE true;
DELETE FROM COUNTRY WHERE true;

INSERT INTO COUNTRY (CODE, NAME)
VALUES ('ARG', 'Argentina'),
       ('BEL', 'Belgium'),
       ('BRA', 'Brazil'),
       ('CIV', 'Ivory Coast'),
       ('CHL', 'Chili'),
       ('CMR', 'Cameroon'),
       ('COL', 'Columbia'),
       ('CRO', 'Croatia'),
       ('DEU', 'Germany'),
       ('DZA', 'Algeria'),
       ('ECU', 'Spain'),
       ('ENG', 'England'),
       ('ESP', 'Spain'),
       ('FRA', 'France'),
       ('GEO', 'Georgia'),
       ('HRV', 'Croatia'),
       ('ITA', 'Italy'),
       ('MAR', 'Morocco'),
       ('MLI', 'Mali'),
       ('NGA', 'Nigeria'),
       ('NLD', 'Netherlands'),
       ('POL', 'Poland'),
       ('PRT', 'Portugal'),
       ('ROU', 'Romania'),
       ('RUS', 'Russia'),
       ('SEN', 'Senegal'),
        ('URU', 'Uruguay'),
('WAL', 'Wales');

INSERT INTO CITY (CODE, NAME)
VALUES ('PAR', 'Paris');

INSERT INTO STADIUM (ID, CAPACITY, NAME)
VALUES (1, 50000, 'Decathlon Arena'),
       (2, 45000, 'Parc des princes'),
       (3, 66000, 'Orange Vélodrome'),
       (4, 60000, 'Groupama stadium'),
       (5, 90000, 'Camp Nou'),
       (6, 82000, 'Santiago Bernabeu'),
       (7, 70000, 'Old Trafford'),
       (8, 40000, 'Stamford Bridge'),
       (9, 65000, 'Emirates Stadium'),
       (10, 45000, 'Anfield Road'),
       (11, 40000, 'Etihad Stadium'),
       (12, 70000, 'White Hart Lane'),
       (13, 65000, 'Allianz Arena'),
       (14, 80000, 'Westfalen Stadion'),
       (15, 65000, 'Juventus Stadium'),
       (16, 55000, 'San Paolo'),
       (17, 50000, 'Est�dio do Drag�o'),
       (18, 65000, 'Stade de Luz'),
       (19, 50000, 'Est�dio Jos� Alvalade');

UPDATE STADIUM
SET STREET     = '1 rue de Boulogne',
    FK_CITY    = 'PAR',
    FK_COUNTRY = 'FRA'
WHERE STADIUM.NAME = 'Parc des princes';


INSERT INTO CLUB(CODE, NAME, FK_COUNTRY, FK_STADIUM)
VALUES ('LOSC', 'Lille OSC', 'FRA', 1),
       ('PSG', 'Paris SG', 'FRA', 2),
       ('OM', 'Marseille', 'FRA', 3),
       ('OL', 'Lyon', 'FRA', 4),
       ('BARCA', 'Barcelona', 'ESP', 5),
       ('REAL', 'Real Madrid', 'ESP', 6),
       ('MU', 'Manchester United', 'ENG', 7),
       ('CHE', 'Chelsea', 'ENG', 8),
       ('ARS', 'Arsenal', 'ENG', 9),
       ('LIV', 'Liverpool', 'ENG', 10),
       ('CITY', 'Manchester City', 'ENG', 11),
       ('TOT', 'Tottenham', 'ENG', 12),
       ('BAY', 'Bayern Munchen', 'DEU', 13),
       ('DOR', 'Borussia Dortmund', 'DEU', 14),
       ('JUVE', 'Juventus', 'ITA', 15),
       ('NAP', 'Napoli', 'ITA', 16),
       ('POR', 'Porto', 'PRT', 17),
       ('BEN', 'Benfica', 'PRT', 18),
       ('SPO', 'Sporting CP', 'PRT', 19);

DROP SEQUENCE PLAYER_SEQ;
CREATE SEQUENCE PLAYER_SEQ START WITH 1 INCREMENT BY 1;

INSERT INTO PLAYER (ID, NAME, FK_COUNTRY, FK_CLUB)
VALUES (NEXTVAL('PLAYER_SEQ'), 'Chevalier', 'FRA', 'PSG'),
       (NEXTVAL('PLAYER_SEQ'), 'Hakimi', 'MAR', 'PSG'),
       (NEXTVAL('PLAYER_SEQ'), 'Mendes', 'PRT', 'PSG'),
       (NEXTVAL('PLAYER_SEQ'), 'Marquinhos', 'BRA', 'PSG'),
       (NEXTVAL('PLAYER_SEQ'), 'Pacho', 'COL', 'PSG'),
       (NEXTVAL('PLAYER_SEQ'), 'Vitinha', 'PRT', 'PSG'),
       (NEXTVAL('PLAYER_SEQ'), 'Neves', 'PRT', 'PSG'),
       (NEXTVAL('PLAYER_SEQ'), 'Ruiz', 'ESP', 'PSG'),
       (NEXTVAL('PLAYER_SEQ'), 'Dembele', 'FRA', 'PSG'),
       (NEXTVAL('PLAYER_SEQ'), 'Doué', 'FRA', 'PSG'),
       (NEXTVAL('PLAYER_SEQ'), 'Barcola', 'FRA', 'PSG'),
       (NEXTVAL('PLAYER_SEQ'), 'Kvaratskhelia', 'GEO', 'PSG');
