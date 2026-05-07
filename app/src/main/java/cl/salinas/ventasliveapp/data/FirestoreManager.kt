package cl.salinas.ventasliveapp.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import cl.salinas.ventasliveapp.model.Venta

class FirestoreManager {

    private val db = FirebaseFirestore.getInstance()

    private var ventasListener: ListenerRegistration? = null
    private var rangoListener: ListenerRegistration? = null

    // 🔵 GUARDAR (PRO)
    fun guardarVenta(venta: Venta) {
        val ref = db.collection("ventas").document()

        val ventaFinal = venta.copy(
            id = ref.id,
            cliente = venta.cliente.trim().lowercase(), // 🔥 normalización base
            nombrePrenda = venta.nombrePrenda.trim().lowercase(),
            timestamp = System.currentTimeMillis()
        )

        ref.set(ventaFinal)
    }

    // 🔵 OBTENER TODAS
    fun obtenerVentas(callback: (List<Venta>) -> Unit) {

        ventasListener?.remove()

        ventasListener = db.collection("ventas")
            .addSnapshotListener { result, error ->

                if (error != null || result == null) {
                    callback(emptyList())
                    return@addSnapshotListener
                }

                val lista = result.documents.mapNotNull { doc ->
                    doc.toObject(Venta::class.java)?.copy(id = doc.id)
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

        rangoListener?.remove()

        rangoListener = db.collection("ventas")
            .whereGreaterThanOrEqualTo("timestamp", start)
            .whereLessThanOrEqualTo("timestamp", end)
            .addSnapshotListener { result, error ->

                if (error != null || result == null) {
                    callback(emptyList())
                    return@addSnapshotListener
                }

                val lista = result.documents.mapNotNull { doc ->
                    doc.toObject(Venta::class.java)?.copy(id = doc.id)
                }

                callback(lista)
            }
    }

    // 🔴 ELIMINAR
    fun eliminarVenta(id: String) {
        db.collection("ventas").document(id).delete()
    }

    // 🟡 ACTUALIZAR COMPLETO (NECESARIO PARA TU APP)
    fun actualizarVenta(id: String, venta: Venta) {
        db.collection("ventas")
            .document(id)
            .set(venta)
    }

    // 🟡 ACTUALIZAR PARCIAL (PRO)
    fun actualizarCampo(id: String, datos: Map<String, Any>) {
        db.collection("ventas")
            .document(id)
            .update(datos)
    }

    // 🧹 LIMPIEZA SEGURA
    fun limpiarListeners() {
        ventasListener?.remove()
        ventasListener = null

        rangoListener?.remove()
        rangoListener = null
    }

    fun eliminarClienteCompleto(cliente: String, callback: (() -> Unit)? = null) {

        db.collection("ventas")
            .whereEqualTo("cliente", cliente.trim().lowercase())
            .get()
            .addOnSuccessListener { result ->

                val batch = db.batch()

                for (doc in result.documents) {
                    batch.delete(doc.reference)
                }

                batch.commit().addOnSuccessListener {
                    callback?.invoke()
                }
            }
    }
}