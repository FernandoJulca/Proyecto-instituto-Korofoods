package com.koroFoods.userServiceSoap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.koroFoods.userServiceSoap.enums.TipoDocumento;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Integer idUsuario;

    @Column(name = "NOMBRES")
    private String nombres;

    @Column(name = "APE_PATERNO")
    private String apePaterno;

    @Column(name = "APE_MATERNO")
    private String apeMaterno;

    @Column(name = "CORREO")
    private String correo;

    @Column(name = "CLAVE")
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_DOC")
    private TipoDocumento tipoDoc;

    @Column(name = "NRO_DOC")
    private String nroDoc;

    @Column(name = "IMAGEN")
    private String imagen;

    @Column(name = "DIRECCION")
    private String direccion;

    @Column(name = "TELEFONO")
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "ID_DISTRITO")
    private Distrito distrito;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_ROL")
    private Rol rol;

    @Column(name = "FECHA_REGISTRO")
    private LocalDateTime fechaRegistro;

    @Column(name = "ACTIVO")
    private Boolean activo;

    @JsonIgnore
    @Transient
    private MultipartFile imagenMultipart; // para la subida de imagens

}
