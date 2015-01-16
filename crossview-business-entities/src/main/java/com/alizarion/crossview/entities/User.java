package com.alizarion.crossview.entities;

import com.alizarion.crossview.entities.notifications.FollowNotification;
import com.alizarion.crossview.entities.notifications.PublishNotification;
import com.alizarion.reference.social.entities.notification.Notification;
import com.alizarion.reference.social.entities.notification.Subject;

import javax.persistence.*;
import java.util.*;

/**
 * @author selim@openlinux.fr.
 */
@Entity
@Table(name = Account.TABLE_PREFIX+"_user")
@NamedQueries({@NamedQuery(name = User.FIND_BY_USERNAME,
        query = "select users from User users where " +
                "users.account.credential.username like :username")})
public class User extends Subject<Account> {

    private static final long serialVersionUID = -3483789499328644747L;

    private static final String TYPE = "user";

    public final static String FIND_BY_USERNAME = "User.FIND_BY_USERNAME";


    @OneToOne
    private Account account;


    @OneToMany(cascade = CascadeType.ALL)
    private Set<Publication> publications =  new HashSet<>();


    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE})
    @JoinTable(name = Account.TABLE_PREFIX+"_user_rss",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "rss_id"))
    private Set<RSSFeed> feeds =  new HashSet<>();



    protected  User(){

    }

    public User(Account userAccount) {
        this.account =  userAccount;
    }





    public List<User> getFollowing(){
        List<User> following  = new ArrayList<>();
        for(Subject subject :  getAccount().getSubjects()){
            if (subject instanceof User){
                following.add((User)subject);

            }
        }
        return following;
    }


    public List<User>  getFollowers(){
        Set<User> followers = new HashSet<>();
        for(Account account : getObservers()){
            followers.add(account.getUser());
        }
        return new ArrayList<>(followers);
    }

    public Integer getFollowingCount(){
        return getFollowing().size();
    }

    public Integer getFollowersCount(){
        return getObserverCount();
    }

    @Override
    public void notifyObservers(Notification notification) {


    }


    /**
     * Method to get all what user has publish or relay
     * @return
     */
    public List<Publication> getStream(){
        Set<Publication> result = new HashSet<>();
        for (Subject subject : this.account.getSubjects()){
            if (subject instanceof Publication){
                result.add((Publication)subject);
            }

        }
        return new ArrayList<>(result);
    }

    /**
     * Method to get all what user has publish or relay and
     * followed user stream
     * @return
     */
    public List<Publication> getSubscriptionStream(){
        Set<Publication> result = new HashSet<>();
        for (Subject subject : this.account.getSubjects()) {
            if (subject instanceof User) {
                result.addAll(((User) subject).getStream());
            }
        }
        return new ArrayList<>(result);
    }




    public Set<RSSFeed> getFeeds() {
        return feeds;
    }

    public void setFeeds(Set<RSSFeed> feeds) {
        this.feeds = feeds;
    }

    public void addRssFeed(RSSFeed rssFeed){
        this.feeds.add(rssFeed);
    }

    public void removeRssFeed(RSSFeed rssFeed){
        this.feeds.remove(rssFeed);
    }

    @Override
    public void notifyOwner(Notification notification) {
        this.account.notify(notification);
    }

    @Override
    public String getSubjectType() {
        return TYPE;
    }

    public void unPublish(final Publication publication){
        this.account.removeSubject(publication);
        publication.removeAllObservers();
        this.publications.remove(publication);

    }

    public Publication publish(final Publication publication){
        publication.setPublisher(this);
        this.account.addSubject(publication);
        // this.account.getSubjects().add(publication);
        notifyObservers(new PublishNotification(publication, null, this.account));

        this.publications.add(publication);
        return publication;

    }

    public void follow(User followedUser){
        this.account.addSubject(followedUser);
        followedUser.notifyOwner(new FollowNotification(
                followedUser,
                followedUser.getAccount(),
                this.getAccount()));
    }

    public void unFollow(User user){
        this.account.removeSubject(user);
    }



    public List<Publication> getUserPublicationsAsList(){
        return new ArrayList<>(this.publications);
    }

    public Set<Publication> getPublications() {
        return publications;
    }

    public void relay(final Publication publication){
        publication.addRepeater(this);
        this.account.addSubject(publication);
    }

    public void unRelay(final Publication publication){
        this.account.removeSubject(publication);
    }



    public Account getAccount() {
        return  this.account;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        if (account != null ? !account.equals(user.account) : user.account != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (account != null ? account.hashCode() : 0);
        return result;
    }
}
