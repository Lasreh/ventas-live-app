package cl.salinas.ventasliveapp.data

import com.google.firebase.firestore.FirebaseFirestore
import cl.salinas.ventasliveapp.model.Venta

class FirestoreManager {

    private val db = FirebaseFirestore.getInstance()

    // 🔵 GUARDAR VENTA
    fun guardarVenta(venta: Venta) {

        val ref = db.collection("ventas").document()

        venta.id = ref.id
        venta.timestamp = System.currentTimeMillis()

        ref.set(venta)
    }

    // 🔵 OBTENER TODAS LAS VENTAS
    fun obtenerVentas(callback: (List<Venta>) -> Unit) {

        db.collection("ventas")
            .addSnapshotListener { result, error ->

                if (error != null || result == null) {
                    callback(emptyList())
                    return@addSnapshotListener
                }

                val lista = result.documents.mapNotNull { doc ->
                    doc.toObject(Venta::class.java)?.apply {
                        id = doc.id
                    }
                }

                callback(lista)
            }
    }

    // 🔵 OBTENER POR RANGO
    fun obtenerVentasPorRango(
        start: Long,
        end: Long,
        callback: (List<Venta>) -> Unit
    ) {
        db.collection("ventas")
            .whereGreaterThanOrEqualTo("timestamp", start)
            .whereLessThanOrEqualTo("timestamp", end)
            .addSnapshotListener { result, error ->

                if (error != null || result == null) {
                    callback(emptyList())
                    return@addSnapshotListener
                }

                val lista = result.documents.mapNotNull { doc ->
                    doc.toObject(Venta::class.java)?.apply {
                        id = doc.id
                    }
                }

                callback(lista)
            }
    }

    // 🔴 ELIMINAR
    fun eliminarVenta(id: String) {
        db.collection("ventas").document(id).delete()
    }

    // 🟡 ACTUALIZAR
    fun actualizarVenta(id: String, venta: Venta) {
        db.collection("ventas").document(id).set(venta)
    }
}