package com.alizarion.crossview.dao;

import com.alizarion.crossview.entities.Account;
import com.alizarion.crossview.entities.Publication;
import com.alizarion.crossview.entities.User;
import com.alizarion.crossview.entities.WebContent;
import com.alizarion.crossview.exception.BadUsernameException;
import com.alizarion.reference.dao.jpa.JpaDao;
import com.alizarion.reference.location.entities.ElectronicAddress;
import com.alizarion.reference.security.entities.Role;
import com.alizarion.reference.security.exception.DuplicateEmailException;
import com.alizarion.reference.security.exception.DuplicateUsernameException;
import com.alizarion.reference.security.oauth.oauth2.exception.BadCredentialException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author selim@openlinux.fr.
 */
public class CrossViewDao extends JpaDao<Long,Account>{



    public CrossViewDao(EntityManager entityManager) {
        super(entityManager);
    }

    public Account createAccount(final String username,
                                 final String email,
                                 final String password,
                                 final Set<Role> roles)
            throws DuplicateUsernameException,
            DuplicateEmailException,
            AddressException {
        try {
            findAccountByUsername(username);
            throw new DuplicateUsernameException(username);
        } catch (BadUsernameException e) {
            if (findAccountByEmail(email) != null){
                throw new DuplicateEmailException(email);
            } else {
                Account account =  new Account(
                        username
                        ,password
                        ,new ElectronicAddress(new InternetAddress(email))
                        ,roles);
                return this.em.merge(account);

            }
        }
    }

    @SuppressWarnings("unchecked")
    public WebContent getWebContentById(String url){
        List<WebContent> result = this.em.
                createNamedQuery(WebContent.FIND_BY_ID)
                .setParameter("url",url).getResultList();
        if (!result.isEmpty()){
            return result.get(0);
        } else {
            return null;
        }
    }




    @SuppressWarnings("unchecked")
    public Account findAccountByUsername(final String username) throws BadUsernameException {
        List<Account> accounts =
                this.em.createNamedQuery(
                        Account.FIND_BY_USERNAME)
                        .setParameter("username" , username)
                        .getResultList();

        if (accounts.isEmpty()){
            throw new BadUsernameException(username) ;
        } else {
            return accounts.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    public Set<User> findUsersByUsernameQuery(final String query){
        return new HashSet<User>(
                this.em.createNamedQuery(User.FIND_BY_USERNAME)
                        .setParameter("username", "%" + query + "%")
                        .getResultList());
    }

    @SuppressWarnings("unchecked")
    public Set<Publication> findPublicationsByHashTag(final String query){
        return new HashSet<Publication>(
                this.em.createNamedQuery(Publication.FIND_BY_TAG)
                        .setParameter("tag", "%" + query + "%")
                        .getResultList());
    }

    @SuppressWarnings("unchecked")
    public Set<Publication> findPublicationsByContentHeaders(final String query){
        Set<WebContent> result = new HashSet<WebContent>(
                this.em.createNamedQuery(WebContent.FIND_BY_WORD)
                        .setParameter("word", "%" + query + "%")
                        .getResultList());
        Set<Publication> publications =  new HashSet<>();
        for(WebContent webContent : result){
            publications.addAll(webContent.getPublications());
        }
        return publications;

    }

    @SuppressWarnings("unchecked")
    public Publication findPublicationByPublisherAndContent(final Long publisherId, final String contentId){
        List<Publication> result =
                this.em.createNamedQuery(Publication.FIND_BY_WEB_CONTENT_PUBLISHER)
                        .setParameter("publisherId", publisherId)
                        .setParameter("contentId",contentId)
                        .getResultList();
        if (!result.isEmpty()){
            return result.get(0);
        }
        return null;


    }

    @SuppressWarnings("unchecked")
    public Set<Publication> findPublicationByPublisher(final String username){
        return  new HashSet<Publication>(
                this.em.createNamedQuery(Publication.FIND_BY_PUBLISHER)
                        .setParameter("username", username)
                        .getResultList());


    }

    @SuppressWarnings("unchecked")
    public Set<Publication> findObserverPublications(final String username)
            throws BadUsernameException {
        Account observer = this.findAccountByUsername(username);
        return  new HashSet<Publication>(
                this.em.createNamedQuery(Publication.FIND_BY_OBSERVER)
                        .setParameter("observerID", observer.getId())
                        .getResultList());


    }

    @SuppressWarnings("unchecked")
    public Account findAccountByCredentialId(final String id) throws BadCredentialException {
        List<Account> accounts =
                this.em.createNamedQuery(
                        Account.FIND_BY_CREDENTIAL_ID)
                        .setParameter("id", Long.parseLong(id))
                        .getResultList();

        if (accounts.isEmpty()){
            throw new BadCredentialException(id);
        } else {
            return accounts.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    public Account findAccountByEmail(final String email){
        List<Account> accounts =  this.em.createNamedQuery(
                Account.FIND_BY_EMAIL).
                setParameter("email",email).
                getResultList();
        if (accounts.isEmpty()){
            return null;
        } else {
            return accounts.get(0);
        }
    }
}
