package com.alizarion.crossview.web.dto;

import com.alizarion.crossview.entities.WebContent;
import com.alizarion.crossview.entities.WebContentCache;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author selim@openlinux.fr.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class WebContentDTO {

    @XmlAttribute(name = "url")
    private String url;

    @XmlAttribute(name = "text")
    private String text;

    @XmlAttribute(name = "title")
    private String title;

    @XmlAttribute(name = "host")
    private String host;

    @XmlAttribute(name = "creationTime")
    private Integer creationTime;

    @XmlAttribute(name = "description")
    private String description;

    @XmlAttribute(name = "temporaryImageUrl")
    private String temporaryImageUrl;

    @XmlElement(name = "imageCache")
    private ImageManagedFileDTO image;

    @XmlElement(name = "rss")
    private FeedDTO rss;

    @XmlElement(name = "cache")
    private Set<WebContentCacheDTO> cache =  new HashSet<>();

    @XmlElement(name = "publicationCount")
    private Integer publicationCount;

    protected WebContentDTO() {
    }

    public WebContentDTO(WebContent content) throws MalformedURLException {
        this.url =  content.getContentId();
        this.text = content.getText();
        this.description = content.getDescription();
        this.title =  content.getTitle();
        this.creationTime =  (int) content.getCreationDate().getTime()/1000;
        this.host =  content.getWebHost().getWebSiteId();
        this.publicationCount = content.getPublicationCount();
        this.rss = content.getWebHost().getRssFeed() != null ?  new FeedDTO(content.getWebHost().getRssFeed()) : null;
        this.temporaryImageUrl = StringUtils.isEmpty(content.getImageURLAsString()) ? null: content.getImageURLAsString();
        this.image = content.getImage() != null ? new ImageManagedFileDTO(content.getImage()) : null ;
        for(WebContentCache cache : content.getCache()){
            this.cache.add(new WebContentCacheDTO(cache));
        }


    }

    public WebContent getVO(EntityManager em){
        return  em.find(WebContent.class,this.url);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemporaryImageUrl() {
        return temporaryImageUrl;
    }

    public void setTemporaryImageUrl(String temporaryImageUrl) {
        this.temporaryImageUrl = temporaryImageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
