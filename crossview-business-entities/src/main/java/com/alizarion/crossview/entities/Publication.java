package com.alizarion.crossview.entities;

import com.alizarion.crossview.entities.notifications.CrossNotification;
import com.alizarion.crossview.entities.notifications.LinkType;
import com.alizarion.crossview.entities.notifications.RelayedNotification;
import com.alizarion.reference.social.entities.notification.Notification;
import com.alizarion.reference.social.entities.notification.Observer;
import com.alizarion.reference.social.entities.notification.Subject;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.*;

/**
 * @author selim@openlinux.fr.
 */
@Entity
@Table(name = Account.TABLE_PREFIX + "_publication")
@NamedQueries({@NamedQuery(name = Publication.FIND_BY_TAG,
        query = "select publication from Publication publication inner join " +
                "publication.tags tag where tag.label like :tag"),
        @NamedQuery(name = Publication.FIND_BY_PUBLISHER,
                query = "select publication from Publication publication  where " +
                        "publication.publisher.account.credential.username = :username"),
        @NamedQuery(name = Publication.FIND_BY_OBSERVER,
                query = "select publication from Publication publication inner join" +
                        " publication.observers observer where " +
                        "observer.id = :observerID"),
        @NamedQuery(name = Publication.FIND_BY_WEB_CONTENT_PUBLISHER,
                query = "select publication from Publication publication " +
                        " where publication.publisher.id = :publisherId and " +
                        "publication.content.contentId = :contentId")})
public  class  Publication extends Subject<Account> implements Serializable {


    private static final long serialVersionUID = 8414335879128473997L;

    public final static String FIND_BY_TAG = "Publication.FIND_BY_TAG";
    public final static String FIND_BY_WORD = "Publication.FIND_BY_WORD";
    public final static String FIND_BY_PUBLISHER = "Publication.FIND_BY_PUBLISHER";
    public final static String FIND_BY_OBSERVER = "Publication.FIND_BY_OBSERVER";
    public final static String FIND_BY_WEB_CONTENT_PUBLISHER = "Publication.FIND_BY_WEB_CONTENT_PUBLISHER";




    public static final String TYPE = "publication";

    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.REFRESH,CascadeType.MERGE,CascadeType.PERSIST})
    @JoinTable(name = Account.TABLE_PREFIX+ "_publication_tag",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "tag"))
    private Set<Tag> tags =  new HashSet<>();

    @ManyToOne(optional = false,cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "content_id")
    private PublicationContent content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User publisher;

    /*@ManyToMany
    @JoinTable(name = Account.TABLE_PREFIX + "_cross_link_join_table",
            joinColumns = @JoinColumn(name = "publication_id",nullable = false),
            inverseJoinColumns = @JoinColumn(name = "linked_with_publication_id",nullable = false))
    private Set<Publication> explicitLink = new HashSet<>();   */
    /*
    @ManyToMany
    @JoinTable(name = Account.TABLE_PREFIX + "_allege_join_table",
                joinColumns = @JoinColumn(name = "publication_id",nullable = false),
                inverseJoinColumns = @JoinColumn(name = "allege_publication_id",nullable = false))
    private Set<Publication> allege = new HashSet<>(); */

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "content")
    private Set<CrossLink> links = new HashSet<>();


    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "linkedWith")
    private Set<CrossLink> linkedWith = new HashSet<>();




    @Column(name = "last_update_date", nullable = false)
    private Date lastUpdate;


    protected Publication() {

    }

    public Publication(final PublicationContent content) {
        this.content = content;
        this.lastUpdate =  new Date();

    }

    public void update(){
        this.lastUpdate = new Date();
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Set<CrossLink> getLinks() {
        return links;
    }

    public void setLinks(Set<CrossLink> links) {
        this.links = links;
    }

    public Set<CrossLink> getLinkedWith() {
        return linkedWith;
    }

    public void setLinkedWith(Set<CrossLink> linkedWith) {
        this.linkedWith = linkedWith;
    }


    public void addLink(CrossLink link){
        this.links.add(link);
    }


    public void removeFromLink(CrossLink  link){
        this.links.remove(link);
    }

    public void removeFromLinkedWith(CrossLink  link){
        this.linkedWith.remove(link);
    }




    public Map<Publication,Set<User>> getAllRelatedContents(){
        Map<Publication,Set<User>> contents = new HashMap<>();
        for(Publication  publication :content.getPublications()){
            for (CrossLink link :  publication.getLinkedWith()) {
                if (contents.containsKey(link.getContent())) {
                    contents.get(link.getContent()).add(link.getLinker());
                } else {
                    Set<User> linkers = new HashSet<>();
                    linkers.add(link.getLinker());
                    contents.put(link.getContent(), linkers);

                }
            }


            for (CrossLink link :  publication.getLinks()){
                if (contents.containsKey(link.getLinkedWith())){
                    contents.get(link.getLinkedWith()).add(link.getLinker());

                } else {
                    Set<User> linkers = new HashSet<>();
                    linkers.add(link.getLinker());
                    contents.put(link.getLinkedWith(), linkers);

                }
            }
        }
        return contents;

    }

    public Integer getLinksCount(){
        Map<Publication,Set<User>> links = getAllRelatedContents();
        Set<PublicationContent> contents = new HashSet<>();
        for (Map.Entry<Publication,Set<User>> entry : links.entrySet()){
            contents.add(entry.getKey().getContent());
        }
        return contents.size();
    }


    public void addRepeater(User user){
        notifyOwner(
                new RelayedNotification(
                        this,
                        this.publisher.getAccount(),
                        user.getAccount()));

    }



    public User getPublisher() {
        return publisher;
    }

    @Override
    public void notifyObservers(Notification notification) {
        for(Observer observer :  getObservers()){
            if (!observer.getId().equals(notification.getNotifier().getId())){

                observer.notify(notification.getInstance(observer));
            }
        }
    }

    @Override
    public void notifyOwner(Notification notification) {
        this.publisher.getAccount().notify(notification);
    }


    public void crossView(final Publication  linkWith,final User linker){
        //notify observers   of the 2 contents
        for(Publication publication : this.content.getPublications()){
            publication.notifyObservers(new CrossNotification(
                    publication,
                    publication.getPublisher().getAccount(),
                    linker.getAccount()));
        }

        for(Publication publication : linkWith.getContent().getPublications()){
            publication.notifyObservers(new CrossNotification(
                    publication,
                    publication.getPublisher().getAccount(),
                    linker.getAccount()));
        }

        this.addLink(new CrossLink(LinkType.C, this, linkWith, linker));
    }

    public CrossLink unCrossView(final Publication  linkWith,final User linker){
        CrossLink link = new CrossLink(LinkType.C,this,linkWith,linker);
        for(CrossLink crossLink :  this.links){
            if (crossLink.equals(link)){
                this.links.remove(crossLink);
                linkWith.removeFromLink(crossLink);
                return crossLink;
            }
        }
        return null;

    }

    public Set<CrossLink> getAllCrossedLinks(){
        Set<CrossLink> crossLinks =  new HashSet<>();
        for (CrossLink link:  this.links){
            crossLinks.add(link);
        }
        for (CrossLink link : this.linkedWith){
            crossLinks.add(link);
        }
        return crossLinks ;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    @Override
    public String getSubjectType() {
        return TYPE;
    }


    public Set<Tag> getTags() {
        return tags;
    }

    @XmlElement(name="tags")
    public Set<String> getTagsAsString() {
        Set<String> stringTags =  new HashSet<>();
        for(Tag tag :  this.tags){
            stringTags.add(tag.getLabel());
        }
        return stringTags;
    }

    public PublicationContent getContent() {
        return content;
    }

    public void addTag(Tag tag){
        this.tags.add(tag);
    }

    public void setContent(PublicationContent content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Publication)) return false;
        if (!super.equals(o)) return false;

        Publication that = (Publication) o;

        return !(content != null ? !content.equals(that.content) : that.content != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    @PreRemove
    public void preRemove(){
        this.publisher.getPublications().remove(this);
        this.content.getPublications().remove(this);
        this.links.clear();
        this.linkedWith.clear();
        for(Observer observer: getObservers()){
            observer.removeSubject(this);
        }

    }
}
