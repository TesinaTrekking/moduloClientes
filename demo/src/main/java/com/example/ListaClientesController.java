package com.example;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ListaClientesController implements Initializable {

    @FXML
    private TableView<Cliente> tablaClientes;

    @FXML
    private TextField buscarField;

    @FXML
    private CheckBox mostrarInactivosCheckBox;

    @FXML
    private TableColumn<Cliente, Integer> colId;

    @FXML
    private TableColumn<Cliente, String> colNombre;

    @FXML
    private TableColumn<Cliente, String> colApellido;

    @FXML
    private TableColumn<Cliente, String> colEmail;

    @FXML
    private TableColumn<Cliente, String> colSexo;

    @FXML
    private TableColumn<Cliente, Boolean> colActivo;

    @FXML
    private TableColumn<Cliente, String> colDni;

    @FXML
    private TableColumn<Cliente, String> colTelefono;

    @FXML
    private TableColumn<Cliente, java.time.LocalDate> colFechaNacimiento;

    private FilteredList<Cliente> clientesFiltrados;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        buscarField.textProperty().addListener((observable, anterior, actual) -> aplicarFiltros());
        mostrarInactivosCheckBox.selectedProperty().addListener((observable, anterior, actual) -> aplicarFiltros());
        cargarClientes();
    }

    private void cargarClientes() {
        ObservableList<Cliente> clientes = ConexionDB.obtenerTodosLosClientes();
        clientesFiltrados = new FilteredList<>(clientes);
        tablaClientes.setItems(clientesFiltrados);
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        if (clientesFiltrados == null) return;
        String texto = buscarField.getText().trim().toLowerCase();
        boolean mostrarInactivos = mostrarInactivosCheckBox.isSelected();
        clientesFiltrados.setPredicate(cliente -> {
            boolean coincideTexto = texto.isEmpty()
                    || contiene(cliente.getDni(), texto)
                    || contiene(cliente.getNombre(), texto)
                    || contiene(cliente.getApellido(), texto);
            return coincideTexto && (mostrarInactivos || cliente.isActivo());
        });
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    @FXML
    private void irAgregarCliente() throws IOException {
        App.setRoot("agregar-cliente");
    }

    @FXML
    private void editarCliente() {
        Cliente clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (clienteSeleccionado == null) {
            mostrarAlerta("Selecciona un cliente", "Por favor selecciona un cliente para editar.");
            return;
        }

        App.prepararEdicionCliente(clienteSeleccionado);
        try {
            App.setRoot("editar-cliente");
        } catch (IOException e) {
            App.limpiarClienteEnEdicion();
            mostrarAlerta("Error", "No se pudo abrir el formulario de edición.");
        }
    }

    @FXML
    private void eliminarCliente() {
        Cliente clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();

        if (clienteSeleccionado == null) {
            mostrarAlerta("Selecciona un cliente", "Por favor selecciona un cliente para dar de baja.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar baja");
        confirmacion.setHeaderText("¿Estás seguro?");
        confirmacion.setContentText("¿Eliminar a " + clienteSeleccionado.getNombre() + " "
                + clienteSeleccionado.getApellido() + " del listado activo?");

        if (confirmacion.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
            ConexionDB.eliminarCliente(clienteSeleccionado.getId());
            cargarClientes();
            mostrarAlerta("Éxito", "Cliente dado de baja correctamente.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}