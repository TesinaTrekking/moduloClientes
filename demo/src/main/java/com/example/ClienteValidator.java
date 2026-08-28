package com.example;

import java.time.LocalDate;
import java.util.regex.Pattern;

public final class ClienteValidator {

    private static final Pattern DNI_VALIDO = Pattern.compile("\\d{7,8}");
    private static final Pattern NOMBRE_VALIDO = Pattern.compile("[\\p{L}]+(?:[ '-][\\p{L}]+)*");
    private static final Pattern EMAIL_VALIDO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern TELEFONO_VALIDO = Pattern.compile("[0-9+() -]{7,20}");

    private ClienteValidator() {
    }

    public static String validar(String dni, String nombre, String apellido, LocalDate fechaNacimiento,
            String email, String telefono, String sexo, String contactoNombre, String contactoTelefono,
            String contactoRelacion) {
        if (dni.isEmpty() || !DNI_VALIDO.matcher(dni).matches()) {
            return "El DNI es obligatorio y debe contener entre 7 y 8 números.";
        }
        if (nombre.isEmpty() || apellido.isEmpty()) {
            return "El nombre y apellido son obligatorios.";
        }
        if (!NOMBRE_VALIDO.matcher(nombre).matches() || !NOMBRE_VALIDO.matcher(apellido).matches()) {
            return "El nombre y apellido solo pueden contener letras, espacios, guiones o apóstrofes.";
        }
        if (nombre.length() > 50 || apellido.length() > 50) {
            return "El nombre y apellido no pueden superar los 50 caracteres.";
        }
        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now())) {
            return "La fecha de nacimiento es obligatoria y no puede ser futura.";
        }
        if (email.length() > 100 || (!email.isEmpty() && !EMAIL_VALIDO.matcher(email).matches())) {
            return "Introduce un email válido de hasta 100 caracteres.";
        }
        if (telefono.isEmpty() || !TELEFONO_VALIDO.matcher(telefono).matches()) {
            return "El teléfono es obligatorio y debe tener un formato válido.";
        }
        if (sexo == null || sexo.isEmpty()) {
            return "Selecciona el sexo del cliente.";
        }
        boolean contactoIncompleto = contactoNombre.isEmpty() || contactoTelefono.isEmpty()
                || contactoRelacion.isEmpty();
        boolean contactoIniciado = !contactoNombre.isEmpty() || !contactoTelefono.isEmpty()
                || !contactoRelacion.isEmpty();
        if (contactoIniciado && contactoIncompleto) {
            return "Completa nombre, teléfono y relación del contacto de emergencia.";
        }
        if (!contactoTelefono.isEmpty() && !TELEFONO_VALIDO.matcher(contactoTelefono).matches()) {
            return "El teléfono del contacto de emergencia no es válido.";
        }
        return null;
    }
}