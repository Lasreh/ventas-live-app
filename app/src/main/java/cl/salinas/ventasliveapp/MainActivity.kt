package cl.salinas.ventasliveapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cl.salinas.ventasliveapp.adapter.VentaAdapter
import cl.salinas.ventasliveapp.data.FirestoreManager
import cl.salinas.ventasliveapp.databinding.ActivityMainBinding
import cl.salinas.ventasliveapp.model.Venta
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val firestore = FirestoreManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerVentas.layoutManager =
            LinearLayoutManager(this)

        // 📅 FILTRO FECHA
        binding.btnFecha.setOnClickListener {
            abrirSelectorFecha()
        }

        // 🔵 CARGA INICIAL
        cargarVentasHoy()

        // 🟢 AGREGAR
        binding.btnAgregar.setOnClickListener {
            startActivity(Intent(this, AgregarVentaActivity::class.java))
        }

        // 📊 CLIENTES
        binding.btnResumen.setOnClickListener {
            startActivity(Intent(this, ClientesActivity::class.java))
        }
    }

    // 🔄 IMPORTANTE: recargar cuando vuelves a la pantalla
    override fun onResume() {
        super.onResume()
        cargarVentasHoy()
    }

    // 🔵 TURNO ACTUAL
    private fun cargarVentasHoy() {

        val (start, end) = DateUtils.obtenerRangoHoyTurno()

        firestore.obtenerVentasPorRango(start, end) { ventas ->
            mostrarVentas(ventas)
        }
    }

    // 📅 FILTRO POR FECHA
    private fun abrirSelectorFecha() {

        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->

                val (start, end) =
                    DateUtils.obtenerRangoTurno(year, month, day)

                firestore.obtenerVentasPorRango(start, end) { ventas ->
                    mostrarVentas(ventas)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // 🧠 MOSTRAR VENTAS
    private fun mostrarVentas(ventas: List<Venta>) {

        binding.recyclerVentas.adapter = VentaAdapter(
            ventas,
            onDelete = { venta ->

                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Eliminar venta")
                    .setMessage("¿Seguro que quieres eliminar esta venta?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        firestore.eliminarVenta(venta.id)

                        // 🔄 recargar lista después de borrar
                        cargarVentasHoy()
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

                startActivity(intent)
            }
        )
    }
}