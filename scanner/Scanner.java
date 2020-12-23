package scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import token.*;

public class Scanner {
    private static char aux = ' ';
    private static int col = 0;
    private static int lin = 1;

    private static ArrayList<String> reservadas;
    
    static{
        reservadas = new ArrayList<String>();

        reservadas.add("main");
        reservadas.add("if");
        reservadas.add("else");
        reservadas.add("while");
        reservadas.add("do");
        reservadas.add("for");
        reservadas.add("int");
        reservadas.add("float");
        reservadas.add("char");

    }

    public static Token scan(BufferedReader reader) throws IOException {
        String result;
        boolean comentario;

        do{
            result = "";
            comentario = false;

            //Pular BLANK
            //aux == ' ' || aux == '\t' || aux == '\n'
            while(Character.isWhitespace(aux)){

                aux = readWhite(reader);

            }

            //Inteiro ou Float
            if(Character.isDigit(aux) || aux == '.'){

                //Inteiro
                while(Character.isDigit(aux)){

                    result = result + aux;
                    aux = read(reader);

                };

                //Float
                if(aux == '.'){

                    result = result + aux;
                    aux = read(reader);

                    //Float mal formado
                    if(!Character.isDigit(aux)){
                        return new Token(Tabela.FLOAT_MAL_FORMADO, "Float mal formado");
                    }

                    while(Character.isDigit(aux)){

                        result = result + aux;
                        aux = read(reader);

                    }

                    return new Token(Tabela.FLOAT, result);

                }
                else{
                    return new Token(Tabela.INT, result);

                }

            }

            //Identificador ou Palavra Reservada
            else if(Character.isLetter(aux) || aux == '_'){

                do{
                    
                    result = result + aux;
                    aux = read(reader);

                }while(Character.isLetterOrDigit(aux) || aux == '_');

                
                if(reservadas.contains(result)){
                    return new Token(codReservada(result),result);
                }
                

                return new Token(Tabela.IDENTIFICADOR,result);

            }

            //Char
            else if(aux == '\''){

                result = result + aux;
                aux = read(reader);

                if(Character.isLetterOrDigit(aux)){

                    result = result + aux;
                    aux = read(reader);

                    if(aux == '\''){

                        result = result + aux;
                        aux = read(reader);

                        return new Token(Tabela.CHAR,result);

                    }

                }

                return new Token(Tabela.CHAR_MAL_FORMADO,"Char Mal Formado");
                
            }
            
            //Comentarios e Divisao
            else if(aux == '/'){

                result = result + aux;
                aux = read(reader);

                if(aux == '/'){

                    while(aux != '\n' && aux != 65535){

                        aux = (char) reader.read();

                    }

                    aux = (char) reader.read();

                    col = 0;
                    lin = lin + 1;

                    comentario = true;

                }

                else if(aux == '*'){

                    do{

                        aux = readWhite(reader);

                        while(aux == '*'){

                            aux = readWhite(reader);

                            if(aux == '/'){
                                aux = readWhite(reader);
                                comentario = true;
                            }

                        }

                        if(aux == 65535){
                            return new Token(Tabela.EOF_EM_COMENTARIO, "Fim De Arquivo Dentro De Comentario");
                        }

                    }while(!comentario);

                }
                
                else{
                    return new Token(Tabela.DIV,result);
                }

            }

            //Operadores
            //Soma
            else if(aux == '+'){

                result = result + aux;
                aux = read(reader);

                return new Token(Tabela.SOMA, result);

            }
            
            //Sub
            else if(aux == '-'){

                result = result + aux;
                aux = read(reader);

                return new Token(Tabela.SUB, result);

            }
            
            //Mult
            else if(aux == '*'){

                result = result + aux;
                aux = read(reader);

                return new Token(Tabela.MULT, result);
                
            }
            
            //Atribuidor ou Igual
            else if(aux == '='){

                result = result + aux;
                aux = read(reader);

                if(aux == '='){
                    result = result + aux;
                    aux = read(reader);
                    return new Token(Tabela.IGUAL, result);
                }else{
                    return new Token(Tabela.ATRIBUIDOR, result);
                }

            }

            //Comparadores
            else if(aux == '<'){

                result = result + aux;
                aux = read(reader);

                if(aux == '='){

                    result = result + aux;
                    aux = read(reader);

                    return new Token(Tabela.MENOREQ, result);

                }else{
                    
                    return new Token(Tabela.MENOR, result);

                }

            }

            else if(aux == '>'){

                result = result + aux;
                aux = read(reader);

                if(aux == '='){

                    result = result + aux;
                    aux = read(reader);

                    return new Token(Tabela.MAIOREQ, result);

                }else{
                    
                    return new Token(Tabela.MAIOR, result);

                }

            }

            else if(aux == '!'){

                result = result + aux;
                aux = read(reader);

                if(aux == '='){
                    result = result + aux;
                    aux = read(reader);

                    return new Token(Tabela.DIF, result);

                }else{

                    return new Token(Tabela.EXACLAMACAO_SOZINHA, "Exclamação Sozinha");

                }

            }

            //Especiais
            else if(aux == '('){

                result = result + aux;
                aux = read(reader);

                return new Token(Tabela.APARENTESES, result);

            }

            else if(aux == ')'){

                result = result + aux;
                aux = read(reader);

                return new Token(Tabela.FPARENTESES, result);

            }

            else if(aux == '{'){

                result = result + aux;
                aux = read(reader);

                return new Token(Tabela.ACHAVES, result);

            }

            else if(aux == '}'){

                result = result + aux;
                aux = read(reader);

                return new Token(Tabela.FCHAVES, result);

            }

            else if(aux == ','){

                result = result + aux;
                aux = read(reader);

                return new Token(Tabela.VIRGULA, result);

            }

            else if(aux == ';'){

                result = result + aux;
                aux = read(reader);

                return new Token(Tabela.PONTOVIRGULA, result);

            }

            //Fim De Arquivo
            else if(aux == 65535){
                lin = 1;
                col = 0;
                aux = ' ';
                return new Token(Tabela.EOF, "EOF");

            }

            //Caracter Invalido
            else{

                aux = read(reader);
                return new Token(Tabela.CARACTER_INVALIDO, "Character Invalido");

            }
        
        }while(comentario);

        return new Token(Tabela.ERROR_NOT_FOUND, result);

    }

    private static char readWhite(BufferedReader bf) throws IOException {
        if(aux == '\n'){
            lin = lin + 1;
            col = 0;
        
        }else if(aux == '\t'){
            col = col + 4;
    
        }else{
            col = col + 1;
        }
    
        return (char)bf.read();
    }
    
    private static char read(BufferedReader bf) throws IOException {
        col = col + 1;
        return (char) bf.read();
    }

    public static int getCol(){
        return col;
    }

    public static int getLin(){
        return lin;
    }

    private static int codReservada(String palavra){

        if(palavra.equals("main")){
            return Tabela.MAIN;

        }else if(palavra.equals("if")){
            return Tabela.IF;
            
        }else if(palavra.equals("else")){
            return Tabela.ELSE;
            
        }else if(palavra.equals("while")){
            return Tabela.WHILE;
            
        }else if(palavra.equals("do")){
            return Tabela.DO;
            
        }else if(palavra.equals("for")){
            return Tabela.FOR;
            
        }else if(palavra.equals("int")){
            return Tabela.DCINT;
            
        }else if(palavra.equals("float")){
            return Tabela.DCFLOAT;
            
        }else if(palavra.equals("char")){
            return Tabela.DCCHAR;
            
        }else{
            return -1;
        }

    }

}