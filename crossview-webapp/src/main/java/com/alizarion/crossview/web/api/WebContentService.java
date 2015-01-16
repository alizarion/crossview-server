package com.alizarion.crossview.web.api;

import com.alizarion.crossview.exception.ParsingWebContentException;
import com.alizarion.crossview.services.EntityFacade;
import com.alizarion.crossview.web.dto.WebContentDTO;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

/**
 * @author selim@openlinux.fr.
 */

@Path(value = "/protected")
public class WebContentService implements Serializable {

    private static final long serialVersionUID = 6494185519882623101L;

    @EJB
    private EntityFacade facade;

    public WebContentService() {
    }

    @GET
    @Path("webcontent")
    @PermitAll
    @Produces("application/json")
    public WebContentDTO getWebContent(@QueryParam("url")final String url)
            throws IOException, ParsingWebContentException {
        return new WebContentDTO(this.facade.getWebContentByURL(new URL(url)));
    }


   /* @GET
    @Path("webcontent/crossed")
    @PermitAll
    @Produces("application/json")
    public List<CrossLinkDTO> getRelatedWebContent(@QueryParam("url")final String url)
            throws IOException, ParsingWebContentException {
        WebContent content = this.facade.getWebContentByURL(new URL(url));
        Set<CrossLinkDTO> contentDTOs= new HashSet<>();
        for (Map.Entry<PublicationContent,Set<User>> entry: content.getAllRelatedContents().entrySet()){
            contentDTOs.add(new CrossLinkDTO((WebContent)entry.getKey(),entry.getValue()));
        }
        return new ArrayList<>(contentDTOs);
    }  */


}
