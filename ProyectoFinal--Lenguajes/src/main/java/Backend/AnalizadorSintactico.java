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

    public void estructuraDataBase(String texto, List<Token> create, List<Token> identificador, List<Token> signos) {
        String cleanedTexto = texto.replaceAll("\\s*;\\s*", " ; ");

        String[] tokens = cleanedTexto.split("\\s+");

        for (int i = 0; i < tokens.length - 3; i++) {
            // Verificar que los tokens actuales formen la estructura deseada
            if (tokens[i].equals("CREATE") && containsToken(create, "CREATE")
                    && tokens[i + 1].equals("DATABASE") && containsToken(create, "DATABASE")
                    && containsToken(identificador, tokens[i + 2])) {

                // Verificar si el token siguiente es el punto y coma, que ahora siempre será un token separado
                if (tokens[i + 3].equals(";") && containsToken(signos, ";")) {
                    System.out.println("Estructura válida encontrada: CREATE DATABASE " + tokens[i + 2] + " ;");
                    return;
                }
            }
        }

        System.out.println("Error: No se encontró una estructura válida de 'CREATE DATABASE <identificador> ;'");
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

    public void estructuraTablaDesdeTexto(String texto, List<Token> create, List<Token> identificador, List<Token> tipoDato, List<Token> signos, List<Token> enteros) {
        String[] lineas = texto.split("\n");
        StringBuilder consultaCompleta = new StringBuilder();
        boolean comenzandoEstructura = false; // Variable para indicar si comenzamos a acumular

        for (String linea : lineas) {
            String lineaLimpiada = linea.trim(); // Limpiar espacios

            if (lineaLimpiada.startsWith("CREATE TABLE")) {
                comenzandoEstructura = true; // Comenzar a acumular
            }

            if (comenzandoEstructura) {
                consultaCompleta.append(lineaLimpiada).append(" "); // Acumular línea

                // Detener la acumulación si la línea acumulada termina en ");"
                if (consultaCompleta.toString().trim().endsWith(");")) {
                    break; // Salir del bucle si termina con ");"
                }
            }
        }

        // Llamar al método existente con la consulta completa
        estructuraTabla(consultaCompleta.toString(), create, identificador, tipoDato, signos, enteros);
    }

    public void procesarEstructuras(String consulta, List<Token> create, List<Token> identificador, List<Token> tipoDato, List<Token> signos, List<Token> enteros,
            List<Token> aritmeticos, List<Token> logicos,
            List<Token> cadena, List<Token> fecha, List<Token> Decimal, List<Token> racional) {
        String[] lineas = consulta.split("\n");
        StringBuilder estructuraActual = new StringBuilder();
        boolean enEstructura = false;

        for (String linea : lineas) {
            String lineaLimpiada = linea.trim();

            // Detecta el inicio de una nueva estructura
            if (lineaLimpiada.startsWith("CREATE DATABASE") || lineaLimpiada.startsWith("CREATE TABLE")
                    || lineaLimpiada.startsWith("ALTER TABLE") || lineaLimpiada.startsWith("DROP TABLE")
                    || lineaLimpiada.startsWith("INSERT INTO")) { // Añadir INSERT INTO

                if (enEstructura) {
                    // Procesa la estructura anterior antes de comenzar una nueva
                    procesarEstructura(estructuraActual.toString(), create, identificador, tipoDato, signos, enteros, aritmeticos, logicos, cadena, fecha, Decimal, racional);
                    estructuraActual.setLength(0); // Limpia el acumulador para la siguiente estructura
                }
                enEstructura = true;
            }

            // Si estamos dentro de una estructura, acumular la línea
            if (enEstructura) {
                estructuraActual.append(lineaLimpiada).append(" ");

                // Detecta el final de una estructura ';' o ');'
                if (lineaLimpiada.endsWith(";") || lineaLimpiada.endsWith(");")) {
                    procesarEstructura(estructuraActual.toString(), create, identificador, tipoDato, signos, enteros, aritmeticos, logicos, cadena, fecha, Decimal, racional);
                    estructuraActual.setLength(0); // Limpia el acumulador para la siguiente estructura
                    enEstructura = false;
                }
            }
        }

        // Procesa cualquier estructura que haya quedado sin cerrar al finalizar el bucle
        if (enEstructura) {
            procesarEstructura(estructuraActual.toString(), create, identificador, tipoDato, signos, enteros, aritmeticos, logicos, cadena, fecha, Decimal, racional);
        }
    }

    public void procesarEstructura(String consulta, List<Token> create, List<Token> identificador, List<Token> tipoDato, List<Token> signos, List<Token> enteros,
            List<Token> aritmeticos, List<Token> logicos,
            List<Token> cadena, List<Token> fecha, List<Token> Decimal, List<Token> racional) {
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
        } else {
            System.out.println("Error: No se reconoció una estructura válida de 'CREATE DATABASE', 'CREATE TABLE', 'ALTER TABLE', 'DROP TABLE' o 'INSERT INTO'.");
        }
    }

    public void estructuraModificadores(String texto, List<Token> create, List<Token> identificador, List<Token> signos, List<Token> entero, List<Token> tipoDato) {
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
    
    private void estructuraEliminacion(String texto, List<Token> create, List<Token> identificador, List<Token> signos, List<Token> racionales, List<Token> enteros, 
                                      List<Token> booleanos, List<Token> decimales, List<Token> fecha){
        
    }
    
}
