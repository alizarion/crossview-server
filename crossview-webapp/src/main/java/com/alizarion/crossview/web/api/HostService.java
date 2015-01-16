package com.alizarion.crossview.web.api;

import com.alizarion.crossview.services.EntityFacade;
import com.alizarion.crossview.web.dto.WebHostDTO;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.Serializable;
import java.net.MalformedURLException;

/**
 * @author selim@openlinux.fr.
 */
@Path("/protected/host")
public class HostService implements Serializable {

    private static final long serialVersionUID = 3791175993616178101L;

    @EJB
    EntityFacade facade;

    @Path("/")
    @GET
    @RolesAllowed("profile")
    @Produces("application/json")
    public WebHostDTO getHostAndFeeds(@QueryParam("hostUrl") String  host)
            throws MalformedURLException {
        return facade.getWebHostWithFeeds(host);
    }
}
