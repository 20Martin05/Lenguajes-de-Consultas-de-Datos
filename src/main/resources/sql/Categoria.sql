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

INSERT INTO categorias (id, nombre, descripcion, estado) VALUES
    (1, 'LÁCTEOS', 'Productos lácteos y derivados', TRUE),
    (2, 'CARNES', 'Carnes rojas, blancas y embutidos', TRUE),
    (3, 'FRUTAS', 'Frutas frescas y seleccionadas', TRUE),
    (4, 'VERDURAS', 'Verduras y hortalizas frescas', TRUE),
    (5, 'GRANOS', 'Granos secos y legumbres', TRUE),
    (6, 'PANADERÍA', 'Panes, pasteles y repostería', TRUE),
    (7, 'BEBIDAS', 'Bebidas refrescantes y jugos', TRUE),
    (8, 'ENLATADOS', 'Alimentos en conserva y enlatados', TRUE),
    (9, 'SNACKS', 'Bocaditos y snacks variados', TRUE);

SELECT setval(
    pg_get_serial_sequence('categorias', 'id'),
    (SELECT MAX(id) FROM categorias),
    true
);

SELECT * FROM categorias;