/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

/**
 *
 * @author gabrielh
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReporteModificaciones extends JFrame {

    private JTable tablaErrores;
    private DefaultTableModel modeloTabla;

    public ReporteModificaciones(String texto) {
        initComponents();
        this.setLayout(new BorderLayout());

        // Inicializar la tabla de errores
        inicializarTabla(texto);

        // Agregar la tabla en un JScrollPane al centro del BorderLayout
        add(new JScrollPane(tablaErrores), BorderLayout.CENTER);

        this.setSize(800, 600);
        this.setVisible(true);
        pack();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void inicializarTabla(String texto) {
        // Define las columnas del modelo de la tabla
        String[] columnas = {"Nombre Tabla", "Tipo Modificación", "Fila", "Columna"};

        // Crear el modelo de la tabla sin permitir edición
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Inicializa la tabla con el modelo y evita reordenar columnas
        tablaErrores = new JTable(modeloTabla);
        tablaErrores.getTableHeader().setReorderingAllowed(false);

        // Buscar estructuras ALTER TABLE y DROP TABLE IF EXISTS y añadirlas a la tabla
        buscarModificaciones(texto);
    }

    private void buscarModificaciones(String texto) {
        // Expresión regular para detectar "ALTER TABLE nombre" seguido de una modificación
        Pattern patronAlter = Pattern.compile(
                "ALTER TABLE\\s+(\\w+)\\s+(ADD COLUMN|ALTER COLUMN|DROP COLUMN|ADD CONSTRAINT)", Pattern.CASE_INSENSITIVE);
        Pattern patronDrop = Pattern.compile("DROP TABLE IF EXISTS\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
        buscarYAgregar(texto, patronAlter, "ALTER TABLE");
        buscarYAgregar(texto, patronDrop, "DROP TABLE IF EXISTS");
    }

    private void buscarYAgregar(String texto, Pattern patron, String tipoOperacion) {
        Matcher matcher = patron.matcher(texto);

        while (matcher.find()) {
            String nombreTabla = matcher.group(1);  // Captura el nombre de la tabla
            String tipoModificacion = tipoOperacion.equals("ALTER TABLE") ? matcher.group(2) : tipoOperacion;

            // Calcular la posición de línea y columna desde el inicio del texto hasta el inicio del match
            int posicionInicio = matcher.start();
            int linea = 0;
            int columna = 0;

            // Recorremos el texto hasta la posición de inicio y contamos las líneas y columnas
            for (int i = 0; i < posicionInicio; i++) {
                if (texto.charAt(i) == '\n') {
                    linea++;
                    columna = 0;  // Resetea columna después de un salto de línea
                } else {
                    columna++;
                }
            }

            // Agregar la información a la tabla
            modeloTabla.addRow(new Object[]{nombreTabla, tipoModificacion, linea, columna});
        }
    }

}
