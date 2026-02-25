/*
SHOW SERVER_ENCODING;
SHOW CLIENT_ENCODING;

*/
SET CLIENT_ENCODING TO 'UTF8';

INSERT INTO TB_PLATO (NOMBRE, PRECIO, STOCK, TIPO_PLATO, IMAGEN) VALUES
-- Entradas (E)
('Tarta de la Abuela (Ratatouille)', 28.00, 50, 'E', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720383/KoroFoods/Menu/nhzddmqkgz953xezujb8.png'),
('Gyozas del Dragon (Dragon Ball)', 25.00, 60, 'E', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771784389/gyozas-goku_luvblw.png'),
('Tequeños Galácticos (Star Wars)', 22.00, 45, 'E', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720269/KoroFoods/Menu/wnfvbhphsk5nwga6dtas.png'),
-- Entradas Peruanas (E)
('Causa Limeña Rellena', 18.00, 55, 'E', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741027/causa_qeupwq.jpg'),
('Anticuchos de Corazón', 22.00, 50, 'E', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741030/anticuchos-de-corazon_lrbesm.jpg'),
('Papa a la Huancaína', 15.00, 60, 'E', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741038/papa-huancaina_y1kh9g.jpg'),
('Tiradito de Pescado', 25.00, 40, 'E', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741036/tiradito-pescado_vdmqsq.jpg'),
('Choritos a la Chalaca', 20.00, 45, 'E', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741041/choritos-chalaca_xwxiy7.jpg'),
('Solterito Arequipeño', 16.00, 50, 'E', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741040/solterito-ariquipenio_nvyhz0.jpg'),
('Ocopa Arequipeña', 17.00, 48, 'E', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741029/ocopa_n1zzg3.jpg'),
-- Segundos/Platos Principales (S)
('Sopa de Champiñones (Mario Bros)', 25.00, 40, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720442/KoroFoods/Menu/dgsmyw2vcnfivd3osyox.png'),
('Onigiri de Pescado (One Piece)', 20.00, 55, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720506/KoroFoods/Menu/z4trykmma2uz5i225akc.png'),
('Tacos de la Araña (Spiderman)', 28.00, 50, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720476/KoroFoods/Menu/ms5adwvgakvvuv4kzcwh.png'),
('Fideos de la Alegría (Intensamente)', 23.00, 60, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720536/KoroFoods/Menu/zlkoja3uzp5ceaypufru.png'),
('Empanadas del Ogro (Shrek)', 25.00, 45, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771784449/empanadas-sherk_oe9rok.png'),
('Bento del Asesino (Death Note)', 45.00, 35, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720578/KoroFoods/Menu/lwcq25odswxiui7ntetq.png'),
('Ramen de la Aldea (Naruto)', 35.00, 50, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720602/KoroFoods/Menu/sncdsmkqwnkffqbriebm.png'),
('Hamburguesa del Tiempo (Interstellar)', 48.00, 30, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720620/KoroFoods/Menu/mctcrd3y0eui94lk06ks.png'),
('Plato de la Batalla (Kratos)', 55.00, 25, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720659/KoroFoods/Menu/wpbfkjq77kwls0tbzao1.png'),
('Salchipapas del Espacio (Solar Opposites)', 28.00, 45, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720698/KoroFoods/Menu/fjpc98s70zqgh3d8bpgq.png'),
('Pollo Hermanos (Breaking Bad)', 42.00, 35, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720724/KoroFoods/Menu/nlpr3kvgqvcs2gbtaplc.png'),
('Alitas del Murciélago (Batman)', 35.00, 40, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720746/KoroFoods/Menu/eoj64qnqq5hbmckctkdh.png'),
('Ceviche Legendario (One Piece)', 42.00, 30, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720794/KoroFoods/Menu/ldwfjwoo7evxpvlto7up.png'),
-- Segundos/Platos Principales Peruanos (S)
('Lomo Saltado del Héroe', 38.00, 45, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720776/KoroFoods/Menu/jpn6qals8n7prpusxqbe.png'),
('Chupe de Camarones', 32.00, 35, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741003/chupe-camaron_kukxgx.jpg'),
('Parihuela Marina', 28.00, 40, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741013/parihuela_qyzgcz.jpg'),
('Shambar Trujillano', 22.00, 38, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741017/shambar_pskbvr.jpg'),
('Chilcano de Pescado', 18.00, 50, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741712/chilcano_yvfzb5.jpg'),
('Ají de Gallina Criollo', 28.00, 50, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741002/Aji-gallina_evpkja.jpg'),
('Arroz con Pato a la Norteña', 35.00, 40, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741006/c-Arroz-con-pato_f5usub.png'),
('Carapulcra con Sopa Seca', 32.00, 42, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741018/sopa-seca_abfyhc.jpg'),
('Rocoto Relleno Arequipeño', 30.00, 38, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741014/rocoto-relleno_zhpkv4.png'),
('Seco de Cabrito con Frejoles', 36.00, 35, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741016/seco-cabrito_nne7sk.png'),
('Tacu Tacu con Bistec', 32.00, 45, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741020/Tacux2-Bistec_ect3f7.jpg'),
('Pachamanca a la Olla', 40.00, 30, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741011/pachamanca_dbgsq4.jpg'),
('Chicharrón de Chancho con Camote', 28.00, 48, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741005/Chicharron-Camote_kiwyxh.png'),
('Pollo a la Brasa con Papas', 35.00, 55, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741025/pollo-a-la-brasa_dqapnq.png'),
('Arroz con Mariscos', 38.00, 40, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741023/arroz-con-mariscos_vflp2m.png'),
('Juane Selvático', 26.00, 42, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741010/juane_t6tu1h.png'),
('Escabeche de Pescado', 32.00, 38, 'S', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771741008/escbache-pescado_uxdysa.png'),

-- Postres (P)
('Tarta Nocturna de Tinta Negra', 22.00, 40, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771788534/tarta-nocturna_mkg4p7.png'),
('Red Velvet Obsesión', 20.00, 35, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771788535/red-velvet-obsesion_mctm5z.png'),
('Cheesecake Doble Identidad', 18.00, 30, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771788537/cheesecake-doble-identidad_em55qe.png'),
('Brownie Impacto Final', 19.00, 25, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771788536/brownie-impacto-final_bcar9d.png'),
('Perla de Coco Prohibida', 23.00, 20, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771788537/perla-coco_qxiehn.png'),
('Tiramisú Ilusión Mental', 21.00, 28, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771788536/tiramisu-ilusion-mental_zck6cf.png'),
('Milhojas Tentación Nocturna', 17.00, 32, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771788537/milhojas-tentacion-nocturna_nhwwvj.png'),
('Macarons Alfa & Omega', 24.00, 18, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771788535/macaron-alfa-omega_rtjm8c.png'),
('Parfait Flor de Cerezo', 16.00, 45, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771788538/parfait-flor-cerezo_lip2id.png'),
('Soufflé Silencio Dulce', 22.00, 22, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771788534/souffle-silencio-dulce_ai4iei.png'),

-- Postres Peruanos (P)
('Suspiro a la Limeña', 14.00, 60, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740976/suspiro-de-limennia_ts5gbn.jpg'),
('Mazamorra Morada con Arroz con Leche', 12.00, 65, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740971/mazamorra-morada-con-leche_rgpn9h.jpg'),
('Picarones con Miel de Chancaca', 15.00, 55, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740973/picarones_a8gqwn.jpg'),
('Turrón de Doña Pepa', 10.00, 70, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740974/turron_r8lzy8.jpg'),
('Alfajores Limeños', 8.00, 80, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740963/alfajores_qb1yua.jpg'),
('Champús Cusqueño', 10.00, 50, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740982/champus_p3qmx0.png'),
('Ranfañote Arequipeño', 12.00, 45, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740979/ranfaniote_eid4no.png'),
('Helado de Lúcuma', 9.00, 75, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740978/helado-lucuma_rb2apq.png'),
('Arroz Zambito', 11.00, 60, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740970/arroz-zambito_kbn4nx.jpg'),
('Flan de Quinua', 10.00, 55, 'P', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740981/flan-quinua_vafsnm.png'),
-- Bebidas (B)
('Coctel Ilusionista (Los Ilusionistas)', 22.00, 40, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720640/KoroFoods/Menu/qelq4kdcmhgnwdh1mx1h.png'),
('Chicha Morada Shinigami', 12.00, 100, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720847/KoroFoods/Menu/jgc9ikjboxxw0khbay9u.png'),
('Limonada del Reino Champiñón', 10.00, 100, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720864/KoroFoods/Menu/qywrl1jks5rjtpit5fjq.png'),
('Inca Kola Saiyan', 8.00, 120, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720888/KoroFoods/Menu/xk5kfs7dimszj3l6q1li.png'),
('Jugo de Maracuyá Pokémon', 10.00, 90, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720916/KoroFoods/Menu/q6zxbk0edwjc08v05lul.png'),
('Café de Gotham City', 15.00, 80, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720932/KoroFoods/Menu/l5yoio5hybucbzbthshz.png'),
('Té Verde del Maestro Jedi', 12.00, 70, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771720963/KoroFoods/Menu/jwtfq8h4xl2veksz16mh.png'),
-- Bebidas Peruanas (B)
('Chicha Morada Casera', 8.00, 100, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740986/chicha-morada_j6besr.jpg'),
('Emoliente Tradicional', 6.00, 90, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740990/emoliente_j7u5l7.jpg'),
('Refresco de Maracuyá', 7.00, 95, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740994/maracuya_sfwuqm.png'),
('Cremolada de Chirimoya', 9.00, 70, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740989/cremolada-chirimoya_znzqpg.jpg'),
('Jugo de Lúcuma', 8.00, 85, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740999/lucuma_rxtoid.png'),
('Chicha de Jora', 10.00, 60, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740984/chicha-jora_ol5sag.jpg'),
('Pisco Sour Clásico', 18.00, 50, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740997/pisco-sour_gtoqa8.jpg'),
('Chilcano de Pisco', 16.00, 55, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740988/chilcano-pisco_rtrstt.jpg'),
('Mate de Coca', 5.00, 100, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740995/mate-coca_kgomcy.jpg'),
('Café Pasado Peruano', 7.00, 90, 'B', 'https://res.cloudinary.com/dvacublsz/image/upload/v1771740992/cafe_ysydzz.png');

INSERT INTO TB_ETIQUETA (NOMBRE, DESCRIPCION) VALUES
('Picante', 'Contiene ingredientes picantes'),
('Vegetariano', 'Sin carne ni pescado'),
('Vegano', 'Sin productos de origen animal'),
('Sin Gluten', 'Apto para celíacos'),
('Especial Niños', 'Porciones y sabores para niños'),
('Recomendado', 'Plato más vendido'),
('Nuevo', 'Nuevo en el menú'),
('Saludable', 'Opción nutritiva y balanceada');

INSERT INTO TB_PLATO_ETIQUETAS (ID_PLATO, ID_ETIQUETA) VALUES
(1, 2), (1, 8), -- Tarta vegetariana y saludable
(3, 6), -- Tequeños recomendados
(4, 2), (4, 4), -- Sopa vegetariana sin gluten
(5, 6), -- Onigiri recomendado
(7, 5), (7, 8), -- Fideos especial niños y saludable
(8, 5), (8, 6), -- Empanadas especial niños recomendado
(9, 1), (9, 6), -- Bento picante recomendado
(10, 6), (10, 7), -- Ramen recomendado y nuevo
(13, 1), -- Plato de batalla picante
(17, 8), (17, 6), -- Lomo saltado saludable recomendado
(18, 8), (18, 4); -- Ceviche saludable sin gluten