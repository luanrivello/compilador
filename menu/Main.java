package menu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import parser.Parser;

public class Main {

    public static void main(String[] args) {
        System.out.println("\n----------------INICIO---------------\n");

        try {

            //File arquivo = new File(args[0]);
            File arquivo = new File("samples/ARQUIVOTESTE_LINUX.txt");
            
            BufferedReader reader = new BufferedReader(new FileReader(arquivo));

            boolean sucesso;

            try {
                sucesso = Parser.parsear(reader);
            } catch (Exception e) {
                e.printStackTrace();;
                sucesso = false;
            }

            if(!sucesso){

                System.out.println("Ultimo Token: \n" + Parser.getToken());
                System.out.println("- - - - - - - - - - - - - - - - - - -");

                if(Parser.isErrorToken()){
                    System.out.println("Error:" + Parser.getToken().nome());
                    System.out.println("Code:" + Parser.getToken().getCode());

                }else{
                    System.out.print("Error: " + Parser.getErrorMsg());
                    
                }

                if(Parser.getLin() == 1 && Parser.getCol() == 0){
                    System.out.println(" no final do arquivo");
                }else{
                    System.out.println("\nLinha: " + Parser.getLin());
                    System.out.println("Coluna: " + Parser.getCol());
                }
                

            }else{
                System.out.println(Parser.getCodInter());
            }

            System.out.println("\n-----------------FIM-----------------\n");

        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado.");

        }

    }

}