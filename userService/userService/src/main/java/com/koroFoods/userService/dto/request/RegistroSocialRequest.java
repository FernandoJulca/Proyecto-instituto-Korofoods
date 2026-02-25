package com.koroFoods.userService.dto.request;

import com.koroFoods.userService.enums.TipoDocumento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroSocialRequest {

    private String tempToken; //token temporal ya sea github o google ya que esto llega y jwt gnera el nuevo token

    private String nombres;
    private String correo;
    private String imagen;
    private String provider;

    private String apePaterno;
    private String apeMaterno;
    private TipoDocumento tipoDocumento;
    private String nroDoc;
    private String telefono;
    private String direccion;
    private Integer idDistrito;
    private String clave;

}
