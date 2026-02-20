/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.analizadores;

import java.io.StringReader;

/**
 *
 * @author gabrielh
 */
public class Analizadores {

    public static void main(String[] args) {
        String text = """
                             INICIO 
                                  VAR a = 10
                                  VAR b = 20
                                  SI (a < b) ENTONCES
                                      MOSTRAR "a es menor que b"
                                  FIN SI
                                  MIENTRAS (a < 15) HACER
                                      a = a + 1
                                      MOSTRAR a
                                  FIN MIENTRAS
                                  MOSTRAR "Fin del programa"
                              FIN
                              %%%%
                              %DEFAULT=1
                              %COLOR_TEXTO_SI=12,45-5,1|1
                              %FIGURA_MIENTRAS=CIRCULO|1
                              %DEFAULT=3
                              """;

                StringBuilder stringBuilder = new StringBuilder();
                Lexer lexer = new Lexer(new StringReader(text));
                Parser parser = new Parser(lexer);

                try {
                    parser.parse();
                    

                    if(lexer.getErrores().isEmpty() && parser.getSintaxError().isEmpty()){
                        System.out.println("No hay errores");
                    } else {
                        System.out.println("Hay errores....");
                        System.out.println(stringBuilder.toString());
                    }
                } catch (Exception e) {
                    System.out.println("Ocurrio un error inesperado");
                }
    }
}
