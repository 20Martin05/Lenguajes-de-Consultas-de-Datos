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

INSERT INTO clientes (id, nombre, apellido, cedula, telefono, correo) VALUES
    (1, 'Martin', 'Rosero', '1723456780', '0991234567', 'martin.rosero@ups.edu.ec'),
    (2, 'Patsy', 'Prieto', '1723456781', '0991234568', 'patsy.prieto@ups.edu.ec'),
    (3, 'Carlos', 'Mendoza', '1723456782', '0991234569', 'carlos.mendoza@gmail.com'),
    (4, 'Ana', 'Gomez', '1723456783', '0991234570', 'ana.gomez@hotmail.com'),
    (5, 'Luis', 'Torres', '1723456784', '0991234571', 'luis.torres@yahoo.com'),
    (6, 'Elena', 'Morales', '1723456785', '0991234572', 'elena.morales@outlook.com'),
    (7, 'Diego', 'Vargas', '1723456786', '0991234573', 'diego.vargas@gmail.com'),
    (8, 'Sofia', 'Castillo', '1723456787', '0991234574', 'sofia.castillo@hotmail.com'),
    (9, 'Javier', 'Rios', '1723456788', '0991234575', 'javier.rios@gmail.com'),
    (10, 'Valeria', 'Paredes', '1723456789', '0991234576', 'valeria.paredes@ups.edu.ec');

SELECT setval(
    pg_get_serial_sequence('clientes', 'id'),
    (SELECT MAX(id) FROM clientes),
    true
);

SELECT * FROM clientes;