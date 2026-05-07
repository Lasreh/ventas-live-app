package cl.salinas.ventasliveapp.model

data class Venta(
    val id: String = "",
    val nombrePrenda: String = "",
    val cliente: String = "",
    val precio: Long = 0L,
    val pagado: Boolean = false,
    val timestamp: Long = 0L
)