-- ==========================================
-- TABLA: PRODUCTOS
-- ==========================================
DROP TABLE IF EXISTS productos CASCADE;

CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DOUBLE PRECISION NOT NULL CHECK (precio >= 0),
    cantidad INT NOT NULL CHECK (cantidad >= 0),
    categoria_id INT,
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id)
            REFERENCES categorias(id)
                ON DELETE RESTRICT
);

SELECT setval(
    pg_get_serial_sequence('productos', 'id'),
    (SELECT MAX(id) FROM productos),
    true
);

SELECT * FROM productos;