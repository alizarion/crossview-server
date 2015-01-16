package com.alizarion.crossview.exception;

import com.alizarion.reference.exception.ApplicationException;
import com.alizarion.reference.location.exception.EmailMalformedException;
import com.alizarion.reference.security.exception.DuplicateEmailException;
import com.alizarion.reference.security.exception.DuplicateUsernameException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author selim@openlinux.fr.
 */
@Provider
public class SignUpExceptionHandler implements ExceptionMapper<ApplicationException> {

    private static final String EMAIL_MALFORMED_EXCEPTION_KEY = "EMAIL_MALFORMED";

    private static final String DUPLICATE_USERNAME_EXCEPTION_KEY = "DUPLICATE_USERNAME_EXCEPTION_KEY";

    private static final String DUPLICATE_EMAIL_EXCEPTION_KEY = "DUPLICATE_EMAIL_EXCEPTION_KEY";


    @Override
    public Response toResponse(final ApplicationException exception) {
        if (exception instanceof EmailMalformedException){
            return   Response.status(Response.Status.BAD_REQUEST)
                    .entity(EMAIL_MALFORMED_EXCEPTION_KEY).build();
        } else if (exception instanceof DuplicateUsernameException){
            return  Response.status(Response.Status.BAD_REQUEST)
                    .entity(DUPLICATE_USERNAME_EXCEPTION_KEY).build();
        } else if (exception instanceof DuplicateEmailException){
            return  Response.status(Response.Status.BAD_REQUEST)
                    .entity(DUPLICATE_EMAIL_EXCEPTION_KEY).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).
                    entity(exception.getMessage()).build();
        }
    }
}
