package pro.carl.edu.sagan1.logic.compiling;

import java.awt.event.ActionEvent;

/**
 * Trouble during the cross-compilation.
 *
 * @since 0.0
 * @version 1.0.0 - 20/10/2011
 */
/* package private*/ class CompilationExecutionException extends Exception {

    private ActionEvent ae1,ae2;
    
    CompilationExecutionException() {
        super();
    }
    
    CompilationExecutionException(ActionEvent ae1) {
        super();
        this.ae1=ae1;
        this.ae2=null;
    }
    
    CompilationExecutionException(ActionEvent ae1,ActionEvent ae2) {
        super();
        this.ae1=ae1;
        this.ae2=ae2;
    }

    public ActionEvent getActionEvent1() {
        return ae1;
    }

    public ActionEvent getActionEvent2() {
        return ae2;
    }
   
}
