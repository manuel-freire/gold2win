-- insert admin (username a, password aa)
INSERT INTO IWUser (ID, DINERO_DISPONIBLE, DINERO_RETENIDO, EMAIL, ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, ROLES, USERNAME) VALUES
(1, 0.0, 0.0, 'julianix882@gmail.com', TRUE, 'julián', 'Reguera Peñalosa', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'ADMIN,USER', 'julianix'),
(2, 100.0, 20.0, 'maria.garcia@example.com', TRUE, 'María', 'García López', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'USER', 'mgarcia'),
(3, 200.0, 50.0, 'carlos.hernandez@example.com', TRUE, 'Carlos', 'Hernández Ruiz', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'USER', 'chernandez'),
(4, 50.0, 0.0, 'ana.martinez@example.com', TRUE, 'Ana', 'Martínez Sánchez', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'USER', 'amartinez'),
(5, 300.0, 30.0, 'luis.flores@example.com', TRUE, 'Luis', 'Flores Mendoza', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'ADMIN', 'lflores'),
(6, 0.0, 0.0, 'sofia.rodriguez@example.com', TRUE, 'Sofía', 'Rodríguez Gómez', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'USER', 'srodriguez'),
(7, 120.0, 10.0, 'andres.lopez@example.com', TRUE, 'Andrés', 'López Martínez', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'USER', 'alopez'),
(8, 80.0, 5.0, 'carla.gomez@example.com', TRUE, 'Carla', 'Gómez García', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'USER', 'cgomez'),
(9, 500.0, 100.0, 'diego.sanchez@example.com', TRUE, 'Diego', 'Sánchez Pérez', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'ADMIN,USER', 'dsanchez'),
(10, 60.0, 0.0, 'lucia.ramirez@example.com', TRUE, 'Lucía', 'Ramírez Torres', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'USER', 'lramirez'),
(11, 130.55, 22.00, 'ramon@gmail.com', TRUE, 'Ramon', 'apellido1 apellido2', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'ADMIN,USER', 'Ramon'),
(12, 130.55, 22.00, 'Jose_Luis@gmail.com', TRUE, 'Jose Luis', 'apellido1 apellido2', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'USER', 'Jose Luis');

-- start id numbering from a value that is larger than any assigned above
ALTER SEQUENCE "PUBLIC"."GEN" RESTART WITH 1024;
