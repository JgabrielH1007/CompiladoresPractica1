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
                        font-family: 'Roboto', sans-serif; 
                        padding: 12px; 
                        margin: 0;
                        color: #333333;
                        background-color: #ffffff;
                    }
                    h2 { 
                        color: #1976D2;
                        font-weight: bold; 
                        font-size: 18px; 
                        margin-top: 10px;
                        margin-bottom: 12px;
                        border-bottom: 2px solid #1976D2;
                        padding-bottom: 4px;
                        text-transform: uppercase;
                    }
                    table { 
                        width: 100%; 
                        border-collapse: collapse; 
                        table-layout: fixed;
                        border-radius: 6px;
                        overflow: hidden;
                        box-shadow: 0 2px 6px rgba(0,0,0,0.1);
                    }
                    th, td { 
                        padding: 10px 6px; 
                        text-align: left; 
                        font-size: 11px; 
                        word-wrap: break-word; 
                        overflow-wrap: break-word;
                    }
                    th { 
                        background-color: #1976D2; 
                        color: #ffffff; 
                        font-weight: bold; 
                    }
                    td {
                        border-bottom: 1px solid #E0E0E0;
                    }
                    tr:last-child td {
                        border-bottom: none; 
                    }
                    tr:nth-child(even) {
                        background-color: #F8F9FA; 
                    }
                    
                    th:nth-child(1) { width: 16%; }
                    th:nth-child(2) { width: 12%; } 
                    th:nth-child(3) { width: 12%; } 
                    th:nth-child(4) { width: 18%; } 
                    th:nth-child(5) { width: 42%; } 
                </style>
            </head>
            <body>
                <h2>Reporte de Errores</h2>
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