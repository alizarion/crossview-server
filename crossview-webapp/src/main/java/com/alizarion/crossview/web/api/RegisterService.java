package com.alizarion.crossview.web.api;

import com.alizarion.crossview.services.EntityFacade;
import com.alizarion.crossview.web.dto.AccountDTO;
import com.alizarion.reference.exception.ApplicationException;
import com.alizarion.reference.location.exception.EmailMalformedException;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.mail.internet.AddressException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.Serializable;

/**
 * @author selim@openlinux.fr.
 */
@Path(value = "/public/account")
public class RegisterService implements Serializable {

    private static final long serialVersionUID = 2131830032072148436L;


    @EJB
    private EntityFacade facade;

    @POST
    @Path(value = "accounts")
    @PermitAll
    public Response createAccount(@NotNull
                                  @FormParam("username")
                                  @Size(min = 3, max = 12) String  username,
                                  @NotNull
                                  @Size(min = 6)
                                  @FormParam("password") String password,
                                  @NotNull @FormParam("email") String email)
            throws ApplicationException {
        try {
            return Response.status(Response.Status.CREATED)
                    .entity(new AccountDTO(this.facade.createAccount(
                            username,email,password))).build();
        } catch (AddressException e) {
            throw new EmailMalformedException(email);
        } catch (NullPointerException e){
            e.printStackTrace();
            throw new ApplicationException("BAD_REQUEST");
        }
    }
}
