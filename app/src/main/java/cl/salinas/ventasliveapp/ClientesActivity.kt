package cl.salinas.ventasliveapp

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cl.salinas.ventasliveapp.adapter.ClienteAdapter
import cl.salinas.ventasliveapp.data.FirestoreManager
import cl.salinas.ventasliveapp.databinding.ActivityClientesBinding
import cl.salinas.ventasliveapp.model.ClienteConVentas
import java.util.Calendar

class ClientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientesBinding
    private val firestore = FirestoreManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerClientes.layoutManager =
            LinearLayoutManager(this)

        // 📅 FILTRO FECHA
        binding.btnFechaClientes.setOnClickListener {
            abrirSelectorFecha()
        }

        // 🔵 CARGA INICIAL (TURNO ACTUAL)
        cargarPorFechaHoy()
    }

    // 🔄 IMPORTANTE: refresco al volver
    override fun onResume() {
        super.onResume()
        cargarPorFechaHoy()
    }

    // 📅 SELECTOR FECHA
    private fun abrirSelectorFecha() {

        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->

                val (start, end) =
                    DateUtils.obtenerRangoTurno(year, month, day)

                cargarPorRango(start, end)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // 🔵 HOY (TURNO ACTUAL)
    private fun cargarPorFechaHoy() {

        val (start, end) = DateUtils.obtenerRangoHoyTurno()

        cargarPorRango(start, end)
    }

    // 📊 CARGA PRINCIPAL
    private fun cargarPorRango(start: Long, end: Long) {

        firestore.obtenerVentasPorRango(start, end) { ventas ->

            val agrupado = ventas.groupBy { it.cliente }

            val lista = agrupado.map { (cliente, compras) ->

                ClienteConVentas(
                    cliente = cliente,
                    total = compras.sumOf { it.precio },
                    ventas = compras
                )
            }

            val totalGeneral = ventas.sumOf { it.precio }

            runOnUiThread {

                binding.txtTotalGeneral.text =
                    "Total general: $$totalGeneral"

                binding.recyclerClientes.adapter =
                    ClienteAdapter(lista)
            }
        }
    }
}