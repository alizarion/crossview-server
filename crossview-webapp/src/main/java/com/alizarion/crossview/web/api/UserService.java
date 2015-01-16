package com.alizarion.crossview.web.api;

import com.alizarion.crossview.entities.Publication;
import com.alizarion.crossview.entities.User;
import com.alizarion.crossview.entities.WebContent;
import com.alizarion.crossview.exception.BadUsernameException;
import com.alizarion.crossview.exception.ParsingWebContentException;
import com.alizarion.crossview.services.EntityFacade;
import com.alizarion.crossview.web.dto.CrossLinkDTO;
import com.alizarion.crossview.web.dto.LinkedPublicationDTO;
import com.alizarion.crossview.web.dto.PublicationDTO;
import com.alizarion.crossview.web.dto.UserDTO;
import com.alizarion.reference.security.oauth.oauth2.exception.BadCredentialException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.*;

/**
 * @author selim@openlinux.fr.
 */
@Path("/protected/user")
public class UserService implements Serializable {

    private static final long serialVersionUID = -2082205705418644373L;

    @EJB
    EntityFacade facade;

    @GET
    @Produces("application/json")
    @Path("{username}")
    @PermitAll
    public UserDTO  getUser(@PathParam("username") String username)
            throws BadUsernameException {
        if (!StringUtils.isEmpty(username)){
            return new UserDTO(facade.getCrossViewDao().
                    findAccountByUsername(username).getUser());
        }
        throw new BadUsernameException("");
    }


    @Path("{username}/publication")
    @GET
    @RolesAllowed(value = "profile")
    @Produces("application/json")
    public Set<PublicationDTO> getStream(@PathParam("username") final String username) throws
            MalformedURLException, BadCredentialException, BadUsernameException {
        Set<PublicationDTO> dtos =  new HashSet<>();
        for (Publication publication : facade.getUserStream(username)){
            dtos.add(new PublicationDTO(publication));
        }
        return dtos;
    }

    @Path("{username}/publication/{id}")
    @GET
    @PermitAll
    public PublicationDTO getById(@PathParam("username")final String username, @PathParam("id")final Long id)
            throws MalformedURLException {
        return new PublicationDTO(facade.getPublicationById(id));
    }




    @Path("{username}/publication/{id}")
    @DELETE
    @RolesAllowed(value = "profile")
    public Response removePublication(@PathParam("username")final String username,
                                      @PathParam("id")final Long id)
            throws MalformedURLException, BadCredentialException {
        facade.removePublication(id);
        return Response.status(Response.Status.OK).build();
    }


    @GET
    @Path("{username}/publication/{id}/links")
    @PermitAll
    @Produces("application/json")
    public List<CrossLinkDTO> getRelatedWebContent(@PathParam("username")final String username, @PathParam("id")final Long id)
            throws IOException, ParsingWebContentException {
        Map<WebContent,Set<LinkedPublicationDTO>> contentDTOs = facade.getPublicationLinks(id);

        List<CrossLinkDTO> crossLinks =  new ArrayList<>();
        for (Map.Entry<WebContent,Set<LinkedPublicationDTO>> entry : contentDTOs.entrySet()){
            crossLinks.add(new CrossLinkDTO(entry.getKey(),entry.getValue()));
        }

        return crossLinks;
    }

    @POST
    @Path("{username}/publication/{id}")
    @RolesAllowed(value = "profile")
    @Consumes("application/json")
    @Produces("application/json")
    @Deprecated
    public PublicationDTO publish(@PathParam("username") String username,PublicationDTO publicationDTO)
            throws MalformedURLException,
            ParsingWebContentException,
            BadCredentialException,
            BadUsernameException {
        Publication publication = this.facade.publish(username,publicationDTO);
        return new PublicationDTO(publication);
    }


    @GET
    @Path("{username}/followers")
    @Produces("application/json")
    public Set<UserDTO> getFollowers(@PathParam("username") final String username
    ) throws BadUsernameException {
        List<User> followers = facade.getFollowersByUsername(username);
        Set<UserDTO> followersDTO = new HashSet<>();
        for (User  user :followers){
            followersDTO.add(new UserDTO(user));
        }
        return followersDTO;
    }

    @GET
    @Path("{username}/following")
    @Produces("application/json")
    public Set<UserDTO> getFollowing(@PathParam("username") final String username
    ) throws BadUsernameException {
        List<User> following = facade.getFollowingByUsername(username);
        Set<UserDTO> followingDTO = new HashSet<>();
        for (User  user :following){
            followingDTO.add(new UserDTO(user));
        }
        return followingDTO;
    }

}
