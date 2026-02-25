/*
SHOW SERVER_ENCODING;
SHOW CLIENT_ENCODING;

*/
SET CLIENT_ENCODING TO 'UTF8';


-- Insertar datos de ejemplo con los nuevos campos
INSERT INTO TB_PAGO (
    ID_USUARIO, 
    ID_RESERVA, 
    ID_PEDIDO, 
    TIPO_PAGO, 
    MONTO, 
    METODO_PAGO, 
    FECHA_PAGO, 
    ESTADO, 
    REFERENCIA_PAGO,
    FECHA_CREACION,
    CODIGO_OPERACION,
    HASH_IMAGEN,
    URL_CAPTURA,
    TEXTO_EXTRAIDO,
    MONTO_DETECTADO,
    FECHA_DETECTADA,
    OBSERVACIONES
) VALUES

-- Pagos de RESERVAS (DR - Depósito Reserva)
(
    5, 1, NULL, 'DR', 15.00, 'YAPE', 
    '2026-02-20 10:30:00', 
    'PAG',
    'KORO-A1B2C3D4',
    '2026-02-20 10:25:00',
    'YP-789456123',
    'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
    'https://res.cloudinary.com/demo/image/upload/sample1.jpg',
    'Yape\nS/ 15.00\n20/02/2026 10:30\nYP-789456123\nKoroFood Restaurant',
    15.00,
    '2026-02-20 10:30:00',
    'Depósito para evento Noche de Animes - Validación automática exitosa'
),

(
    5, 2, NULL, 'DR', 15.00, 'PLIN', 
    '2026-03-10 14:15:00', 
    'PAG',
    'KORO-E5F6G7H8',
    '2026-03-10 14:10:00',
    'PL-321654987',
    'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9d0e1f2',
    'https://res.cloudinary.com/demo/image/upload/sample2.jpg',
    'Plin\nS/ 15.00\n10/03/2026 14:15\nPL-321654987\nKoroFood',
    15.00,
    '2026-03-10 14:15:00',
    'Depósito para evento Cine Temático - Validación automática exitosa'
),

(
    5, 3, NULL, 'DR', 15.00, 'TARJETA', 
    '2026-02-10 11:00:00', 
    'PAG',
    'KORO-I9J0K1L2',
    '2026-02-10 10:58:00',
    NULL, -- Tarjeta no tiene código de operación de Yape/Plin
    NULL, -- Tarjeta no requiere captura
    NULL,
    NULL,
    15.00,
    '2026-02-10 11:00:00',
    'Depósito para almuerzo familiar - Pago con tarjeta'
),

(
    5, 4, NULL, 'DR', 15.00, 'YAPE', 
    '2026-02-15 16:45:00', 
    'PAG',
    'KORO-M3N4O5P6',
    '2026-02-15 16:40:00',
    'YP-654987321',
    'f1e2d3c4b5a6978563214789654123abcdef0123456789abcdef0123456789ab',
    'https://res.cloudinary.com/demo/image/upload/sample4.jpg',
    'Yape\nS/ 15.00\n15/02/2026 16:45\nYP-654987321\nKoroFood',
    15.00,
    '2026-02-15 16:45:00',
    'Depósito para cena romántica - Validación automática exitosa'
),

-- Pagos de PEDIDOS (PP - Pago Pedido)
(
    2, NULL, 1, 'PP', 120.00, 'TARJETA', 
    '2026-02-25 21:30:00', 
    'PAG',
    'KORO-Q7R8S9T0',
    '2026-02-25 21:28:00',
    NULL, -- Tarjeta
    NULL,
    NULL,
    NULL,
    120.00,
    '2026-02-25 21:30:00',
    'Pago completo pedido evento anime - Pago con tarjeta'
),

(
    2, NULL, 2, 'PP', 95.50, 'YAPE', 
    '2026-02-15 14:45:00', 
    'PAG',
    'KORO-U1V2W3X4',
    '2026-02-15 14:40:00',
    'YP-147258369',
    'abc123def456ghi789jkl012mno345pqr678stu901vwx234yz567890abcdef12',
    'https://res.cloudinary.com/demo/image/upload/sample6.jpg',
    'Yape\nS/ 95.50\n15/02/2026 14:45\nYP-147258369',
    95.50,
    '2026-02-15 14:45:00',
    'Pago completo almuerzo familiar - Validación automática exitosa'
),

(
    2, NULL, 4, 'PP', 75.00, 'EFECTIVO', 
    '2026-02-18 20:15:00', 
    'PAG',
    'KORO-Y5Z6A7B8',
    '2026-02-18 20:15:00',
    NULL, -- Efectivo no tiene código
    NULL, -- Efectivo no requiere captura
    NULL,
    NULL,
    75.00,
    '2026-02-18 20:15:00',
    'Pago en efectivo - Sin validación requerida'
);