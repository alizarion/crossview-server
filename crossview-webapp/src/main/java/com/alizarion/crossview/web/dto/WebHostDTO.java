package com.alizarion.crossview.web.dto;

import com.alizarion.crossview.entities.RSSFeed;
import com.alizarion.crossview.entities.WebHost;

import javax.persistence.EntityManager;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.net.MalformedURLException;

/**
 * @author selim@openlinux.fr.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class WebHostDTO {

    @XmlAttribute(name = "url")
    private String hostUrl;

    @XmlAttribute(name = "faviconImgId")
    private String faviconImage;

    @XmlAttribute(name = "title")
    private String title;

    @XmlElement(name = "feed")
    private FeedDTO feed;


    public WebHostDTO() {
    }

    public WebHostDTO(WebHost webHost) {
        this.hostUrl = webHost.getWebSiteId();
        this.faviconImage = webHost.getFaviconImage() != null
                ? webHost.getFaviconImage().getUUID(): null;
        this.title =  webHost.getTitle();
    }

    public void setFeed(RSSFeed feed)
            throws MalformedURLException {
        this.feed = new FeedDTO(feed);
        this.feed.setFeeds(feed.getContents());
    }

    public WebHost getVO(EntityManager em){
        return   em.find(WebHost.class,this.hostUrl);
    }
}
