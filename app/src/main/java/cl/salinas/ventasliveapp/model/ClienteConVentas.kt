package cl.salinas.ventasliveapp.model

data class ClienteConVentas(
    val cliente: String,
    val total: Long,
    val ventas: List<Venta>
)