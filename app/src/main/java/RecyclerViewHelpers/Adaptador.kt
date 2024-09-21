package RecyclerViewHelpers

import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import gerardo.quintanillas.crud_recuperacion.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbEscritores

class Adaptador(var Datos: MutableList<tbEscritores>): RecyclerView.Adapter<ViewHolder>() {

    fun actualizarPantalla(uuid: String, nuevoNombre:String, nuevaEdad:Int, nuevoPeso:Double, nuevoCorreo:String){

        val index = Datos.indexOfFirst { it.uuid == uuid }
        Datos[index].nombreEscritor = nuevoNombre
        Datos[index].edad = nuevaEdad
        Datos[index].peso = nuevoPeso
        Datos[index].correo = nuevoCorreo
        notifyDataSetChanged()
    }

    fun actualizarEscritores(nuevaLista: List<tbEscritores>) {
        this.Datos.clear()      // Limpia la lista actual
        this.Datos.addAll(nuevaLista)  // Añade la nueva lista
        notifyDataSetChanged()  // Notifica al adaptador que los datos han cambiado
    }

    fun eliminarRegistro(nombreEscritor: String, posicion: Int){
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            val deleteEscritor = objConexion?.prepareStatement("delete tbEscritor where Nombre_Escritor = ?")!!
            deleteEscritor.setString(1, nombreEscritor)
            deleteEscritor.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
        }

        Datos = listaDatos.toMutableList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    fun editarEscritor(nombreEscritor: String, edadEscritor: Int, pesoEscritor: Double, correoEscritor:String, uuid: String){
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            val updateEscritor = objConexion?.prepareStatement("update tbEscritor set Nombre_Escritor = ?, " +
                    "Edad_Escritor = ?, Peso_Escritor = ?, Correo_Escritor = ? where UUID_Escritor = ?")!!
            updateEscritor.setString(1, nombreEscritor)
            updateEscritor.setInt(2, edadEscritor)
            updateEscritor.setDouble(3, pesoEscritor)
            updateEscritor.setString(4, correoEscritor)
            updateEscritor.setString(5, uuid)
            updateEscritor.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
            withContext(Dispatchers.Main){
                actualizarPantalla(uuid, nombreEscritor,edadEscritor,pesoEscritor,correoEscritor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent,false)
        return ViewHolder(vista)
    }

    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = Datos[position]
        holder.textView.text = item.nombreEscritor
        holder.textEdad.text = item.edad.toString()
        holder.textPeso.text = item.peso.toString()
        holder.textCorreo.text = item.correo

        holder.imgEliminar.setOnClickListener{
            val contexto = holder.textView.context

            val builder = AlertDialog.Builder(contexto)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Estas seguro que deseas eliminar?")

            builder.setPositiveButton("Si"){
                dialog,wich ->
                eliminarRegistro(item.nombreEscritor, position)
            }
            builder.setNegativeButton("No"){
                    dialog,wich ->
                dialog.dismiss()
            }
            builder.show()
        }

        holder.imgEditar.setOnClickListener{

            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar")
            builder.setMessage("¿Deseas Actualizar Escritor?")

            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(50, 20, 50, 20)

            // Crear los EditText para cada campo
            val cuadroTextoNombre = EditText(context).apply {
                hint = "Nombre del escritor"
                setText(item.nombreEscritor) // Mostrar el nombre actual
            }
            layout.addView(cuadroTextoNombre)

            val cuadroTextoEdad = EditText(context).apply {
                hint = "Edad del escritor"
                inputType = InputType.TYPE_CLASS_NUMBER // Solo números para la edad
                setText(item.edad.toString()) // Mostrar la edad actual
            }
            layout.addView(cuadroTextoEdad)

            val cuadroTextoPeso = EditText(context).apply {
                hint = "Peso del escritor (kg)"
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL // Números decimales para el peso
                setText(item.peso.toString()) // Mostrar el peso actual
            }
            layout.addView(cuadroTextoPeso)

            val cuadroTextoCorreo = EditText(context).apply {
                hint = "Correo electrónico del escritor"
                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS // Para correos electrónicos
                setText(item.correo) // Mostrar el correo actual
            }
            layout.addView(cuadroTextoCorreo)

            // Agregar el LinearLayout al diálogo
            builder.setView(layout)

            // Configurar los botones del diálogo
            builder.setPositiveButton("Actualizar") { dialog, which ->
                // Obtener los valores de los campos
                val nuevoNombre = cuadroTextoNombre.text.toString()
                val nuevaEdad = cuadroTextoEdad.text.toString().toInt()
                val nuevoPeso = cuadroTextoPeso.text.toString().toDouble()
                val nuevoCorreo = cuadroTextoCorreo.text.toString()

                // Llamada al método para actualizar con los nuevos datos
                editarEscritor(nuevoNombre, nuevaEdad, nuevoPeso, nuevoCorreo, item.uuid)
            }
            builder.setNegativeButton("Cancelar"){
                    dialog,wich ->
                dialog.dismiss()
            }
            builder.show()

        }

    }
}