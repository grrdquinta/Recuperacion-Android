package modelo

import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection? {

        try {
            val url = "jdbc:oracle:thin:@172.20.10.4:1521:xe"
            val usuario = "Gerardo_Quintanilla"
            val contrasena = "1234"

            val connection = DriverManager.getConnection(url, usuario, contrasena)
            return connection
        }catch (e: Exception){
            println("error: $e")
            return null
        }
    }
}

