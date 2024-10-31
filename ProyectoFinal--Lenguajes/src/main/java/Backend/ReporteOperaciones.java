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


public class ReporteOperaciones extends JFrame {
    private DefaultTableModel modeloTabla;

    public ReporteOperaciones(String texto) {
        setTitle("Reporte de Operaciones SQL");
        setSize(400, 300);
        setLocationRelativeTo(null);
        initComponents();
        procesarTexto(texto); // Procesar el texto al inicializar el JFrame
    }

    private void initComponents() {
        // Crear el modelo de la tabla
        String[] columnas = {"Operación", "Cantidad"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloTabla);
        tabla.setFillsViewportHeight(true);

        // Añadir la tabla a un JScrollPane
        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void procesarTexto(String texto) {
        contarOperaciones(texto);
    }

    private void contarOperaciones(String texto) {
        // Expresiones regulares para cada tipo de operación
        Pattern patronCreate = Pattern.compile("\\bCREATE\\b", Pattern.CASE_INSENSITIVE);
        Pattern patronDelete = Pattern.compile("\\bDELETE\\b", Pattern.CASE_INSENSITIVE);
        Pattern patronUpdate = Pattern.compile("\\bUPDATE\\b", Pattern.CASE_INSENSITIVE);
        Pattern patronSelect = Pattern.compile("\\bSELECT\\b", Pattern.CASE_INSENSITIVE);
        Pattern patronAlter = Pattern.compile("\\bALTER\\b", Pattern.CASE_INSENSITIVE);

        // Contar y agregar los resultados a la tabla
        agregarResultado("CREATE", contarCoincidencias(texto, patronCreate));
        agregarResultado("DELETE", contarCoincidencias(texto, patronDelete));
        agregarResultado("UPDATE", contarCoincidencias(texto, patronUpdate));
        agregarResultado("SELECT", contarCoincidencias(texto, patronSelect));
        agregarResultado("ALTER", contarCoincidencias(texto, patronAlter));
    }

    private int contarCoincidencias(String texto, Pattern patron) {
        Matcher matcher = patron.matcher(texto);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private void agregarResultado(String operacion, int cantidad) {
        modeloTabla.addRow(new Object[]{operacion, cantidad});
    }

    // Método principal para pruebas
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String texto = "CREATE TABLE empleados;\n" +
                           "SELECT * FROM empleados;\n" +
                           "UPDATE empleados SET nombre = 'Juan';\n" +
                           "DELETE FROM empleados WHERE id = 1;\n" +
                           "ALTER TABLE empleados ADD COLUMN fecha_nacimiento DATE;\n" +
                           "CREATE TABLE departamentos;\n";

            ReporteOperaciones reporte = new ReporteOperaciones(texto);
            reporte.setVisible(true);
        });
    }
}


