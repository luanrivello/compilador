package token;

public class Tabela{
    //Erros
    public static int ERROR_NOT_FOUND = -404;
    public static int CARACTER_INVALIDO = -101;
    public static int FLOAT_MAL_FORMADO = -202;
    public static int CHAR_MAL_FORMADO = -303;
    public static int EXACLAMACAO_SOZINHA = -111;
    public static int EOF_EM_COMENTARIO = -669;

    //Tipos De Dados
    public static int INT = 101;
    public static int FLOAT = 102;
    public static int CHAR = 103;

    //Identificador
    public static int IDENTIFICADOR = 201;

    //Comparadores
    public static int MENOR = 302;
    public static int MAIOR = 303;
    public static int MENOREQ = 304;
    public static int MAIOREQ = 305;
    public static int IGUAL = 306;
    public static int DIF = 307;

    //Operadores
    public static int SOMA = 401;
    public static int SUB = 402;
    public static int MULT = 403;
    public static int DIV = 404;
    public static int ATRIBUIDOR = 405;

    //Especiais
    public static int APARENTESES = 501;
    public static int FPARENTESES = 502;
    public static int ACHAVES = 503;
    public static int FCHAVES = 504;
    public static int VIRGULA = 505;
    public static int PONTOVIRGULA = 506;
    public static int EOF = 507;

    //Reservados
    public static int MAIN = 1001;
    public static int IF = 1002;
    public static int ELSE = 1003;
    public static int WHILE = 1004;
    public static int DO = 1005;
    public static int FOR = 1006;
    public static int DCINT = 1007;
    public static int DCFLOAT = 1008;
    public static int DCCHAR = 1009;

    //Grupos
    public static int TIPOS = 100;
    public static int COMPARADORES = 300;
    public static int OPERADORES = 400;
    public static int ESPECIAIS = 500;
    public static int RESERVADAS = 1000;

}