-- ==========================================
-- TABLA: MÉTODOS DE PAGO
-- ==========================================
-- Elimina la tabla anterior para permitir recrear el esquema desde cero.
DROP TABLE IF EXISTS metodo_de_pago CASCADE;

-- Define los métodos de pago disponibles para las facturas.
CREATE TABLE metodo_de_pago (
    -- Identificador autoincremental y clave primaria.
    id SERIAL PRIMARY KEY,
    -- Nombre del método de pago, por ejemplo efectivo o tarjeta.
    pago VARCHAR(30),
    -- Descripción adicional del método de pago.
    descripcion VARCHAR(50),
    -- Indica si el método de pago se encuentra activo.
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

SELECT setval(
    pg_get_serial_sequence('metodo_de_pago', 'id'),
    (SELECT MAX(id) FROM metodo_de_pago),
    true
);

-- Consulta de comprobación del contenido de la tabla.
SELECT * FROM metodo_de_pago;