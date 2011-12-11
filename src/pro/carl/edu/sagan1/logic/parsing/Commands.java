package pro.carl.edu.sagan1.logic.parsing;

/**
 * Enumerates all SAGAN commands, defines the help text message code and
 * if it's a meta or a concrete robot command.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public enum Commands {

    FORWARD     ("command.help.forward",false),     
    BACKWARD    ("command.help.backward",false),     
    TURNLEFT    ("command.help.turnleft",false),     
    TURNRIGHT   ("command.help.turnright",false),     
    WAIT        ("command.help.wait",false),     
    SENDSIGNAL  ("command.help.sendsignal",false),
    
    $FASTON   ("command.help.faston",true),   // Convention: META COMMANDS should start with $.
    $FASTOFF  ("command.help.fastoff",true),
    $RESTART  ("command.help.restart",true)
    
    ;
    
    private String helpTxtId;
    private boolean metaCommand=false;
    
    private Commands(String helptxtId,boolean isMetaCommand) {
        this.helpTxtId=helptxtId;
        this.metaCommand=isMetaCommand;
    }

    public String getHelpTxtId() {
        return helpTxtId;
    }
    
    public boolean isMetaCommand() {
        return metaCommand;
    }
    
}
