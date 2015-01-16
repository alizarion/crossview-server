package com.alizarion.crossview.entities;

import com.alizarion.reference.filemanagement.entities.ImageManagedFile;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * @author selim@openlinux.fr.
 */
@Entity
@Table(name = Account.TABLE_PREFIX+"_website")
@XmlAccessorType(XmlAccessType.NONE)
public class WebHost implements Serializable {

    private static final long serialVersionUID = -1590574708239281703L;

    @Id
    @Column(name = "web_site_id")
    @XmlAttribute(name = "url")
    private String webSiteId;


    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST},mappedBy = "webHost")
    private RSSFeed rssFeed;

    @Column(name = "home_title")
    private String title;

    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE}
            ,optional = true)
    private ImageManagedFile faviconImage;

    protected WebHost() {
    }

    public WebHost(final String webSiteId)
            throws MalformedURLException,
            URISyntaxException {
        this.webSiteId = webSiteId;
    }

    public String getWebSiteId() {
        return webSiteId;
    }

    @XmlAttribute(name = "has_favicon")
    public boolean hasFavicon(){
        return  (this.faviconImage != null);
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public ImageManagedFile getFaviconImage() {
        return faviconImage;
    }

    public void setRssFeed(RSSFeed rssFeed) {
        if (rssFeed != null){
            rssFeed.setWebHost(this);
        }
        this.rssFeed = rssFeed;
    }

    public String getTitle() {
        return title;
    }

    public RSSFeed getRssFeed() {
        return rssFeed;
    }

    public void setFaviconImage(ImageManagedFile faviconImage) {
        this.faviconImage = faviconImage;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WebHost)) return false;

        WebHost webSite = (WebHost) o;

        return !(webSiteId != null ?
                !webSiteId.equals(webSite.webSiteId) :
                webSite.webSiteId != null);

    }

    @Override
    public int hashCode() {
        return webSiteId != null ? webSiteId.hashCode() : 0;
    }
}
