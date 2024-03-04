package com.topostechnology.rest.client.response;

import java.util.List;

public class SaldoResponse {

    public String codigo;
    public String mensaje;
    public String msisdn;
    public List<FreeUnit> listSaldos;
    public String expireDate;
    public String saldo;

    public List<FreeUnit> getListSaldos() {
        return listSaldos;
    }

    public void setListSaldos(List<FreeUnit> listSaldos) {
        this.listSaldos = listSaldos;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }



    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }


}
