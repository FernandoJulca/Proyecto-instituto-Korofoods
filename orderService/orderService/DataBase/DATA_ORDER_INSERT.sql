INSERT INTO TB_PEDIDO (ID_MESA, ID_USUARIO, ID_RESERVA, SUBTOTAL, TOTAL, ESTADO) VALUES
(1, 3, 1, 120.00, 120.00, 'PA'),
(5, 3, 3, 95.50, 95.50, 'PA'),
(20, 3, 4, 156.00, 156.00, 'EP'),
(7, 3, 2, 75.00, 75.00, 'PA');

INSERT INTO TB_DETALLE_PEDIDO (ID_PEDIDO, ID_PLATO, ESTADO, CANTIDAD, PRECIO_UNITARIO, SUBTOTAL) VALUES
-- Pedido 1 (Reserva evento anime)
(1, 9, 'PED', 2, 45.00, 90.00), -- Bento del Asesino
(1, 19, 'PED', 2, 12.00, 24.00), -- Chicha Morada
(1, 5, 'PED', 1, 20.00, 20.00), -- Onigiri
-- Pedido 2 (Reserva simple)
(2, 17, 'PED', 2, 38.00, 76.00), -- Lomo Saltado
(2, 21, 'PED', 2, 10.00, 20.00), -- Limonada
-- Pedido 3 (En proceso)
(3, 18, 'PED', 2, 42.00, 84.00), -- Ceviche
(3, 15, 'PED', 1, 42.00, 42.00), -- Pollo Hermanos
(3, 23, 'PED', 2, 15.00, 30.00), -- Café de Gotham
-- Pedido 4
(4, 10, 'ENT', 2, 35.00, 70.00), -- Ramen
(4, 22, 'ENT', 1, 8.00, 8.00); -- Inca Kola