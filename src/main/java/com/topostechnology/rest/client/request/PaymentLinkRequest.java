package com.topostechnology.rest.client.request;

public class PaymentLinkRequest {

    private String msisdn;

    @Override
    public String toString() {
        return "PaymentLinkRequest{" +
                "msisdn='" + msisdn + '\'' +
                ", diasExpiracion=" + diasExpiracion +
                ", ordenCompra=" + ordenCompra +
                ", modoProduccion=" + modoProduccion +
                ", origen='" + origen + '\'' +
                '}';
    }

    private int diasExpiracion;
    private PyLPurshaseOrder ordenCompra;
    private Boolean modoProduccion;
    private String origen;

    public Boolean getModoProduccion() {
        return modoProduccion;
    }

    public void setModoProduccion(Boolean modoProduccion) {
        this.modoProduccion = modoProduccion;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public int getDiasExpiracion() {
        return diasExpiracion;
    }

    public void setDiasExpiracion(int diasExpiracion) {
        this.diasExpiracion = diasExpiracion;
    }






    public PyLPurshaseOrder getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(PyLPurshaseOrder ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }
}
