# MySQL
CREATE DATABASE souvenirdb DEFAULT CHARACTER SET utf8mb4;

USE souvenirdb;

CREATE TABLE countries (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL,
    PRIMARY KEY (id)
) DEFAULT CHARACTER SET utf8mb4;

CREATE TABLE producers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL,
    country_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (country_id) REFERENCES countries (id)
) DEFAULT CHARACTER SET utf8mb4;

CREATE TABLE souvenirs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    producer_id BIGINT NOT NULL,
    release_date DATE NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (producer_id) REFERENCES producers (id)
) DEFAULT CHARACTER SET utf8mb4;

INSERT INTO countries(name) VALUES
('Франция'), ('Австралия'), ('Германия'), ('Италия'), ('Австрия'), ('Швеция'), ('Япония');

INSERT INTO producers(name, country_id) VALUES
('Организация 1234', 2), ('ИП А.А.Иванов', 5), ('ОАО 2345', 4), ('ООО 5678', 6),
('Тестовая Организация 1', 1), ('ПАО Тест', 4), ('ООО Ромашка', 3);

INSERT INTO souvenirs(name, producer_id, release_date, price) VALUES
('Открытка', 6, '2022-03-16', 1999.99), ('Рюкзак', 1, '2021-01-01', 5578.90), ('Блокнот', 2, '2022-05-15', 354.12),
('Чайник', 2, '2018-01-24', 4500.00), ('Рюкзак', 4, '2021-08-28', 7899.90), ('Блокнот', 3, '2019-11-24', 125.87),
('Часы', 1, '2015-10-10', 15987.56), ('Книга', 7, '2018-03-15', 1234.78), ('Открытка', 2, '2022-01-15', 341.90),
('Магнит на холодильник', 5, '2020-12-28', 2567.12), ('Часы', 2, '2016-10-25', 7685.99), ('Тарелка', 3, '2021-11-03', 5678.13),
('Кружка', 1, '2019-01-01', 4123.90), ('Ложка', 6, '2020-04-09', 3211.13), ('Рюкзак', 2, '2020-02-17', 1250.56),
('Книга', 3, '2021-10-10', 567.89), ('Чайник', 6, '2018-05-22', 1678.99);