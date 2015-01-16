package com.alizarion.crossview.services;

import com.alizarion.crossview.dao.CrossViewDao;
import com.alizarion.crossview.entities.*;
import com.alizarion.crossview.exception.BadUsernameException;
import com.alizarion.crossview.exception.ParsingWebContentException;
import com.alizarion.crossview.mbean.CrossViewConfigMBean;
import com.alizarion.crossview.service.HooverService;
import com.alizarion.crossview.web.dto.*;
import com.alizarion.crossview.web.helper.Query;
import com.alizarion.reference.filemanagement.dao.ManagedFileDao;
import com.alizarion.reference.filemanagement.entities.ImageManagedFile;
import com.alizarion.reference.filemanagement.entities.ManagedFileWriterVisitor;
import com.alizarion.reference.filemanagement.entities.ManagedImageFileDataVisitor;
import com.alizarion.reference.security.exception.DuplicateEmailException;
import com.alizarion.reference.security.exception.DuplicateUsernameException;
import com.alizarion.reference.security.oauth.oauth2.exception.BadCredentialException;
import com.alizarion.reference.social.entities.notification.Subject;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author selim@openlinux.fr.
 */
@Stateless
@Path("/")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class EntityFacade implements Serializable {

    private static final long serialVersionUID = -5573371702999649493L;


    @EJB
    CrossViewConfigMBean configMBean;

    @EJB
    HooverService hoover;

    private CrossViewDao crossViewDao;

    private ManagedFileDao managedFileDao;

    @PersistenceContext
    private EntityManager em;

    @Context
    SecurityContext securityContext;


    @PostConstruct
    private void init(){
        this.crossViewDao =  new CrossViewDao(this.em);
        this.managedFileDao =  new ManagedFileDao(this.em);
    }


    public CrossViewDao getCrossViewDao() {
        return crossViewDao;
    }

    public Account createAccount(final String username,
                                 final String email,
                                 final String password)
            throws DuplicateUsernameException,
            AddressException,
            DuplicateEmailException, BadUsernameException {
        return this.crossViewDao.createAccount(username
                ,email
                ,password
                ,this.configMBean.geInitRoles());
    }

    public User getUserPrincipal() throws BadCredentialException {
        return getPrincipalAccount(
                this.securityContext.
                        getUserPrincipal().getName()).getUser();
    }

    private Account getPrincipalAccount(final String principal)
            throws BadCredentialException {
        return this.crossViewDao.findAccountByCredentialId(principal);
    }

    public WebContent getWebContentByURL(final URL url) throws ParsingWebContentException, IOException {
        WebContent webContent = this.crossViewDao.getWebContentById(url.toString());

        if(webContent != null){
            return webContent;

        } else {
            return this.hoover.getWebContent(url);
        }
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Publication publish(final String username,PublicationDTO publicationDTO)
            throws BadCredentialException, BadUsernameException {
        User principal  = getUserPrincipal();
        User pretendedUser =  this.crossViewDao
                .findAccountByUsername(username).getUser();
        if (!pretendedUser.equals(principal)){
            throw new BadUsernameException(username);
        }
        Publication publication = principal.publish(publicationDTO.getVO(this.em));
        this.em.clear();

        return this.em.merge(publication);
    }

    public List<Publication> getUserStream(final String username)
            throws BadCredentialException, BadUsernameException {
        return this.crossViewDao.findAccountByUsername(username).getUser().getStream();
    }

    public RSSFeed rssSubscribe(FeedDTO feedDTO) throws
            BadCredentialException,
            MalformedURLException {
        User  principal = getUserPrincipal();
        RSSFeed  feed = feedDTO.getVO(this.em);
        principal.addRssFeed(feed);
        return feed;
    }

    public void rssUnSubscribe(FeedDTO feedDTO)
            throws BadCredentialException,
            MalformedURLException {
        User principal = getUserPrincipal();
        principal.removeRssFeed(feedDTO.getVO(this.em));
    }

    public ImageManagedFile getImageByUUID(String uuid) {
        return (ImageManagedFile) this.managedFileDao.getManagedFileByUUID(uuid);
    }

    public Account updateAccount(AccountDTO accountDTO)
            throws BadCredentialException, MalformedURLException, AddressException {
        Account account = accountDTO.getVO(getUserPrincipal(), this.em);
        return this.em.merge(account);
    }

    public WebHostDTO getWebHostWithFeeds(final String hostId)
            throws MalformedURLException {
        WebHost host = this.em.find(WebHost.class,hostId);
        this.em.clear();
        //temp code
        if (host.getRssFeed() != null) {
            SyndFeedInput input = new SyndFeedInput();
            try {
                SyndFeed feed =
                        input.build(new XmlReader(host.getRssFeed().getUrl()));
                for (SyndEntry entry : feed.getEntries()) {
                    host.getRssFeed().
                            addWebContent(getWebContentByURL(new URL(entry.getLink())));
                }
            } catch (FeedException | IOException | ParsingWebContentException e) {
                e.printStackTrace();
            }
        }
        //end temp code
        WebHostDTO hostDTO = new WebHostDTO(host);
        hostDTO.setFeed(host.getRssFeed());
        return hostDTO;
    }

    public Set<Subject> findPublicationByQuery(final String queryString,final Integer maxResult)  {
        //extract tags
        Query query = new Query(queryString);
        Set<Subject> result =  new HashSet<>();
        if (!query.getUserTags().isEmpty()){
            for (String userTag : query.getUserTags()){
                if (result.size() < maxResult) {
                    result.addAll(crossViewDao.findUsersByUsernameQuery(userTag));
                    try {
                        result.addAll(crossViewDao.findObserverPublications(userTag));
                    } catch (BadUsernameException ignored) {

                    }
                }  else {
                    return result;
                }
            }
        }

        if(!query.getHashTags().isEmpty()){
            for (String hashTag : query.getHashTags()){
                if (result.size() < maxResult) {
                    result.addAll(crossViewDao.findPublicationsByHashTag(hashTag));
                }  else {
                    return result;
                }
            }
        }

        if(!query.getWords().isEmpty()){
            for (String word : query.getWords()){
                if (result.size() < maxResult) {
                    result.addAll(crossViewDao.findPublicationsByContentHeaders(word));
                }  else {
                    return result;
                }
            }
        }

        return result;
    }

    public Account doFollow(String username)
            throws BadCredentialException, BadUsernameException {
        User principal = getUserPrincipal();
        User user = crossViewDao
                .findAccountByUsername(username)
                .getUser();
        principal.follow(user);
        return  this.em.merge(principal).getAccount();
    }

    public Account doUnFollow(String username)
            throws BadCredentialException, BadUsernameException {
        User principal = getUserPrincipal();
        User user = crossViewDao
                .findAccountByUsername(username)
                .getUser();
        principal.unFollow(user);
        return  this.em.merge(principal).getAccount();
    }

    public List<User> getFollowersByUsername(final String username)
            throws BadUsernameException {
        return this.crossViewDao.findAccountByUsername(username)
                .getUser().getFollowers();
    }

    /**
     * Method to get user following
     * @param username
     * @return
     * @throws BadUsernameException
     */
    public List<User> getFollowingByUsername(final String username)
            throws BadUsernameException {
        return this.crossViewDao.findAccountByUsername(username)
                .getUser().getFollowing();
    }

    /**
     *
     * @return
     * @throws BadCredentialException
     */
    public List<Publication> getUserSubscriptionStream()
            throws BadCredentialException {
        return getUserPrincipal().getSubscriptionStream();

    }


    /**
     * Method to Relay publication
     * @param id
     * @throws BadCredentialException
     */
    public Publication principalRelay(final Long id)
            throws BadCredentialException {
        getUserPrincipal().relay(em.find(Publication.class,id));
        em.flush();
        em.clear();
        return em.find(Publication.class,id);
    }

    /**
     * Method to Relay publication
     * @param id
     * @throws BadCredentialException
     */
    public Publication principalUnRelay(final Long id)
            throws BadCredentialException {
        getUserPrincipal().unRelay(em.find(Publication.class, id));
        em.flush();
        em.clear();
        return em.find(Publication.class,id);
    }

    public void storeProfileAvatar(final byte [] bytes) throws
            com.alizarion.reference.exception.ApplicationException, IOException {
        User principal = getUserPrincipal();
        ImageManagedFile avatar = new ImageManagedFile();
        ManagedImageFileDataVisitor fileDataVisitor =
                new ManagedImageFileDataVisitor(
                        principal.getAccount().
                                getCredential().
                                getUsername()+"_Avatar.jpg",
                        new ByteArrayInputStream(bytes));

        avatar.accept(fileDataVisitor);
        avatar = this.em.merge(avatar);

        ManagedFileWriterVisitor fileWriterVisitor =
                new ManagedFileWriterVisitor(
                        new ByteArrayInputStream(bytes),
                        configMBean.getManagedFileRootFolder().toString());
        avatar.accept(fileWriterVisitor);
        avatar =  em.merge(avatar);
        em.flush();
        principal.getAccount().getProfile().setAvatar(avatar);
        em.merge(principal.getAccount());

    }



    public Publication getPublicationById(final Long id) {
        return this.em.find(Publication.class,id);
    }

    public Publication crossPublications(final Long originalPublicationId,
                                         final Long publicationToCrossWithId)
            throws BadCredentialException {
        User principal =  getUserPrincipal();
        Publication userPublication =
                em.find(Publication.class,originalPublicationId);
        this.em.merge(userPublication);

        Publication publicationToCrossWith =  this.em.find(Publication.class,
                publicationToCrossWithId);

        if (!publicationToCrossWith.
                getPublisher().
                getAccount().
                getCredential().
                getUsername().
                equals(principal.
                        getAccount().
                        getCredential().
                        getUsername())){
            throw new BadCredentialException("you can only cross with your own publications");
        }

        userPublication.crossView(publicationToCrossWith
                , principal);
        em.flush();
        return this.em.merge(userPublication);


    }

    public  Map<WebContent,Set<LinkedPublicationDTO>> getPublicationLinks(final Long id)
            throws MalformedURLException {
        Map<WebContent,Set<LinkedPublicationDTO>> contentDTOs =  new HashMap<>();

        Publication publication =  getPublicationById(id);
        if(publication!=null) {
            Map<Publication, Set<User>> links = publication.getAllRelatedContents();
            for (Map.Entry<Publication, Set<User>> entry : links.entrySet()) {
                if (contentDTOs.containsKey((WebContent) entry.getKey().getContent())) {
                    contentDTOs.get((WebContent) entry.
                            getKey().getContent()).add(new
                            LinkedPublicationDTO(entry.getKey(), entry.getValue()));
                } else {
                    Set<LinkedPublicationDTO> linkedPublicationDTOs = new HashSet<>();
                    linkedPublicationDTOs.add(new LinkedPublicationDTO(entry.getKey(), entry.getValue()));
                    contentDTOs.put((WebContent) entry.getKey().getContent(), linkedPublicationDTOs);
                }
            }
        }
        return contentDTOs;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Publication unCrossPublications(final Long userPublicationId, final Long linkedWithPublicationId)
            throws BadCredentialException {

        User principal =  getUserPrincipal();
        Publication userPublication =
                em.find(Publication.class,userPublicationId);
        Publication linkedWithPublication =
                em.find(Publication.class,linkedWithPublicationId);
        CrossLink link =  userPublication
                .unCrossView(linkedWithPublication,principal);
        if (link == null){
            link = linkedWithPublication.unCrossView(userPublication,principal);

        }
        if (link == null){
            throw new BadCredentialException("cannot delete an inexistent link");
        }
        this.em.clear();

        link = this.em.find(CrossLink.class,link.getId());
        //link.preRemove();
        this.em.remove(link);

        this.em.flush();
        this.em.clear();
        return  em.find(Publication.class,userPublicationId);

    }

    public void removePublication(Long id) throws BadCredentialException {
        Publication publication =
                em.find(Publication.class,id);
        if (publication!=null) {
            if (!publication.
                    getPublisher().
                    getAccount().
                    getCredential().
                    getUsername().
                    equals(getUserPrincipal().
                            getAccount().
                            getCredential().
                            getUsername())) {
                throw new BadCredentialException("user not allowed to delete this status");
            }

            this.em.clear();

            publication =
                    em.find(Publication.class, id);
            this.em.clear();
            for (CrossLink link : publication.getAllCrossedLinks()) {
                em.remove(em.find(CrossLink.class, link.getId()));
            }

            this.em.flush();
            this.em.clear();

            publication = em.find(Publication.class, id);
            em.remove(publication);
        }

    }

    public Publication getPublicationByWeContent(final  URL url)
            throws ParsingWebContentException,
            IOException,
            BadCredentialException {

        User principal =  getUserPrincipal();
        WebContent content = getWebContentByURL(url);
        Publication  publication = this.crossViewDao
                .findPublicationByPublisherAndContent(
                        principal.getId(), content.getContentId());
        //em.detach(content);

        if(publication == null){

            publication = new Publication(content);
            publication = principal.publish(publication);
            publication =  em.merge(publication);
            this.em.flush();
        }  else {
            publication.update();
            publication =  em.merge(publication);
        }
        return publication;

    }
}
