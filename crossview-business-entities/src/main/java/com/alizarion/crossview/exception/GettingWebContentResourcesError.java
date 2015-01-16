package com.alizarion.crossview.exception;

import com.alizarion.reference.exception.ApplicationError;

/**
 * @author selim@openlinux.fr.
 */
public class GettingWebContentResourcesError extends ApplicationError {

    private static final long serialVersionUID = 8469417879121468314L;

    private final static String MSG = "Cannot get WebContent resource ";
    public GettingWebContentResourcesError(String msg) {
        super(MSG  + msg);
    }

    public GettingWebContentResourcesError(String msg, Throwable cause) {
        super(MSG + msg, cause);
    }
}
