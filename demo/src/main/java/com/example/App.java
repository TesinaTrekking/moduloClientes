package com.example;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;
    private static Cliente clienteEnEdicion;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("lista-clientes"), 1200, 700);
        stage.setMinWidth(1100);
        stage.setMinHeight(650);
        stage.setTitle("Gestión de Clientes");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    static void prepararEdicionCliente(Cliente cliente) {
        clienteEnEdicion = cliente;
    }

    static Cliente obtenerClienteEnEdicion() {
        return clienteEnEdicion;
    }

    static void limpiarClienteEnEdicion() {
        clienteEnEdicion = null;
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        ConexionDB.crearTabla();
        launch();
    }

}
