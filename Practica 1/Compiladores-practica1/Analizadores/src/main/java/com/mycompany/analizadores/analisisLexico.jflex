package com.mycompany.analizadores;
import java_cup.runtime.*;
import java.util.List;
import java.util.ArrayList;

%% 

%public
%unicode
%class Lexer
%cup
%line
%column

DIGITO = [0-9]
LETRA = [a-zA-Z]
ID = {LETRA}({LETRA}|{DIGITO})*
ESPACIO = [ \t\f\r\n]+
COMENT = \#[^\r\n]*
DIGITO_HEXA = [0-9a-fA-F]
COLOR_HEX = H{DIGITO_HEXA}{6}
DECIMAL = {DIGITO}+"."{DIGITO}+
ENTERO = {DIGITO}+
CADENA = \"([^\"\\]|\\.)*\"

%{
    private List<ErrorLexico> errores = new ArrayList<>();

    public List<ErrorLexico> getErrores() {
        return errores;
    }

    private Symbol getToken(int type) {
        
        return new Symbol(type, yyline + 1, yycolumn + 1);

    }

    private Symbol getToken(int type, Object value) {
        
        return new Symbol(type, yyline + 1, yycolumn + 1, value);

    }

%}

%%
"INICIO" { return getToken(sym.INICIO);}
"FIN" { return getToken(sym.FIN);}
"VAR" { return getToken(sym.VAR);}
"SI" { return getToken(sym.SI);}
"ENTONCES" { return getToken(sym.ENTONCES);}
"MOSTRAR" { return getToken(sym.MOSTRAR);}
"HACER" { return getToken(sym.HACER);}
"MIENTRAS" { return getToken(sym.MIENTRAS);}
"LEER" { return getToken(sym.LEER);}
"ELIPSE" { return getToken(sym.ELIPSE);}
"RECTANGULO" { return getToken(sym.RECTANGULO);}
"CIRCULO" { return getToken(sym.CIRCULO);}
"PARALELOGRAMO" { return getToken(sym.PARALELOGRAMO);}
"ROMBO" { return getToken(sym.ROMBO);}
"RECTANGULO_REDONDEADO" { return getToken(sym.RECTANGULO_REDONDEADO);}
"ARIAL" { return getToken(sym.ARIAL);}
"TIMES_NEW_ROMAN" { return getToken(sym.TIMES_NEW_ROMAN);}
"COMIC_SANS" { return getToken(sym.COMIC_SANS);}
"VERDANA" { return getToken(sym.VERDANA);}
{ESPACIO} { /* Ignorar espacios en blanco */ }
{COMENT} { /* Ignorar comentarios */ }
{COLOR_HEX} { return getToken(sym.COLOR_HEX, yytext());}
{DECIMAL} { return getToken(sym.DECIMAL, Double.parseDouble(yytext()));}
{ENTERO} { return getToken(sym.ENTERO, Integer.parseInt(yytext()));}
{CADENA} { return getToken(sym.CADENA, yytext().substring(1, yytext().length() - 1));}
{ID} { return getToken(sym.ID, yytext());}
"%DEFAULT" { return getToken(sym.DEFAULT);} 
"%COLOR_TEXTO_SI" { return getToken(sym.COLOR_TEXTO_SI);}
"%COLOR_SI" { return getToken(sym.COLOR_SI);}
"%FIGURA_SI" { return getToken(sym.FIGURA_SI);}
"%LETRA_SI" { return getToken(sym.LETRA_SI);}
"%LETRA_SIZE_SI" { return getToken(sym.LETRA_SIZE_SI);}
"%COLOR_TEXTO_MIENTRAS" { return getToken(sym.COLOR_TEXTO_MIENTRAS);}
"%COLOR_MIENTRAS" { return getToken(sym.COLOR_MIENTRAS);}
"%FIGURA_MIENTRAS" { return getToken(sym.FIGURA_MIENTRAS);}
"%LETRA_MIENTRAS" { return getToken(sym.LETRA_MIENTRAS);}
"%LETRA_SIZE_MIENTRAS" { return getToken(sym.LETRA_SIZE_MIENTRAS);}
"%COLOR_TEXTO_BLOQUE" { return getToken(sym.COLOR_TEXTO_BLOQUE);}
"%COLOR_BLOQUE" { return getToken(sym.COLOR_BLOQUE);}
"%FIGURA_BLOQUE" { return getToken(sym.FIGURA_BLOQUE);}
"%LETRA_BLOQUE" { return getToken(sym.LETRA_BLOQUE);}
"%LETRA_SIZE_BLOQUE" { return getToken(sym.LETRA_SIZE_BLOQUE);}
"+" { return getToken(sym.MAS); }
"-" { return getToken(sym.MENOS); }
"*" { return getToken(sym.MULTIPLICADO); }
"/" { return getToken(sym.DIVIDIDO); }
"=" { return getToken(sym.IGUAL); }
"(" { return getToken(sym.PARENTESIS_ABIERTO); }
")" { return getToken(sym.PARENTESIS_CERRADO); }
"==" { return getToken(sym.IGUAL_IGUAL); }
"!=" { return getToken(sym.DIFERENTE); }
"<=" { return getToken(sym.MENOR_IGUAL); }
">=" { return getToken(sym.MAYOR_IGUAL); }
"||" { return getToken(sym.OR); }
"&&" { return getToken(sym.AND); }
"<" { return getToken(sym.MENOR); }
">" { return getToken(sym.MAYOR); }
"!" { return getToken(sym.NOT); }
"%%%%" { return getToken(sym.SEPARADOR); }
"," { return getToken(sym.COMA);}
"|" { return getToken(sym.PIPE);}
. { errores.add(new ErrorLexico(yytext(), yyline + 1, yycolumn +1)); } 