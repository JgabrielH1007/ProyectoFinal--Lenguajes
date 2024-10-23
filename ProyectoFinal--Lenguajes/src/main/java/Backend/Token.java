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
    private String token;
    private int line;
    private int column;
    private String tipo;

    public Token(String token, int line, int column, String tipo) {
        this.token = token;
        this.line = line;
        this.column = column;
        this.tipo = tipo;
    }

    public String getToken() {
        return token;
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
    
    

    @Override
    public String toString() {
        return "Token: " + token + ", Linea: " + line + ", Columna: " + column;
    }
}