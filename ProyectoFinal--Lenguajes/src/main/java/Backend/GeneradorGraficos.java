/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

/**
 *
 * @author gabrielh
 */
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GeneradorGraficos {

    public void generarGraficos(String texto) {
        StringBuilder graph = new StringBuilder();
        graph.append("digraph G {\n");
        graph.append("    node [shape=box, fontname=\"Arial\"];\n");

        String[] lines = texto.split("\n");
        int counter = 1;
        String previousNode = null;

        int columnCount = 3;
        int rowCount = 0;  

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.startsWith("CREATE TABLE")) {
                String tableName = line.split(" ")[2];
                StringBuilder tableBlock = new StringBuilder();
                String nodeName = "node" + counter++;

                tableBlock.append("    ").append(nodeName).append(" [label=\"")
                        .append(tableName).append("\\n");

                for (i = i + 1; i < lines.length; i++) {
                    String columnLine = lines[i].trim();

                    if (columnLine.endsWith(");")) {
                        columnLine = columnLine.replace(");", "").trim();
                        tableBlock.append(columnLine).append("\\n");
                        break;
                    }

                    columnLine = columnLine.replace(",", "").trim();
                    tableBlock.append(columnLine).append("\\n");
                }

                tableBlock.append("FOREIGN KEY (departamento_id) REFERENCES departamentos(id)\\n");
                tableBlock.append("\"];\n");
                graph.append(tableBlock.toString());

                // Generación de estructuras ALTER TABLE
            } else if (line.startsWith("ALTER TABLE")) {
                String[] parts = line.split(" ");
                String tableName = parts[2];
                String nodeName = "node" + counter++;
                StringBuilder alterBlock = new StringBuilder();
                alterBlock.append("    ").append(nodeName).append(" [label=\"");
                alterBlock.append("ALTER TABLE ").append(tableName);
                // Captura el resto de la operación sin repetir "ALTER TABLE"
                for (i = i; i < lines.length; i++) {
                    String operationLine = lines[i].trim();

                    if (operationLine.contains("ADD COLUMN")) {
                        alterBlock.append("\\nADD COLUMN");
                        // Agregar solo la parte restante de la línea después de "ADD COLUMN"
                        operationLine = operationLine.substring(operationLine.indexOf("ADD COLUMN") + "ADD COLUMN".length()).trim();
                    } else if (operationLine.contains("ALTER COLUMN")) {
                        alterBlock.append("\\nALTER COLUMN");
                        operationLine = operationLine.substring(operationLine.indexOf("ALTER COLUMN") + "ALTER COLUMN".length()).trim();
                    } else if (operationLine.contains("ADD CONSTRAINT")) {
                        alterBlock.append("\\nADD CONSTRAINT");
                        operationLine = operationLine.substring(operationLine.indexOf("ADD CONSTRAINT") + "ADD CONSTRAINT".length()).trim();
                    } else if (operationLine.contains("DROP COLUMN")) {
                        alterBlock.append("\\nDROP COLUMN");
                        operationLine = operationLine.substring(operationLine.indexOf("DROP COLUMN") + "DROP COLUMN".length()).trim();
                    }

                    if (operationLine.endsWith(";")) {
                        operationLine = operationLine.replace(";", "").trim();
                        alterBlock.append("\\n").append(operationLine);
                        break; // Termina el bucle al final de la declaración
                    }

                    // Agregar la operación actual a la etiqueta (si no es la última línea)
                    if (!operationLine.isEmpty()) {
                        alterBlock.append("\\n").append(operationLine);
                    }
                }

                alterBlock.append("\"];\n");
                graph.append(alterBlock.toString());

                // Generación de DROP TABLE
            } else if (line.startsWith("DROP TABLE")) {
                String[] parts = line.split(" ", 6); // Limita el split a 6 partes
                String tableName = parts[4]; // Obtiene el nombre de la tabla
                String nodeName = "node" + counter++;

                graph.append("    ").append(nodeName).append(" [label=\"DROP TABLE IF EXISTS ")
                        .append(tableName).append("\"];\n");
            }

            if (counter > 1 && previousNode != null) {
                graph.append("    { rank=same; ").append(previousNode).append(" -> ").append("node" + (counter - 1)).append(" [style=invis]; }\n");
            }
            previousNode = "node" + (counter - 1);

            if (counter % columnCount == 1) { // Nueva fila
                if (counter > 1) {
                    graph.append("    { rank=same; ");
                    for (int j = counter - columnCount; j < counter; j++) {
                        graph.append("node").append(j).append(" ");
                    }
                    graph.append("; }\n");
                }
            }
        }

        graph.append("}\n");

        // Escribe el archivo .dot
        try (FileWriter writer = new FileWriter("output.dot")) {
            writer.write(graph.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Archivo Graphviz generado como 'output.dot'. Puedes convertirlo usando Graphviz.");

        generarImagen();
    }

    private void generarImagen() {
        try {
            String[] command = {
                "dot", "-Tpng", "output.dot", "-o", "output.png"
            };
            Process process = new ProcessBuilder(command).start();
            process.waitFor();

            // Abre la imagen generada
            File imageFile = new File("output.png");
            if (imageFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(imageFile);
                } else {
                    System.out.println("No se puede abrir la imagen automáticamente. Archivo generado: output.png");
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
