package com.uaa.registro_uaa

import org.junit.Test
import org.junit.Assert.*

/**
 * Pruebas unitarias para EmailValidator
 * Actualizado: Solo acepta "al" en minúsculas + exactamente 6 dígitos
 */
class EmailValidatorTest {

    // ========== PRUEBAS PARA CORREOS DE ALUMNOS (VÁLIDOS) ==========

    @Test
    fun validarCorreo_alumnoFormatoValido6Digitos_returnsTrue() {
        val resultado = EmailValidator.validarCorreoPorRol("al123456@edu.uaa.mx", "alumno")
        assertTrue(resultado)
    }

    @Test
    fun validarCorreo_alumnoMinusculas6Digitos_returnsTrue() {
        val resultado = EmailValidator.validarCorreoPorRol("al999999@edu.uaa.mx", "alumno")
        assertTrue(resultado)
    }

    @Test
    fun validarCorreo_alumnoMinusculas6DigitosCeros_returnsTrue() {
        val resultado = EmailValidator.validarCorreoPorRol("al000000@edu.uaa.mx", "alumno")
        assertTrue(resultado)
    }

    // ========== PRUEBAS PARA CORREOS DE ALUMNOS (INVÁLIDOS) ==========

    @Test
    fun validarCorreo_alumnoMayusculas_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("AL123456@edu.uaa.mx", "alumno")
        assertFalse(resultado)  // Ya NO acepta mayúsculas
    }

    @Test
    fun validarCorreo_alumnoMayusculasTodoElCorreo_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("AL123456@EDU.UAA.MX", "alumno")
        assertFalse(resultado)  // Ya NO acepta mayúsculas
    }

    @Test
    fun validarCorreo_alumnoPrimeraLetraMayuscula_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("Al123456@edu.uaa.mx", "alumno")
        assertFalse(resultado)  // Ya NO acepta "Al"
    }

    @Test
    fun validarCorreo_alumno5Digitos_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("al12345@edu.uaa.mx", "alumno")
        assertFalse(resultado)  // Ahora requiere exactamente 6 dígitos
    }

    @Test
    fun validarCorreo_alumno7Digitos_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("al1234567@edu.uaa.mx", "alumno")
        assertFalse(resultado)  // Ahora requiere exactamente 6 dígitos
    }

    @Test
    fun validarCorreo_alumno10Digitos_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("al1234567890@edu.uaa.mx", "alumno")
        assertFalse(resultado)  // Ahora requiere exactamente 6 dígitos
    }

    @Test
    fun validarCorreo_alumnoMenosDe6Digitos_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("al1234@edu.uaa.mx", "alumno")
        assertFalse(resultado)
    }

    @Test
    fun validarCorreo_alumnoSinPrefijo_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("12345@edu.uaa.mx", "alumno")
        assertFalse(resultado)
    }

    @Test
    fun validarCorreo_alumnoMenosDe5Digitos_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("al1234@edu.uaa.mx", "alumno")
        assertFalse(resultado)
    }

    @Test
    fun validarCorreo_alumnoMasDe10Digitos_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("al12345678901@edu.uaa.mx", "alumno")
        assertFalse(resultado)
    }

    @Test
    fun validarCorreo_alumnoConLetras_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("al123ab@edu.uaa.mx", "alumno")
        assertFalse(resultado)
    }

    @Test
    fun validarCorreo_alumnoDominioIncorrecto_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("al12345@gmail.com", "alumno")
        assertFalse(resultado)
    }

    @Test
    fun validarCorreo_alumnoSinArroba_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("al12345edu.uaa.mx", "alumno")
        assertFalse(resultado)
    }

    @Test
    fun validarCorreo_alumnoVacio_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("", "alumno")
        assertFalse(resultado)
    }

    // ========== PRUEBAS PARA CORREOS DE DOCENTES ==========

    @Test
    fun validarCorreo_docenteFormatoValido_returnsTrue() {
        val resultado = EmailValidator.validarCorreoPorRol("juan.perez@edu.uaa.mx", "docente")
        assertTrue(resultado)
    }

    @Test
    fun validarCorreo_docenteSinApellido_returnsTrue() {
        val resultado = EmailValidator.validarCorreoPorRol("juan@edu.uaa.mx", "docente")
        assertTrue(resultado)
    }

    @Test
    fun validarCorreo_docenteMayusculas_returnsTrue() {
        val resultado = EmailValidator.validarCorreoPorRol("MARIA.LOPEZ@EDU.UAA.MX", "docente")
        assertTrue(resultado)
    }

    @Test
    fun validarCorreo_docenteConNumeros_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("juan123@edu.uaa.mx", "docente")
        assertFalse(resultado)
    }

    @Test
    fun validarCorreo_docenteEmpiezaConNumero_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("123juan@edu.uaa.mx", "docente")
        assertFalse(resultado)
    }

    @Test
    fun validarCorreo_docenteDominioIncorrecto_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("juan.perez@gmail.com", "docente")
        assertFalse(resultado)
    }

    @Test
    fun validarCorreo_docenteConPrefijo_al_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("al12345@edu.uaa.mx", "docente")
        assertFalse(resultado)
    }

    @Test
    fun validarCorreo_docenteVacio_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("", "docente")
        assertFalse(resultado)
    }

    @Test
    fun validarCorreo_docenteDosPuntos_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("juan..perez@edu.uaa.mx", "docente")
        assertFalse(resultado)
    }

    // ========== PRUEBAS PARA ROLES INVÁLIDOS ==========

    @Test
    fun validarCorreo_rolInvalido_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("correo@edu.uaa.mx", "administrador")
        assertFalse(resultado)
    }

    @Test
    fun validarCorreo_rolVacio_returnsFalse() {
        val resultado = EmailValidator.validarCorreoPorRol("correo@edu.uaa.mx", "")
        assertFalse(resultado)
    }

    // ========== PRUEBAS PARA EXTRACCIÓN DE ID ==========

    @Test
    fun extraerIdAlumno_formatoValido_returnsId() {
        val id = EmailValidator.extraerIdAlumno("al12345@edu.uaa.mx")
        assertEquals("12345", id)
    }


    @Test
    fun extraerIdAlumno_sinPrefijoAl_returnsNull() {
        val id = EmailValidator.extraerIdAlumno("juan@edu.uaa.mx")
        assertNull(id)
    }

    @Test
    fun extraerIdAlumno_correoVacio_returnsNull() {
        val id = EmailValidator.extraerIdAlumno("")
        assertNull(id)
    }
}