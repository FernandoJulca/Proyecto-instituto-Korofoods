package com.koroFoods.userServiceSoap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.koroFoods.userServiceSoap.model.Usuario;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer>{
Optional<Usuario> findByCorreo(String correo);
    
    Optional<Usuario> findByNroDoc(String nroDoc);
    
    List<Usuario> findByRol_IdRolOrderByFechaRegistroDesc(Integer idRol);

    List<Usuario> findByActivoOrderByFechaRegistroDesc(Boolean activo);

    List<Usuario> findByRol_IdRolAndActivoOrderByFechaRegistroDesc(Integer idRol, Boolean activo);

    List<Usuario> findByRol_IdRolInOrderByFechaRegistroDesc(List<Integer> roles);

    List<Usuario> findByRol_IdRolInAndActivoOrderByFechaRegistroDesc(List<Integer> roles, Boolean activo);

}
