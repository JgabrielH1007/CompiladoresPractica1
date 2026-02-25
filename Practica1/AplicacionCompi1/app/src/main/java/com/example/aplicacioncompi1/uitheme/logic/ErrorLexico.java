/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.aplicacioncompi1.uitheme.logic;

/**
 *
 * @author gabrielh
 */
public class ErrorLexico {
    private final String lexema;
    private final int linea;
    private final int columna;
    
    public ErrorLexico(String lexema, int linea, int columna){
        this.lexema = lexema;
        this.linea = linea;
        this.columna = columna;
    }

    public String getLexema() {
        return lexema;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }
}
