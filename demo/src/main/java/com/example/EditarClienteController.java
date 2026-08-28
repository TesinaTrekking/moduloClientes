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

        dniField.setText(textoSeguro(cliente.getDni()));
        nombreField.setText(textoSeguro(cliente.getNombre()));
        apellidoField.setText(textoSeguro(cliente.getApellido()));
        fechaNacimientoPicker.setValue(cliente.getFechaNacimiento());
        emailField.setText(textoSeguro(cliente.getEmail()));
        telefonoField.setText(textoSeguro(cliente.getTelefono()));
        sexoComboBox.setValue(cliente.getSexo());
        contactoNombreField.setText(textoSeguro(cliente.getContactoEmergenciaNombre()));
        contactoTelefonoField.setText(textoSeguro(cliente.getContactoEmergenciaTelefono()));
        contactoRelacionField.setText(textoSeguro(cliente.getContactoEmergenciaRelacion()));
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

        String dni = textoSeguro(dniField.getText()).trim();
        String nombre = textoSeguro(nombreField.getText()).trim();
        String apellido = textoSeguro(apellidoField.getText()).trim();
        LocalDate fechaNacimiento = fechaNacimientoPicker.getValue();
        String email = textoSeguro(emailField.getText()).trim();
        String telefono = textoSeguro(telefonoField.getText()).trim();
        String sexo = sexoComboBox.getValue();
        String contactoNombre = textoSeguro(contactoNombreField.getText()).trim();
        String contactoTelefono = textoSeguro(contactoTelefonoField.getText()).trim();
        String contactoRelacion = textoSeguro(contactoRelacionField.getText()).trim();
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

    private String textoSeguro(String valor) {
        return valor == null ? "" : valor;
    }
}