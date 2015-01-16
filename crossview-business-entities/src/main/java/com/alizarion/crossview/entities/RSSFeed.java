package com.alizarion.crossview.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author selim@openlinux.fr.
 */
@Entity
@Table(name = Account.TABLE_PREFIX+"_rss_feed")
@XmlAccessorType(XmlAccessType.NONE)
public class RSSFeed implements Serializable {

    private static final long serialVersionUID = 5139800455232438697L;


    @Id
    @Column(name = "url_id")
    @XmlAttribute(name = "url")
    private String urlId;

    @Column(name = "creation_date",nullable = false)
    private Date creationDate;

    @OneToOne
    @JoinColumn(name = "host_id")
    private WebHost webHost;

    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Set<WebContent> contents = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "feeds")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private Set<User> subscribers = new HashSet<>();


    protected RSSFeed() {
    }


    public RSSFeed(URL urlId) {
        this.urlId = urlId.toString();
        this.creationDate =  new Date();
    }

    public WebHost getWebHost() {
        return webHost;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Set<WebContent> getContents() {
        return contents;
    }

    public String getUrlId() {
        return urlId;
    }

    public void setWebHost(WebHost webHost) {
        this.webHost = webHost;
    }

    public Set<User> getSubscribers() {
        return subscribers;
    }

    public void addWebContent(WebContent webContent){
        this.contents.add(webContent);
    }

    public Integer getSubscribersCount(){
        return this.subscribers.size();
    }

    public URL getUrl() throws MalformedURLException {
        return new URL(this.urlId);
    }

    public void setSubscribers(Set<User> subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RSSFeed)) return false;

        RSSFeed rssFeed = (RSSFeed) o;

        return !(urlId != null ? !urlId.equals(rssFeed.urlId) :
                rssFeed.urlId != null) && !(webHost != null ?
                !webHost.equals(rssFeed.webHost) : rssFeed.webHost != null);

    }

    @Override
    public int hashCode() {
        int result = urlId != null ? urlId.hashCode() : 0;
        result = 31 * result + (webHost != null ? webHost.hashCode() : 0);
        return result;
    }
}
