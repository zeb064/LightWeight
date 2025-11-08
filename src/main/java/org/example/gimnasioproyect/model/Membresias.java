package org.example.gimnasioproyect.model;

public class Membresias {
    private Integer idMembresia;
    private String tipo;
    private Double precioMembresia;

    public Membresias() {}

    public Membresias(Integer idMembresia, String tipo, Double precioMembresia) {
        this.idMembresia = idMembresia;
        this.tipo = tipo;
        this.precioMembresia = precioMembresia;
    }

    public Integer getIdMembresia() {
        return idMembresia;
    }

    public void setIdMembresia(Integer idMembresia) {
        this.idMembresia = idMembresia;
    }

    public String getTipoMembresia() {
        return tipo;
    }

    public void setTipoMembresia(String tipoMembresia) {
        this.tipo = tipoMembresia;
    }

    public Double getPrecioMembresia() {
        return precioMembresia;
    }

    public void setPrecioMembresia(Double precioMembresia) {
        this.precioMembresia = precioMembresia;
    }
}
