package cl.salinas.ventasliveapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cl.salinas.ventasliveapp.R
import cl.salinas.ventasliveapp.model.Venta
import cl.salinas.ventasliveapp.util.toPriceCLP
import cl.salinas.ventasliveapp.util.toTitleCase

class VentaAdapter(
    private val onDelete: (Venta) -> Unit,
    private val onEdit: (Venta) -> Unit
) : ListAdapter<Venta, VentaAdapter.VentaViewHolder>(DiffCallback) {

    class VentaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val prenda: TextView = view.findViewById(R.id.txtPrenda)
        val cliente: TextView = view.findViewById(R.id.txtCliente)
        val precio: TextView = view.findViewById(R.id.txtPrecio)

        val btnDelete: Button = view.findViewById(R.id.btnEliminar)
        val btnEdit: Button = view.findViewById(R.id.btnEditar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_venta, parent, false)
        return VentaViewHolder(view)
    }

    override fun onBindViewHolder(holder: VentaViewHolder, position: Int) {

        val venta = getItem(position)

        holder.prenda.text = venta.nombrePrenda.toTitleCase()
        holder.cliente.text = "Cliente: ${venta.cliente.toTitleCase()}"

        // 💥 FORMATO PRO (SIN SPANNABLE)
        holder.precio.text = venta.precio.toPriceCLP()

        holder.btnDelete.setOnClickListener {
            onDelete(venta)
        }

        holder.btnEdit.setOnClickListener {
            onEdit(venta)
        }
    }

    companion object {

        private val DiffCallback = object : DiffUtil.ItemCallback<Venta>() {

            override fun areItemsTheSame(oldItem: Venta, newItem: Venta): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Venta, newItem: Venta): Boolean {
                return oldItem == newItem
            }
        }
    }
}