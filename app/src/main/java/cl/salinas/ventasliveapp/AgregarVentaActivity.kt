package cl.salinas.ventasliveapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cl.salinas.ventasliveapp.data.FirestoreManager
import cl.salinas.ventasliveapp.databinding.ActivityAgregarVentaBinding
import cl.salinas.ventasliveapp.model.Venta
import cl.salinas.ventasliveapp.util.toTitleCase

class AgregarVentaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarVentaBinding
    private val firestore = FirestoreManager()

    private var ventaId: String? = null
    private var modoEditar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAgregarVentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔵 modo editar
        modoEditar = intent.getStringExtra("modo") == "editar"

        if (modoEditar) {
            ventaId = intent.getStringExtra("ventaId")

            binding.editCliente.setText(
                intent.getStringExtra("cliente")?.toTitleCase()
            )

            binding.editNombre.setText(
                intent.getStringExtra("nombrePrenda")?.toTitleCase()
            )

            // 🔥 FIX: precio es Long (no Int)
            binding.editPrecio.setText(
                intent.getLongExtra("precio", 0L).toString()
            )

            binding.btnGuardar.text = "Actualizar venta"
        }

        binding.btnGuardar.setOnClickListener {

            val cliente = binding.editCliente.text.toString().trim().lowercase()
            val nombre = binding.editNombre.text.toString().trim()
            val precioText = binding.editPrecio.text.toString().trim()

            if (nombre.isEmpty() || cliente.isEmpty() || precioText.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val precio = precioText.toLongOrNull()
            if (precio == null) {
                Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pagadoActual = intent.getBooleanExtra("pagado", false)

            // 🔥 IMPORTANTE: NO pisar timestamp en edición
            val venta = Venta(
                id = ventaId ?: "",
                nombrePrenda = nombre,
                cliente = cliente,
                precio = precio,
                pagado = pagadoActual,
                timestamp = if (modoEditar) {
                    // mantener el original (ideal)
                    intent.getLongExtra("timestamp", System.currentTimeMillis())
                } else {
                    System.currentTimeMillis()
                }
            )

            if (modoEditar && ventaId != null) {
                firestore.actualizarVenta(ventaId!!, venta)
                Toast.makeText(this, "Venta actualizada", Toast.LENGTH_SHORT).show()
            } else {
                firestore.guardarVenta(venta)
                Toast.makeText(this, "Venta guardada", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }
}