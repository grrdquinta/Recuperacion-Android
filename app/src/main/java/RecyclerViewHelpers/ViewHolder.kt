package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gerardo.quintanillas.crud_recuperacion.R

class ViewHolder(view: View):RecyclerView.ViewHolder(view) {

    val textView = view.findViewById<TextView>(R.id.txtNombreCard)
    val textEdad = view.findViewById<TextView>(R.id.txtEdadCard)
    val textPeso = view.findViewById<TextView>(R.id.txtPesoCard)
    val textCorreo = view.findViewById<TextView>(R.id.txtCorreoCard)
    val imgEditar = view.findViewById<ImageView>(R.id.imgEditar)
    val imgEliminar = view.findViewById<ImageView>(R.id.imgEliminar)
}