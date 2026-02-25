package com.example.aplicacioncompi1.uitheme.logic

import java.io.StringReader

class Analizador(private val input: String) {

    private lateinit var lexer: Lexer
    private lateinit var parser: Parser
    // Cambiamos a nullable para evitar crashes si falla el análisis
    private var generadorDiagrama: GeneradorDiagrama? = null

    // Hacemos que sea pública para poder leerla desde el MainActivity
    var esCorrecto: Boolean = false
        private set // Pero solo se puede modificar desde aquí adentro

    fun analizar() {
        try {
            val reader = StringReader(input)

            lexer = Lexer(reader)
            parser = Parser(lexer)
            parser.parse() // Si esto falla, salta directo al catch

            esCorrecto = true
            generadorDiagrama = GeneradorDiagrama(input)

        } catch (e: Exception) {
            esCorrecto = false
            println("Error durante el análisis: ${e.message}")

            // val generadorReportesError = GeneradorReportesError(lexer, parser)
            // generadorReportesError.generarReporte()
        }
    }

    // Retorna nullable en caso de que haya fallado
    fun getDiagrama(): GeneradorDiagrama? {
        return generadorDiagrama
    }
}