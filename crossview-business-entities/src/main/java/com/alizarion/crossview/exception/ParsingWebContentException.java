package com.alizarion.crossview.exception;

import com.alizarion.reference.exception.ApplicationException;

/**
 * @author selim@openlinux.fr.
 */
public class ParsingWebContentException extends ApplicationException {

    private static final long serialVersionUID = 8927828365191153115L;

    private static final String MSG = "error on parsing url ";
    public ParsingWebContentException(String msg) {
        super(MSG + msg);
    }

    public ParsingWebContentException(String msg, Throwable cause) {
        super(MSG + msg, cause);
    }

    public ParsingWebContentException(Throwable cause) {
        super(cause);
    }
}
