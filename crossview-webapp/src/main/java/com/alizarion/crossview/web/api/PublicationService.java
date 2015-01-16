package com.alizarion.crossview.web.api;

import com.alizarion.crossview.services.EntityFacade;
import com.alizarion.crossview.web.dto.PublicationDTO;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.Serializable;
import java.net.MalformedURLException;

/**
 * @author selim@openlinux.fr.
 */
@Path("/public/publication")
public class PublicationService implements Serializable {

    private static final long serialVersionUID = 6494185519882623101L;


    @EJB
    EntityFacade entityFacade;

    @Path("{id}")
    @GET
    @PermitAll
    public PublicationDTO getById(@PathParam("id")final Long id)
            throws MalformedURLException {
       return new PublicationDTO(entityFacade.getPublicationById(id));
    }
}
