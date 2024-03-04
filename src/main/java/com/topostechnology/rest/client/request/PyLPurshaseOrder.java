package com.topostechnology.rest.client.request;

import java.util.ArrayList;
import java.util.List;

public class PyLPurshaseOrder {
    @Override
    public String toString() {
        return "OrdenCompra{" +
                "productos=" + productos +
                ", cliente=" + cliente +
                '}';
    }

    private List<PyLProduct> productos = new ArrayList<>();
    private PyLCustomer cliente;

    public List<PyLProduct> getProductos() {
        return productos;
    }

    public void setProductos(List<PyLProduct> productos) {
        this.productos = productos;
    }

    public PyLCustomer getCliente() {
        return cliente;
    }

    public void setCliente(PyLCustomer cliente) {
        this.cliente = cliente;
    }
}
