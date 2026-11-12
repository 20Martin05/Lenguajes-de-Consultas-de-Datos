-- ==========================================
-- TABLA: FACTURA_DETALLE
-- ==========================================
DROP TABLE IF EXISTS factura_detalle CASCADE;

CREATE TABLE factura_detalle (
    id               SERIAL PRIMARY KEY,
    factura_id       INT NOT NULL,
    producto_id      INT NOT NULL,
    cantidad         INT NOT NULL CHECK (cantidad > 0),
    precio_unitario  NUMERIC(10,2) NOT NULL CHECK (precio_unitario >= 0),
    CONSTRAINT fk_detalle_factura FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE RESTRICT,
    CONSTRAINT uq_factura_producto UNIQUE (factura_id, producto_id)
);

CREATE INDEX idx_detalle_factura ON factura_detalle (factura_id);

INSERT INTO factura_detalle (id, factura_id, producto_id, cantidad, precio_unitario) VALUES
    (1, 1, 1, 1, 15.75),
    (2, 2, 2, 1, 21.5),
    (3, 3, 3, 1, 27.25),
    (4, 4, 4, 1, 33),
    (5, 5, 5, 1, 38.75),
    (6, 6, 6, 1, 44.5),
    (7, 7, 7, 1, 50.25),
    (8, 8, 8, 1, 56),
    (9, 9, 9, 1, 61.75),
    (10, 10, 10, 1, 67.5),
    (11, 11, 11, 1, 73.25),
    (12, 12, 12, 1, 79),
    (13, 13, 13, 1, 84.75),
    (14, 14, 14, 1, 90.5),
    (15, 15, 15, 1, 96.25),
    (16, 16, 1, 1, 102),
    (17, 17, 2, 1, 107.75),
    (18, 18, 3, 1, 113.5),
    (19, 19, 4, 1, 119.25),
    (20, 20, 5, 1, 125),
    (21, 21, 6, 1, 130.75),
    (22, 22, 7, 1, 136.5),
    (23, 23, 8, 1, 142.25),
    (24, 24, 9, 1, 148),
    (25, 25, 10, 1, 153.75),
    (26, 26, 11, 1, 159.5),
    (27, 27, 12, 1, 165.25),
    (28, 28, 13, 1, 171),
    (29, 29, 14, 1, 176.75),
    (30, 30, 15, 1, 182.5),
    (31, 31, 1, 1, 188.25),
    (32, 32, 2, 1, 194),
    (33, 33, 3, 1, 199.75),
    (34, 34, 4, 1, 205.5),
    (35, 35, 5, 1, 211.25),
    (36, 36, 6, 1, 217),
    (37, 37, 7, 1, 222.75),
    (38, 38, 8, 1, 228.5),
    (39, 39, 9, 1, 234.25),
    (40, 40, 10, 1, 240),
    (41, 41, 11, 1, 245.75),
    (42, 42, 12, 1, 251.5),
    (43, 43, 13, 1, 257.25),
    (44, 44, 14, 1, 263),
    (45, 45, 15, 1, 268.75),
    (46, 46, 1, 1, 274.5),
    (47, 47, 2, 1, 280.25),
    (48, 48, 3, 1, 286),
    (49, 49, 4, 1, 291.75),
    (50, 50, 5, 1, 297.5);

SELECT setval(
    pg_get_serial_sequence('factura_detalle', 'id'),
    (SELECT MAX(id) FROM factura_detalle),
    true
);

SELECT * FROM factura_detalle;