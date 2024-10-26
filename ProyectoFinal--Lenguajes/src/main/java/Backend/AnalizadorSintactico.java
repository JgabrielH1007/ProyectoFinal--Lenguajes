/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import java.util.List;

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

}
