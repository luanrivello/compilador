package token;

public class Token{
    private int code;
    private String string;

    public Token(int code, String string){
        this.code = code;
        this.string = string;
        
    }

    public int getCode(){
        return this.code;
    }

    public String nome(){
        return this.string;
    }

    @Override
    public String toString(){

        return "Code: " + this.code + "\n" + "Token: " + this.string;

    }

}