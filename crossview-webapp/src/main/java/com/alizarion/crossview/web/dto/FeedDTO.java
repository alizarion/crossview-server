package com.alizarion.crossview.web.dto;

import com.alizarion.crossview.entities.RSSFeed;
import com.alizarion.crossview.entities.WebContent;
import com.alizarion.crossview.entities.WebHost;

import javax.persistence.EntityManager;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author selim@openlinux.fr.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class FeedDTO {

    @XmlAttribute(name = "url")
    private String url;

    @XmlElement(name = "host")
    private WebHostDTO host;

    @XmlElement(name = "feeds")
    private Set<WebContentDTO> feeds = new HashSet<>();


    private Integer subscriberCount;

    public FeedDTO(RSSFeed rssFeed )  {
        this.url = rssFeed.getUrlId();
        //  this.subscriberCount = rssFeed.getSubscribersCount();
        // TODO implementer externalizable resources
        setHost(rssFeed.getWebHost());
    }


    public void setHost(WebHost hostVO)
    {
        this.host =  new WebHostDTO(hostVO);
    }

    public void setFeeds(Set<WebContent> feedsVO)
            throws MalformedURLException {
        for (WebContent webContent :  feedsVO){
            this.feeds.add(new WebContentDTO(webContent));
        }
    }


    public FeedDTO() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(Integer subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public RSSFeed getVO(EntityManager em) throws MalformedURLException {
        RSSFeed rssFeed =  em.find(RSSFeed.class,this.url);

        if (rssFeed == null){
            rssFeed = new RSSFeed(new URL(this.url));
            rssFeed =  em.merge(rssFeed);

        }
        return rssFeed;
    }
}
