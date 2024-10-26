/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

/**
 *
 * @author gabrielh
 */
public class Token {
    private String texto;
    private int line;
    private int column;
    private String tipo;
    private int inicio;
    
    public Token(String texto, int line, int column, String tipo, int inicio) {
        this.texto = texto;
        this.line = line;
        this.column = column;
        this.tipo = tipo;
        this.inicio = inicio;
    }

    public String getTexto() {
        return texto;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getTipo() {
        return tipo;
    }

    public int getInicio() {
        return inicio;
    }
    
    @Override
    public String toString() {
        return "Token: " + texto + ", Linea: " + line + ", Columna: " + column;
    }

}