package com.example.aplicacioncompi1.uitheme.logic

class GeneradorReportesError(private val lexer: Lexer, private val parser: Parser) {

    fun hayErrores(): Boolean {
        return lexer.errores.isNotEmpty() || parser.sintaxError.isNotEmpty()
    }

    fun generarReporteHtml(): String {
        val html = StringBuilder()

        html.append("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { 
                        font-family: sans-serif; 
                        padding: 0; 
                        margin: 0; 
                    }
                    h2 { 
                        color: #0055a4; 
                        font-weight: normal; 
                        font-size: 18px; 
                        margin-bottom: 10px;
                    }
                    table { 
                        width: 100%; 
                        border-collapse: collapse; 
                        table-layout: fixed; /* Obliga a la tabla a no salirse de la pantalla */
                    }
                    th, td { 
                        border: 1px solid black; 
                        padding: 6px; 
                        text-align: left; 
                        font-size: 11px; /* Letra más pequeña para que quepa bien todo */
                        word-wrap: break-word; /* Fuerza el salto de línea si el texto es muy largo */
                        overflow-wrap: break-word;
                    }
                    th { 
                        font-weight: bold; 
                        background-color: #f0f0f0; 
                    }
                    /* Le damos anchos específicos a las columnas para aprovechar el espacio */
                    th:nth-child(1) { width: 15%; } /* Lexema */
                    th:nth-child(2) { width: 12%; } /* Línea */
                    th:nth-child(3) { width: 14%; } /* Columna */
                    th:nth-child(4) { width: 19%; } /* Tipo */
                    th:nth-child(5) { width: 40%; } /* Descripción */
                </style>
            </head>
            <body>
                <h2>Reporte de errores</h2>
                <table>
                    <tr>
                        <th>Lexema</th>
                        <th>Línea</th>
                        <th>Col</th>
                        <th>Tipo</th>
                        <th>Descripción</th>
                    </tr>
        """.trimIndent())

        for (error in lexer.errores) {
            html.append("<tr>")
            html.append("<td>${error.lexema}</td>")
            html.append("<td>${error.linea}</td>")
            html.append("<td>${error.columna}</td>")
            html.append("<td>Léxico</td>")
            html.append("<td>Símbolo no existe en el lenguaje</td>")
            html.append("</tr>")
        }

        for (error in parser.sintaxError) {
            html.append("<tr>")
            html.append("<td>${error.lexema}</td>")
            html.append("<td>${error.linea}</td>")
            html.append("<td>${error.columna}</td>")
            html.append("<td>Sintáctico</td>")
            html.append("<td>${error.descripcion}</td>")
            html.append("</tr>")
        }

        html.append("""
                </table>
            </body>
            </html>
        """.trimIndent())

        return html.toString()
    }
}