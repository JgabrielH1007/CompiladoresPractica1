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
                    body { font-family: sans-serif; padding: 10px; }
                    h3 { color: #0055a4; font-weight: normal; margin-top: 20px; }
                    table { width: 100%; border-collapse: collapse; table-layout: fixed; }
                    th, td { border: 1px solid black; padding: 6px; text-align: left; font-size: 12px; word-wrap: break-word; }
                    th { font-weight: bold; background-color: #f8f8f8; }
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