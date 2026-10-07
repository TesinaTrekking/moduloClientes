package com.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

@SuppressWarnings("unused")
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

    @FXML private CheckBox autorizacionMenoresCheckBox;
    @FXML private TextField tutorNombreField;
    @FXML private TextField tutorApellidoField;
    @FXML private TextField tutorDniField;
    @FXML private TextField tutorTelefonoField;
    @FXML private Label tutorNombreLabel;
    @FXML private Label tutorApellidoLabel;
    @FXML private Label tutorDniLabel;
    @FXML private Label tutorTelefonoLabel;

    @FXML
    private Label fichaMedicaLabel;

    private File fichaMedicaSeleccionada;

    @FXML
    public void initialize() {
        sexoComboBox.getItems().addAll("Masculino", "Femenino", "No binario", "Otro", "Prefiero no decir");
        sexoComboBox.setValue("Prefiero no decir");
        actualizarCamposTutor();
    }

    @FXML
    private void cambiarAutorizacionMenores() {
        actualizarCamposTutor();
    }

    private void actualizarCamposTutor() {
        boolean mostrar = autorizacionMenoresCheckBox.isSelected();
        Node[] campos = {tutorNombreLabel, tutorNombreField, tutorApellidoLabel, tutorApellidoField,
            tutorDniLabel, tutorDniField, tutorTelefonoLabel, tutorTelefonoField};
        for (Node campo : campos) {
            campo.setVisible(mostrar);
            campo.setManaged(mostrar);
        }
    }

    @FXML
    public void seleccionarFichaMedica() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar ficha medica");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf", "*.PDF"));
        Window ventana = fichaMedicaLabel.getScene().getWindow();
        File archivo = selector.showOpenDialog(ventana);
        if (archivo == null) {
            return;
        }
        if (archivo.length() > TAMANO_MAXIMO_FICHA) {
            fichaMedicaSeleccionada = null;
            fichaMedicaLabel.setText("Ningun archivo seleccionado");
                mostrarAlerta(Alert.AlertType.ERROR, "Archivo demasiado grande",
                    "Elegi una ficha medica de hasta 2 MB.");
            return;
        }
        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Formato no valido", "La ficha medica debe ser un PDF.");
            return;
        }
        fichaMedicaSeleccionada = archivo;
        fichaMedicaLabel.setText(archivo.getName());
    }

    @FXML
    public void irListaClientes() throws IOException {
        App.setRoot("lista-clientes");
    }

    @FXML
    public void guardarCliente() {
        byte[] fichaMedica = null;
        String nombreFichaMedica = null;
        if (fichaMedicaSeleccionada != null) {
            try {
                fichaMedica = Files.readAllBytes(fichaMedicaSeleccionada.toPath());
                if (fichaMedica.length > TAMANO_MAXIMO_FICHA) {
                        mostrarAlerta(Alert.AlertType.ERROR, "Archivo demasiado grande",
                            "Elegi una ficha medica de hasta 2 MB.");
                    return;
                }
                if (fichaMedica.length < 5
                        || fichaMedica[0] != '%'
                        || fichaMedica[1] != 'P'
                        || fichaMedica[2] != 'D'
                        || fichaMedica[3] != 'F'
                        || fichaMedica[4] != '-') {
                    mostrarAlerta(Alert.AlertType.ERROR, "Formato no valido", "La ficha medica debe ser un PDF.");
                    return;
                }
                nombreFichaMedica = fichaMedicaSeleccionada.getName();
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo leer el archivo",
                    "Elegi otra ficha medica e intenta nuevamente.");
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
        boolean autorizacionMenores = autorizacionMenoresCheckBox.isSelected();
        String tutorNombre = tutorNombreField.getText().trim();
        String tutorApellido = tutorApellidoField.getText().trim();
        String tutorDni = tutorDniField.getText().trim();
        String tutorTelefono = tutorTelefonoField.getText().trim();
        String error = ClienteValidator.validar(dni, nombre, apellido, fechaNacimiento, email, telefono, sexo,
            contactoNombre, contactoTelefono, contactoRelacion, autorizacionMenores, tutorNombre,
            tutorApellido, tutorDni, tutorTelefono);
        if (error != null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Revisa los datos", error);
            return;
        }

        if (!ConexionDB.insertarCliente(dni, nombre, apellido, fechaNacimiento, email, telefono, sexo,
            contactoNombre, contactoTelefono, contactoRelacion, true, autorizacionMenores, tutorNombre,
            tutorApellido, tutorDni, tutorTelefono, fichaMedica, nombreFichaMedica)) {
                mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar",
                    "Verifica los datos y que el DNI no este registrado.");
            return;
        }
        if (fichaMedica != null) {
            try {
                FichaMedica.guardarEnCarpeta(dni, fichaMedica);
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Ficha no guardada",
                    "El cliente se guardo, pero la ficha no pudo copiarse a la carpeta del programa.");
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
        fichaMedicaLabel.setText("Ningun archivo seleccionado");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Cliente guardado", "El cliente se guardo.");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}