package pro.carl.edu.sagan1.logic.parsing;

import static pro.carl.edu.sagan1.gui.i18n.I18N.i18n;
import static pro.carl.edu.sagan1.logic.MasterMind.log;


/**
 * Interprets a single SAGAN instruction line and returns an encapsulating
 * object, holding the parameters as well.
 * 
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
public class ScriptLineParser {
    
    /**
     * 
     */
    public SingleCommand parse(String line,int pos) throws ScriptParsingException {
        
        line=line.toUpperCase().trim();
        if (line.isEmpty()) 
            return null;
        
        String lineBefore;
        do {
            lineBefore=line;
            line=line.replaceAll("  "," ");
        }
        while(!lineBefore.equals(line));
       
        String[] cmdParts=line.toUpperCase().split(" ");
        
        try {
            if (cmdParts[0].trim().isEmpty())
                return null; // empty line
            else if (cmdParts[0].trim().startsWith("#"))
                return null; // comment
            else if (cmdParts[0].trim().startsWith(Commands.BACKWARD.name()))
                return new SingleCommand(Commands.BACKWARD,Integer.parseInt(cmdParts[1]),null,null);
            else if (cmdParts[0].trim().startsWith(Commands.FORWARD.name()))
                return new SingleCommand(Commands.FORWARD,Integer.parseInt(cmdParts[1]),null,null);
            else if (cmdParts[0].trim().startsWith(Commands.SENDSIGNAL.name()))
                return new SingleCommand(Commands.SENDSIGNAL,null,null,null);
            else if (cmdParts[0].trim().startsWith(Commands.WAIT.name()))
                return new SingleCommand(Commands.WAIT,null,null,Integer.parseInt(cmdParts[1]));
            else if (cmdParts[0].trim().startsWith(Commands.TURNLEFT.name()))
                return new SingleCommand(Commands.TURNLEFT,null,Integer.parseInt(cmdParts[1]),null);
            else if (cmdParts[0].trim().startsWith(Commands.TURNRIGHT.name()))
                return new SingleCommand(Commands.TURNRIGHT,null,Integer.parseInt(cmdParts[1]),null);
            else if (cmdParts[0].trim().startsWith(Commands.$FASTON.name()))
                return new SingleCommand(Commands.$FASTON,null,null,null);
            else if (cmdParts[0].trim().startsWith(Commands.$FASTOFF.name()))
                return new SingleCommand(Commands.$FASTOFF,null,null,null);
            else if (cmdParts[0].trim().startsWith(Commands.$RESTART.name()))
                return new SingleCommand(Commands.$RESTART,null,null,null);
            else {
                log("log.parser.unknowncommand","["+line+"], ["+(pos+1)+"]");
                throw new ScriptParsingException(line,pos,i18n("log.parser.unknowncommand"));
            }
        }
        catch(NumberFormatException e) {
            log("log.parser.numbererror","["+line+"], ["+(pos+1)+"]");
            throw new ScriptParsingException(line,pos,i18n("log.parser.numbererror"));
        }
        catch(ArrayIndexOutOfBoundsException f) {
            log("log.parser.parammissing","["+line+"], ["+(pos+1)+"]");
            throw new ScriptParsingException(line,pos,i18n("log.parser.parammissing"));
        }
    }
    
}
