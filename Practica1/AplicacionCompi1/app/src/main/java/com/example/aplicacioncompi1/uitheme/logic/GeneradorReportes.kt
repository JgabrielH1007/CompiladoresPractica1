package com.example.aplicacioncompi1.uitheme.logic


class GeneradorReportes(private val parser: Parser) {

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
                    h3 { 
                        color: #1976D2; 
                        font-weight: bold; 
                        font-size: 16px;
                        margin-top: 24px; 
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
                        margin-bottom: 20px;
                    }
                    th, td { 
                        padding: 10px 8px; 
                        text-align: left; 
                        font-size: 12px; 
                        word-wrap: break-word; 
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
                </style>
            </head>
            <body>
        """.trimIndent())

        html.append("<h3>Reporte de ocurrencias de operadores matemáticos</h3>")
        html.append("<table><tr><th>Operador</th><th>Línea</th><th>Columna</th><th>Ocurrencia</th></tr>")

        for (op in parser.operadores) {
            html.append("<tr><td>${op.operador}</td><td>${op.linea}</td><td>${op.columna}</td><td>${op.ocurrencia}</td></tr>")
        }
        html.append("</table>")

        html.append("<h3>Reporte de estructuras de control</h3>")
        html.append("<table><tr><th>Objeto</th><th>Línea</th><th>Condición</th></tr>")

        for (est in parser.estructuras) {
            html.append("<tr><td>${est.objeto}</td><td>${est.linea}</td><td>${est.condicion}</td></tr>")
        }
        html.append("</table>")

        html.append("</body></html>")
        return html.toString()
    }
}