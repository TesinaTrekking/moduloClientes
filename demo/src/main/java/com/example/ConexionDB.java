package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ConexionDB {

    private static final String URL = "jdbc:sqlite:crud.db";

    static Connection conectar() {
        try {
            Connection conexion = DriverManager.getConnection(URL);
            return conexion;
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
            return null;
        }
    }

    public static void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS clientes (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "dni TEXT UNIQUE," +
                     "nombre TEXT NOT NULL," +
                     "apellido TEXT NOT NULL," +
                     "fecha_nacimiento TEXT," +
                     "email TEXT," +
                     "telefono TEXT," +
                     "sexo_id INTEGER," +
                     "sexo TEXT NOT NULL," +
                     "contacto_emergencia_nombre TEXT," +
                     "contacto_emergencia_telefono TEXT," +
                     "contacto_emergencia_relacion TEXT," +
                     "activo INTEGER NOT NULL DEFAULT 1," +
                     "fecha_alta TEXT NOT NULL DEFAULT CURRENT_DATE," +
                     "fecha_modificacion TEXT NOT NULL DEFAULT CURRENT_DATE)";

        try (Connection conexion = conectar();
             Statement statement = conexion.createStatement()) {

            statement.execute(sql);
            agregarColumnasSiFaltan(conexion);
                statement.execute("CREATE TABLE IF NOT EXISTS sexos ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL UNIQUE)");
                statement.execute("INSERT OR IGNORE INTO sexos (nombre) VALUES "
                    + "('Masculino'), ('Femenino'), ('No binario'), ('Otro'), ('Prefiero no decir')");
                agregarColumnaSexoIdSiFalta(conexion);
                statement.execute("UPDATE clientes SET sexo_id = (SELECT id FROM sexos WHERE sexos.nombre = clientes.sexo) "
                    + "WHERE sexo_id IS NULL AND sexo IS NOT NULL");
                statement.execute("CREATE TABLE IF NOT EXISTS contactos_emergencia ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, cliente_id INTEGER NOT NULL UNIQUE,"
                    + "nombre TEXT NOT NULL, telefono TEXT NOT NULL, relacion TEXT NOT NULL,"
                    + "FOREIGN KEY (cliente_id) REFERENCES clientes(id))");
                statement.execute("INSERT OR IGNORE INTO contactos_emergencia (cliente_id, nombre, telefono, relacion) "
                    + "SELECT id, contacto_emergencia_nombre, contacto_emergencia_telefono, "
                    + "contacto_emergencia_relacion FROM clientes WHERE contacto_emergencia_nombre IS NOT NULL "
                    + "AND contacto_emergencia_nombre <> '' AND contacto_emergencia_telefono IS NOT NULL "
                    + "AND contacto_emergencia_telefono <> '' AND contacto_emergencia_relacion IS NOT NULL "
                    + "AND contacto_emergencia_relacion <> ''");
                statement.execute("CREATE TABLE IF NOT EXISTS fichas_medicas ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "cliente_id INTEGER NOT NULL UNIQUE,"
                    + "presion TEXT, alergias TEXT, medicacion TEXT,"
                    + "fecha_vencimiento_apto TEXT, observaciones TEXT,"
                    + "FOREIGN KEY (cliente_id) REFERENCES clientes(id))");
            System.out.println("Tabla 'clientes' lista.");

        } catch (SQLException e) {
            System.out.println("Error al crear la tabla: " + e.getMessage());
        }
    }

    private static void agregarColumnasSiFaltan(Connection conexion) throws SQLException {
        String[] columnas = {"dni TEXT", "fecha_nacimiento TEXT", "telefono TEXT",
                "contacto_emergencia_nombre TEXT", "contacto_emergencia_telefono TEXT",
                "contacto_emergencia_relacion TEXT", "fecha_alta TEXT", "fecha_modificacion TEXT"};
        for (String columna : columnas) {
            try (Statement statement = conexion.createStatement()) {
                statement.execute("ALTER TABLE clientes ADD COLUMN " + columna);
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    throw e;
                }
            }
        }
    }

    private static void agregarColumnaSexoIdSiFalta(Connection conexion) throws SQLException {
        try (Statement statement = conexion.createStatement()) {
            statement.execute("ALTER TABLE clientes ADD COLUMN sexo_id INTEGER");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column name")) {
                throw e;
            }
        }
    }

    public static boolean insertarCliente(String dni, String nombre, String apellido, LocalDate fechaNacimiento,
            String email, String telefono, String sexo, String contactoNombre, String contactoTelefono,
            String contactoRelacion, boolean activo) {
        String sql = "INSERT INTO clientes (dni, nombre, apellido, fecha_nacimiento, email, telefono, sexo, "
                + "contacto_emergencia_nombre, contacto_emergencia_telefono, contacto_emergencia_relacion, activo, "
                + "fecha_alta, fecha_modificacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_DATE, CURRENT_DATE)";

        try (Connection conexion = conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, dni);
            statement.setString(2, nombre);
            statement.setString(3, apellido);
            statement.setString(4, fechaNacimiento.toString());
            statement.setString(5, email);
            statement.setString(6, telefono);
            statement.setString(7, sexo);
            statement.setString(8, contactoNombre);
            statement.setString(9, contactoTelefono);
            statement.setString(10, contactoRelacion);
            statement.setBoolean(11, activo);

            statement.executeUpdate();
            int clienteId = obtenerUltimoId(conexion);
            sincronizarSexoYContacto(conexion, clienteId, sexo, contactoNombre, contactoTelefono, contactoRelacion);
            System.out.println("Cliente guardado con éxito.");
            return true;

        } catch (SQLException e) {
            System.out.println("Error al guardar: " + e.getMessage());
            return false;
        }
    }

    public static boolean actualizarCliente(int id, String dni, String nombre, String apellido, LocalDate fechaNacimiento,
            String email, String telefono, String sexo, String contactoNombre, String contactoTelefono,
            String contactoRelacion, boolean activo) {
        String sql = "UPDATE clientes SET dni = ?, nombre = ?, apellido = ?, fecha_nacimiento = ?, email = ?, "
                + "telefono = ?, sexo = ?, contacto_emergencia_nombre = ?, contacto_emergencia_telefono = ?, "
                + "contacto_emergencia_relacion = ?, activo = ?, fecha_modificacion = CURRENT_DATE WHERE id = ?";

        try (Connection conexion = conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, dni);
            statement.setString(2, nombre);
            statement.setString(3, apellido);
            statement.setString(4, fechaNacimiento.toString());
            statement.setString(5, email);
            statement.setString(6, telefono);
            statement.setString(7, sexo);
            statement.setString(8, contactoNombre);
            statement.setString(9, contactoTelefono);
            statement.setString(10, contactoRelacion);
            statement.setBoolean(11, activo);
            statement.setInt(12, id);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                sincronizarSexoYContacto(conexion, id, sexo, contactoNombre, contactoTelefono, contactoRelacion);
                System.out.println("Cliente actualizado con éxito.");
                return true;
            } else {
                System.out.println("No se encontró ningún cliente con ese ID.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    public static void eliminarCliente(int id) {
        String sql = "UPDATE clientes SET activo = 0, fecha_modificacion = CURRENT_DATE WHERE id = ?";

        try (Connection conexion = conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, id);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Cliente eliminado con éxito.");
            } else {
                System.out.println("No se encontró ningún cliente con ese ID.");
            }

        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }

    public static ObservableList<Cliente> obtenerTodosLosClientes() {
        ObservableList<Cliente> clientes = FXCollections.observableArrayList();
        String sql = "SELECT c.id, c.dni, c.nombre, c.apellido, c.fecha_nacimiento, c.email, c.telefono, "
            + "COALESCE(s.nombre, c.sexo) AS sexo, "
            + "COALESCE(ce.nombre, c.contacto_emergencia_nombre) AS contacto_nombre, "
            + "COALESCE(ce.telefono, c.contacto_emergencia_telefono) AS contacto_telefono, "
            + "COALESCE(ce.relacion, c.contacto_emergencia_relacion) AS contacto_relacion, c.activo, "
                + "c.fecha_alta, c.fecha_modificacion FROM clientes c "
                + "LEFT JOIN sexos s ON s.id = c.sexo_id "
                + "LEFT JOIN contactos_emergencia ce ON ce.cliente_id = c.id "
                + "ORDER BY c.apellido, c.nombre";

        try (Connection conexion = conectar();
             Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String dni = resultSet.getString("dni");
                String nombre = resultSet.getString("nombre");
                String apellido = resultSet.getString("apellido");
                LocalDate fechaNacimiento = parsearFecha(resultSet.getString("fecha_nacimiento"));
                String email = resultSet.getString("email");
                String telefono = resultSet.getString("telefono");
                String sexo = resultSet.getString("sexo");
                String contactoNombre = resultSet.getString("contacto_emergencia_nombre");
                String contactoTelefono = resultSet.getString("contacto_emergencia_telefono");
                String contactoRelacion = resultSet.getString("contacto_emergencia_relacion");
                boolean activo = resultSet.getBoolean("activo");
                LocalDate fechaAlta = parsearFecha(resultSet.getString("fecha_alta"));
                LocalDate fechaModificacion = parsearFecha(resultSet.getString("fecha_modificacion"));
                clientes.add(new Cliente(id, dni, nombre, apellido, fechaNacimiento, email, telefono, sexo,
                    contactoNombre, contactoTelefono, contactoRelacion, activo, fechaAlta, fechaModificacion));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener clientes: " + e.getMessage());
        }

        return clientes;
    }

    private static LocalDate parsearFecha(String fecha) {
        return fecha == null || fecha.isEmpty() ? null : LocalDate.parse(fecha);
    }

    private static int obtenerUltimoId(Connection conexion) throws SQLException {
        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT last_insert_rowid()")) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    private static void sincronizarSexoYContacto(Connection conexion, int clienteId, String sexo,
            String contactoNombre, String contactoTelefono, String contactoRelacion) throws SQLException {
        try (PreparedStatement statement = conexion.prepareStatement(
                "UPDATE clientes SET sexo_id = (SELECT id FROM sexos WHERE nombre = ?) WHERE id = ?")) {
            statement.setString(1, sexo);
            statement.setInt(2, clienteId);
            statement.executeUpdate();
        }

        boolean tieneContacto = !contactoNombre.isEmpty() && !contactoTelefono.isEmpty()
                && !contactoRelacion.isEmpty();
        if (tieneContacto) {
            try (PreparedStatement statement = conexion.prepareStatement(
                    "INSERT INTO contactos_emergencia (cliente_id, nombre, telefono, relacion) VALUES (?, ?, ?, ?) "
                    + "ON CONFLICT(cliente_id) DO UPDATE SET nombre = excluded.nombre, telefono = excluded.telefono, "
                    + "relacion = excluded.relacion")) {
                statement.setInt(1, clienteId);
                statement.setString(2, contactoNombre);
                statement.setString(3, contactoTelefono);
                statement.setString(4, contactoRelacion);
                statement.executeUpdate();
            }
        } else {
            try (PreparedStatement statement = conexion.prepareStatement(
                    "DELETE FROM contactos_emergencia WHERE cliente_id = ?")) {
                statement.setInt(1, clienteId);
                statement.executeUpdate();
            }
        }
    }

    public static boolean guardarFichaMedica(int clienteId, String presion, String alergias, String medicacion,
            LocalDate fechaVencimientoApto, String observaciones) {
        String sql = "INSERT INTO fichas_medicas (cliente_id, presion, alergias, medicacion, "
                + "fecha_vencimiento_apto, observaciones) VALUES (?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT(cliente_id) DO UPDATE SET presion = excluded.presion, alergias = excluded.alergias, "
                + "medicacion = excluded.medicacion, fecha_vencimiento_apto = excluded.fecha_vencimiento_apto, "
                + "observaciones = excluded.observaciones";
        try (Connection conexion = conectar(); PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, clienteId);
            statement.setString(2, presion);
            statement.setString(3, alergias);
            statement.setString(4, medicacion);
            statement.setString(5, fechaVencimientoApto == null ? null : fechaVencimientoApto.toString());
            statement.setString(6, observaciones);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al guardar ficha médica: " + e.getMessage());
            return false;
        }
    }

    public static FichaMedica obtenerFichaMedica(int clienteId) {
        String sql = "SELECT id, cliente_id, presion, alergias, medicacion, fecha_vencimiento_apto, "
                + "observaciones FROM fichas_medicas WHERE cliente_id = ?";
        try (Connection conexion = conectar(); PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, clienteId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new FichaMedica(resultSet.getInt("id"), resultSet.getInt("cliente_id"),
                            resultSet.getString("presion"), resultSet.getString("alergias"),
                            resultSet.getString("medicacion"), parsearFecha(resultSet.getString("fecha_vencimiento_apto")),
                            resultSet.getString("observaciones"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener ficha médica: " + e.getMessage());
        }
        return null;
    }
    }
