DROP TABLE IF EXISTS facturas CASCADE;
CREATE TABLE facturas (
    id  SERIAL PRIMARY KEY,
    numero  VARCHAR(30) UNIQUE NOT NULL,
    fecha   DATE NOT NULL,
    subtotal    NUMERIC(10,2) NOT NULL,
    impuesto    NUMERIC(10,2) NOT NULL,
    total   NUMERIC(10,2) NOT NULL,
    cliente_id  INT NOT NULL,
    metodo_pago_id INT NOT NULL,
    CONSTRAINT fk_factura_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_factura_metodo_pago FOREIGN KEY (metodo_pago_id) REFERENCES metodo_de_pago(id)
);

SELECT setval(
    pg_get_serial_sequence('facturas', 'id'),
    (SELECT MAX(id) FROM facturas),
    true
);

SELECT * FROM facturas;