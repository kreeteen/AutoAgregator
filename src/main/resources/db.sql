DROP TABLE IF EXISTS car_mods_map CASCADE;
DROP TABLE IF EXISTS mods_categories CASCADE;
DROP TABLE IF EXISTS vehicle_cars CASCADE;
DROP TABLE IF EXISTS project_tags CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 1. Пользователи
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50),
    phone_number VARCHAR(20) NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Направления тюнинга (Категории / Страницы)
CREATE TABLE project_tags (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE -- 'Дрифт', 'Станс', 'Оффроуд', 'Драг'
);

-- 3. Доступные опции-галочки (Привязаны к конкретной категории)
CREATE TABLE mods_categories (
    id SERIAL PRIMARY KEY,
    project_tag_id INT NOT NULL,     -- К какой странице/категории относится галочка
    name VARCHAR(100) NOT NULL,      -- Название галочки ('Лебедка', 'Гидроручник')

    CONSTRAINT fk_mod_tag FOREIGN KEY (project_tag_id) REFERENCES project_tags(id) ON DELETE CASCADE
);

-- 4. Автомобили
CREATE TABLE vehicle_cars (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    project_tag_id INT NOT NULL,     -- Страница, на которой лежит авто
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    manufacture_year INT NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    city VARCHAR(100) NOT NULL,
    description TEXT,                 -- Здесь продавец распишет все нюансы текстом
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_car_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_car_tag FOREIGN KEY (project_tag_id) REFERENCES project_tags(id) ON DELETE RESTRICT
);

CREATE TABLE car_mods_map (
    car_id INT NOT NULL,
    mod_category_id INT NOT NULL,

    PRIMARY KEY (car_id, mod_category_id),
    CONSTRAINT fk_map_car FOREIGN KEY (car_id) REFERENCES vehicle_cars(id) ON DELETE CASCADE,
    CONSTRAINT fk_map_mod FOREIGN KEY (mod_category_id) REFERENCES mods_categories(id) ON DELETE CASCADE
);
