package com.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class AgregarClienteController {

    private static final long TAMANO_MAXIMO_FICHA = 2L * 1024 * 1024;

    @FXML
    private TextField dniField;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField apellidoField;

    @FXML
    private DatePicker fechaNacimientoPicker;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telefonoField;

    @FXML
    private ComboBox<String> sexoComboBox;

    @FXML
    private TextField contactoNombreField;

    @FXML
    private TextField contactoTelefonoField;

    @FXML
    private TextField contactoRelacionField;

    @FXML
    private Label fichaMedicaLabel;

    private File fichaMedicaSeleccionada;

    @FXML
    private void initialize() {
        sexoComboBox.getItems().addAll("Masculino", "Femenino", "No binario", "Otro", "Prefiero no decir");
        sexoComboBox.setValue("Prefiero no decir");
    }

    @FXML
    private void seleccionarFichaMedica() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar ficha médica");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf", "*.PDF"));
        Window ventana = fichaMedicaLabel.getScene().getWindow();
        File archivo = selector.showOpenDialog(ventana);
        if (archivo == null) {
            return;
        }
        if (archivo.length() > TAMANO_MAXIMO_FICHA) {
            fichaMedicaSeleccionada = null;
            fichaMedicaLabel.setText("Ningún archivo seleccionado");
                mostrarAlerta(Alert.AlertType.ERROR, "Archivo demasiado grande",
                    "Elegí una ficha médica de hasta 2 MB.");
            return;
        }
        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato no válido", "La ficha médica debe ser un PDF.");
            return;
        }
        fichaMedicaSeleccionada = archivo;
        fichaMedicaLabel.setText(archivo.getName());
    }

    @FXML
    private void irListaClientes() throws IOException {
        App.setRoot("lista-clientes");
    }

    @FXML
    private void guardarCliente() {
        byte[] fichaMedica = null;
        String nombreFichaMedica = null;
        if (fichaMedicaSeleccionada != null) {
            try {
                fichaMedica = Files.readAllBytes(fichaMedicaSeleccionada.toPath());
                if (fichaMedica.length > TAMANO_MAXIMO_FICHA) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Archivo demasiado grande",
                            "Elegí una ficha médica de hasta 2 MB.");
                    return;
                }
                if (fichaMedica.length < 5
                        || fichaMedica[0] != '%'
                        || fichaMedica[1] != 'P'
                        || fichaMedica[2] != 'D'
                        || fichaMedica[3] != 'F'
                        || fichaMedica[4] != '-') {
                    mostrarAlerta(Alert.AlertType.ERROR, "Formato no válido", "La ficha médica debe ser un PDF.");
                    return;
                }
                nombreFichaMedica = fichaMedicaSeleccionada.getName();
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo leer el archivo",
                    "Elegí otra ficha médica e intentá nuevamente.");
                return;
            }
        }
        String dni = dniField.getText().trim();
        String nombre = nombreField.getText().trim();
        String apellido = apellidoField.getText().trim();
        LocalDate fechaNacimiento = fechaNacimientoPicker.getValue();
        String email = emailField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String sexo = sexoComboBox.getValue();
        String contactoNombre = contactoNombreField.getText().trim();
        String contactoTelefono = contactoTelefonoField.getText().trim();
        String contactoRelacion = contactoRelacionField.getText().trim();
        String error = ClienteValidator.validar(dni, nombre, apellido, fechaNacimiento, email, telefono, sexo,
                contactoNombre, contactoTelefono, contactoRelacion);
        if (error != null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Revisá los datos", error);
            return;
        }

        if (!ConexionDB.insertarCliente(dni, nombre, apellido, fechaNacimiento, email, telefono, sexo,
            contactoNombre, contactoTelefono, contactoRelacion, true, fichaMedica, nombreFichaMedica)) {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar",
                    "Verificá los datos y que el DNI no esté registrado.");
            return;
        }
        if (fichaMedica != null) {
            try {
                FichaMedica.guardarEnCarpeta(dni, fichaMedica);
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Ficha no guardada",
                    "El cliente se guardó, pero la ficha no pudo copiarse a la carpeta del programa.");
                return;
            }
        }

        nombreField.clear();
        apellidoField.clear();
        dniField.clear();
        fechaNacimientoPicker.setValue(null);
        emailField.clear();
        telefonoField.clear();
        contactoNombreField.clear();
        contactoTelefonoField.clear();
        contactoRelacionField.clear();
        fichaMedicaSeleccionada = null;
        fichaMedicaLabel.setText("Ningún archivo seleccionado");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Cliente guardado", "El cliente se guardó.");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}