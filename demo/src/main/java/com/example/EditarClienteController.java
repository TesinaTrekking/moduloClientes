package com.example;

import java.io.IOException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class EditarClienteController {

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
        Cliente cliente = App.obtenerClienteEnEdicion();
        if (cliente == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No hay un cliente seleccionado para editar.");
            return;
        }

        dniField.setText(cliente.getDni());
        nombreField.setText(cliente.getNombre());
        apellidoField.setText(cliente.getApellido());
        fechaNacimientoPicker.setValue(cliente.getFechaNacimiento());
        emailField.setText(cliente.getEmail());
        telefonoField.setText(cliente.getTelefono());
        sexoComboBox.setValue(cliente.getSexo());
        contactoNombreField.setText(cliente.getContactoEmergenciaNombre());
        contactoTelefonoField.setText(cliente.getContactoEmergenciaTelefono());
        contactoRelacionField.setText(cliente.getContactoEmergenciaRelacion());
        activoCheckBox.setSelected(cliente.isActivo());
    }

    @FXML
    private void cancelarEdicion() throws IOException {
        App.limpiarClienteEnEdicion();
        App.setRoot("lista-clientes");
    }

    @FXML
    private void guardarCambios() throws IOException {
        Cliente cliente = App.obtenerClienteEnEdicion();
        if (cliente == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No hay un cliente seleccionado para editar.");
            return;
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
            mostrarAlerta(Alert.AlertType.ERROR, "Datos inválidos", error);
            return;
        }

        boolean actualizado = ConexionDB.actualizarCliente(cliente.getId(), dni, nombre, apellido, fechaNacimiento,
                email, telefono, sexo, contactoNombre, contactoTelefono, contactoRelacion, activoCheckBox.isSelected());
        if (!actualizado) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el cliente.");
            return;
        }

        App.limpiarClienteEnEdicion();
        App.setRoot("lista-clientes");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Cliente actualizado correctamente.");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}