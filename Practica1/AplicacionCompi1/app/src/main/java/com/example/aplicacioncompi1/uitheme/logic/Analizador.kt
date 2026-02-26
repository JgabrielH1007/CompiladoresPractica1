package com.example.aplicacioncompi1.uitheme.logic

import java.io.StringReader

class Analizador(private val input: String) {

    private lateinit var lexer: Lexer
    private lateinit var parser: Parser
    private var generadorDiagrama: GeneradorDiagrama? = null

    var esCorrecto: Boolean = false
        private set

    var reporteErrores: String = ""
        private set

    var reportes: String = ""
        private set

    fun analizar() {
        try {
            val reader = StringReader(input)
            lexer = Lexer(reader)
            parser = Parser(lexer)
            parser.parse()

            val generadorReportes = GeneradorReportesError(lexer, parser)

            if (generadorReportes.hayErrores()) {
                esCorrecto = false
                reporteErrores = generadorReportes.generarReporteHtml()
            } else {
                esCorrecto = true
                generadorDiagrama = GeneradorDiagrama(input)

                val generadorReportes1 = GeneradorReportes(parser)
                reportes = generadorReportes1.generarReporteHtml()
            }

        } catch (e: Exception) {
            esCorrecto = false
            val generadorReportes = GeneradorReportesError(lexer, parser)
            reporteErrores = "Error crítico en el análisis.\n\n" + generadorReportes.generarReporteHtml()
        }
    }

    fun getDiagrama(): GeneradorDiagrama? {
        return generadorDiagrama
    }
}