TRUNCATE TABLE repartidores;

-- ==========================================================
-- REPARTIDORES
-- ==========================================================
INSERT INTO repartidores (id, fecha_creación, fecha_ultima_modificacion, apellido, dni, estado_repartidor, nombre, teléfono, tipo_vehículo)
VALUES
    (UUID_TO_BIN('111bc999-9c0b-4ef8-bb6d-6bb9bd380111'), NOW(), NOW(), 'Quito', '28111222', 1, 'Esteban', '388111222', 'MOTO'),
    (UUID_TO_BIN('222bc999-9c0b-4ef8-bb6d-6bb9bd380222'), NOW(), NOW(), 'Melano', '30222333', 1, 'Rosa', '388222333', 'AUTO');
