package com.koroFoods.userServiceSoap.mapper;

import com.koroFoods.userServiceSoap.enums.TipoDocumento;
import com.koroFoods.userServiceSoap.generated.TipoDocumentoSoap;
import com.koroFoods.userServiceSoap.model.Distrito;
import com.koroFoods.userServiceSoap.model.Rol;
import com.koroFoods.userServiceSoap.model.Usuario;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;

@Component
public class UsuarioSoapMapper {

    /**
     * Convierte de objeto SOAP a Entidad JPA
     */
    public Usuario soapAEntidad(com.koroFoods.userServiceSoap.generated.Usuario usuarioSoap) {
        if (usuarioSoap == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        
        if (usuarioSoap.getIdUsuario() != null) {
            usuario.setIdUsuario(usuarioSoap.getIdUsuario());
        }
        
        usuario.setNombres(usuarioSoap.getNombres());
        usuario.setApePaterno(usuarioSoap.getApePaterno());
        usuario.setApeMaterno(usuarioSoap.getApeMaterno());
        usuario.setCorreo(usuarioSoap.getCorreo());
        usuario.setClave(usuarioSoap.getClave());
        usuario.setTipoDoc(TipoDocumento.valueOf(usuarioSoap.getTipoDoc().value()));
        usuario.setNroDoc(usuarioSoap.getNroDoc());
        usuario.setDireccion(usuarioSoap.getDireccion());
        usuario.setTelefono(usuarioSoap.getTelefono());

        // Mapear distrito
        if (usuarioSoap.getIdDistrito() != null) {
            Distrito distrito = new Distrito();
            distrito.setIdDistrito(usuarioSoap.getIdDistrito());
            usuario.setDistrito(distrito);
        }

        // Mapear rol
        if (usuarioSoap.getIdRol() != 1 || usuarioSoap.getIdRol() != 4) {
            Rol rol = new Rol();
            rol.setIdRol(usuarioSoap.getIdRol());
            usuario.setRol(rol);
        }

        if (usuarioSoap.isActivo() != null) {
            usuario.setActivo(usuarioSoap.isActivo());
        }

        return usuario;
    }

    /**
     * Convierte de Entidad JPA a objeto SOAP
     */
    public com.koroFoods.userServiceSoap.generated.Usuario entidadASoap(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        com.koroFoods.userServiceSoap.generated.Usuario usuarioSoap = new com.koroFoods.userServiceSoap.generated.Usuario();
        
        usuarioSoap.setIdUsuario(usuario.getIdUsuario());
        usuarioSoap.setNombres(usuario.getNombres());
        usuarioSoap.setApePaterno(usuario.getApePaterno());
        usuarioSoap.setApeMaterno(usuario.getApeMaterno());
        usuarioSoap.setCorreo(usuario.getCorreo());
        usuarioSoap.setTipoDoc(
        		com.koroFoods.userServiceSoap.generated.TipoDocumentoSoap.valueOf(usuario.getTipoDoc().name())
        );
        usuarioSoap.setNroDoc(usuario.getNroDoc());
        usuarioSoap.setDireccion(usuario.getDireccion());
        usuarioSoap.setTelefono(usuario.getTelefono());
        
        if (usuario.getDistrito() != null) {
            usuarioSoap.setIdDistrito(usuario.getDistrito().getIdDistrito());
        }
        
        if (usuario.getRol() != null) {
            usuarioSoap.setIdRol(usuario.getRol().getIdRol());
        }
        
        usuarioSoap.setActivo(usuario.getActivo());
        
        if (usuario.getFechaRegistro() != null) {
            usuarioSoap.setFechaRegistro(
                convertirLocalDateTimeAXMLGregorianCalendar(usuario.getFechaRegistro())
            );
        }
        
        return usuarioSoap;
    }

    /**
     * Mapea los campos desde el request de creación a una entidad
     */
    public Usuario crearRequestAEntidad(
            String nombres, String apePaterno, String apeMaterno, 
            String correo, String clave, TipoDocumentoSoap tipoDoc, 
            String nroDoc, String direccion, String telefono, 
            Integer idDistrito, Integer idRol) {
        
        Usuario usuario = new Usuario();
        usuario.setNombres(nombres);
        usuario.setApePaterno(apePaterno);
        usuario.setApeMaterno(apeMaterno);
        usuario.setCorreo(correo);
        usuario.setClave(clave);
        usuario.setTipoDoc(TipoDocumento.valueOf(tipoDoc.value()));
        usuario.setNroDoc(nroDoc);
        usuario.setDireccion(direccion);
        usuario.setTelefono(telefono);

        if (idDistrito != null) {
            Distrito distrito = new Distrito();
            distrito.setIdDistrito(idDistrito);
            usuario.setDistrito(distrito);
        }

        Rol rol = new Rol();
        rol.setIdRol(idRol);
        usuario.setRol(rol);

        return usuario;
    }

    /**
     * Mapea los campos desde el request de actualización a una entidad
     */
    public Usuario actualizarRequestAEntidad(
            String nombres, String apePaterno, String apeMaterno, 
            String correo, String clave, TipoDocumentoSoap tipoDoc, 
            String nroDoc, String direccion, String telefono, 
            Integer idDistrito, Integer idRol, Boolean activo) {
        
        Usuario usuario = new Usuario();
        usuario.setNombres(nombres);
        usuario.setApePaterno(apePaterno);
        usuario.setApeMaterno(apeMaterno);
        usuario.setCorreo(correo);
        
        // Solo setear clave si no está vacía
        if (clave != null && !clave.trim().isEmpty()) {
            usuario.setClave(clave);
        }
        
        usuario.setTipoDoc(TipoDocumento.valueOf(tipoDoc.value()));
        usuario.setNroDoc(nroDoc);
        usuario.setDireccion(direccion);
        usuario.setTelefono(telefono);
        usuario.setActivo(activo);

        if (idDistrito != null) {
            Distrito distrito = new Distrito();
            distrito.setIdDistrito(idDistrito);
            usuario.setDistrito(distrito);
        }

        Rol rol = new Rol();
        rol.setIdRol(idRol);
        usuario.setRol(rol);

        return usuario;
    }

    /**
     * Convierte LocalDateTime a XMLGregorianCalendar
     */
    private XMLGregorianCalendar convertirLocalDateTimeAXMLGregorianCalendar(LocalDateTime localDateTime) {
        try {
            GregorianCalendar gregorianCalendar = GregorianCalendar.from(
                localDateTime.atZone(ZoneId.systemDefault())
            );
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            return null;
        }
    }
}