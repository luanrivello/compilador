package parser;

import java.util.ArrayList;

public class Formatador {
    private ArrayList<String> codAux;
    private static int temp = 0;
    private static int label = 1;

    public Formatador(){
        
        codAux = new ArrayList<String>();

    }

    public ArrayList<String> getCodAux() {
        return codAux;
    }

    public static int getTemp() {
        return temp;
    }

    public static void setTemp(int temp) {
        Formatador.temp = temp;
    }

    public static int getLabel() {
        return label;
    }

    public static void setLabel(int label) {
        Formatador.label = label;
    }

}