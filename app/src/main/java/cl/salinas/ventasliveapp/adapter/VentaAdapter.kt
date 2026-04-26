package cl.salinas.ventasliveapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cl.salinas.ventasliveapp.R
import cl.salinas.ventasliveapp.model.Venta

class VentaAdapter(
    private val lista: List<Venta>,
    private val onDelete: (Venta) -> Unit,
    private val onEdit: (Venta) -> Unit
) : RecyclerView.Adapter<VentaAdapter.VentaViewHolder>() {

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

        val venta = lista.getOrNull(position) ?: return

        holder.prenda.text = venta.nombrePrenda
        holder.cliente.text = "Cliente: ${venta.cliente}"
        holder.precio.text = "$${venta.precio}"

        // 🗑 eliminar
        holder.btnDelete.setOnClickListener {
            onDelete(venta)
        }

        // ✏️ editar
        holder.btnEdit.setOnClickListener {
            onEdit(venta)
        }
    }

    override fun getItemCount(): Int = lista.size
}