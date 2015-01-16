package com.alizarion.crossview.entities;

import com.alizarion.reference.filemanagement.entities.ImageManagedFile;
import com.alizarion.reference.social.entities.notification.Notification;
import com.alizarion.reference.social.entities.notification.Subject;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author selim@openlinux.fr.
 */
@Entity
@Table(name = Account.TABLE_PREFIX+"_profile")
public class Profile extends Subject<Account> implements Serializable {

    private static final long serialVersionUID = -68787515443882408L;

    @Column(name = "description"
            ,nullable = true,
            length = 255)
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar_image_id")
    private ImageManagedFile avatar;

    @OneToOne
    private Account account;

    protected  Profile(){
    }


    public Profile(Account account) {
        this.account = account;
    }

    @Override
    public void notifyObservers(Notification notification) {

    }

    @Override
    public void notifyOwner(Notification notification) {
        this.account.notify(notification);
    }

    @Override
    public String getSubjectType() {
        return null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ImageManagedFile getAvatar() {
        return avatar;
    }

    public void setAvatar(ImageManagedFile avatar) {
        this.avatar = avatar;
    }
}
