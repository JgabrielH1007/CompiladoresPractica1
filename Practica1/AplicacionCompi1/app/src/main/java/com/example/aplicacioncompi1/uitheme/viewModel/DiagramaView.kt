package com.example.aplicacioncompi1.uitheme.viewModel

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.aplicacioncompi1.uitheme.logic.NodoDiagrama

class DiagramaView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var nodos: List<NodoDiagrama> = emptyList()
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    private val paintFondo = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val paintBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 5f; color = Color.BLACK
    }
    private val paintTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }
    private val paintLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 4f; color = Color.DKGRAY
    }

    private fun obtenerAltoNodo(nodo: NodoDiagrama): Float {
        val lineas = nodo.texto.split("\n").size
        return kotlin.math.max(120f, (lineas * 50f) + 60f)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val anchoPantalla = MeasureSpec.getSize(widthMeasureSpec)
        if (nodos.isEmpty()) {
            setMeasuredDimension(anchoPantalla, 0)
            return
        }

        var alturaTotal = 100f
        for (nodo in nodos) {
            alturaTotal += obtenerAltoNodo(nodo) + 100f
        }
        alturaTotal += 100f

        setMeasuredDimension(anchoPantalla, alturaTotal.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (nodos.isEmpty()) return

        val anchoNodo = 500f
        val espaciadoVertical = 100f
        val xCentro = width / 2f
        var yActual = 100f

        for ((index, nodo) in nodos.withIndex()) {
            val altoNodo = obtenerAltoNodo(nodo)
            val left = xCentro - (anchoNodo / 2)
            val right = xCentro + (anchoNodo / 2)
            val bottom = yActual + altoNodo

            aplicarEstilosAlPincel(nodo)
            dibujarFigura(canvas, left, yActual, right, bottom, nodo.estilo.figura)

            val lineasTexto = nodo.texto.split("\n")
            val alturaLinea = paintTexto.descent() - paintTexto.ascent()
            val alturaBloqueTexto = alturaLinea * lineasTexto.size

            var yTexto = yActual + (altoNodo / 2) - (alturaBloqueTexto / 2) - paintTexto.ascent()

            for (linea in lineasTexto) {
                canvas.drawText(linea, xCentro, yTexto, paintTexto)
                yTexto += alturaLinea
            }

            if (index < nodos.size - 1) {
                canvas.drawLine(xCentro, bottom, xCentro, bottom + espaciadoVertical, paintLinea)

                val yFlecha = bottom + espaciadoVertical
                val pathFlecha = Path()
                pathFlecha.moveTo(xCentro, yFlecha)
                pathFlecha.lineTo(xCentro - 15f, yFlecha - 20f)
                pathFlecha.lineTo(xCentro + 15f, yFlecha - 20f)
                pathFlecha.close()
                canvas.drawPath(pathFlecha, paintFondo.apply { color = Color.DKGRAY })
            }

            yActual += altoNodo + espaciadoVertical
        }
    }

    private fun aplicarEstilosAlPincel(nodo: NodoDiagrama) {
        try {
            paintFondo.color = Color.parseColor(nodo.estilo.colorFondo)
            paintTexto.color = Color.parseColor(nodo.estilo.colorTexto)
        } catch (e: Exception) {
            paintFondo.color = Color.WHITE; paintTexto.color = Color.BLACK
        }

        val scaledDensity = resources.displayMetrics.scaledDensity
        paintTexto.textSize = (nodo.estilo.tamanoFuente.toFloat() * scaledDensity)

        paintTexto.typeface = when (nodo.estilo.fuente.uppercase()) {
            "ARIAL" -> Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            "TIMES_NEW_ROMAN" -> Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            "COMIC_SANS" -> Typeface.create("casual", Typeface.NORMAL)
            "VERDANA" -> Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            else -> Typeface.DEFAULT
        }
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
                canvas.drawRoundRect(rect, 40f, 40f, paintFondo)
                canvas.drawRoundRect(rect, 40f, 40f, paintBorde)
            }
            "ELIPSE", "CIRCULO" -> {
                canvas.drawOval(rect, paintFondo)
                canvas.drawOval(rect, paintBorde)
            }

            "ROMBO" -> {
                path.moveTo(left + (right - left) / 2, top)
                path.lineTo(right, top + (bottom - top) / 2)
                path.lineTo(left + (right - left) / 2, bottom)
                path.lineTo(left, top + (bottom - top) / 2)
                path.close()
                canvas.drawPath(path, paintFondo)
                canvas.drawPath(path, paintBorde)
            }
            "PARALELOGRAMO" -> {
                val offset = 60f
                path.moveTo(left + offset, top)
                path.lineTo(right, top)
                path.lineTo(right - offset, bottom)
                path.lineTo(left, bottom)
                path.close()
                canvas.drawPath(path, paintFondo)
                canvas.drawPath(path, paintBorde)
            }
            else -> {
                canvas.drawRect(rect, paintFondo)
                canvas.drawRect(rect, paintBorde)
            }
        }
    }
}