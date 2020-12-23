package parser;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Stack;

import scanner.Scanner;
import token.*;

public class Parser {

    private static String errorMsg = "404 error not found";

    //Parser
    private static Token token = null;
    private static boolean errorToken = false;
    private static BufferedReader reader;

    //Semantico
    private static ArrayList<String> variaveis = new ArrayList<String>();
    private static ArrayList<Integer> tipos = new ArrayList<Integer>();
    private static ArrayList<Integer> deep = new ArrayList<Integer>();
    private static int seaLevel = 0;
    private static Stack<Integer> tipoAnterior = new Stack<Integer>();

    //Codigo Intermediario
    private static String codInter = "";
    private static ArrayList<Formatador> formatadores = new ArrayList<Formatador>();
    private static int formAtual = 0;
    private static boolean isFloat = false;

    //RETORNOS
    //-1 = erro de sintaxes
    //0 = nao possui sintaxe
    //1 = sintaxe declarada corretamente

    public static boolean parsear(BufferedReader input) throws Exception{
        
        reader = input;
        formatadores.add(new Formatador());

        //<programa>
        boolean aux = programa();

        //nao pode ter token depois de |int main"("")" <bloco>|
        if(aux){

            read();

            if(token.getCode() == Tabela.EOF){
                return true;
            }

        }

        return false;

    }

    private static void read() throws Exception{
        
        try {
            token = Scanner.scan(reader);
            
            //System.out.println("Token: " + token.nome());
            //System.out.println("Code: " + token.getCode() + "\n");

            if(token.getCode() < 0){
    
                errorToken = true;
                throw new Exception();
    
            }

        } catch (Exception e) {

            if(token.getCode() < 0){
    
                errorToken = true;
                throw new Exception();
    
            }
            
            errorToken = true;
            token = new Token(-1,"Token Exception");
        }   

    }

    private static boolean programa() throws Exception{

        read();

        //int
        if(token.getCode() == Tabela.DCINT){

            read();

            //main
            if(token.getCode() == Tabela.MAIN){

                read();

                // (
                if(token.getCode() == Tabela.APARENTESES){

                    read();

                    // )
                    if(token.getCode() == Tabela.FPARENTESES){

                        //<bloco>
                        read();
                        return bloco() == 1;

                    }

                }

            }

        }

        return false;

    }

    private static int bloco() throws Exception{

        // {
        if(token.getCode() == Tabela.ACHAVES){

            read();

            int aux;

            while(true){

                //<decl_var>*
                do{

                    //<decl_var>
                    aux = declVar();

                    if(aux == 1){
                        read();
                    
                    }else if(aux == -1){
                        return -1;

                    }
                    
                }while(aux == 1);

                //<comando>*
                do{
                    
                    //<comando>
                    aux = comando();

                    if(aux == 1){
                        read();
                    
                    }else{

                        //}
                        if(token.getCode() == Tabela.FCHAVES){
                            return 1;
        
                        }else if(aux == -1){
                            return -1;
                        }
                        
                    }
                    
                }while(aux >= 1);
            }

        }

        return 0;

    }

    private static int declVar() throws Exception{

        //int | float | char
        if(token.getCode() == Tabela.DCINT || token.getCode() == Tabela.DCFLOAT ||token.getCode() == Tabela.DCCHAR){

            int auxTipo = token.getCode();

            //Int
            if(auxTipo == 1007){
                auxTipo = 101;

            //Float
            }else if(auxTipo == 1008){
                auxTipo = 102;

            //Char
            }else{
                auxTipo = 103;

            }

            read();

            //<id> 
            if(token.getCode() != Tabela.IDENTIFICADOR){
                errorMsg = "Erro de sintaxe";
                return -1;
            
            }
               
            //Salvar tipo 
            if(variaveis.contains(token.nome()) && deep.get(variaveis.indexOf(token.nome()))== seaLevel){
                errorMsg = "Variaveis de nome repetido";
                return -1;
                
            }else{
                variaveis.add(token.nome());
                tipos.add(auxTipo);
                deep.add(seaLevel);
            }
            

            read();

            //{,<id>}*;
            while(token.getCode() != Tabela.PONTOVIRGULA){

                if(token.getCode() == Tabela.VIRGULA){

                    read();
                
                }else{
                    errorMsg = "Erro de sintaxe";
                    return -1;

                }

                //Salvar tipo
                if(token.getCode() == Tabela.IDENTIFICADOR){

                    if(variaveis.contains(token.nome()) && deep.get(variaveis.indexOf(token.nome()))== seaLevel){
                        errorMsg = "Variaveis de nome repetido";
                        return -1;
                        
                    }

                    variaveis.add(token.nome());
                    tipos.add(auxTipo);
                    deep.add(seaLevel);

                    read();

                }else{
                    errorMsg = "Erro de sintaxe";
                    return -1;

                }

            }

            return 1;

        }

        return 0;

    }

    private static int comando() throws Exception{

        int aux;

        //<comando_básico>
        aux = comandoBasico();

        if(aux == 1 || aux == -1){
            return aux;

        }else{
            
            //<iteração>
            aux = iteracao();

            if(aux == 1 || aux == -1){
                return aux;
    
            }else{

                //if
                if(token.getCode() == Tabela.IF){
                    formatadores.get(formAtual).getCodAux().add("if ");

                    read();

                    // (
                    if(token.getCode() == Tabela.APARENTESES){

                        read();

                        //<expr_relacional>
                        aux = exprRelacional(true,true); 

                        if(aux > 1){
                            
                            // )
                            if(token.getCode() == Tabela.FPARENTESES){
                                int labFim = Formatador.getLabel();
                                Formatador.setLabel(Formatador.getLabel() + 2);
                                formatadores.get(formAtual).getCodAux().add(" go to label" + (labFim));
                                formatadores.get(formAtual).getCodAux().add("\n");
                                codInterExprFormatar(formatadores.get(formAtual));

                                read();

                                //<comando>
                                aux = comando();
                                
                                if(aux == 1){


                                    read();

                                    //else
                                    if(token.getCode() == Tabela.ELSE){

                                        codInter = codInter + "\n\ngo to label" + (labFim + 1);
                                        codInter = codInter + "\n\nlabel" + labFim;

                                        read();

                                        //<comando>
                                        aux = comando();

                                        codInter = codInter + "\n\nlabel" + (labFim + 1);

                                        if(aux == 1){
                                            return 1;
                                        
                                        }else{
                                            return -1;
                                        }

                                    }else{

                                        codInter = codInter + "\n\nlabel" + labFim;
                                        return 2;
                                    }

                                }else{
                                    return -1;
                                }

                            }else{
                                errorMsg = "Erro de sintaxe";
                                return -1;    
                            } 

                        }else{
                            return -1;
                        }

                    }else{
                        errorMsg = "Erro de sintaxe";
                        return -1;
                    }

                }

            }

        }

        return 0;

    }

    private static int comandoBasico() throws Exception{
        
        int aux;

        aux = atribuicao();

        if(aux == 1 || aux == -1){
            return aux;

        }else{

            //Descer
            seaLevel = seaLevel + 1;
            aux = bloco();
            
            //Desdeclarar variaveis
            int index;
            while(deep.contains(seaLevel)){
                index = deep.indexOf(seaLevel);
                
                variaveis.remove(index);
                tipos.remove(index);
                deep.remove(index);

            }

            //Subir
            seaLevel = seaLevel -1;

            if(aux == 1 || aux == -1){
                return aux;
    
            }

        }

        return 0;

    }
    
    private static int iteracao() throws Exception{
        int aux;

        //while
        if(token.getCode() == Tabela.WHILE){
            
            read();

            // (
            if(token.getCode() == Tabela.APARENTESES){
                int labFim = Formatador.getLabel();
                Formatador.setLabel(Formatador.getLabel() + 2);
                formatadores.get(formAtual).getCodAux().add("\nlabel" + labFim);
                formatadores.get(formAtual).getCodAux().add("\nif ");

                read();

                //<expr_relacional>
                aux = exprRelacional(true, true);
                
                formatadores.get(formAtual).getCodAux().add(" go to label" + (labFim + 1));
                codInterExprFormatar(formatadores.get(formAtual));

                if(aux > 1){
                    
                    // )
                    if(token.getCode() == Tabela.FPARENTESES){
                        
                        read();

                        //<comando>
                        aux = comando();
                        
                        formatadores.get(formAtual).getCodAux().add("\n");
                        codInterExprFormatar(formatadores.get(formAtual));

                        codInter = codInter + "go to label" + labFim;
                        codInter = codInter + "\nlabel" + (labFim + 1);
                        codInter = codInter + "\n";

                        if(aux == 1){
                            return 1;
                        }else{
                            return -1;
                        }

                    }else{
                        errorMsg = "Erro de sintaxe";
                        return -1;    
                    } 

                }else{
                    errorMsg = "Erro de sintaxe";
                    return -1;
                }

            }else{
                errorMsg = "Erro de sintaxe";
                return -1;
            }

        //Do
        }else if(token.getCode() == Tabela.DO){
            int labFim = Formatador.getLabel();
            Formatador.setLabel(Formatador.getLabel() + 1);
            codInter = codInter + "\nlabel" + labFim;

            read();

            //<comando>
            aux = comando();

            if(aux == 1){
                
                read();

                //while
                if(token.getCode() == Tabela.WHILE){
                    
                    read();

                    // (
                    if(token.getCode() == Tabela.APARENTESES){
                        formatadores.get(formAtual).getCodAux().add("if ");

                        read();

                        //<expr_relacional>
                        aux = exprRelacional(true,false);

                        formatadores.get(formAtual).getCodAux().add(" go to label" + labFim );
                        formatadores.get(formAtual).getCodAux().add("\n");
                        codInterExprFormatar(formatadores.get(formAtual));

                        if(aux > 1){
                            
                            // )
                            if(token.getCode() == Tabela.FPARENTESES){
                            
                                read();

                                // ;
                                if(token.getCode() == Tabela.PONTOVIRGULA){
                                    return 1;
                                }else{
                                    errorMsg = "Erro de sintaxe";
                                    return -1;
                                }

                            }else{
                                errorMsg = "Erro de sintaxe";
                                return -1;
                            }

                        }else{
                            errorMsg = "Erro de sintaxe";
                            return -1;
                        }

                    }else{
                        errorMsg = "Erro de sintaxe";
                        return -1;
                    }
                    
                }else{
                    errorMsg = "Erro de sintaxe";
                    return -1;
                }

            }else{
                return -1;
            }

        }

        return 0;
    
    }

    private static int atribuicao() throws Exception{

        // <id>
        if(token.getCode() == Tabela.IDENTIFICADOR){
        
            if(variaveis.contains(token.nome())){
                tipoAnterior.push(tipos.get(indexOfDeep(token.nome())));
                formatadores.get(formAtual).getCodAux().add(token.nome());
                
                if(tipos.get(indexOfDeep(token.nome())) == 102){
                    isFloat = true;
                }

            }else{
                errorMsg = "Variável não declarada";
                return -1;
            }

            //System.out.println("Nome: " + token.nome());
            //System.out.println("Codigo: " + tipos.get(indexOfDeep(token.nome())));
            //System.out.println("Level: " + deep.get(indexOfDeep(token.nome())));
            //System.out.println("-----------");

            read();
            
            // =
            if(token.getCode() == Tabela.ATRIBUIDOR){

                formatadores.get(formAtual).getCodAux().add(token.nome());
                read();
                
                //<expr_arit>
                int aux;
                aux = exprAritimetica(true);

                if(isFloat)
                    isFloat = false;

                if(aux > 1){
                    int ant = tipoAnterior.pop();

                    if(!(ant == 103 && aux == 103 || ant == 102 && aux != 103 || ant == 101 && aux == 101)){
                       
                        //msgs de erro
                        if(ant == 103 && aux == 101){
                            errorMsg = "Não se pode atribuir um int a uma variável declarada como char";
                        
                        }else if(ant == 103 && aux == 102){
                            errorMsg = "Não se pode atribuir um float a uma variável declarada como char";
                        
                        }else if(ant == 101 && aux == 103){
                            errorMsg = "Não se pode atribuir um char a uma variável declarada como int";
                        
                        }else if(ant == 102 && aux == 103){
                            errorMsg = "Não se pode atribuir um char a uma variável declarada como float";
                        
                        }else if(ant == 101 && aux == 102){
                            errorMsg = "Não se pode atribuir um float a uma variável declarada como int";
                        
                        }else{
                            errorMsg = "Atribuição incompativel";
                        }
                        
                        return -1;

                    // ;
                    }else if(token.getCode() == Tabela.PONTOVIRGULA){
                        formatadores.get(formAtual).getCodAux().add("\n");
                        codInterExprFormatar(formatadores.get(formAtual));
                        return 1;
                    }else{
                        errorMsg = "Erro de sintaxe";
                        return -1;
                    }

                }else{
                    return -1;
                }

            }else{
                errorMsg = "Erro de sintaxe";
                return -1;
            }

        }

        return 0;
    
    }

    private static int indexOfDeep(String nome){
        int i;

        for(i = variaveis.size()-1 ; i >= 0 ; i--){
            
            if(variaveis.get(i).equals(nome)){
                return i;
            }

        }

        return -1;

    }

    private static int exprRelacional(boolean antes, boolean inverter) throws Exception{
        int aux;
        
        formatadores.add(new Formatador());
        formAtual = formAtual + 1;

        //<expr_arit>
        aux = exprAritimetica(antes);

        formatadores.get(formAtual).getCodAux().add("\n");
        formatadores.get(formAtual-1).getCodAux().add(formatadores.get(formAtual).getCodAux().get(0));
        //codInterExprFormatar(formatadores.get(formAtual));
        formatadores.remove(formAtual);
        formAtual = formAtual - 1;

        if(aux > 1){

            //<op_relacional>
            if(token.getCode()/Tabela.COMPARADORES == 1){

                ArrayList<String> helpme = formatadores.get(formAtual).getCodAux();

                if(inverter){
                    switch(token.getCode()){

                        case 302: helpme.add(">=");
                            break;
                        case 303: helpme.add("<=");
                            break;
                        case 304: helpme.add(">");
                            break;
                        case 305: helpme.add("<");
                            break;
                        case 306: helpme.add("!=");
                            break;
                        default: helpme.add("==");
                            break;

                    }
                }else{
                    helpme.add(token.nome());
                }
                read();

                formatadores.add(new Formatador());
                formAtual = formAtual + 1;

                aux = exprAritimetica(false);

                formatadores.get(formAtual).getCodAux().add("\n");
                formatadores.get(formAtual-1).getCodAux().add(formatadores.get(formAtual).getCodAux().get(0));
                //codInterExprFormatar(formatadores.get(formAtual));
                formatadores.remove(formAtual);
                formAtual = formAtual - 1;

                //<expr_arit>
                if(aux > 1){
                    
                    int ant = tipoAnterior.pop();
                    
                    //Operacao entra chars ou operacao entre floats e ints
                    if(ant == 103 && aux == 103 || ant != 103 & aux != 103){

                        if(ant == 103){
                            return 103;
                        }else if(ant == 102 || aux == 102){

                            return 102;
                        }else{
                            return 101;
                        }
                    
                    }else{
                        errorMsg = "Operação entre tipos incompativeis" ;
                        return -1;
                    }

                }else{
                    return -1;
                }

            }else{
                errorMsg = "Erro de sintaxe";
                return -1;
            }

        }else{
            return -1;
        }
    
    }

    private static int exprAritimetica(boolean antes) throws Exception{
        int aux;

        aux = termo(antes);

        if(aux > 1){
            
            // "+-"
            if(token.getCode() == Tabela.SOMA || token.getCode() == Tabela.SUB){    
                
                formatadores.get(formAtual).getCodAux().add("+");
                read();

                //<termo> ou <expr_arit>
                novoTemp(formatadores.get(formAtual));
                aux = exprAritimetica(true);

                if(aux > 1){

                    int ant = tipoAnterior.pop();

                    //Operacao entra chars ou operacao entre floats e ints
                    if(ant == 103 && aux == 103 || ant != 103 & aux != 103){
                        
                        if(ant == 103){
                            return 103;
                        }else if(ant == 102 || aux == 102){
                            return 102;
                        }else{
                            return 101;
                        }
                    
                    }else{
                        errorMsg = "Operação entre tipos incompativeis" ;
                        return -1;
                    }

                }else{
                    return -1;
                }

            }else{
                return aux;
            }

        }

        return -1;

    }

    private static int termo(boolean antes) throws Exception{
        int aux;

        aux = fator(antes);

        if(aux > 1){
            
            read();

            if(token.getCode() == Tabela.PONTOVIRGULA){
                tipoAnterior.pop();
                
            }

            // "* /"
            if(token.getCode() == Tabela.MULT || token.getCode() == Tabela.DIV){

                boolean isDiv = (token.getCode() == Tabela.DIV);
                formatadores.get(formAtual).getCodAux().add(token.nome());

                read();
                
                //<termo> ou <fator>
                novoTemp(formatadores.get(formAtual));
                aux = termo(true);

                if(aux > 1){

                    int ant = tipoAnterior.pop();
                    

                    //Operacao entra chars ou operacao entre floats e ints
                    if(ant == 103 && aux == 103 || ant != 103 & aux != 103){

                        if(ant == 103){
                            return 103;
                        }else if(ant == 102 || aux == 102 || isDiv){
                            return 102;
                        }else{
                            return 101;
                        }
                    
                    }else{
                        errorMsg = "Operação entre tipos incompatíveis" ;
                        return -1;
                    }

                }else{
                    return -1;
                }

            }else{
                return aux;
            }

        }

        return -1;

    }

    private static int fator(boolean antes) throws Exception{
        
        // (
        if(token.getCode() == Tabela.APARENTESES){
            int aux, tempTotal;
            read();

            //<expr_arit>
            //nova expressao
            novoTemp(formatadores.get(formAtual));
            formatadores.add(new Formatador());
            formAtual = formAtual + 1;

            ArrayList<String> codAux = formatadores.get(formAtual).getCodAux();
            
            //temp total da nova expressao
            tempTotal = Formatador.getTemp();
            codAux.add("T" + Formatador.getTemp());
            codAux.add("=");
            Formatador.setTemp(Formatador.getTemp() + 1);

            aux = exprAritimetica(antes);
            
            //formatar e remover nova expressao
            formatadores.get(formAtual).getCodAux().add("\n");
            codInterExprFormatar(formatadores.get(formAtual));
            formatadores.remove(formAtual);
            formAtual = formAtual - 1;

            //temp do total da nova expressao
            formatadores.get(formAtual).getCodAux().add("T" + tempTotal);

            if(aux > 1){
                // )
                if(token.getCode() == Tabela.FPARENTESES){
                    return aux;
                }else{
                    errorMsg = "Erro de sintaxe";
                    return -1;
                }

            }else{
                return -1;
            }

        //<id> | int | float | char
        }else if(token.getCode()/Tabela.TIPOS == 1){
            int tp = token.getCode();

            if(token.getCode() == 101 && isFloat){
                formatadores.get(formAtual).getCodAux().add(token.nome() + ".0");
            }else{
                formatadores.get(formAtual).getCodAux().add(token.nome());
            }

            if(antes){
                tipoAnterior.push(tp);
                
            }

            return tp;
            
        }else if(token.getCode() == 201){
            int tp;

            if(tipos.contains(variaveis.indexOf(token.nome())))
            if(tipos.get(variaveis.indexOf(token.nome())) == 101 && isFloat){
                formatadores.get(formAtual).getCodAux().add(" int_to_float " +token.nome());
            }else{
                formatadores.get(formAtual).getCodAux().add(token.nome());
            }

            if(variaveis.contains(token.nome())){
                
                tp = tipos.get(indexOfDeep(token.nome()));
            
            }else{
                errorMsg = "Variável não declarada";
                return -1;
            }

            if(antes){
                tipoAnterior.push(tp);
                
            }

            return tp;

        }else{
            errorMsg = "Fator mal formado";
            return -1;
        }

    }    

    public static int getCol(){
        return Scanner.getCol();
    }

    public static int getLin(){
        return Scanner.getLin();
    }

    public static boolean isErrorToken(){
        return errorToken;
    }

    public static Token getToken(){
        return token;
    }

    public static String getErrorMsg(){
        return errorMsg;
    }

    public static String getCodInter(){
        return codInter;
    }

    private static void codInterExprFormatar(Formatador f){
        ArrayList<String> codAux = f.getCodAux();
        int i = codAux.size() - 1, j, k;
        String aux;

        //System.out.println(codAux);

        while(i >= 0){

            aux = codAux.get(i);

            j = i;
            while(aux != "\n" && i != 0){
                i = i -1;
                aux = codAux.get(i); 
            }

            if(i == 0){
                codInter = codInter + "\n";
            }

            k = i;
            while(k <= j){
                codInter = codInter + codAux.get(k);
                k = k + 1;
            }

            i = i -1;

        }

        codAux.clear();  

    }

    private static void novoTemp(Formatador f){
        ArrayList<String> codAux = f.getCodAux();
        codAux.add("T" + Formatador.getTemp());
        codAux.add("\n");
        codAux.add("T" + Formatador.getTemp());
        codAux.add("=");
        Formatador.setTemp(Formatador.getTemp() + 1);
    }

}
