package com.example.aplicacioncompi1.uitheme.logic

enum class TipoNodo { INICIO_FIN, PROCESO, ENTRADA_SALIDA, CONDICION, CICLO }

data class EstiloGrafico(
    var figura: String,
    var colorFondo: String = "#FFFFFF",
    var colorTexto: String = "#000000",
    var fuente: String = "ARIAL",
    var tamanoFuente: Double = 12.0
)

data class NodoDiagrama(
    val indice: Int,
    val tipo: TipoNodo,
    val texto: String,
    val estilo: EstiloGrafico,
    val subNodos: MutableList<NodoDiagrama> = mutableListOf()
)

class GeneradorDiagrama(val input: String) {

    private val configuraciones = mutableMapOf<String, String>()
    private var contadorIndices = 1

    fun generarDiagrama(): List<NodoDiagrama> {
        val bloques = input.split(Regex("%%%%|SEPARADOR"))
        val codigoCrudo = bloques.getOrNull(0)?.trim() ?: ""
        val configCruda = bloques.getOrNull(1)?.trim() ?: ""

        procesarConfiguraciones(configCruda)

        val nodos = procesarCodigo(codigoCrudo)

        return nodos
    }

    private fun procesarConfiguraciones(configCruda: String) {
        if (configCruda.isEmpty()) return
        val lineas = configCruda.lines().map { it.trim() }.filter { it.isNotEmpty() && it.startsWith("%") }

        for (linea in lineas) {
            val sinPorcentaje = linea.removePrefix("%")
            val partes = sinPorcentaje.split("=")
            if (partes.size == 2) {
                val instruccion = partes[0].trim()
                val valorYIndice = partes[1].split("|")

                if (valorYIndice.size == 2) {
                    val valor = valorYIndice[0].trim()
                    val indice = valorYIndice[1].trim()

                    configuraciones["$instruccion|$indice"] = valor
                } else if (instruccion == "DEFAULT") {
                    configuraciones["DEFAULT|${partes[1].trim()}"] = "TRUE"
                }
            }
        }
    }

    private fun procesarCodigo(codigoCrudo: String): MutableList<NodoDiagrama> {
        val lineas = codigoCrudo.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val nodosGenerados = mutableListOf<NodoDiagrama>()

        for (linea in lineas) {
            if (linea == "INICIO" || linea == "FIN" || linea == "FIN SI" || linea == "FIN MIENTRAS") continue

            val indiceActual = contadorIndices++
            var tipoNodo = TipoNodo.PROCESO
            var textoMostrar = linea
            var figuraDefault = "RECTANGULO"

            when {
                linea.startsWith("VAR ") || linea.contains("=") -> {
                    tipoNodo = TipoNodo.PROCESO
                    figuraDefault = "RECTANGULO"
                }
                linea.startsWith("MOSTRAR") || linea.startsWith("LEER") -> {
                    tipoNodo = TipoNodo.ENTRADA_SALIDA
                    figuraDefault = "PARALELOGRAMO"
                }
                linea.startsWith("SI ") -> {
                    tipoNodo = TipoNodo.CONDICION
                    figuraDefault = "ROMBO"
                    textoMostrar = linea.substringAfter("(").substringBefore(")")
                }
                linea.startsWith("MIENTRAS ") -> {
                    tipoNodo = TipoNodo.CICLO
                    figuraDefault = "ROMBO"
                    textoMostrar = linea.substringAfter("(").substringBefore(")")
                }
            }

            val estilo = EstiloGrafico(figura = figuraDefault)

            aplicarConfiguracionesAlEstilo(indiceActual, tipoNodo, estilo)

            nodosGenerados.add(NodoDiagrama(indiceActual, tipoNodo, textoMostrar, estilo))
        }

        return nodosGenerados
    }

    private fun aplicarConfiguracionesAlEstilo(indice: Int, tipoNodo: TipoNodo, estilo: EstiloGrafico) {

        if (tipoNodo == TipoNodo.CONDICION) {
            configuraciones["COLOR_SI|$indice"]?.let { estilo.colorFondo = parsearColor(it) }
            configuraciones["COLOR_TEXTO_SI|$indice"]?.let { estilo.colorTexto = parsearColor(it) }
            configuraciones["FIGURA_SI|$indice"]?.let { estilo.figura = it }
            configuraciones["LETRA_SI|$indice"]?.let { estilo.fuente = it }
        }
        else if (tipoNodo == TipoNodo.CICLO) {
            configuraciones["COLOR_MIENTRAS|$indice"]?.let { estilo.colorFondo = parsearColor(it) }
            configuraciones["COLOR_TEXTO_MIENTRAS|$indice"]?.let { estilo.colorTexto = parsearColor(it) }
            configuraciones["FIGURA_MIENTRAS|$indice"]?.let { estilo.figura = it }
            configuraciones["LETRA_MIENTRAS|$indice"]?.let { estilo.fuente = it }
        }
        else {
            configuraciones["COLOR_BLOQUE|$indice"]?.let { estilo.colorFondo = parsearColor(it) }
            configuraciones["COLOR_TEXTO_BLOQUE|$indice"]?.let { estilo.colorTexto = parsearColor(it) }
            configuraciones["FIGURA_BLOQUE|$indice"]?.let { estilo.figura = it }
            configuraciones["LETRA_BLOQUE|$indice"]?.let { estilo.fuente = it }
        }
    }

    private fun parsearColor(valor: String): String {
        if (valor.startsWith("H")) {
            return "#" + valor.substring(1)
        }

        if (valor.contains(",")) {
            val partesRgb = valor.split(",")
            if (partesRgb.size == 3) {
                try {
                    val r = evaluarExpresion(partesRgb[0]).toInt().coerceIn(0, 255)
                    val g = evaluarExpresion(partesRgb[1]).toInt().coerceIn(0, 255)
                    val b = evaluarExpresion(partesRgb[2]).toInt().coerceIn(0, 255)

                    return String.format("#%02X%02X%02X", r, g, b)
                } catch (e: Exception) {
                    return "#000000"
                }
            }
        }
        return valor
    }

    fun evaluarExpresion(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Carácter inesperado: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm()
                    else if (eat('-'.code)) x -= parseTerm()
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor()
                    else if (eat('/'.code)) x /= parseFactor()
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch in '0'.code..'9'.code || ch == '.'.code) {
                    while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Carácter inesperado: " + ch.toChar())
                }
                return x
            }
        }.parse()
    }
}