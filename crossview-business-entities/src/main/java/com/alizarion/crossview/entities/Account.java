package com.alizarion.crossview.entities;

import com.alizarion.reference.location.entities.ElectronicAddress;
import com.alizarion.reference.security.entities.Credential;
import com.alizarion.reference.security.entities.Role;
import com.alizarion.reference.social.entities.notification.Notification;
import com.alizarion.reference.social.entities.notification.Observer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author selim@openlinux.fr.
 */
@Entity
@Table(name = Account.TABLE_PREFIX+"_account")
@NamedQueries({@NamedQuery(name = Account.FIND_BY_USERNAME,
        query = "select ac from Account" +
                " ac where ac.credential.username = :username"),
        @NamedQuery(name = Account.FIND_BY_EMAIL,
                query = " select ac from Account ac where " +
                        "ac.credential.person.primaryElectronicAddress.emailAddress = " +
                        ":email"),
        @NamedQuery(name = Account.FIND_BY_CREDENTIAL_ID,
                query = "select ac  from Account " +
                        " ac where ac.credential.id = :id")
})
public class Account  extends Observer implements Serializable {

    public static final String TABLE_PREFIX = "crossview";

    public final static String FIND_BY_USERNAME = "FIND_BY_USERNAME";

    public final static String FIND_BY_EMAIL = "FIND_BY_EMAIL";

    public final static String FIND_BY_CREDENTIAL_ID = "FIND_BY_CREDENTIAL_ID";



    private static final long serialVersionUID = -3690362052349239633L;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @OneToOne(
            targetEntity = Credential.class,
            cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "credential_id")
    private Credential credential;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;


    @Column(nullable = false)
    private Date creationDate;




    protected Account() {
    }

    public Account(final String userName,
                   final String password,
                   ElectronicAddress email,
                   final Set<Role> roles) {
        this.profile  = new Profile(this);
        this.credential = new Credential(userName,password,email,roles);
        this.user = new User(this);
        this.creationDate = new Date();
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public Date getCreationDate() {
        return creationDate;
    }


    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getUser() {
        return user;
    }

    public void printNotifications(){
        System.out.println(this.credential.getUsername() +" has "
                +getUser().getObservers().size() + " followers");

        System.out.println("and is following  " +this.user.getFollowing().size());

        System.out.println("and has "+this.user.getStream().size()+" publication(s) in stream");

        for (Notification notification :  getNotifications()){
            System.out.println(this.credential.getUsername() +" => "+
                    ((Account) notification.getNotifier()).
                            getCredential().getUsername() +" has "+
                    notification.getType() + " your " +
                    notification.getSubject().getSubjectType()+
                    ":"+ notification.getSubject().getId());
        }

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account)) return false;
        if (!super.equals(o)) return false;

        Account account = (Account) o;

        if (creationDate != null ? !creationDate.equals(account.creationDate) : account.creationDate != null)
            return false;
        if (credential != null ? !credential.equals(account.credential) : account.credential != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (credential != null ? credential.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        return result;
    }
}
