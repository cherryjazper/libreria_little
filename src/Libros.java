import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.*;
import java.util.ResourceBundle;

public class Libros{

    private JPanel Ventana;
    private JTextField textIsbn;
    private JTextField textTitulo;
    private JTextField textAutor;
    private JButton butAñadir;
    private JButton butGuardar;
    private JButton butPrimerRegistro;
    private JButton butAnterior;
    private JButton butSiguiente;
    private JButton butUltimoRegistro;
    private JLabel labIsbn;
    private JLabel labTitulo;
    private JLabel labAutor;

    private static final String URL = "jdbc:mysql://localhost:3306/examen_mf0227";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "test123";
    private Connection conn;
    private ResultSet resultSet;


    public Libros(){
        conexionddBB();
        cargarPrimerRegistro();
        cargarDatos();
        actualizarEstadoBotones();
        butGuardar.setEnabled(false);

        KeyListener fieldListener = new KeyAdapter() {


            @Override
            public void keyReleased(KeyEvent e) {
                verificarCampos();
            }
        };
        textIsbn.addKeyListener(fieldListener);
        textTitulo.addKeyListener(fieldListener);
        textAutor.addKeyListener(fieldListener);

        butAñadir.addActionListener(e -> limpiarCampos());
        butGuardar.addActionListener(e -> guardarRegistro());

        butPrimerRegistro.addActionListener(e ->  {
            moverRegistro("PRIMERO");
            actualizarEstadoBotones();
        });
        butAnterior.addActionListener(e-> {
            moverRegistro("ANTERIOR");
            actualizarEstadoBotones();
        });
        butSiguiente.addActionListener(e-> {
            moverRegistro("SIGUIENTE");
            actualizarEstadoBotones();
        });
        butUltimoRegistro.addActionListener(e-> {
            moverRegistro("ULTIMO");
            actualizarEstadoBotones();
        });

    }
    private void conexionddBB() {
        try {
            conn = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            JOptionPane.showMessageDialog(null, "Conexión exitosa a la base de datos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar a la base de datos: " + e.getMessage());
        }
    }


    private void moverRegistro(String direccion) {
        try {
            if (resultSet == null || resultSet.isClosed()) {
                return;
            }

            boolean moved = false;
            switch (direccion) {
                case "PRIMERO":
                    moved = resultSet.first();
                    break;
                case "ANTERIOR":
                    moved = resultSet.previous();
                    break;
                case "SIGUIENTE":
                    moved = resultSet.next();
                    break;
                case "ULTIMO":
                    moved = resultSet.last();
                    break;
            }

            if (moved) {
                mostrarRegistro();
                actualizarEstadoBotones();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarPrimerRegistro() {
        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = stmt.executeQuery("SELECT * FROM libros");

            if (resultSet.next()) {
                mostrarRegistro();
            }
            actualizarEstadoBotones();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar registros: " + e.getMessage());
        }
    }



    private void mostrarRegistro() {
        try {
            if (resultSet != null && !resultSet.isClosed()) {
                textIsbn.setText(resultSet.getString("isbn"));
                textTitulo.setText(resultSet.getString("titulo"));
                textAutor.setText(resultSet.getString("autor"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void actualizarEstadoBotones() {
        try {
            if (resultSet == null || resultSet.isClosed()) {
                butPrimerRegistro.setEnabled(false);
                butAnterior.setEnabled(false);
                butSiguiente.setEnabled(false);
                butUltimoRegistro.setEnabled(false);
                return;
            }

            boolean isFirst = resultSet.isFirst();
            boolean isLast = resultSet.isLast();

            butPrimerRegistro.setEnabled(!isFirst);
            butAnterior.setEnabled(!isFirst);
            butSiguiente.setEnabled(!isLast);
            butUltimoRegistro.setEnabled(!isLast);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    private void limpiarCampos() {
        textIsbn.setText("");
        textTitulo.setText("");
        textAutor.setText("");
        textIsbn.requestFocus();
        butGuardar.setEnabled(true);
    }

    private void guardarRegistro() {
        try {


            String isbn = textIsbn.getText().trim();
            String titulo = textTitulo.getText().trim();
            String autor = textAutor.getText().trim();

            if (isbn.isEmpty() || titulo.isEmpty() || autor.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos deben estar llenos.");
                return;
            }
            Connection conn =DriverManager.getConnection(URL,USUARIO,CONTRASENA);
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO libros (isbn, titulo, autor) VALUES (?, ?, ?)");
            stmt.setString(1, isbn);
            stmt.setString(2, titulo);
            stmt.setString(3, autor);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Libro guardado con éxito.");
            butGuardar.setEnabled(false);
            cargarPrimerRegistro();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el libro: " + e.getMessage());
        }
    }

    private void cargarDatos() {
        try {
            Connection conn =DriverManager.getConnection(URL,USUARIO,CONTRASENA);
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM libros");

            if (resultSet.next()) mostrarRegistro(resultSet);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error al cargar datos: " + e.getMessage());
        }
    }

    private void mostrarRegistro(ResultSet resultSet) {
        try {
            if (resultSet != null && !resultSet.isClosed()) {
                textIsbn.setText(resultSet.getString("isbn"));
                textTitulo.setText(resultSet.getString("titulo"));
                textAutor.setText(resultSet.getString("autor"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void verificarCampos() {
        boolean todosLlenos = !textIsbn.getText().isEmpty() &&
                !textTitulo.getText().isEmpty() &&
                !textAutor.getText().isEmpty();
        butGuardar.setEnabled(todosLlenos);
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("Libros");
        frame.setContentPane(new Libros().Ventana);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}