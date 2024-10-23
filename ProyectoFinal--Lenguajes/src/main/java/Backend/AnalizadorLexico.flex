package Backend;

import java.util.ArrayList;
import java.util.List;

%%
%{
private List<Token> listaCreate = new ArrayList<>();
private List<Token> listaTipoDato = new ArrayList<>();
private List<Token> listaEntero = new ArrayList<>();
private List<Token> listaDecimal = new ArrayList<>();
private List<Token> listaFecha = new ArrayList<>();
private List<Token> listaCadena = new ArrayList<>();
private List<Token> listaIdentificador = new ArrayList<>();
private List<Token> listaBooleano = new ArrayList<>();
private List<Token> listaAgregacion = new ArrayList<>();
private List<Token> listaSignos = new ArrayList<>();
private List<Token> listaAritmeticos = new ArrayList<>();
private List<Token> listaRacionales = new ArrayList<>();
private List<Token> listaLogicos = new ArrayList<>();
private List<Token> listaComentarios = new ArrayList<>();
private List<Token> listaErrores = new ArrayList<>();


public void addListCreate(String token) {
        listaCreate.add(new Token(token, yyline, yycolumn,"create"));
}
public void addListTipoDato(String token) {
        listaTipoDato.add(new Token(token, yyline, yycolumn,"TipoDato"));
}

public void addListEntero(String token) {
        listaEntero.add(new Token(token, yyline, yycolumn,"Entero"));
}

public void addListDecimal(String token){
        listaDecimal.add(new Token(token, yyline, yycolumn,"Decimal"));
}

public void addListFecha(String token){
        listaFecha.add(new Token(token, yyline, yycolumn,"Fecha"));
}

public void addListCadena(String token){
        listaCadena.add(new Token(token, yyline, yycolumn,"Cadena"));
}

public void addListIdentificador(String token){
        listaIdentificador.add(new Token(token, yyline, yycolumn,"Identificador"));
}

public void addListBooleano(String token){
        listaBooleano.add(new Token(token, yyline, yycolumn,"Booleano"));
}

public void addListAgregacion(String token){
        listaAgregacion.add(new Token(token, yyline, yycolumn,"Agregacion"));
}

public void addListSignos(String token){
        listaSignos.add(new Token(token, yyline, yycolumn,"Signos"));
}

public void addListAritmeticos(String token){
        listaAritmeticos.add(new Token(token, yyline, yycolumn,"Aritmeticos"));
}

public void addListRacionales(String token){
        listaRacionales.add(new Token(token, yyline, yycolumn,"Racionales"));
}

public void addListLogicos(String token){
        listaLogicos.add(new Token(token, yyline, yycolumn,"Logicos"));
}

public void addListComentario(String token){
        listaComentarios.add(new Token(token, yyline, yycolumn,"Comentario"));
}

public void addListaErrores(String token){
        listaErrores.add(new Token(token, yyline, yycolumn,"Error"));
}

public List<Token> getListaCreate() {
    return listaCreate;
}

public List<Token> getListaTipoDato() {
    return listaTipoDato;
}

public List<Token> getListaEntero() {
    return listaEntero;
}

public List<Token> getListaDecimal() {
    return listaDecimal;
}

public List<Token> getListaFecha() {
    return listaFecha;
}

public List<Token> getListaCadena() {
    return listaCadena;
}

public List<Token> getListaIdentificador() {
    return listaIdentificador;
}

public List<Token> getListaBooleano() {
    return listaBooleano;
}

public List<Token> getListaAgregacion() {
    return listaAgregacion;
}

public List<Token> getListaSignos() {
    return listaSignos;
}

public List<Token> getListaAritmeticos() {
    return listaAritmeticos;
}

public List<Token> getListaRacionales() {
    return listaRacionales;
}

public List<Token> getListaLogicos() {
    return listaLogicos;
}

public List<Token> getListaComentarios() {
    return listaComentarios;
}

public List<Token> getListaErrores() {
    return listaErrores;
}

public int getLinea() {
        return yyline;
}

public int getColumna() {
        return yycolumn;
}

%}

%public
%class AnalizadorLexico
%unicode
%line
%column
%standalone

ENTERO = [0-9]+
ESPACIO = [ \r\t\n]+
FECHA = \'(\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])\'
IDENTIFICADOR = [a-z0-9]+(_[a-z0-9]+)*
CADENA = \'[^\']*\' 
COMENTARIO = "--" [\t\n\r\f ]+ [^\n\r]*
ESPACIOS = [" "\r\t\b\n]

%%
{ESPACIOS}          { /* Ignore */ }
{ENTERO}            { addListEntero(yytext()); System.out.println("ENTERO: " + yytext()); }
{ENTERO}.{ENTERO}   { addListDecimal(yytext()); System.out.println("DECIMAL: " + yytext()); }
{FECHA}             { addListFecha(yytext()); System.out.println("FECHA: " + yytext()); }
{CADENA}            { addListCadena(yytext()); System.out.println("CADENA: " + yytext()); }
{COMENTARIO}        { addListComentario(yytext()); System.out.println("COMENTARIO: " + yytext()); }
{IDENTIFICADOR}     {addListIdentificador(yytext()); System.out.println("IDENTIFICADOR: "+yytext())}    
"CREATE"            { addListCreate(yytext()); System.out.println("CREATE: " + yytext()); }
"DATABASE"          { addListCreate(yytext()); System.out.println("DATABASE: " + yytext()); }
"TABLE"             { addListCreate(yytext()); System.out.println("TABLE: " + yytext()); }
"KEY"               { addListCreate(yytext()); System.out.println("KEY: " + yytext()); }
"NULL"              { addListCreate(yytext()); System.out.println("NULL: " + yytext()); }
"PRIMARY"           { addListCreate(yytext()); System.out.println("PRIMARY: " + yytext()); }
"UNIQUE"            { addListCreate(yytext()); System.out.println("UNIQUE: " + yytext()); }
"FOREIGN"           { addListCreate(yytext()); System.out.println("FOREIGN: " + yytext()); }
"REFERENCES"        { addListCreate(yytext()); System.out.println("REFERENCES: " + yytext()); }
"ALTER"             { addListCreate(yytext()); System.out.println("ALTER: " + yytext()); }
"ADD"               { addListCreate(yytext()); System.out.println("ADD: " + yytext()); }
"COLUMN"            { addListCreate(yytext()); System.out.println("COLUMN: " + yytext()); }
"TYPE"              { addListCreate(yytext()); System.out.println("TYPE: " + yytext()); }
"DROP"              { addListCreate(yytext()); System.out.println("DROP: " + yytext()); }
"CONSTRAINT"        { addListCreate(yytext()); System.out.println("CONSTRAINT: " + yytext()); }
"IF"                { addListCreate(yytext()); System.out.println("IF: " + yytext()); }
"EXIST"             { addListCreate(yytext()); System.out.println("EXIST: " + yytext()); }
"CASCADE"           { addListCreate(yytext()); System.out.println("CASCADE: " + yytext()); }
"ON"                { addListCreate(yytext()); System.out.println("ON: " + yytext()); }
"DELETE"            { addListCreate(yytext()); System.out.println("DELETE: " + yytext()); }
"SET"               { addListCreate(yytext()); System.out.println("SET: " + yytext()); }
"UPDATE"            { addListCreate(yytext()); System.out.println("UPDATE: " + yytext()); }
"INSERT"            { addListCreate(yytext()); System.out.println("INSERT: " + yytext()); }
"INTO"              { addListCreate(yytext()); System.out.println("INTO: " + yytext()); }
"VALUES"            { addListCreate(yytext()); System.out.println("VALUES: " + yytext()); }
"SELECT"            { addListCreate(yytext()); System.out.println("SELECT: " + yytext()); }
"FROM"              { addListCreate(yytext()); System.out.println("FROM: " + yytext()); }
"WHERE"             { addListCreate(yytext()); System.out.println("WHERE: " + yytext()); }
"AS"                { addListCreate(yytext()); System.out.println("AS: " + yytext()); }
"GROUP"             { addListCreate(yytext()); System.out.println("GROUP: " + yytext()); }
"ORDER"             { addListCreate(yytext()); System.out.println("ORDER: " + yytext()); }
"BY"                { addListCreate(yytext()); System.out.println("BY: " + yytext()); }
"ASC"               { addListCreate(yytext()); System.out.println("ASC: " + yytext()); }
"DESC"              { addListCreate(yytext()); System.out.println("DESC: " + yytext()); }
"LIMIT"             { addListCreate(yytext()); System.out.println("LIMIT: " + yytext()); }
"JOIN"              { addListCreate(yytext()); System.out.println("JOIN: " + yytext()); }
"INTEGER"           { addListTipoDato(yytext()); System.out.println("INTEGER: " + yytext()); }
"BIGINT"            { addListTipoDato(yytext()); System.out.println("BIGINT: " + yytext()); }
"VARCHAR"           { addListTipoDato(yytext()); System.out.println("VARCHAR: " + yytext()); }
"DECIMAL"           { addListTipoDato(yytext()); System.out.println("DECIMAL: " + yytext()); }
"DATE"              { addListTipoDato(yytext()); System.out.println("DATE: " + yytext()); }
"TEXT"              { addListTipoDato(yytext()); System.out.println("TEXT: " + yytext()); }
"BOOLEAN"           { addListTipoDato(yytext()); System.out.println("BOOLEAN: " + yytext()); }
"SERIAL"            { addListTipoDato(yytext()); System.out.println("SERIAL: " + yytext()); }
"TRUE"              { addListBooleano(yytext()); System.out.println("TRUE: " + yytext()); }
"FALSE"             { addListBooleano(yytext()); System.out.println("FALSE: " + yytext()); }
"SUM"               { addListAgregacion(yytext()); System.out.println("SUM: " + yytext()); }
"AVG"               { addListAgregacion(yytext()); System.out.println("AVG: " + yytext()); }
"COUNT"             { addListAgregacion(yytext()); System.out.println("COUNT: " + yytext()); }
"MAX"               { addListAgregacion(yytext()); System.out.println("MAX: " + yytext()); }
"MIN"               { addListAgregacion(yytext()); System.out.println("MIN: " + yytext()); }
"("                 { addListSignos(yytext()); System.out.println("SIGNO: " + yytext()); }
")"                 { addListSignos(yytext()); System.out.println("SIGNO: " + yytext()); }
","                 { addListSignos(yytext()); System.out.println("SIGNO: " + yytext()); }
";"                 { addListSignos(yytext()); System.out.println("SIGNO: " + yytext()); }
"."                 { addListSignos(yytext()); System.out.println("SIGNO: " + yytext()); }
"="                 { addListSignos(yytext()); System.out.println("SIGNO: " + yytext()); }
"+"                 { addListAritmeticos(yytext()); System.out.println("ARITMETICO: " + yytext()); }
"-"                 { addListAritmeticos(yytext()); System.out.println("ARITMETICO: " + yytext()); }
"*"                 { addListAritmeticos(yytext()); System.out.println("ARITMETICO: " + yytext()); }
"/"                 { addListAritmeticos(yytext()); System.out.println("ARITMETICO: " + yytext()); }
"<"                 { addListRacionales(yytext()); System.out.println("RACIONAL: " + yytext()); }
">"                 { addListRacionales(yytext()); System.out.println("RACIONAL: " + yytext()); }
"<="                { addListRacionales(yytext()); System.out.println("RACIONAL: " + yytext()); }
">="                { addListRacionales(yytext()); System.out.println("RACIONAL: " + yytext()); }
"AND"               { addListLogicos(yytext()); System.out.println("LOGICO: " + yytext()); }
"OR"                { addListLogicos(yytext()); System.out.println("LOGICO: " + yytext()); }
"NOT"               { addListLogicos(yytext()); System.out.println("LOGICO: " + yytext()); }
.                   {System.out.println("Error:"+yytext());
                     addListaErrores("ERROR>> Linea: "+yyline + ", columna: "+ yycolumn+", Token -> "+yytext()); }
