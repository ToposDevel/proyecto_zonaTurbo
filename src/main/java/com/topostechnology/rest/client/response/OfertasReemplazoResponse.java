package com.topostechnology.rest.client.response;

import java.util.List;

public class OfertasReemplazoResponse {

    private String msisdn;
    private String ofertaPrimaria;
    private String codigo;

    private String mensaje;
    private List<OfertasReemplazo> ofertasList;

    public List<OfertasReemplazo> getOfertasList() {
        return ofertasList;
    }

    public void setOfertasList(List<OfertasReemplazo> ofertasList) {
        this.ofertasList = ofertasList;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
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


    public String getOfertaPrimaria() {
        return ofertaPrimaria;
    }

    public void setOfertaPrimaria(String ofertaPrimaria) {
        this.ofertaPrimaria = ofertaPrimaria;
    }
}
