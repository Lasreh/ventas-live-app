package cl.salinas.ventasliveapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cl.salinas.ventasliveapp.R
import cl.salinas.ventasliveapp.model.ClienteConVentas

class ClienteAdapter(
    private val lista: List<ClienteConVentas>
) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    class ClienteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cliente = view.findViewById<TextView>(R.id.txtCliente)
        val total = view.findViewById<TextView>(R.id.txtTotal)
        val detalle = view.findViewById<TextView>(R.id.txtDetalle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {

        val item = lista[position]

        val total = item.ventas.sumOf { it.precio }

        holder.cliente.text = item.cliente
        holder.total.text = "Total: $$total"

        holder.detalle.text = item.ventas.joinToString("\n") {
            "- ${it.nombrePrenda} $${it.precio}"
        }
    }

    override fun getItemCount(): Int = lista.size
}