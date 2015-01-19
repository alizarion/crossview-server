package com.alizarion.crossview.web.api;

import com.alizarion.crossview.entities.Publication;
import com.alizarion.crossview.entities.RSSFeed;
import com.alizarion.crossview.exception.BadUsernameException;
import com.alizarion.crossview.exception.ParsingWebContentException;
import com.alizarion.crossview.services.EntityFacade;
import com.alizarion.crossview.web.dto.AccountDTO;
import com.alizarion.crossview.web.dto.FeedDTO;
import com.alizarion.crossview.web.dto.PublicationDTO;
import com.alizarion.reference.exception.ApplicationException;
import com.alizarion.reference.security.oauth.oauth2.exception.BadCredentialException;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.mail.internet.AddressException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author selim@openlinux.fr.
 */
@Path("/protected/account")
public class AccountService  implements Serializable {

    private static final long serialVersionUID = -7167328612457455399L;

    @EJB
    EntityFacade facade;

    @POST
    @Path("feed")
    @Consumes("application/json")
    @RolesAllowed(value = "profile")
    public FeedDTO feedSubscribe(FeedDTO feedDTO)
            throws MalformedURLException,
            BadCredentialException {
        return new FeedDTO(this.facade.rssSubscribe(feedDTO));
    }


    @GET
    @Path("/")
    @Produces("application/json")
    @RolesAllowed(value = "profile")
    public AccountDTO getPrincipalAccount()
            throws BadCredentialException {
        return new AccountDTO(this.facade.getUserPrincipal().getAccount());
    }

    @GET
    @Path("feed")
    @Produces("application/json")
    @RolesAllowed(value = "profile")
    public List<FeedDTO> getUserFeed()
            throws MalformedURLException,
            BadCredentialException {
        List<FeedDTO> feeds =  new ArrayList<>();
        for (RSSFeed rssFeed : this.facade.getUserPrincipal().getFeeds()){
            feeds.add(new FeedDTO(rssFeed));
        }
        return feeds;
    }

    @POST
    @Path("relay/{publicationID}")
    @Produces("application/json")
    @RolesAllowed(value = "profile")
    public PublicationDTO relay(@PathParam("publicationID") final String publicationID)
            throws BadCredentialException, MalformedURLException {
        return new PublicationDTO(facade.principalRelay(Long.parseLong(publicationID)));
    }

    @DELETE
    @Path("relay/{publicationID}")
    @Produces("application/json")
    @RolesAllowed(value = "profile")
    public PublicationDTO unRelay(@PathParam("publicationID") final String publicationID)
            throws BadCredentialException, MalformedURLException {
        return new PublicationDTO(facade.principalUnRelay(Long.parseLong(publicationID)));
    }

    @DELETE
    @Path("feed")
    @Consumes("application/json")
    @RolesAllowed(value = "profile")
    public Response getUserFeed(FeedDTO feedDTO)
            throws MalformedURLException, BadCredentialException {
        this.facade.rssUnSubscribe(feedDTO);
        return Response.status(Response.Status.OK).entity("removed").build();
    }

    @POST
    @Path("follow/{username}")
    @RolesAllowed(value = "profile")
    @Produces("application/json")
    public AccountDTO follow(@PathParam("username") final String username)
            throws BadUsernameException, BadCredentialException {
        return new AccountDTO(facade.doFollow(username));
    }






    @PUT
    @Path("/")
    @RolesAllowed(value = "profile")
    @Produces("application/json")
    @Consumes("application/json")
    public AccountDTO updateAccountProfile(AccountDTO account)
            throws BadUsernameException,
            BadCredentialException,
            MalformedURLException, AddressException {
        return new AccountDTO(facade.updateAccount(account));
    }

    @POST
    @Path("unfollow/{username}")
    @RolesAllowed(value = "profile")
    @Produces("application/json")
    public AccountDTO unfollow(@PathParam("username") final String username)
            throws BadUsernameException, BadCredentialException {
        return new AccountDTO(facade.doUnFollow(username));
    }

    @Path("/stream")
    @GET
    @RolesAllowed(value = "profile")
    @Produces("application/json")
    public Set<PublicationDTO> getSubscriptionStream() throws
            MalformedURLException, BadCredentialException, BadUsernameException {
        Set<PublicationDTO> dtos =  new HashSet<>();
        for (Publication publication : facade.getUserSubscriptionStream()){
            dtos.add(new PublicationDTO(publication));
        }
        for(Publication publication :facade.getUserPrincipal().getStream()){
            PublicationDTO publicationDTO =  new PublicationDTO(publication);
            if (!dtos.contains(publicationDTO)) {
                dtos.add(new PublicationDTO(publication));
            }
        }
        return dtos;
    }

    @POST
    @Path("publication/{userPublicationId}/cross/{allegedPublicationId}")
    @RolesAllowed(value = "profile")
    @Produces("application/json")
    public  PublicationDTO  cross(@PathParam("userPublicationId") final Long userPublicationId,
                                  @PathParam("allegedPublicationId") final Long allegedPublicationId)
            throws BadCredentialException, MalformedURLException {
        return  new PublicationDTO(facade.crossPublications(userPublicationId,allegedPublicationId));
    }


    @POST
    @Path("publish")
    @RolesAllowed(value = "profile")
    @Produces("application/json")
    public PublicationDTO getPublicationByContent(@QueryParam("url")final String url)
            throws IOException, ParsingWebContentException, BadCredentialException {

        return new PublicationDTO(this.facade.getPublicationByWeContent(new URL(url)));
    }



    @DELETE
    @Path("publication/{userPublicationId}/cross/{allegedPublicationId}")
    @RolesAllowed(value = "profile")
    @Produces("application/json")
    public  PublicationDTO  unCross(@PathParam("userPublicationId") final Long userPublicationId,
                                    @PathParam("allegedPublicationId") final Long allegedPublicationId)
            throws BadCredentialException, MalformedURLException {
        return  new PublicationDTO(facade.unCrossPublications(userPublicationId,allegedPublicationId));
    }


    @POST
    @Path("/gcm")
    @RolesAllowed(value = "profile")
    public Response registerNotificationID(){
        //implement GOogle Cloud messaging
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/upload")
    @Consumes("multipart/form-data")
    @RolesAllowed(value = "profile")
    public Response uploadFile(MultipartFormDataInput input) throws IOException, ApplicationException {
        String fileName = "";

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("avatar");

        for (InputPart inputPart : inputParts) {

            try {
                //convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class,null);
                byte [] bytes = IOUtils.toByteArray(inputStream);

                facade.storeProfileAvatar(bytes);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return Response.status(200)
                .entity("uploadFile is called, Uploaded file name : " + fileName).build();
    }

}
