package com.example;

import java.io.IOException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class AgregarClienteController {

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
    private CheckBox activoCheckBox;

    @FXML
    private TextField contactoNombreField;

    @FXML
    private TextField contactoTelefonoField;

    @FXML
    private TextField contactoRelacionField;

    @FXML
    private void initialize() {
        sexoComboBox.getItems().addAll("Masculino", "Femenino", "No binario", "Otro", "Prefiero no decir");
        sexoComboBox.setValue("Prefiero no decir");
        activoCheckBox.setSelected(true);
    }

    @FXML
    private void irListaClientes() throws IOException {
        App.setRoot("lista-clientes");
    }

    @FXML
    private void guardarCliente() {
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
            mostrarAlerta(Alert.AlertType.ERROR, "Datos inválidos", error);
            return;
        }

        if (!ConexionDB.insertarCliente(dni, nombre, apellido, fechaNacimiento, email, telefono, sexo,
                contactoNombre, contactoTelefono, contactoRelacion, activoCheckBox.isSelected())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el cliente.");
            return;
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
        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente guardado correctamente.");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}