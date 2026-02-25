INSERT INTO TB_CALIFICACION 
(ID_USUARIO, TIPO_ENTIDAD, ID_ENTIDAD, PUNTUACION, COMENTARIO, ESTADO)
VALUES
-- Plato 1
(6, 'PLATO', 1, 5, 'Brutal, volvería a pedirlo mil veces', 'ACT'),
(7, 'PLATO', 1, 4, 'Muy rico pero llegó un poco frío', 'ACT'),


-- Plato 2
(6, 'PLATO', 2, 3, 'Normalito, nada wow', 'ACT'),
(7, 'PLATO', 2, 4, 'Buen sabor y buena porción', 'ACT'),


-- Plato 3
(6, 'PLATO', 3, 5, 'Delicioso, sabor casero', 'ACT'),
(7, 'PLATO', 3, 5, 'Perfecto, 10/10', 'ACT'),


-- Plato 4
(6, 'PLATO', 4, 1, 'Terrible experiencia 😭', 'ACT'),
(7, 'PLATO', 4, 2, 'Podría mejorar bastante', 'ACT'),

-- Plato 5
(6,  'PLATO', 5, 5, 'Mi favorito de todos', 'ACT'),
(7, 'PLATO', 5, 5, 'Excelente presentación', 'ACT'),


-- Estados diferentes para pruebas
(6, 'PLATO', 6, 3, 'Regular', 'INA'),
(7, 'PLATO', 6, 1, 'Comentario ofensivo', 'REP');