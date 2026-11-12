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

INSERT INTO productos (id, nombre, precio, cantidad, categoria_id) VALUES
    (1, 'Leche Entera 1L', 1.10, 100, 1),
    (2, 'Queso Mozzarella 500g', 3.50, 50, 1),
    (3, 'Yogurt Natural 1L', 2.25, 60, 1),
    (4, 'Pechuga de Pollo 1kg', 4.80, 40, 2),
    (5, 'Carne Molida Especial 1kg', 5.20, 35, 2),
    (6, 'Manzanas Rojas 1kg', 2.00, 80, 3),
    (7, 'Plátano Seda 1kg', 1.20, 90, 3),
    (8, 'Zanahoria 1kg', 0.90, 70, 4),
    (9, 'Tomate Riñón 1kg', 1.00, 85, 4),
    (10, 'Arroz Flor 5kg', 6.50, 45, 5),
    (11, 'Lenteja 1kg', 1.80, 55, 5),
    (12, 'Pan de Molde Blanco', 1.75, 40, 6),
    (13, 'Jugo de Naranja 1L', 1.60, 65, 7),
    (14, 'Atún en Aceite 160g', 1.45, 120, 8),
    (15, 'Papas Fritas Onduladas 150g', 1.30, 75, 9);

SELECT setval(
    pg_get_serial_sequence('productos', 'id'),
    (SELECT MAX(id) FROM productos),
    true
);

SELECT * FROM productos;