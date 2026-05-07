package cl.salinas.ventasliveapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cl.salinas.ventasliveapp.R
import cl.salinas.ventasliveapp.model.ClienteConVentas
import cl.salinas.ventasliveapp.util.toPriceCLP
import cl.salinas.ventasliveapp.util.toTitleCase
class ClienteAdapter(
    private val onTogglePagado: (ClienteConVentas) -> Unit,
    private val onDeleteCliente: (ClienteConVentas) -> Unit
) : ListAdapter<ClienteConVentas, ClienteAdapter.ClienteViewHolder>(DiffCallback) {

    class ClienteViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val cliente = view.findViewById<TextView>(R.id.txtCliente)
        val total = view.findViewById<TextView>(R.id.txtTotal)
        val detalle = view.findViewById<TextView>(R.id.txtDetalle)
        val estado = view.findViewById<TextView>(R.id.txtEstado)
        val btnPagado = view.findViewById<Button>(R.id.btnPagado)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)

        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {

        val item = getItem(position)

        holder.cliente.text = item.cliente.toTitleCase()
        holder.total.text = "Total: ${item.total.toPriceCLP()}"

        holder.detalle.text = item.ventas.joinToString("\n") {
            "- ${it.nombrePrenda.toTitleCase()} ${it.precio.toPriceCLP()}"
        }

        val pagado = item.ventas.all { it.pagado }

        if (pagado) {
            holder.estado.text = "Pagado"
            holder.estado.setTextColor(Color.parseColor("#16A34A"))
            holder.btnPagado.text = "Pendiente"
        } else {
            holder.estado.text = "Pendiente"
            holder.estado.setTextColor(Color.parseColor("#DC2626"))
            holder.btnPagado.text = "Pagado"
        }

        holder.btnPagado.setOnClickListener {
            onTogglePagado(item)
        }

        holder.btnEliminar.setOnClickListener {
            onDeleteCliente(item)
        }
    }

    companion object {

        val DiffCallback = object : DiffUtil.ItemCallback<ClienteConVentas>() {

            override fun areItemsTheSame(oldItem: ClienteConVentas, newItem: ClienteConVentas): Boolean {
                return oldItem.cliente.trim().lowercase() ==
                        newItem.cliente.trim().lowercase()
            }

            override fun areContentsTheSame(oldItem: ClienteConVentas, newItem: ClienteConVentas): Boolean {
                return oldItem == newItem
            }
        }
    }
}