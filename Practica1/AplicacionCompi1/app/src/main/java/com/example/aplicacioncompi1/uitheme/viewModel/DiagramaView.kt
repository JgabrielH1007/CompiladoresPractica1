package com.example.aplicacioncompi1.uitheme.viewModel

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.aplicacioncompi1.uitheme.logic.NodoDiagrama

class DiagramaView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Lista de nodos que recibirá desde tu GeneradorDiagrama
    var nodos: List<NodoDiagrama> = emptyList()
        set(value) {
            field = value
            requestLayout() // Le avisa a Android que debe recalcular el tamaño
            invalidate()    // Obliga a redibujar el lienzo
        }

    // Pinceles para dibujar
    private val paintFondo = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val paintBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = Color.BLACK
    }
    private val paintTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }
    private val paintLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.DKGRAY
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (nodos.isEmpty()) return

        val anchoNodo = 400f
        val altoNodo = 150f
        val espaciadoVertical = 100f

        var xCentro = width / 2f
        var yActual = 100f

        for ((index, nodo) in nodos.withIndex()) {
            val left = xCentro - (anchoNodo / 2)
            val top = yActual
            val right = xCentro + (anchoNodo / 2)
            val bottom = yActual + altoNodo

            aplicarEstilosAlPincel(nodo)

            dibujarFigura(canvas, left, top, right, bottom, nodo.estilo.figura)

            val yTexto = top + (altoNodo / 2) - ((paintTexto.descent() + paintTexto.ascent()) / 2)
            canvas.drawText(nodo.texto, xCentro, yTexto, paintTexto)

            if (index < nodos.size - 1) {
                canvas.drawLine(xCentro, bottom, xCentro, bottom + espaciadoVertical, paintLinea)
                // Aquí podrías agregar lógica para dibujar la flechita (triángulo) al final de la línea
            }

            // Mover la coordenada Y para el siguiente nodo
            yActual += altoNodo + espaciadoVertical
        }
    }

    private fun aplicarEstilosAlPincel(nodo: NodoDiagrama) {
        try {
            paintFondo.color = Color.parseColor(nodo.estilo.colorFondo)
            paintTexto.color = Color.parseColor(nodo.estilo.colorTexto)
        } catch (e: Exception) {
            paintFondo.color = Color.WHITE
            paintTexto.color = Color.BLACK
        }

        val scaledDensity = resources.displayMetrics.scaledDensity
        paintTexto.textSize = (nodo.estilo.tamanoFuente.toFloat() * scaledDensity)

        val typeface = when (nodo.estilo.fuente.uppercase()) {
            "ARIAL" -> Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            "TIMES_NEW_ROMAN" -> Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            "COMIC_SANS" -> Typeface.create("casual", Typeface.NORMAL) // 'casual' es lo más cercano a Comic Sans en Android nativo
            "VERDANA" -> Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD) // Aproximación
            else -> Typeface.DEFAULT
        }
        paintTexto.typeface = typeface
    }

    private fun dibujarFigura(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float, tipoFigura: String) {
        val rect = RectF(left, top, right, bottom)
        val path = Path()

        when (tipoFigura.uppercase()) {
            "RECTANGULO" -> {
                canvas.drawRect(rect, paintFondo)
                canvas.drawRect(rect, paintBorde)
            }
            "RECTANGULO_REDONDEADO" -> {
                canvas.drawRoundRect(rect, 30f, 30f, paintFondo)
                canvas.drawRoundRect(rect, 30f, 30f, paintBorde)
            }
            "ELIPSE", "CIRCULO" -> {
                // Si es círculo estricto, podrías forzar que ancho == alto. Por ahora usamos óvalo.
                canvas.drawOval(rect, paintFondo)
                canvas.drawOval(rect, paintBorde)
            }
            "ROMBO" -> {
                val midX = left + (right - left) / 2
                val midY = top + (bottom - top) / 2
                path.moveTo(midX, top)
                path.lineTo(right, midY)
                path.lineTo(midX, bottom)
                path.lineTo(left, midY)
                path.close()
                canvas.drawPath(path, paintFondo)
                canvas.drawPath(path, paintBorde)
            }
            "PARALELOGRAMO" -> {
                val offset = 60f // Inclinación
                path.moveTo(left + offset, top)
                path.lineTo(right, top)
                path.lineTo(right - offset, bottom)
                path.lineTo(left, bottom)
                path.close()
                canvas.drawPath(path, paintFondo)
                canvas.drawPath(path, paintBorde)
            }
            else -> { // Por defecto rectángulo
                canvas.drawRect(rect, paintFondo)
                canvas.drawRect(rect, paintBorde)
            }
        }
    }
}