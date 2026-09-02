package com.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FichaMedica {
    private static final File CARPETA = new File("fichas_medicas");

    private final byte[] contenido;
    private final String nombre;

    public FichaMedica(byte[] contenido, String nombre) {
        this.contenido = contenido;
        this.nombre = nombre;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public String getNombre() {
        return nombre;
    }

    public static void guardarEnCarpeta(String dni, byte[] contenido) throws IOException {
        if (!CARPETA.exists() && !CARPETA.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta fichas_medicas");
        }
        Files.write(new File(CARPETA, dni + ".pdf").toPath(), contenido);
    }

    public static void borrarDeCarpeta(String dni) throws IOException {
        Files.deleteIfExists(new File(CARPETA, dni + ".pdf").toPath());
    }
}