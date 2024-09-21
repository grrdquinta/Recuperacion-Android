package modelo

import java.util.UUID

data class tbEscritores(
    val uuid: String,
    var nombreEscritor: String,
    var edad: Int,
    var peso: Double,
    var correo: String
)
