package com.alizarion.crossview.web.api;

import com.alizarion.crossview.entities.Publication;
import com.alizarion.crossview.entities.User;
import com.alizarion.crossview.services.EntityFacade;
import com.alizarion.crossview.web.dto.PublicationDTO;
import com.alizarion.crossview.web.dto.SubjectDTO;
import com.alizarion.crossview.web.dto.UserDTO;
import com.alizarion.reference.social.entities.notification.Subject;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author selim@openlinux.fr.
 */
@Path("/protected")
public class SearchService  implements Serializable {


    private static final Integer MAX_RESULT = 100;
    private static final Integer QUERY_MIN_LENGTH = 3;

    private static final long serialVersionUID = 1920247248785944008L;

    @EJB
    EntityFacade facade;

    @GET
    @Produces("application/json")
    @Path("search")
    @PermitAll
    public Set<SubjectDTO> search(@QueryParam("query") String query) throws MalformedURLException
             {
        Set<SubjectDTO> finalResult =  new HashSet<>();
        if(query.length() >= QUERY_MIN_LENGTH) {
            Set<Subject> queryResult = facade.findPublicationByQuery(query, MAX_RESULT);

            for (Subject subject : queryResult) {
                if (subject instanceof Publication) {
                    finalResult.add(new PublicationDTO((Publication) subject));
                } else if (subject instanceof User) {
                    finalResult.add(new UserDTO((User) subject));

                }
            }
        }
        return finalResult;
    }



}
