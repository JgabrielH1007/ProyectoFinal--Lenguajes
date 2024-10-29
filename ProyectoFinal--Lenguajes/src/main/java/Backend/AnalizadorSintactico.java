/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gabrielh
 */
public class AnalizadorSintactico {

    private List<Errores> erroresSintacticos = new ArrayList<>();

    private void estructuraDataBase(String texto, List<Token> create, List<Token> identificador, List<Token> signos) {
        String cleanedTexto = texto.replaceAll("\\s*;\\s*", " ; ");
        String[] tokens = cleanedTexto.split("\\s+");

        boolean validStructureFound = false; // Bandera para detectar estructuras válidas

        for (int i = 0; i < tokens.length - 3; i++) {
            // Verificar que los tokens actuales formen la estructura deseada
            if (tokens[i].equals("CREATE") && containsToken(create, "CREATE")
                    && tokens[i + 1].equals("DATABASE") && containsToken(create, "DATABASE")
                    && containsToken(identificador, tokens[i + 2])) {

                // Verificar si el token siguiente es el punto y coma
                if (tokens[i + 3].equals(";") && containsToken(signos, ";")) {
                    System.out.println("Estructura válida encontrada: CREATE DATABASE " + tokens[i + 2] + " ;");
                    validStructureFound = true; // Estructura válida encontrada
                    return;
                }
            }
        }

        // Si no se encontró una estructura válida, añadir errores a la lista
        if (!validStructureFound) {
            // Validar qué parte de la estructura está faltando
            if (!tokens[0].equals("CREATE")) {
                erroresSintacticos.add(new Errores("Falta 'CREATE'", "La instrucción debe comenzar con 'CREATE'."));
            } else if (!tokens[1].equals("DATABASE")) {
                erroresSintacticos.add(new Errores("Falta 'DATABASE'", "Se esperaba 'DATABASE' después de 'CREATE'."));
            } else if (!containsToken(identificador, tokens[2])) {
                erroresSintacticos.add(new Errores("Identificador no válido", "El identificador debe ser un nombre válido."));
            } else if (!tokens[3].equals(";")) {
                erroresSintacticos.add(new Errores("Falta ';'", "La instrucción debe terminar con un punto y coma."));
            } else {
                // Caso general si ninguna validación anterior se cumplió
                erroresSintacticos.add(new Errores("Error de sintaxis", "La instrucción no sigue la sintaxis esperada."));
            }

            // Mostrar todos los errores registrados
            for (Errores error : erroresSintacticos) {
                System.out.println("Error: " + error.getError() + " - " + error.getDefinicion());
            }
        }
    }

// Método auxiliar para verificar si un token está en una lista de Tokens
    private boolean containsToken(List<Token> list, String value) {
        for (Token token : list) {
            if (token.getTexto().equals(value)) {

                return true;
            }
        }

        return false;
    }

    private void estructuraTabla(String texto, List<Token> create, List<Token> identificador, List<Token> tipoDato, List<Token> signos, List<Token> enteros) {
        String cleanedTexto = texto.replaceAll("\\(", " ( ").replaceAll("\\)", " ) ")
                .replaceAll(",", " , ").replaceAll("\\s*;\\s*", " ; ");
        String[] tokens = cleanedTexto.split("\\s+");

        boolean estructuraValida = false;
        boolean procesandoLlave = false; // Indica si estamos dentro de una estructura de llave
        int i = 0;

        if (tokens[i].equals("CREATE") && containsToken(create, "CREATE")
                && tokens[i + 1].equals("TABLE") && containsToken(create, "TABLE")
                && containsToken(identificador, tokens[i + 2])
                && tokens[i + 3].equals("(") && containsToken(signos, "(")) {

            i += 4;
            estructuraValida = true;

            while (i < tokens.length) {
                if (i < tokens.length - 1 && tokens[i].equals(")") && tokens[i + 1].equals(";")) {
                    break; // Salir del bucle si se encuentra el cierre de la estructura
                }

                if (containsToken(identificador, tokens[i])) {
                    i++;

                    if (containsToken(tipoDato, tokens[i])) {
                        i++;

                        if (i < tokens.length && tokens[i].equals("(")) {
                            i++;
                            if (containsToken(enteros, tokens[i])) {
                                i++;
                                if (i < tokens.length && tokens[i].equals(",") && containsToken(signos, ",")) {
                                    i++;
                                    if (containsToken(enteros, tokens[i])) {
                                        i++;
                                    } else {
                                        System.out.println("Error: Parámetro de tipo de dato no válido.");
                                        return;
                                    }
                                }
                                if (i < tokens.length && tokens[i].equals(")") && containsToken(signos, ")")) {
                                    i++;
                                } else {
                                    System.out.println("Error: Falta cerrar paréntesis en tipo de dato.");
                                    return;
                                }
                            } else {
                                System.out.println("Error: Parámetro de tipo de dato no válido.");
                                return;
                            }
                        }

                        while (i < tokens.length && (tokens[i].equals("PRIMARY") || tokens[i].equals("NOT") || tokens[i].equals("UNIQUE"))) {
                            if (tokens[i].equals("PRIMARY") && i + 1 < tokens.length && tokens[i + 1].equals("KEY")) {
                                i += 2;
                            } else if (tokens[i].equals("NOT") && i + 1 < tokens.length && tokens[i + 1].equals("NULL")) {
                                i += 2;
                            } else if (tokens[i].equals("UNIQUE")) {
                                i++;
                            }
                        }

                        if (i < tokens.length && tokens[i].equals(",") && containsToken(signos, ",")) {
                            i++;
                        }
                    } else {
                        System.out.println("Error: Tipo de dato no válido en la declaración de columna.");
                        return;
                    }

                } else if (!procesandoLlave && tokens[i].equals("CONSTRAINT") && containsToken(create, "CONSTRAINT")) {
                    procesandoLlave = true; // Iniciamos el procesamiento de la llave
                    i++;

                    if (containsToken(identificador, tokens[i])) { // <identificador> para CONSTRAINT
                        i++;

                        if (tokens[i].equals("FOREIGN") && tokens[i + 1].equals("KEY")
                                && containsToken(create, "FOREIGN")
                                && containsToken(create, "KEY")) {
                            i += 2;

                            if (tokens[i].equals("(") && containsToken(signos, "(")
                                    && containsToken(identificador, tokens[i + 1])
                                    && tokens[i + 2].equals(")")
                                    && containsToken(signos, ")")) {
                                i += 3;

                                if (tokens[i].equals("REFERENCES") && containsToken(create, "REFERENCES")) {
                                    i++;
                                    if (containsToken(identificador, tokens[i])
                                            && tokens[i + 1].equals("(")
                                            && containsToken(signos, "(")
                                            && containsToken(identificador, tokens[i + 2])
                                            && tokens[i + 3].equals(")")
                                            && containsToken(signos, ")")) {
                                        i += 4;
                                        procesandoLlave = false; // Terminamos el procesamiento de la llave
                                        continue; // Continuar el bucle
                                    } else {
                                        System.out.println("Error: Estructura no válida en REFERENCES.");
                                        return;
                                    }
                                } else {
                                    System.out.println("Error: Estructura no válida en FOREIGN KEY.");
                                    return;
                                }
                            } else {
                                System.out.println("Error: Estructura no válida en FOREIGN KEY.");
                                return;
                            }
                        } else {
                            System.out.println("Error: Estructura no válida en FOREIGN KEY.");
                            return;
                        }
                    } else {
                        System.out.println("Error: Identificador no válido para CONSTRAINT.");
                        return;
                    }
                } else {
                    System.out.println("Error: Identificador de columna o estructura de llave no válido.");
                    return;
                }
            }

            if (i < tokens.length - 1 && tokens[i].equals(")") && tokens[i + 1].equals(";")) {
                System.out.println("Estructura válida encontrada: CREATE TABLE <identificador> (...) ;");
            } else {
                System.out.println("Error: Estructura incompleta, falta cerrar con ')' y ';'.");
            }
        } else {
            System.out.println("Error: No se encontró una estructura válida de 'CREATE TABLE <identificador> (...) ;'");
        }
    }

    private void estructuraTablaDesdeTexto(String texto, List<Token> create, List<Token> identificador, List<Token> tipoDato, List<Token> signos, List<Token> enteros) {
        // Eliminar comentarios de una sola línea
        texto = eliminarComentarios(texto);

        String[] lineas = texto.split("\n");
        StringBuilder consultaCompleta = new StringBuilder();
        boolean comenzandoEstructura = false;

        for (String linea : lineas) {
            String lineaLimpiada = linea.trim();

            if (lineaLimpiada.startsWith("CREATE TABLE")) {
                comenzandoEstructura = true;
            }

            if (comenzandoEstructura) {
                consultaCompleta.append(lineaLimpiada).append(" ");
                if (consultaCompleta.toString().trim().endsWith(");")) {
                    break;
                }
            }
        }

        estructuraTabla(consultaCompleta.toString(), create, identificador, tipoDato, signos, enteros);
    }

    public String eliminarComentarios(String texto) {
        return texto.replaceAll("--[^\n\r]*", "").trim();
    }

    public void procesarEstructuras(String consulta, List<Token> create, List<Token> identificador, List<Token> tipoDato, List<Token> signos, List<Token> enteros,
            List<Token> aritmeticos, List<Token> logicos,
            List<Token> cadena, List<Token> fecha, List<Token> Decimal, List<Token> racional, List<Token> agregacion) {

        // Eliminar comentarios de una sola línea
        consulta = eliminarComentarios(consulta);

        String[] lineas = consulta.split("\n");
        StringBuilder estructuraActual = new StringBuilder();
        boolean enEstructura = false;

        for (String linea : lineas) {
            String lineaLimpiada = linea.trim();

            if (lineaLimpiada.startsWith("CREATE DATABASE") || lineaLimpiada.startsWith("CREATE TABLE")
                    || lineaLimpiada.startsWith("ALTER TABLE") || lineaLimpiada.startsWith("DROP TABLE")
                    || lineaLimpiada.startsWith("INSERT INTO") || lineaLimpiada.startsWith("DELETE FROM") || lineaLimpiada.startsWith("SELECT")
                    || lineaLimpiada.startsWith("UPDATE")) {

                if (enEstructura) {
                    procesarEstructura(estructuraActual.toString(), create, identificador, tipoDato, signos, enteros, aritmeticos, logicos, cadena, fecha, Decimal, racional, agregacion);
                    estructuraActual.setLength(0);
                }
                enEstructura = true;
            }

            if (enEstructura) {
                estructuraActual.append(lineaLimpiada).append(" ");
                if (lineaLimpiada.endsWith(";") || lineaLimpiada.endsWith(");")) {
                    procesarEstructura(estructuraActual.toString(), create, identificador, tipoDato, signos, enteros, aritmeticos, logicos, cadena, fecha, Decimal, racional, agregacion);
                    estructuraActual.setLength(0);
                    enEstructura = false;
                } else {
                    erroresSintacticos.add(new Errores("Falta ';'", "La instrucción debe terminar con un punto y coma."));
                    for (Errores error : erroresSintacticos) {
                        System.out.println("Error: " + error.getError() + " - " + error.getDefinicion());
                    }
                }
            }
        }

    }

    public void procesarEstructura(String consulta, List<Token> create, List<Token> identificador, List<Token> tipoDato, List<Token> signos, List<Token> enteros,
            List<Token> aritmeticos, List<Token> logicos,
            List<Token> cadena, List<Token> fecha, List<Token> Decimal, List<Token> racional, List<Token> agregacion) {
        consulta = consulta.trim(); // Eliminar espacios en blanco alrededor de la consulta

        // Determinar el tipo de estructura a procesar
        if (consulta.startsWith("CREATE DATABASE")) {
            // Procesar estructura de base de datos
            estructuraDataBase(consulta, create, identificador, signos);
        } else if (consulta.startsWith("CREATE TABLE")) {
            // Procesar estructura de tabla
            estructuraTablaDesdeTexto(consulta, create, identificador, tipoDato, signos, enteros);
        } else if (consulta.startsWith("ALTER TABLE") || consulta.startsWith("DROP TABLE")) {
            // Procesar estructura de modificación (ALTER)
            estructuraModificadores(consulta, create, identificador, signos, enteros, tipoDato);
        } else if (consulta.startsWith("INSERT INTO")) { // Añadir procesador para INSERT
            estructuraInsercion(consulta, create, identificador, signos, enteros, aritmeticos, logicos, cadena, fecha, Decimal, racional);
        } else if (consulta.startsWith("DELETE FROM")) {
            estructuraEliminacion(consulta, create, identificador, signos, racional, enteros, logicos, Decimal, fecha);
        } else if (consulta.startsWith("SELECT")) {
            estructuraLectura(consulta, create, identificador, signos, racional, enteros, logicos, Decimal, fecha, agregacion);
        } else if (consulta.startsWith("UPDATE")) {
            estructuraUpdate(consulta, create, identificador, signos, racional, enteros, logicos, Decimal, fecha, aritmeticos);
        } else {
            System.out.println("Error: No se reconoció una estructura válida de 'CREATE DATABASE', 'CREATE TABLE', 'ALTER TABLE', 'DROP TABLE' o 'INSERT INTO'.");
        }
    }

    private void estructuraModificadores(String texto, List<Token> create, List<Token> identificador, List<Token> signos, List<Token> entero, List<Token> tipoDato) {
        // Preprocesar el texto para facilitar la separación de palabras clave y signos
        String cleanedTexto = texto.replaceAll("\\(", " ( ").replaceAll("\\)", " ) ")
                .replaceAll(",", " , ").replaceAll("\\s*;\\s*", " ; ");
        String[] tokens = cleanedTexto.split("\\s+");

        int i = 0;

        // Verificar si el texto comienza con "ALTER TABLE" o "DROP TABLE"
        if (tokens[i].equals("ALTER") && containsToken(create, "ALTER")
                && tokens[i + 1].equals("TABLE") && containsToken(create, "TABLE")
                && containsToken(identificador, tokens[i + 2])) {

            i += 3; // Avanzamos después de "ALTER TABLE <identificador>"

            // Evaluar tipo de alteración en la tabla
            if (tokens[i].equals("ADD")) {
                i++;
                if (tokens[i].equals("COLUMN") && containsToken(create, "COLUMN")) {
                    // Estructura: ALTER TABLE <identificador> ADD COLUMN <identificador> [Tipo_de_dato];
                    i++;
                    if (containsToken(identificador, tokens[i]) && containsToken(tipoDato, tokens[i + 1])) {
                        i += 2;
                        if (i < tokens.length && tokens[i].equals(";")) {
                            System.out.println("Estructura válida: ALTER TABLE ADD COLUMN.");
                            return;
                        }
                    }
                } else if (tokens[i].equals("CONSTRAINT") && containsToken(create, "CONSTRAINT")) {
                    // Estructuras con CONSTRAINT
                    i++;
                    if (containsToken(identificador, tokens[i])) {
                        i++;
                        if (containsToken(tipoDato, tokens[i])) {
                            // Estructura: ALTER TABLE <identificador> ADD CONSTRAINT <identificador> [Tipo_de_dato];
                            i++;
                            if (i < tokens.length && tokens[i].equals(";")) {
                                System.out.println("Estructura válida: ALTER TABLE ADD CONSTRAINT.");
                                return;
                            }
                        } else if (tokens[i].equals("UNIQUE") && containsToken(create, "UNIQUE") && tokens[i + 1].equals("(")
                                && containsToken(identificador, tokens[i + 2]) && tokens[i + 3].equals(")")) {
                            // Estructura: ALTER TABLE <identificador> ADD CONSTRAINT <identificador> UNIQUE (<identificador>);
                            i += 4;
                            if (i < tokens.length && tokens[i].equals(";")) {
                                System.out.println("Estructura válida: ALTER TABLE ADD CONSTRAINT UNIQUE.");
                                return;
                            }
                        } else if (tokens[i].equals("FOREIGN") && tokens[i + 1].equals("KEY")
                                && tokens[i + 2].equals("(") && containsToken(identificador, tokens[i + 3])
                                && tokens[i + 4].equals(")") && tokens[i + 5].equals("REFERENCES")
                                && containsToken(identificador, tokens[i + 6]) && tokens[i + 7].equals("(")
                                && containsToken(identificador, tokens[i + 8]) && tokens[i + 9].equals(")")) {
                            // Estructura: ALTER TABLE <identificador> ADD CONSTRAINT <identificador> FOREIGN KEY (<identificador>) REFERENCES <identificador>(<identificador>);
                            i += 10;
                            if (i < tokens.length && tokens[i].equals(";")) {
                                System.out.println("Estructura válida: ALTER TABLE ADD CONSTRAINT FOREIGN KEY.");
                                return;
                            }
                        }
                    }
                }
            } else if (tokens[i].equals("ALTER") && tokens[i + 1].equals("COLUMN") && containsToken(create, "ALTER")
                    && containsToken(create, "COLUMN") && containsToken(identificador, tokens[i + 2])
                    && tokens[i + 3].equals("TYPE") && containsToken(create, "TYPE")) {
                // Estructura: ALTER TABLE <identificador> ALTER COLUMN <identificador> TYPE [Tipo_de_dato con o sin paréntesis]
                i += 4;
                if (containsToken(tipoDato, tokens[i])) {
                    i++;
                    // Si el tipo de dato tiene paréntesis, avanzar los tokens
                    if (tokens[i].equals("(")) {
                        i++; // Mover después de '('
                        while (!tokens[i].equals(")")) {
                            i++;
                        }
                        i++; // Mover después de ')'
                    }
                    if (i < tokens.length && tokens[i].equals(";")) {
                        System.out.println("Estructura válida: ALTER TABLE ALTER COLUMN TYPE.");
                        return;
                    }
                }
            } else if (tokens[i].equals("DROP") && tokens[i + 1].equals("COLUMN") && containsToken(create, "DROP")
                    && containsToken(create, "COLUMN") && containsToken(identificador, tokens[i + 2])) {
                // Estructura: ALTER TABLE <identificador> DROP COLUMN <identificador>;
                i += 3;
                if (i < tokens.length && tokens[i].equals(";")) {
                    System.out.println("Estructura válida: ALTER TABLE DROP COLUMN.");
                    return;
                }
            }
        } else if (tokens[i].equals("DROP") && tokens[i + 1].equals("TABLE") && containsToken(create, "DROP")
                && containsToken(create, "TABLE")) {
            // Verificar "IF EXISTS" o "IF EXIST" y "CASCADE"
            i += 2;
            if (tokens[i].equals("IF") && (tokens[i + 1].equals("EXISTS") || tokens[i + 1].equals("EXIST"))
                    && containsToken(create, "IF") && containsToken(create, tokens[i + 1])) {
                i += 2;
            }
            if (containsToken(identificador, tokens[i]) && tokens[i + 1].equals("CASCADE")
                    && containsToken(create, "CASCADE")) {
                // Estructura: DROP TABLE IF EXISTS <identificador> CASCADE;
                i += 2;
                if (i < tokens.length && tokens[i].equals(";")) {
                    System.out.println("Estructura válida: DROP TABLE IF EXISTS CASCADE.");
                    return;
                }
            }
        }

        System.out.println("Error: No se encontró una estructura válida.");
    }

    private void estructuraInsercion(String texto, List<Token> create, List<Token> identificador,
            List<Token> signos, List<Token> entero, List<Token> aritmeticos,
            List<Token> logicos, List<Token> cadena, List<Token> fecha,
            List<Token> decimal, List<Token> racional) {

        // Eliminar espacios en blanco al inicio y al final
        texto = texto.trim();

        // Verificar que empiece con "INSERT INTO"
        if (!texto.startsWith("INSERT INTO")) {
            System.out.println("Error: La instrucción debe comenzar con 'INSERT INTO'.");
            return;
        }

        // Dividir la cadena en tokens usando un espacio como separador
        String[] partes = texto.split("\\s+");

        // Verificar que el primer token sea "INSERT"
        if (!partes[0].equals("INSERT") || !containsToken(create, "INSERT")) {
            System.out.println("Error: Se esperaba 'INSERT'.");
            return;
        }

        // Verificar que el segundo token sea "INTO"
        if (partes[1].equals("INTO") && containsToken(create, "INTO")) {
            // Obtener el identificador
            String tabla = partes[2];
            if (!containsToken(identificador, tabla)) {
                System.out.println("Error: Tabla no válida: " + tabla);
                return;
            }

            // Buscar la parte que contiene las columnas
            int inicioColumnas = texto.indexOf("(");
            int finColumnas = texto.indexOf(")");
            if (inicioColumnas == -1 || finColumnas == -1 || inicioColumnas >= finColumnas) {
                System.out.println("Error: No se encontraron columnas.");
                return;
            }
            String columnas = texto.substring(inicioColumnas + 1, finColumnas);
            String[] columnasTokens = columnas.split(",");

            // Validar columnas
            for (String columna : columnasTokens) {
                columna = columna.trim();
                if (!containsToken(identificador, columna)) {
                    System.out.println("Error: Identificador de columna inválido: " + columna);
                    return;
                }
            }

            // Buscar la parte de "VALUES"
            int indexValues = texto.indexOf("VALUES");
            if (indexValues == -1) {
                System.out.println("Error: Se esperaba la palabra clave 'VALUES'.");
                return;
            }

            // Obtener la parte de valores
            String valores = texto.substring(indexValues + 7).trim(); // +7 para saltar "VALUES "

            // Eliminar el punto y coma final si existe
            if (valores.endsWith(";")) {
                valores = valores.substring(0, valores.length() - 1).trim();
            }

            // Validar que haya valores
            if (valores.isEmpty()) {
                System.out.println("Error: No se encontraron valores después de 'VALUES'.");
                return;
            }

            // Manejar múltiples conjuntos de valores
            String[] conjuntosValores = valores.split("\\s*,\\s*(?=\\()"); // Separar por comas que estén seguidas de un paréntesis de apertura
            for (String conjunto : conjuntosValores) {
                conjunto = conjunto.trim();
                if (!conjunto.startsWith("(") || !conjunto.endsWith(")")) {
                    System.out.println("Error: Cada conjunto de valores debe estar encerrado en paréntesis.");
                    return;
                }

                conjunto = conjunto.substring(1, conjunto.length() - 1); // Quitar paréntesis
                String[] datosTokens = conjunto.split(",\\s*"); // Separar por comas dentro del conjunto

                for (String dato : datosTokens) {
                    dato = dato.trim();
                    // Validar si es un dato literal o un valor nulo
                    if (dato.equals("TRUE") || dato.equals("FALSE") || dato.equals("NULL")
                            || esDatoValido(dato, entero, decimal, fecha, cadena)
                            || esExpresion(dato, aritmeticos, logicos)
                            || esExpresionComparativa(dato, entero, decimal, fecha, cadena, aritmeticos, logicos)) {
                        // El dato es válido
                    } else {
                        System.out.println("Error: Dato inválido en VALUES: " + dato);
                        return;
                    }
                }
            }

            System.out.println("Estructura válida: INSERT INTO con múltiples conjuntos de VALUES.");
            return;
        } else {
            System.out.println("Error: Se esperaba 'INTO' después de 'INSERT'.");
        }
    }

// Método para verificar expresiones comparativas
    private boolean esExpresionComparativa(String dato, List<Token> entero, List<Token> decimal,
            List<Token> fecha, List<Token> cadena, List<Token> aritmeticos, List<Token> logicos) {
        // Definir operadores comparativos válidos
        String[] operadores = {"<", ">", "<=", ">="};

        for (String operador : operadores) {
            if (dato.contains(operador)) {
                // Dividir por el operador para verificar ambos lados
                String[] partes = dato.split("\\s*" + operador + "\\s*");
                if (partes.length == 2) {
                    // Validar que ambas partes sean números o expresiones válidas
                    if (esDatoValido(partes[0], entero, decimal, fecha, cadena)
                            || esExpresion(partes[0], aritmeticos, logicos)) {
                        if (esDatoValido(partes[1], entero, decimal, fecha, cadena)
                                || esExpresion(partes[1], aritmeticos, logicos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

// Método auxiliar para verificar si un dato es válido
    private boolean esDatoValido(String dato, List<Token> entero, List<Token> decimal,
            List<Token> fecha, List<Token> cadena) {
        return containsToken(entero, dato) || containsToken(decimal, dato)
                || containsToken(fecha, dato) || esCadena(dato);
    }

    private boolean esCadena(String dato) {
        return dato.startsWith("'") && dato.endsWith("'") && dato.length() > 2;
    }

// Método auxiliar para verificar si un dato es una expresión válida
    private boolean esExpresion(String dato, List<Token> aritmeticos, List<Token> logicos) {
        for (Token token : aritmeticos) {
            if (dato.contains(token.getTexto())) {
                return true;
            }
        }
        for (Token token : logicos) {
            if (dato.contains(token.getTexto())) {
                return true;
            }
        }
        return false;
    }

    //No lee decimales
    private void estructuraEliminacion(String texto, List<Token> create, List<Token> identificador, List<Token> signos,
            List<Token> racionales, List<Token> enteros, List<Token> booleanos,
            List<Token> decimales, List<Token> fecha) {
        texto = texto.trim();

        // Modificación en la expresión regular para no separar los decimales
        String[] tokens = texto.split(" (?=(?:[^\"']|\"[^\"]*\"|'[^']*')*$)|(?=;)|(?<=;)|(?=\\.)|(?<=\\.)|(?=,)|(?=\\()|(?=\\))|(?<=\\()|(?<=\\))");
        // Unir tokens decimales
        List<String> mergedTokens = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            if (i < tokens.length - 2 && tokens[i].matches("[0-9]+") && tokens[i + 1].equals(".") && tokens[i + 2].matches("[0-9]+")) {
                // Unir el entero, el punto y el otro entero
                mergedTokens.add(tokens[i] + tokens[i + 1] + tokens[i + 2]);
                i += 2; // Saltar los dos tokens que ya fueron unidos
            } else {
                mergedTokens.add(tokens[i]);
            }
        }

        // Convertir la lista de tokens unidos de nuevo a un arreglo
        tokens = mergedTokens.toArray(new String[0]);

        // Validar la estructura básica de DELETE FROM
        if (tokens.length < 3 || !tokens[0].equals("DELETE") || !tokens[1].equals("FROM") || !containsToken(identificador, tokens[2])) {
            System.out.println("Error: Estructura DELETE FROM no válida.");
            return;
        }

        int i = 3; // Índice después de "DELETE FROM <identificador>"
        boolean tieneCondicionWhere = false;

        // Verificar si hay cláusula WHERE
        if (i < tokens.length && tokens[i].equals("WHERE")) {
            tieneCondicionWhere = true;
            i++; // Avanzar después de "WHERE"

            // Procesar condiciones en la cláusula WHERE utilizando el método procesarCondicionWhere
            if (!procesarCondicionWhere(tokens, new int[]{i}, identificador, signos, racionales, enteros, decimales, booleanos, fecha)) {
                System.out.println("Error: Condiciones WHERE no válidas.");
                return; // Salir si las condiciones no son válidas
            }

            // Avanzar el índice hasta el final de la cláusula WHERE
            while (i < tokens.length && !tokens[i].equals(";")) {
                i++;
            }
        }

        // Validar punto y coma final
        if (i < tokens.length && tokens[i].equals(";")) {
            if (tieneCondicionWhere) {
                System.out.println("Estructura válida: DELETE con cláusula WHERE.");
            } else {
                System.out.println("Estructura válida: DELETE sin cláusula WHERE.");
            }
        } else {
            System.out.println("Error: Estructura de DELETE incompleta.");
        }
    }

// Método para verificar si un token es decimal
    private boolean isDecimal(String token) {
        return token.matches("\\d+\\.\\d+");
    }

    private void estructuraLectura(String texto, List<Token> create, List<Token> identificador, List<Token> signos,
            List<Token> racionales, List<Token> enteros, List<Token> booleanos,
            List<Token> decimales, List<Token> fecha, List<Token> agregacion) {
        texto = texto.trim();

        // Dividir el texto en tokens utilizando un patrón de separación adecuado, incluyendo comas y paréntesis
        String[] tokens = texto.split(" (?=(?:[^\"']|\"[^\"]*\"|'[^']*')*$)|(?=;)|(?<=;)|(?=\\.)|(?<=\\.)|(?=,)|(?=\\()|(?=\\))|(?<=\\()|(?<=\\))");
        // Unir tokens decimales en caso de que estén separados
        List<String> mergedTokens = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            if (i < tokens.length - 2 && tokens[i].matches("[0-9]+") && tokens[i + 1].equals(".") && tokens[i + 2].matches("[0-9]+")) {
                mergedTokens.add(tokens[i] + tokens[i + 1] + tokens[i + 2]);
                i += 2; // Saltar los tokens ya unidos
            } else {
                mergedTokens.add(tokens[i]);
            }
        }
        tokens = mergedTokens.toArray(new String[0]);

        // Verificar si comienza con SELECT
        if (tokens.length > 0 && !tokens[0].equals("SELECT")) {
            System.out.println("Nota: La sentencia no comienza con 'SELECT', pero será procesada.");
        }

        int i = 0; // Empezar desde el primer token

        // Si hay al menos un token y es "SELECT", procesamos la selección
        if (tokens.length > 0 && tokens[i].equals("SELECT")) {
            i++; // Avanzar después de "SELECT"
        }

        // Verificar selección de columna o '*'
        boolean seleccionColumnas = true;
        if (i < tokens.length && tokens[i].equals("*")) {
            i++;
            seleccionColumnas = false;
        } else {
            // Procesar selección de columnas
            while (i < tokens.length && !tokens[i].equals("FROM")) {
                // Verificar funciones de agregación
                if (containsToken(agregacion, tokens[i])) {
                    i++;
                    if (i < tokens.length && tokens[i].equals("(")) {
                        i++;
                        if (i < tokens.length && containsToken(identificador, tokens[i])) {
                            i++;
                            if (i < tokens.length && tokens[i].equals(")")) {
                                i++;
                                // Verificar si hay un alias AS
                                if (i < tokens.length && tokens[i].equals("AS")) {
                                    i++;
                                    if (i < tokens.length && containsToken(identificador, tokens[i])) {
                                        i++;
                                    } else {
                                        System.out.println("Error: Alias faltante después de 'AS'.");
                                        return;
                                    }
                                }
                            } else {
                                System.out.println("Error: Cierre de paréntesis faltante en función de agregación.");
                                return;
                            }
                        } else {
                            System.out.println("Error: Identificador faltante en función de agregación.");
                            return;
                        }
                    } else {
                        System.out.println("Error: Apertura de paréntesis faltante en función de agregación.");
                        return;
                    }
                } else if (containsToken(identificador, tokens[i])) {
                    // Procesar un identificador normal
                    i++;
                    // Verificar si hay un punto seguido de otro identificador
                    if (i < tokens.length && tokens[i].equals(".")) {
                        i++;
                        if (i < tokens.length && containsToken(identificador, tokens[i])) {
                            i++; // Procesar el segundo identificador
                        } else {
                            System.out.println("Error: Identificador faltante después del punto.");
                            return;
                        }
                    }
                    // Verificar si hay un alias AS
                    if (i < tokens.length && tokens[i].equals("AS")) {
                        i++;
                        if (i < tokens.length && containsToken(identificador, tokens[i])) {
                            i++;
                        } else {
                            System.out.println("Error: Alias faltante después de 'AS'.");
                            return;
                        }
                    }
                } else {
                    System.out.println("Error: Selección de columna no válida.");
                    return;
                }

                // Verificar si hay una coma para separar columnas
                if (i < tokens.length && tokens[i].equals(",")) {
                    i++;
                }
            }
        }

        // Verificar "FROM" y el identificador de la tabla
        if (i < tokens.length && tokens[i].equals("FROM")) {
            i++;
            if (i < tokens.length && containsToken(identificador, tokens[i])) {
                i++;
            } else {
                System.out.println("Error: Identificador de tabla faltante después de 'FROM'.");
                return;
            }
        } else {
            System.out.println("Error: Falta la palabra clave 'FROM'.");
            return;
        }

        // Procesar posibles cláusulas adicionales
        while (i < tokens.length && !tokens[i].equals(";")) {
            if (tokens[i].equals("JOIN")) {
                i++;
                if (i < tokens.length && containsToken(identificador, tokens[i])) {
                    i++;
                    if (i < tokens.length && containsToken(identificador, tokens[i])) {
                        i++;
                        if (i < tokens.length && tokens[i].equals("ON")) {
                            i++;
                            if (containsToken(identificador, tokens[i]) && tokens[i + 1].equals(".") && containsToken(identificador, tokens[i + 2])) {
                                i += 3;
                                if (tokens[i].equals("=") && containsToken(identificador, tokens[i + 1]) && tokens[i + 2].equals(".") && containsToken(identificador, tokens[i + 3])) {
                                    i += 4;
                                } else {
                                    System.out.println("Error en la condición JOIN.");
                                    return;
                                }
                            } else if (containsToken(identificador, tokens[i])) {
                                i++;
                                if (tokens[i].equals("=") && containsToken(identificador, tokens[i + 1]) && tokens[i + 2].equals(".") && containsToken(identificador, tokens[i + 3])) {
                                    i += 4;
                                } else {
                                    System.out.println("Error en la condición JOIN.");
                                    return;
                                }
                            } else {
                                System.out.println("Error en los identificadores del JOIN.");
                                return;
                            }
                        } else {
                            System.out.println("Error: Se esperaba 'ON' en JOIN.");
                            return;
                        }
                    }
                }
            } else if (tokens[i].equals("WHERE")) {
                i++;
                int[] iRef = {i};
                if (!procesarCondicionWhere(tokens, iRef, identificador, signos, racionales, enteros, decimales, booleanos, fecha)) {
                    return;
                }
                i = iRef[0]; // Actualizar el valor de i después de procesar
            } else if (tokens[i].equals("GROUP") && tokens[i + 1].equals("BY")) {
                i += 2;
                if (containsToken(identificador, tokens[i])) {
                    i++;
                    if (tokens[i].equals(".")) {
                        i++;
                        if (containsToken(identificador, tokens[i])) {
                            i++;
                        } else {
                            System.out.println("Error: Identificador compuesto no válido en GROUP BY.");
                            return;
                        }
                    }
                }
            } else if (tokens[i].equals("ORDER") && tokens[i + 1].equals("BY")) {
                i += 2;
                if (containsToken(identificador, tokens[i])) {
                    i++;
                    if (tokens[i].equals(".")) {
                        i++;
                        if (containsToken(identificador, tokens[i])) {
                            i++;
                        }
                    }
                    if (i < tokens.length && (tokens[i].equals("DESC") || tokens[i].equals("ASC"))) {
                        i++;
                    }
                }
            } else if (tokens[i].equals("LIMIT")) {
                i++;
                if (containsToken(enteros, tokens[i])) {
                    i++;
                } else {
                    System.out.println("Error: Valor de LIMIT no válido.");
                    return;
                }
            } else {
                System.out.println("Error: Estructura de SELECT no válida.");
                return;
            }
        }

        System.out.println("La estructura SELECT es válida.");
    }

    private boolean procesarCondicionWhere(String[] tokens, int[] iRef, List<Token> identificador, List<Token> signos,
            List<Token> racionales, List<Token> enteros, List<Token> decimales, List<Token> booleanos, List<Token> fecha) {

        while (iRef[0] < tokens.length && !tokens[iRef[0]].equals(";")
                && !tokens[iRef[0]].equals("LIMIT") && !tokens[iRef[0]].equals("JOIN")
                && !tokens[iRef[0]].equals("GROUP") && !tokens[iRef[0]].equals("ORDER")) {

            if (tokens[iRef[0]].equals("(")) {
                iRef[0]++;
                if (!procesarCondicionWhere(tokens, iRef, identificador, signos, racionales, enteros, decimales, booleanos, fecha)) {
                    return false;
                }
                if (iRef[0] < tokens.length && tokens[iRef[0]].equals(")")) {
                    iRef[0]++;
                } else {
                    System.out.println("Error: Paréntesis de cierre faltante.");
                    return false;
                }

            } else {
                if (containsToken(identificador, tokens[iRef[0]])) {
                    iRef[0]++;
                    if (iRef[0] < tokens.length && tokens[iRef[0]].equals(".")) {
                        iRef[0]++;
                        if (iRef[0] < tokens.length && containsToken(identificador, tokens[iRef[0]])) {
                            iRef[0]++;
                        } else {
                            System.out.println("Error: Formato <identificador>.<identificador> no válido.");
                            return false;
                        }
                    }

                    // Permitir la estructura: <identificador> = [DATO]
                    if (iRef[0] < tokens.length && (tokens[iRef[0]].equals("=") || tokens[iRef[0]].equals(">")
                            || tokens[iRef[0]].equals("<") || tokens[iRef[0]].equals(">=")
                            || tokens[iRef[0]].equals("<=") || tokens[iRef[0]].equals("!="))) {
                        iRef[0]++;

                        if (iRef[0] < tokens.length) {
                            // Verificar si hay un identificador o estructura identificador.identificador
                            if (containsToken(identificador, tokens[iRef[0]])) {
                                iRef[0]++;
                                if (iRef[0] < tokens.length && tokens[iRef[0]].equals(".")) {
                                    iRef[0]++;
                                    if (iRef[0] < tokens.length && containsToken(identificador, tokens[iRef[0]])) {
                                        iRef[0]++;
                                    } else {
                                        System.out.println("Error: Formato <identificador>.<identificador> no válido.");
                                        return false;
                                    }
                                }
                            } else if (containsToken(enteros, tokens[iRef[0]]) || containsToken(decimales, tokens[iRef[0]])
                                    || containsToken(booleanos, tokens[iRef[0]]) || containsToken(fecha, tokens[iRef[0]])
                                    || tokens[iRef[0]].matches("'[^']*'")) {
                                iRef[0]++;
                            } else {
                                System.out.println("Error: Valor no válido tras operador.");
                                return false;
                            }
                        } else {
                            System.out.println("Error: Valor faltante tras operador.");
                            return false;
                        }
                    } else {
                        System.out.println("Error: Operador faltante o no válido tras identificador.");
                        return false;
                    }
                } else {
                    System.out.println("Error: Condición WHERE no válida.");
                    return false;
                }
            }

            // Verificar si hay un operador lógico (AND/OR) para continuar procesando condiciones
            if (iRef[0] < tokens.length && (tokens[iRef[0]].equals("AND") || tokens[iRef[0]].equals("OR"))) {
                iRef[0]++;
                // Verificar la estructura tras el AND/OR; puede ser identificador o identificador.operador.valor
                if (iRef[0] < tokens.length && containsToken(identificador, tokens[iRef[0]])) {
                    iRef[0]++;
                    if (iRef[0] < tokens.length && tokens[iRef[0]].equals(".")) {
                        iRef[0]++;
                        if (iRef[0] < tokens.length && containsToken(identificador, tokens[iRef[0]])) {
                            iRef[0]++;
                        } else {
                            System.out.println("Error: Formato <identificador>.<identificador> no válido.");
                            return false;
                        }
                    }

                    // Permitir operador y valor tras <identificador>.<identificador> o <identificador>
                    if (iRef[0] < tokens.length && (tokens[iRef[0]].equals("=") || tokens[iRef[0]].equals(">")
                            || tokens[iRef[0]].equals("<") || tokens[iRef[0]].equals(">=")
                            || tokens[iRef[0]].equals("<=") || tokens[iRef[0]].equals("!="))) {
                        iRef[0]++;
                        // Verificar si el valor tras el operador es identificador o identificador.identificador
                        if (iRef[0] < tokens.length && containsToken(identificador, tokens[iRef[0]])) {
                            iRef[0]++;
                            if (iRef[0] < tokens.length && tokens[iRef[0]].equals(".")) {
                                iRef[0]++;
                                if (iRef[0] < tokens.length && containsToken(identificador, tokens[iRef[0]])) {
                                    iRef[0]++;
                                } else {
                                    System.out.println("Error: Formato <identificador>.<identificador> no válido.");
                                    return false;
                                }
                            }
                        } else if (iRef[0] < tokens.length && (containsToken(enteros, tokens[iRef[0]])
                                || containsToken(decimales, tokens[iRef[0]]) || containsToken(booleanos, tokens[iRef[0]])
                                || containsToken(fecha, tokens[iRef[0]]) || tokens[iRef[0]].matches("'[^']*'"))) {
                            iRef[0]++;
                        } else {
                            System.out.println("Error: Valor no válido tras operador.");
                            return false;
                        }
                    } else {
                        System.out.println("Error: Operador faltante o no válido tras identificador.");
                        return false;
                    }
                } else {
                    System.out.println("Error: Estructura no válida tras AND/OR.");
                    return false;
                }
            } else {
                break;
            }
        }
        return true;
    }

    private void estructuraUpdate(String texto, List<Token> create, List<Token> identificador, List<Token> signos,
            List<Token> racionales, List<Token> enteros, List<Token> booleanos,
            List<Token> decimales, List<Token> fecha, List<Token> aritmeticos) {
        texto = texto.trim();

        // Dividir el texto en tokens utilizando un patrón de separación adecuado, ignorando espacios
        String[] tokens = texto.split(" (?=(?:[^\"']|\"[^\"]*\"|'[^']*')*$)|(?=;)|(?<=;)|(?=\\.)|(?<=\\.)|(?=,)|(?=\\()|(?=\\))|(?<=\\()|(?<=\\))");
        // Unir tokens decimales en caso de que estén separados
        List<String> mergedTokens = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            if (i < tokens.length - 2 && tokens[i].matches("[0-9]+") && tokens[i + 1].equals(".") && tokens[i + 2].matches("[0-9]+")) {
                mergedTokens.add(tokens[i] + tokens[i + 1] + tokens[i + 2]);
                i += 2; // Saltar los tokens ya unidos
            } else {
                mergedTokens.add(tokens[i]);
            }
        }
        tokens = mergedTokens.toArray(new String[0]);

        // Validar la estructura básica de UPDATE
        if (tokens.length < 4 || !tokens[0].equals("UPDATE") || !containsToken(identificador, tokens[1]) || !tokens[2].equals("SET")) {
            System.out.println("Error: Estructura UPDATE no válida.");
            return;
        }

        int i = 3; // Índice después de "UPDATE <identificador> SET"
        boolean tieneCondicionWhere = false;

        // Procesar las asignaciones en SET
        while (i < tokens.length && !tokens[i].equals(";")) {
            // Verificar que haya un identificador
            if (!containsToken(identificador, tokens[i])) {
                System.out.println("Error: Se esperaba un identificador después de SET.");
                return;
            }

            String id = tokens[i];
            i++; // Avanzar después del identificador

            // Verificar si hay un operador de asignación
            if (i < tokens.length && tokens[i].equals("=")) {
                i++; // Avanzar después del '='
            } else {
                System.out.println("Error: Faltando '=' después del identificador.");
                return;
            }

            // Verificar el dato asignado
            if (i < tokens.length && (containsToken(enteros, tokens[i]) || isDecimal(tokens[i]) || containsToken(create, tokens[i]) || tokens[i].matches("'[^']*'") || tokens[i].equals("TRUE") || tokens[i].equals("FALSE"))) {
                // Aceptar el dato y avanzar
                i++; // Avanzar después del dato
            } else if (i < tokens.length && containsToken(identificador, tokens[i])) {
                // Si el dato es otro identificador, puede haber una operación aritmética
                String operandoIzquierdo = tokens[i];
                i++; // Avanzar después del identificador

                if (i < tokens.length && containsToken(aritmeticos, tokens[i])) {
                    String operadorAritmetico = tokens[i];
                    i++; // Avanzar después del operador

                    // Verificar el operando derecho
                    if (i < tokens.length && (containsToken(enteros, tokens[i]) || isDecimal(tokens[i]) || containsToken(create, tokens[i]) || tokens[i].matches("'[^']*'") || tokens[i].equals("TRUE") || tokens[i].equals("FALSE"))) {
                        i++; // Avanzar después del dato derecho
                    } else {
                        System.out.println("Error: Dato derecho no válido en la operación.");
                        return;
                    }
                } else {
                    System.out.println("Error: Operador aritmético faltante.");
                    return;
                }
            } else {
                System.out.println("Error: Dato no válido después de '='.");
                return;
            }

            // Verificar si hay una coma para continuar con la siguiente asignación
            if (i < tokens.length && tokens[i].equals(",")) {
                i++; // Avanzar después de la coma
            } else {
                break; // No hay más asignaciones
            }
        }

        // Verificar si hay una cláusula WHERE
        if (i < tokens.length && tokens[i].equals("WHERE")) {
            tieneCondicionWhere = true;
            i++;
            int[] iRef = {i};
            if (!procesarCondicionWhere(tokens, iRef, identificador, signos, racionales, enteros, decimales, booleanos, fecha)) {
                return;
            }
            i = iRef[0];
        }

        // Validar punto y coma final
        if (i < tokens.length && tokens[i].equals(";")) {
            if (tieneCondicionWhere) {
                System.out.println("Estructura válida: UPDATE con cláusula WHERE.");
            } else {
                System.out.println("Estructura válida: UPDATE sin cláusula WHERE.");
            }
        } else {
            System.out.println("Error: Estructura de UPDATE incompleta.");
        }
    }

}
