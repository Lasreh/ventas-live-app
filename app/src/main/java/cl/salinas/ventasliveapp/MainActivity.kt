package cl.salinas.ventasliveapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cl.salinas.ventasliveapp.adapter.VentaAdapter
import cl.salinas.ventasliveapp.data.FirestoreManager
import cl.salinas.ventasliveapp.databinding.ActivityMainBinding
import java.util.Calendar
import cl.salinas.ventasliveapp.util.DateManager


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val firestore = FirestoreManager()

    private lateinit var adapter: VentaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
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

    override fun onResume() {
        super.onResume()

        cargarPorRango(
            DateManager.selectedStart,
            DateManager.selectedEnd
        )
    }

    // 🔵 RECYCLER SOLO UNA VEZ
    private fun setupRecycler() {

        adapter = VentaAdapter(
            onDelete = { venta ->

                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Eliminar venta")
                    .setMessage("¿Seguro que quieres eliminar esta venta?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        firestore.eliminarVenta(venta.id)
                        // ❌ NO recargar lista aquí
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            },

            onEdit = { venta ->

                val intent = Intent(this, AgregarVentaActivity::class.java)

                intent.putExtra("modo", "editar")
                intent.putExtra("ventaId", venta.id)
                intent.putExtra("nombrePrenda", venta.nombrePrenda)
                intent.putExtra("cliente", venta.cliente)
                intent.putExtra("precio", venta.precio)
                intent.putExtra("pagado", venta.pagado)
                intent.putExtra("timestamp", venta.timestamp)

                startActivity(intent)
            }
        )

        binding.recyclerVentas.layoutManager = LinearLayoutManager(this)
        binding.recyclerVentas.adapter = adapter
    }

    // 🔘 BOTONES
    private fun setupButtons() {

        binding.btnFecha.setOnClickListener {
            abrirSelectorFecha()
        }

        binding.btnAgregar.setOnClickListener {
            startActivity(Intent(this, AgregarVentaActivity::class.java))
        }

        binding.btnResumen.setOnClickListener {
            startActivity(Intent(this, ClientesActivity::class.java))
        }
    }

    private fun cargarPorRango(start: Long, end: Long) {

        firestore.obtenerVentasPorRango(start, end) { ventas ->

            adapter.submitList(ventas.toList())
        }
    }

    // 📅 FILTRO POR FECHA
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
}