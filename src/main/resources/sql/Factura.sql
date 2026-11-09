-- ==========================================
-- TABLA: FACTURAS
-- ==========================================
-- Elimina la tabla anterior para permitir recrear el esquema desde cero.
DROP TABLE IF EXISTS facturas CASCADE;
-- Define la estructura utilizada por la entidad JPA Factura.
CREATE TABLE facturas (
    -- Identificador autoincremental y clave primaria.
    id  SERIAL PRIMARY KEY,

    -- Número visible de la factura (único).
    numero  VARCHAR(30) UNIQUE NOT NULL,

    -- Fecha en la que se genera la factura.
    fecha   DATE NOT NULL,

    -- Valor total de los productos antes del impuesto.
    subtotal    NUMERIC(10,2) NOT NULL,

    -- Valor correspondiente al impuesto.
    impuesto    NUMERIC(10,2) NOT NULL,

    -- Valor total de la factura.
    total   NUMERIC(10,2) NOT NULL,

    -- Identificador del cliente asociado a la factura.
    cliente_id  INT NOT NULL,

    -- Identificador del método de pago utilizado en la factura.
    metodo_pago_id INT NOT NULL,

    -- Relaciona cada factura con un cliente.
    CONSTRAINT fk_factura_cliente
        FOREIGN KEY (cliente_id)
            REFERENCES clientes(id),

    -- Relaciona cada factura con un método de pago.
    CONSTRAINT fk_factura_metodo_pago
        FOREIGN KEY (metodo_pago_id)
            REFERENCES metodo_de_pago(id)
);

-- Consulta de comprobación del contenido de la tabla.
SELECT * FROM facturas;