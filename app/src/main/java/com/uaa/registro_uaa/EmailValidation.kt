package com.uaa.registro_uaa

/**
 * Clase utilitaria para validar correos electrónicos según el rol del usuario.
 */
object EmailValidator {

    /**
     * Valida el formato del correo según el rol.
     * @param email El correo electrónico a validar
     * @param rol El rol del usuario ("alumno" o "docente")
     * @return true si el correo es válido para el rol especificado, false en caso contrario
     */
    fun validarCorreoPorRol(email: String, rol: String): Boolean {
        // Expresiones regulares
        val regexAlumno = Regex("^al\\d{6}@edu\\.uaa\\.mx$")
        val regexDocente = Regex("^[a-zA-Z]+(\\.[a-zA-Z]+)?@edu\\.uaa\\.mx$", RegexOption.IGNORE_CASE)

        return when (rol) {
            "alumno" -> regexAlumno.matches(email)
            "docente" -> regexDocente.matches(email)
            else -> false
        }
    }

    /**
     * Extrae el ID numérico del correo de un alumno.
     * @param email El correo del alumno
     * @return El ID numérico o null si no es válido
     */
    fun extraerIdAlumno(email: String): String? {
        if (email.startsWith("al")) {
            // Remover "al" o "AL" independientemente de mayúsculas/minúsculas
            val sinPrefijo = email.substring(2)  // Elimina los primeros 2 caracteres
            return sinPrefijo.substringBefore("@")
        }
        return null
    }
}