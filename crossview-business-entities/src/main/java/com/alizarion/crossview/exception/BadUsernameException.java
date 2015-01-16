package com.alizarion.crossview.exception;

import com.alizarion.reference.exception.ApplicationException;

/**
 * @author selim@openlinux.fr.
 */
public class BadUsernameException extends ApplicationException {

    private static final long serialVersionUID = 4472984191316966391L;

    private static final String MSG = "unknown username :";

    public BadUsernameException(String msg) {
        super(MSG + msg);
    }

    public BadUsernameException(String msg, Throwable cause) {
        super(MSG + msg, cause);
    }

    public BadUsernameException(Throwable cause) {
        super(cause);
    }
}
