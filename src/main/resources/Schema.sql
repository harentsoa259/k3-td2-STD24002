DROP TABLE IF EXISTS dish_ingredient CASCADE;
DROP TABLE IF EXISTS ingredient CASCADE;
DROP TABLE IF EXISTS dish CASCADE;

DROP TYPE IF EXISTS dish_type CASCADE;
DROP TYPE IF EXISTS ingredient_category CASCADE;

CREATE TYPE dish_type AS ENUM ('STARTER','MAIN','DESSERT');
CREATE TYPE ingredient_category AS ENUM ('VEGETABLE','ANIMAL','MARINE','DAIRY','OTHER');

CREATE TABLE dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    dish_type dish_type,
    price NUMERIC(10,2) NULL
);

CREATE TABLE ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    price NUMERIC(10,2),
    category ingredient_category
);

CREATE TABLE dish_ingredient (
    dish_id INT REFERENCES dish(id),
    ingredient_id INT REFERENCES ingredient(id),
    required_quantity NUMERIC(10,2),
    unit VARCHAR(10),
    PRIMARY KEY (dish_id, ingredient_id)
);

INSERT INTO dish_ingredient (dish_id, ingredient_id, required_quantity, unit)
VALUES
(1, 1, 1, 'piece'),
(1, 2, 0.25, 'kg'),
(2, 3, 0.5, 'kg'),
(2, 4, 0.15, 'L');

UPDATE dish SET price = 15000 WHERE id = 1;
UPDATE dish SET price = 18000 WHERE id = 2;
