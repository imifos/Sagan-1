package pro.carl.edu.sagan1.logic.parsing;

/**
 * SAGAN program parsing exception.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class ScriptParsingException extends Exception {
    
    private int pos;
    private String line;
    private String errorMessage;
    
    public ScriptParsingException(String line,int pos,String errorMessage) {
        super();
        this.line=line;
        this.pos=pos;
        this.errorMessage=errorMessage;
    }

    public String getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }
    
    @Override
    public String getMessage() {
        return errorMessage;
    }
}
