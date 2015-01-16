package com.alizarion.crossview.web.dto;

import com.alizarion.crossview.dao.CrossViewDao;
import com.alizarion.crossview.entities.User;
import com.alizarion.crossview.exception.BadUsernameException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.net.MalformedURLException;

/**
 * @author selim@openlinux.fr.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "user")
public class UserDTO extends SubjectDTO{

    private static final long serialVersionUID = 7135609006440297599L;

    @XmlAttribute(name = "username")
    private String username;

    @XmlAttribute(name = "followersCount")
    private Integer followersCount;


    private Boolean canFollow;

    @XmlAttribute(name = "followingCount")
    private Integer followingCount;

    @XmlAttribute(name = "description")
    private String description;

    @XmlAttribute(name = "avatar")
    private ImageManagedFileDTO avatarImg;


    public UserDTO() {
    }

    public UserDTO(User user)  {

        this.username = user.getAccount().getCredential().getUsername();
        this.followersCount =  user.getFollowersCount();
        this.followingCount = user.getFollowingCount();
        this.description = user.getAccount().getProfile().getDescription();
        this.avatarImg =  user.getAccount().getProfile().getAvatar() != null?
                new ImageManagedFileDTO(user.getAccount().getProfile().getAvatar()) :  null;
    }

    public String getUsername() {
        return username;
    }



    public void setCanFollow(Boolean canFollow) {
        this.canFollow = canFollow;
    }

    public User getVO(CrossViewDao crossViewDao)
            throws BadUsernameException,
            MalformedURLException {
        return crossViewDao.findAccountByUsername(this.username).getUser();
    }

    public String getDescription() {
        return description;
    }
}
