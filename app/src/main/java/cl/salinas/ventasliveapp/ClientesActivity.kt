package cl.salinas.ventasliveapp

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cl.salinas.ventasliveapp.adapter.ClienteAdapter
import cl.salinas.ventasliveapp.data.FirestoreManager
import cl.salinas.ventasliveapp.databinding.ActivityClientesBinding
import cl.salinas.ventasliveapp.model.ClienteConVentas
import java.util.Calendar
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import cl.salinas.ventasliveapp.util.toPriceCLP
import cl.salinas.ventasliveapp.util.DateManager

class ClientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientesBinding
    private val firestore = FirestoreManager()

    private lateinit var adapter: ClienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityClientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecycler()
        setupButtons()

        if (DateManager.selectedStart == 0L) {
            DateManager.setToday()
        }

        cargarPorRango(
            DateManager.selectedStart,
            DateManager.selectedEnd
        )
    }

    // 🔵 RECYCLER PRO (CORREGIDO)
    private fun setupRecycler() {

        adapter = ClienteAdapter(
            onTogglePagado = { cliente ->

                val nuevoEstado = !cliente.ventas.all { it.pagado }

                val accion = if (nuevoEstado) "Pagado" else "Pendiente"

                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirmar acción")
                    .setMessage("¿Seguro que quieres marcar este cliente como $accion?")
                    .setPositiveButton("Sí") { _, _ ->

                        cliente.ventas.forEach { venta ->
                            firestore.actualizarCampo(
                                venta.id,
                                mapOf("pagado" to nuevoEstado)
                            )
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            },

            onDeleteCliente = { cliente ->

                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Eliminar cliente")
                    .setMessage("¿Seguro que quieres eliminar TODAS las ventas de ${cliente.cliente}?")
                    .setPositiveButton("Eliminar") { _, _ ->

                        firestore.eliminarClienteCompleto(cliente.cliente.trim().lowercase())
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        binding.recyclerClientes.layoutManager =
            LinearLayoutManager(this)

        binding.recyclerClientes.adapter = adapter
    }

    // 🔘 BOTONES
    private fun setupButtons() {

        binding.btnFechaClientes.setOnClickListener {
            abrirSelectorFecha()
        }
    }

    // 📅 SELECTOR FECHA
    private fun abrirSelectorFecha() {

        val calendar = Calendar.getInstance().apply {
            timeInMillis = DateManager.selectedStart
        }

        DatePickerDialog(
            this,
            { _, year, month, day ->

                val (start, end) =
                    DateUtils.obtenerRangoTurno(year, month, day)

                DateManager.setDate(start, end)

                cargarPorRango(start, end)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // 🔵 HOY
    private fun cargarPorFechaHoy() {

        val (start, end) = DateUtils.obtenerRangoHoyTurno()

        cargarPorRango(start, end)
    }

    // 📊 CARGA PRINCIPAL
    private fun cargarPorRango(start: Long, end: Long) {

        firestore.obtenerVentasPorRango(start, end) { ventas ->

            val agrupado = ventas.groupBy { it.cliente.trim().lowercase() }

            val lista = agrupado.map { (cliente, compras) ->

                ClienteConVentas(
                    cliente = cliente,
                    total = compras.sumOf { it.precio },
                    ventas = compras,
                    pagado = compras.all { it.pagado }
                )
            }

            val totalGeneral = ventas.sumOf { it.precio }

            runOnUiThread {

                val texto = "Total general: ${totalGeneral.toPriceCLP()}"

                val spannable = SpannableString(texto)

                val labelEnd = "Total general: ".length

                spannable.setSpan(
                    ForegroundColorSpan(Color.parseColor("#9A9A9A")),
                    0,
                    labelEnd,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannable.setSpan(
                    ForegroundColorSpan(Color.parseColor("#D4FF00")),
                    labelEnd,
                    texto.length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                binding.txtTotalGeneral.text = spannable

                adapter.submitList(lista)
            }
        }
    }
}