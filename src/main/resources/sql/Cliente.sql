-- ==========================================
-- TABLA: CLIENTES
-- ==========================================
DROP TABLE IF EXISTS clientes CASCADE;

CREATE TABLE clientes (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    cedula VARCHAR(20) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    correo VARCHAR(100)
);

SELECT setval(
    pg_get_serial_sequence('clientes', 'id'),
    (SELECT MAX(id) FROM clientes),
    true
);

SELECT * FROM clientes;