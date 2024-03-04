package com.topostechnology.rest.client.request;

public class PaymentLinkResponse {

    public String idLinkPago;
    public String codigo;
    public String mensaje;
    public String msisdn;
    public String url;
//    private String fechaExpiracion;
    private String codigoErrorConekta;
    private String descripcionConekta;
    private String jsonErrorConekta;

    @Override
    public String toString() {
        return "PaymentLinkResponse{" +
                "idLinkPago='" + idLinkPago + '\'' +
                ", codigo='" + codigo + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", url='" + url + '\'' +
//                ", fechaExpiracion='" + fechaExpiracion + '\'' +
                ", codigoErrorConekta='" + codigoErrorConekta + '\'' +
                ", descripcionConekta='" + descripcionConekta + '\'' +
                ", jsonErrorConekta='" + jsonErrorConekta + '\'' +
                '}';
    }

    public String getIdLinkPago() {
        return idLinkPago;
    }

    public void setIdLinkPago(String idLinkPago) {
        this.idLinkPago = idLinkPago;
    }

    public String getDescripcionConekta() {
        return descripcionConekta;
    }

    public void setDescripcionConekta(String descripcionConekta) {
        this.descripcionConekta = descripcionConekta;
    }

    public String getCodigoErrorConekta() {
        return codigoErrorConekta;
    }

    public void setCodigoErrorConekta(String codigoErrorConekta) {
        this.codigoErrorConekta = codigoErrorConekta;
    }

    public String getJsonErrorConekta() {
        return jsonErrorConekta;
    }

    public void setJsonErrorConekta(String jsonErrorConekta) {
        this.jsonErrorConekta = jsonErrorConekta;
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

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

//    public String getFechaExpiracion() {
//        return fechaExpiracion;
//    }
//
//    public void setFechaExpiracion(String fechaExpiracion) {
//        this.fechaExpiracion = fechaExpiracion;
//    }
}
