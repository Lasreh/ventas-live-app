package cl.salinas.ventasliveapp.model

data class Venta(
    var id: String = "",
    var nombrePrenda: String = "",
    var cliente: String = "",

    // 🔥 IMPORTANTE
    var precio: Long = 0L,

    var timestamp: Long = 0L
)