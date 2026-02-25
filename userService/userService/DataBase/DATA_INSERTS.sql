SET CLIENT_ENCODING TO 'UTF8';


INSERT INTO TB_ROL (DESCRIPCION) VALUES
('A'), -- Administrador
('R'), -- Recepcionista
('M'), -- Mozo
('C'); -- Cliente

INSERT INTO TB_DISTRITO (NOMBRE) VALUES
('Ancón'),
('Ate'),
('Barranco'),
('Breña'),
('Carabayllo'),
('Chaclacayo'),
('Chorrillos'),
('Cieneguilla'),
('Comas'),
('El Agustino'),
('Independencia'),
('Jesús María'),
('La Molina'),
('La Victoria'),
('Lima'),
('Lince'),
('Los Olivos'),
('Lurigancho'),
('Lurín'),
('Magdalena del Mar'),
('Miraflores'),
('Pachacámac'),
('Pucusana'),
('Pueblo Libre'),
('Puente Piedra'),
('Punta Hermosa'),
('Punta Negra'),
('Rímac'),
('San Bartolo'),
('San Borja'),
('San Isidro'),
('San Juan de Lurigancho'),
('San Juan de Miraflores'),
('San Luis'),
('San Martín de Porres'),
('San Miguel'),
('Santa Anita'),
('Santa María del Mar'),
('Santa Rosa'),
('Santiago de Surco'),
('Surquillo'),
('Villa El Salvador'),
('Villa María del Triunfo');

INSERT INTO TB_USUARIO (NOMBRES, APE_PATERNO, APE_MATERNO, CORREO, CLAVE, TIPO_DOC, NRO_DOC, DIRECCION, TELEFONO, ID_DISTRITO, ID_ROL) VALUES 
--admin
('Carlos', 'Pérez', 'García', 'carlos.admin@gmail.com', '$2a$12$LcqfCHJmnXIeglxKPqxeseYP2YwELUVFqAscddldDmw1ggnBdSmbC', 'DNI', '12345678', 'Direccion 1', '987654321', 1, 1),

--recepcionista
('Ana', 'Torres', 'Vega', 'ana.recepcionista@gmail.com', '$2a$12$LcqfCHJmnXIeglxKPqxeseYP2YwELUVFqAscddldDmw1ggnBdSmbC', 'DNI', '78945692', 'Direccion 2', '934567890', 1, 2),
('Juana', 'Casas', 'Henrique', 'juana.recepcionista@gmail.com', '$2a$12$LcqfCHJmnXIeglxKPqxeseYP2YwELUVFqAscddldDmw1ggnBdSmbC', 'DNI', '78909612', 'Direccion 3', '904567890', 1, 2),
('Briggite', 'Zapata', 'Inglesias', 'briggite.recepcionista@gmail.com', '$2a$12$LcqfCHJmnXIeglxKPqxeseYP2YwELUVFqAscddldDmw1ggnBdSmbC', 'DNI', '78940012', 'Direccion 4', '909567890', 1, 2),

--mozo
('Juan', 'Rodríguez', 'Silva', 'juan.mesero@gmail.com', '$2a$12$LcqfCHJmnXIeglxKPqxeseYP2YwELUVFqAscddldDmw1ggnBdSmbC', 'DNI', '45666912', 'Direccion 3', '923456789', 3, 3),

--cliente
('María', 'López', 'Martínez', 'maria.cliente@gmail.com', '$2a$12$LcqfCHJmnXIeglxKPqxeseYP2YwELUVFqAscddldDmw1ggnBdSmbC', 'DNI', '87654321', 'Direccion 2', '912345678', 2, 4),
('Rebeca', 'Yllanes', 'Chávez', 'rebeca2506km@gmail.com', '$2a$12$S4yymkfK617aCMbt1axzwOZAJF.n0FRiA6zVIwGKExPfN2kBGbc2a', 'DNI', '60770958', 'Centro de Lima 666', '908955357', 10, 4),

--MOZO NUEVOS
('Pepe', 'Lucho', 'Silva', 'pepe.mesero@gmail.com', '$2a$12$LcqfCHJmnXIeglxKPqxeseYP2YwELUVFqAscddldDmw1ggnBdSmbC', 'DNI', '45609911', 'Direccion 3', '923456089', 3, 3),
('Enrique', 'Rodríguez', 'Silvado', 'enrique.mesero@gmail.com', '$2a$12$LcqfCHJmnXIeglxKPqxeseYP2YwELUVFqAscddldDmw1ggnBdSmbC', 'DNI', '09679912', 'Direccion 3', '923056789', 3, 3),
('Pedro', 'Manchado', 'Casas', 'pedro.mesero@gmail.com', '$2a$12$LcqfCHJmnXIeglxKPqxeseYP2YwELUVFqAscddldDmw1ggnBdSmbC', 'DNI', '91672912', 'Direccion 3', '920056789', 3, 3),
('Luisa', 'Hermegelinda', 'Hector', 'luisa.mesero@gmail.com', '$2a$12$LcqfCHJmnXIeglxKPqxeseYP2YwELUVFqAscddldDmw1ggnBdSmbC', 'DNI', '75618912', 'Direccion 3', '903456789', 3, 3)