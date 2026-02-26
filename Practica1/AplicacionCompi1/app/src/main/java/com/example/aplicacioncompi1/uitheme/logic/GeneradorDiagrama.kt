package com.example.aplicacioncompi1.uitheme.logic

enum class TipoNodo { INICIO_FIN, PROCESO, CONDICION, CICLO }

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
        val condigoInicial = bloques.getOrNull(0)?.trim() ?: ""
        val configuracionInicial = bloques.getOrNull(1)?.trim() ?: ""

        procesarConfiguraciones(configuracionInicial)

        val nodos = procesarCodigo(condigoInicial)

        return nodos
    }

    private fun procesarConfiguraciones(configuracionInicial: String) {
        if (configuracionInicial.isEmpty()) return
        val lineas = configuracionInicial.lines().map { it.trim() }.filter { it.isNotEmpty() && it.startsWith("%") }

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

    private fun procesarCodigo(codigoInicial: String): MutableList<NodoDiagrama> {
        val lineas = codigoInicial.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val nodosGenerados = mutableListOf<NodoDiagrama>()
        var i = 0

        while (i < lineas.size) {
            val linea = lineas[i]

            if (linea == "INICIO" || linea == "FIN") {
                val estilo = EstiloGrafico(figura = "ELIPSE")
                nodosGenerados.add(NodoDiagrama(0, TipoNodo.INICIO_FIN, linea, estilo))
                i++
            }
            else if (linea.startsWith("SI ")) {
                var texto = linea
                i++
                while (i < lineas.size && lineas[i] != "FIN SI" && !lineas[i].startsWith("%")) {
                    texto += "\n   " + lineas[i]
                    i++
                }
                if (i < lineas.size && lineas[i] == "FIN SI") {
                    texto += "\n" + lineas[i]
                    i++
                }

                val indiceActual = contadorIndices++
                val estilo = EstiloGrafico(figura = "ROMBO")
                aplicarConfiguracionesAlEstilo(indiceActual, TipoNodo.CONDICION, estilo)
                nodosGenerados.add(NodoDiagrama(indiceActual, TipoNodo.CONDICION, texto, estilo))
            }
            else if (linea.startsWith("MIENTRAS ")) {
                var texto = linea
                i++
                while (i < lineas.size && lineas[i] != "FIN MIENTRAS" && !lineas[i].startsWith("%")) {
                    texto += "\n   " + lineas[i]
                    i++
                }
                if (i < lineas.size && lineas[i] == "FIN MIENTRAS") {
                    texto += "\n" + lineas[i]
                    i++
                }

                val indiceActual = contadorIndices++
                val estilo = EstiloGrafico(figura = "PARALELOGRAMO")
                aplicarConfiguracionesAlEstilo(indiceActual, TipoNodo.CICLO, estilo)
                nodosGenerados.add(NodoDiagrama(indiceActual, TipoNodo.CICLO, texto, estilo))
            }
            else if (linea.startsWith("%") || linea == "%%%%") {
                break
            }
            else {
                var texto = linea
                i++
                while (i < lineas.size &&
                    !lineas[i].startsWith("SI ") &&
                    !lineas[i].startsWith("MIENTRAS ") &&
                    lineas[i] != "FIN" &&
                    lineas[i] != "%%%%" &&
                    !lineas[i].startsWith("%")) {
                    texto += "\n" + lineas[i]
                    i++
                }

                val indiceActual = contadorIndices++
                val estilo = EstiloGrafico(figura = "RECTANGULO")
                aplicarConfiguracionesAlEstilo(indiceActual, TipoNodo.PROCESO, estilo)
                nodosGenerados.add(NodoDiagrama(indiceActual, TipoNodo.PROCESO, texto, estilo))
            }
        }

        return nodosGenerados
    }

    private fun aplicarConfiguracionesAlEstilo(indice: Int, tipoNodo: TipoNodo, estilo: EstiloGrafico) {

        if (tipoNodo == TipoNodo.CONDICION) {
            configuraciones["COLOR_SI|$indice"]?.let { estilo.colorFondo = obtenerColor(it) }
            configuraciones["COLOR_TEXTO_SI|$indice"]?.let { estilo.colorTexto = obtenerColor(it) }
            configuraciones["FIGURA_SI|$indice"]?.let { estilo.figura = it }
            configuraciones["LETRA_SI|$indice"]?.let { estilo.fuente = it }
        }
        else if (tipoNodo == TipoNodo.CICLO) {
            configuraciones["COLOR_MIENTRAS|$indice"]?.let { estilo.colorFondo = obtenerColor(it) }
            configuraciones["COLOR_TEXTO_MIENTRAS|$indice"]?.let { estilo.colorTexto = obtenerColor(it) }
            configuraciones["FIGURA_MIENTRAS|$indice"]?.let { estilo.figura = it }
            configuraciones["LETRA_MIENTRAS|$indice"]?.let { estilo.fuente = it }
        }
        else {
            configuraciones["COLOR_BLOQUE|$indice"]?.let { estilo.colorFondo = obtenerColor(it) }
            configuraciones["COLOR_TEXTO_BLOQUE|$indice"]?.let { estilo.colorTexto = obtenerColor(it) }
            configuraciones["FIGURA_BLOQUE|$indice"]?.let { estilo.figura = it }
            configuraciones["LETRA_BLOQUE|$indice"]?.let { estilo.fuente = it }
        }
    }

    private fun obtenerColor(valor: String): String {
        if (valor.startsWith("H")) {
            return "#" + valor.substring(1)
        }

        if (valor.contains(",")) {
            val partesRgb = valor.split(",")
            if (partesRgb.size == 3) {
                try {
                    //obtnengo los valores de las expresiones matematicas manualmente
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

            fun siguienteCaracter() {
                ch = if (++pos < str.length) str[pos].code else -1
            }

            fun ignorar(charToEat: Int): Boolean {
                while (ch == ' '.code) siguienteCaracter()
                if (ch == charToEat) {
                    siguienteCaracter()
                    return true
                }
                return false
            }

            fun obtenerValor(): Double {
                siguienteCaracter()
                val x = obtenerExpresion()
                if (pos < str.length) throw RuntimeException("Carácter inesperado: " + ch.toChar())
                return x
            }

            fun obtenerExpresion(): Double {
                var x = obtenerTermino()
                while (true) {
                    if (ignorar('+'.code)) x += obtenerTermino()
                    else if (ignorar('-'.code)) x -= obtenerTermino()
                    else return x
                }
            }

            fun obtenerTermino(): Double {
                var x = obtenerFactor()
                while (true) {
                    if (ignorar('*'.code)) x *= obtenerFactor()
                    else if (ignorar('/'.code)) x /= obtenerFactor()
                    else return x
                }
            }

            fun obtenerFactor(): Double {
                if (ignorar('+'.code)) return obtenerFactor()
                if (ignorar('-'.code)) return -obtenerFactor()

                var x: Double
                val startPos = pos
                if (ignorar('('.code)) {
                    x = obtenerExpresion()
                    ignorar(')'.code)
                } else if (ch in '0'.code..'9'.code || ch == '.'.code) {
                    while (ch in '0'.code..'9'.code || ch == '.'.code) siguienteCaracter()
                    x = str.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Carácter inesperado: " + ch.toChar())
                }
                return x
            }
        }.obtenerValor()
    }
}