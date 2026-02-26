package com.example.aplicacioncompi1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicacioncompi1.uitheme.viewModel.DiagramaView
import com.example.aplicacioncompi1.uitheme.logic.Analizador
import java.io.BufferedReader
import java.io.InputStreamReader
import android.app.AlertDialog
import android.webkit.WebView

class MainActivity : AppCompatActivity() {

    private lateinit var btnSeleccionar: Button
    private lateinit var tvNombreArchivo: TextView
    private lateinit var btnGenerar: Button
    private lateinit var btnEstadisticas: Button
    private lateinit var miDiagramaView: DiagramaView

    private var textoDelArchivo: String = ""

    private val selectorDeArchivos = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                leerArchivoTxt(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSeleccionar = findViewById(R.id.btnSeleccionar)
        tvNombreArchivo = findViewById(R.id.tvNombreArchivo)
        btnGenerar = findViewById(R.id.btnGenerar)
        btnEstadisticas = findViewById(R.id.btnEstadisticas)
        miDiagramaView = findViewById(R.id.miDiagramaView)

        btnSeleccionar.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "text/plain"
            selectorDeArchivos.launch(intent)
        }

        btnGenerar.setOnClickListener {
            if (textoDelArchivo.isNotEmpty()) {
                val analizador = Analizador(textoDelArchivo)
                analizador.analizar()

                if (analizador.esCorrecto) {
                    val generador = analizador.getDiagrama()
                    if (generador != null) {
                        val nodosGenerados = generador.generarDiagrama()
                        miDiagramaView.nodos = nodosGenerados
                        Toast.makeText(this, "¡Diagrama generado con éxito!", Toast.LENGTH_SHORT)
                            .show()

                        btnEstadisticas.isEnabled = true
                        btnEstadisticas.setOnClickListener {
                            mostrarReportes(analizador.reporteEstadistico)
                        }
                    }
                } else {
                    miDiagramaView.nodos = emptyList()
                    btnEstadisticas.isEnabled = false
                    mostrarReporteDeErrores(analizador.reporteErrores)
                }
            } else {
                Toast.makeText(this, "El archivo está vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarReportes(htmlReporte: String) {
        val webView = WebView(this)
        webView.loadDataWithBaseURL(null, htmlReporte, "text/html", "UTF-8", null)

        AlertDialog.Builder(this)
            .setView(webView)
            .setCancelable(false)
            .setPositiveButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun mostrarReporteDeErrores(htmlReporte: String) {
        val webView = WebView(this)

        webView.loadDataWithBaseURL(null, htmlReporte, "text/html", "UTF-8", null)

        AlertDialog.Builder(this)
            .setView(webView)
            .setCancelable(false)
            .setPositiveButton("Entendido") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun leerArchivoTxt(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var linea: String?

            while (reader.readLine().also { linea = it } != null) {
                stringBuilder.append(linea).append("\n")
            }
            inputStream?.close()

            textoDelArchivo = stringBuilder.toString()
            tvNombreArchivo.text = "¡Archivo cargado en memoria!"
            btnGenerar.isEnabled = true

        } catch (e: Exception) {
            Toast.makeText(this, "Error al leer el archivo", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}