package com.alizarion.crossview.web.dto;

import com.alizarion.crossview.entities.Account;
import com.alizarion.crossview.entities.RSSFeed;
import com.alizarion.crossview.entities.User;
import com.alizarion.reference.location.entities.ElectronicAddress;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author selim@openlinux.fr.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class AccountDTO implements Serializable {


    private static final long serialVersionUID = 6439644457101765293L;

    @XmlAttribute(name = "id")
    private Long id;

    @XmlAttribute(name = "email")
    private String email;

    @XmlAttribute(name = "feeds")
    private Set<FeedDTO> feeds = new HashSet<>();

    @XmlElement(name = "user")
    private UserDTO user;


    public AccountDTO() {
    }

    public AccountDTO(final Account account) {
        this.id = account.getId();
        this.user = new UserDTO(account.getUser());
        this.email = account.getCredential().getEmail().toString();
        for(RSSFeed rssFeed :  account.getUser().getFeeds()){
            this.feeds.add(new FeedDTO(rssFeed));
        }

    }

    public Account getVO(User principal,EntityManager em)
            throws MalformedURLException, AddressException {

        principal.getFeeds().clear();
        for (FeedDTO  feedDTO :this.feeds){
            principal.addRssFeed(feedDTO.getVO(em));
        }
        principal.getAccount().getProfile().
                setDescription(this.user.getDescription());

        //TODO CHECK if email does not already exist, and launch verification workflow
        if (!this.email.equals(principal.getAccount()
                .getCredential().getEmail().toString())) {
            principal.getAccount().getCredential()
                    .setEmail(new ElectronicAddress(
                            new InternetAddress(this.email)));
        }

        return principal.getAccount();
    }


}
