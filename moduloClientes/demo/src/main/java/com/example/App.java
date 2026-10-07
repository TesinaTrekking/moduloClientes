package com.example;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        scene = new Scene(loadFXML("lista-clientes"), 1300, 850);
        stage.setMinWidth(1200);
        stage.setMinHeight(800);
        stage.setTitle("Gestion de Clientes");
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
        URL resource = App.class.getResource(fxml + ".fxml");
        if (resource == null) {
            Path[] candidates = {
                Paths.get("src", "main", "resources", "com", "example", fxml + ".fxml"),
                Paths.get("demo", "src", "main", "resources", "com", "example", fxml + ".fxml")
            };
            for (Path candidate : candidates) {
                if (candidate.toFile().isFile()) {
                    resource = candidate.toAbsolutePath().toUri().toURL();
                    break;
                }
            }
        }
        if (resource == null) {
            throw new IOException("No se encontro el recurso FXML: " + fxml + ".fxml.");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        ConexionDB.crearTabla();
        launch();
    }

}
