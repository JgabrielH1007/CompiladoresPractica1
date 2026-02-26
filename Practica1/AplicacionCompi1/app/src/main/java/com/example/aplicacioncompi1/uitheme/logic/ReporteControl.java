package com.example.aplicacioncompi1.uitheme.logic;

public class ReporteControl {
    private String objeto;
    private int linea;
    private String condicion;

    public ReporteControl(String objeto, int linea, String condicion) {
        this.objeto = objeto;
        this.linea = linea;
        this.condicion = condicion;
    }

    public String getObjeto() { return objeto; }
    public int getLinea() { return linea; }
    public String getCondicion() { return condicion; }
}