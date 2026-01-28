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
    name VARCHAR(150) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    category ingredient_category NOT NULL
);

CREATE TABLE dish_ingredient (
    dish_id INT NOT NULL,
    ingredient_id INT NOT NULL,
    required_quantity DOUBLE PRECISION NOT NULL,
    unit VARCHAR(20) NOT NULL,
    PRIMARY KEY (dish_id, ingredient_id),
    CONSTRAINT fk_dish FOREIGN KEY (dish_id) REFERENCES dish(id) ON DELETE CASCADE,
    CONSTRAINT fk_ingredient_dish FOREIGN KEY (ingredient_id) REFERENCES ingredient(id)
);

INSERT INTO dish_ingredient (dish_id, ingredient_id, required_quantity, unit)
VALUES
(1, 1, 1, 'piece'),
(1, 2, 0.25, 'kg'),
(2, 3, 0.5, 'kg'),
(2, 4, 0.15, 'L');

UPDATE dish SET price = 15000 WHERE id = 1;
UPDATE dish SET price = 18000 WHERE id = 2;

CREATE TYPE ingredient_category AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');

CREATE TYPE movement_type AS ENUM ('IN', 'OUT');

CREATE TYPE Unit AS ENUM ('G', 'KG', 'L', 'ML', 'PIECE');

CREATE TYPE dish_type AS ENUM ('STARTER', 'MAIN_COURSE', 'DESSERT', 'BEVERAGE');

CREATE TABLE stock_movement (
    id SERIAL PRIMARY KEY,
    ingredient_id INT NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    unit VARCHAR(20) NOT NULL, -- Ex: 'KG', 'L', 'G'
    type movement_type NOT NULL,
    creation_datetime TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredient(id) ON DELETE CASCADE
);
