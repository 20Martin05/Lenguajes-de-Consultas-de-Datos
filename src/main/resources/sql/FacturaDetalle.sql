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

SELECT setval(
    pg_get_serial_sequence('factura_detalle', 'id'),
    (SELECT MAX(id) FROM factura_detalle),
    true
);

SELECT * FROM factura_detalle;