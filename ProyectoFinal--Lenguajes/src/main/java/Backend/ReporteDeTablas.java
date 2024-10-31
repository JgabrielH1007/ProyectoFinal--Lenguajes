/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import java.awt.BorderLayout;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author gabrielh
 */
public class ReporteDeTablas extends JFrame {

    private JTable tablaErrores;
    private DefaultTableModel modeloTabla;

    public ReporteDeTablas(String tablasEncontradas) {
        initComponents();
        this.setLayout(new BorderLayout());

        // Inicializar la tabla de errores
        inicializarTabla(tablasEncontradas);

        // Agregar la tabla en un JScrollPane al centro del BorderLayout
        add(new JScrollPane(tablaErrores), BorderLayout.CENTER);

        this.setSize(800, 600);
        this.setVisible(true);
        pack();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void inicializarTabla(String tablasEncontradas) {
        // Define las columnas del modelo de la tabla
        String[] columnas = {"Nombre Tabla", "Linea", "Columna"};

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

        // Buscar estructuras CREATE TABLE y añadirlas a la tabla
        buscarTablas(tablasEncontradas);
    }

    private void buscarTablas(String texto) {
        // Expresión regular para detectar "CREATE TABLE nombre"
        Pattern patron = Pattern.compile("CREATE\\s+TABLE\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patron.matcher(texto);

        while (matcher.find()) {
            String nombreTabla = matcher.group(1);  // Captura el nombre de la tabla

            // Calcular la posición de línea y columna
            int linea = 0;
            int columna = 0;

            for (int i = 0; i < matcher.start(); i++) {
                if (texto.charAt(i) == '\n') {
                    linea++;
                    columna = 0;  // Reiniciar columna en cada nueva línea
                } else {
                    columna++;
                }
            }

            // Agregar la información a la tabla, ajustando la fila y columna para empezar desde 1
            modeloTabla.addRow(new Object[]{nombreTabla, linea + 1, columna + 1});
        }
    }

}
