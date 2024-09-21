package gerardo.quintanillas.crud_recuperacion

import RecyclerViewHelpers.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.tbEscritores
import java.sql.Statement
import java.util.UUID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtEdad = findViewById<EditText>(R.id.txtEdad)
        val txtPeso = findViewById<EditText>(R.id.txtPeso)
        val txtCorreo = findViewById<EditText>(R.id.txtCorreo)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val rcvEscritores = findViewById<RecyclerView>(R.id.rcvEscritores)

        rcvEscritores.layoutManager = LinearLayoutManager(this)

        fun obtenerEscritores(): MutableList<tbEscritores>{

            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("select * from tbescritor")!!

            val listaEscritores = mutableListOf<tbEscritores>()

            while (resultSet.next())
            {
                val uuid = resultSet.getString("UUID_Escritor")
                val nombreEscritor = resultSet.getString("Nombre_Escritor")
                val edadEscritor = resultSet.getInt("Edad_Escritor")
                val pesoEscritor = resultSet.getDouble("Peso_Escritor")
                val correoEscritor = resultSet.getString("Correo_Escritor")

                val valoresJuntos = tbEscritores(uuid, nombreEscritor, edadEscritor, pesoEscritor, correoEscritor)

                listaEscritores.add(valoresJuntos)
            }
            return listaEscritores

        }

        CoroutineScope(Dispatchers.IO).launch {
            val escritoresDB = obtenerEscritores()
            withContext(Dispatchers.Main){
                val adapter = Adaptador(escritoresDB)
                rcvEscritores.adapter = adapter
            }
        }

        btnGuardar.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                val objConexion = ClaseConexion().cadenaConexion()

                val addEscritor = objConexion?.prepareStatement("insert into tbEscritor(uuid_escritor,nombre_escritor,edad_escritor,peso_escritor,correo_escritor) values (?,?,?,?,?)")!!
                addEscritor.setString(1, UUID.randomUUID().toString())
                addEscritor.setString(2, txtNombre.text.toString())
                addEscritor.setInt(3, txtEdad.text.toString().toInt())
                addEscritor.setDouble(4, txtPeso.text.toString().toDouble())
                addEscritor.setString(5, txtCorreo.text.toString())
                addEscritor.executeUpdate()


                val commit = objConexion.prepareStatement("commit")
                commit.executeUpdate()

                val escritoresActualizados = obtenerEscritores()

                // Cambiar el contexto a la MainThread para actualizar la UI
                withContext(Dispatchers.Main) {
                    val adapter = rcvEscritores.adapter as Adaptador
                    adapter.actualizarEscritores(escritoresActualizados)
                }
            }
        }

    }
}