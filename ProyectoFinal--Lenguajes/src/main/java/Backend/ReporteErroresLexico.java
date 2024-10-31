/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author gabrielh
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReporteErroresLexico extends JFrame {

    private JTable tablaErrores;
    private DefaultTableModel modeloTabla;

    public ReporteErroresLexico(List<Token> listaErrores) {
        initComponents(); // Llamada a initComponents para configurar el JFrame
        inicializarTabla(listaErrores); // Configura la tabla con datos

        this.setLayout(new BorderLayout()); // Establece layout del JFrame

        // Agregar la tabla en un JScrollPane al centro del BorderLayout
        add(new JScrollPane(tablaErrores), BorderLayout.CENTER);

        this.setSize(800, 600); // Tamaño del JFrame
        this.setVisible(true);
        pack();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void inicializarTabla(List<Token> listaErrores) {
        // Define las columnas del modelo de la tabla
        String[] columnas = {"Token", "Linea", "Columna", "Descripcion"};

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

        // Llenar el modelo con los errores
        for (Token token : listaErrores) {
            Object[] fila = {
                token.getTexto(),
                token.getLine(),
                token.getColumn(),
                token.getTipo()
            };
            modeloTabla.addRow(fila);
        }
    }
}
