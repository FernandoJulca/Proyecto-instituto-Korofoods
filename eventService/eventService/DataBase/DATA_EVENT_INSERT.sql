
/*
SHOW SERVER_ENCODING;
SHOW CLIENT_ENCODING;
*/

SET CLIENT_ENCODING TO 'UTF8';

INSERT INTO TB_TEMATICA (NOMBRE) VALUES 
('Animes'),
('Peliculas'),
('Manwhas BL'),
('Infantiles'),
('Comics'),
('Series'),
('Videojuegos'),
('Cultural');


INSERT INTO TB_EVENTO (NOMBRE, DESCRIPCION, ID_TEMATICA, FECHA_INICIO, FECHA_FIN, COSTO_EVENTO, IMAGEN) VALUES
('Noche de Animes', 'Un evento para fans de los animes más populares.', 1, '2026-02-25 19:30:00', '2026-02-25 21:30:00', 50.00, 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740951/noche-anime_wcq8zz.jpg'),
('Cine Temático: Películas', 'Proyección de películas icónicas en nuestro salón de eventos.', 2, '2026-03-15 18:00:00', '2026-03-15 20:00:00',60.00, 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740944/cine-tematico_kangdw.jpg'),
('Tarde de Comics y Héroes', 'Celebra a tus héroes favoritos con nosotros.', 5, '2026-04-05 17:00:00', '2026-04-05 19:00:00', 45.00, 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740945/comics-heroes_adj65s.jpg'),
('Aventura Infantil', 'Una tarde mágica para los más pequeños.', 4, '2026-05-10 15:00:00', '2026-05-10 17:00:00', 30.00, 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740950/infantil_md7kcm.jpg'),
('Noche BL', 'Celebración de los mejores Manwhas BL del momento.', 3, '2026-02-20 20:00:00', '2026-02-20 22:00:00', 55.00, 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740947/evento-bl_xnrezu.jpg'),
('Maratón de Series', 'Disfruta de las series más aclamadas.', 6, '2026-03-28 16:00:00', '2026-03-28 18:00:00', 40.00, 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740954/series-aclamadas_kfbr8e.jpg'),
('Gaming Night', 'Torneo de videojuegos con premios especiales.', 7, '2026-04-18 19:00:00', '2026-04-18 21:00:00', 65.00, 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740948/gaming_luzpoy.jpg'),
('Noche Cultural', 'Celebración de la cultura peruana y mundial.', 8, '2026-05-22 18:30:00', '2026-05-22 20:30:00', 35.00, 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740953/noche-cultural_y5zeh4.jpg'),
('God of War Trilogy Remake', 'Celebración de la remasterización de la trilogía original de PS2/PS3.', 7, '2026-02-23 20:30:00', '2026-02-23 23:00:00', 35.00, 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740956/god-of-war_ig11y7.jpg');


INSERT INTO TB_EVENTO_MESA (ID_EVENTO, ID_MESA, FECHA_DESDE, FECHA_HASTA) VALUES 
(1, 13, '2026-02-25 19:00:00', '2026-02-25 23:00:00'),
(1, 14, '2026-02-25 19:00:00', '2026-02-25 23:00:00'),
(1, 15, '2026-02-25 19:00:00', '2026-02-25 23:00:00'),
(2, 16, '2026-03-15 17:30:00', '2026-03-15 21:00:00'),
(2, 17, '2026-03-15 17:30:00', '2026-03-15 21:00:00'),
(3, 18, '2026-04-05 16:30:00', '2026-04-05 20:00:00'),
(4, 19, '2026-05-10 14:30:00', '2026-05-10 18:00:00'),
(4, 20, '2026-05-10 14:30:00', '2026-05-10 18:00:00'),
(9, 13, '2026-02-23 20:30:00', '2026-02-23 23:00:00'),
(9, 14, '2026-02-23 20:30:00', '2026-02-23 23:00:00'),
(9, 15, '2026-02-23 20:30:00', '2026-02-23 23:00:00'),
(9, 16, '2026-02-23 20:30:00', '2026-02-23 23:00:00'),
(9, 17, '2026-02-23 20:30:00', '2026-02-23 23:00:00'),
(9, 18, '2026-02-23 20:30:00', '2026-02-23 23:00:00'),
(9, 19, '2026-02-23 20:30:00', '2026-02-23 23:00:00'),
(9, 20, '2026-02-23 20:30:00', '2026-02-23 23:00:00');