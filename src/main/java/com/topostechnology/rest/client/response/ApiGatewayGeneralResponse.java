package com.topostechnology.rest.client.response;

public class ApiGatewayGeneralResponse {

    public String codigo;
    public String mensaje;

    @Override
    public String toString() {
        return "codigo='" + codigo + '\'' +
                ", mensaje='" + mensaje;
    }


    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
