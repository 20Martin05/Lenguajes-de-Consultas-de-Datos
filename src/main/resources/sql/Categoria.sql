-- ==========================================
-- TABLA: CATEGORÍAS
-- ==========================================
DROP TABLE IF EXISTS categorias CASCADE;

CREATE TABLE categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

SELECT setval(
    pg_get_serial_sequence('categorias', 'id'),
    (SELECT MAX(id) FROM categorias),
    true
);

SELECT * FROM categorias;