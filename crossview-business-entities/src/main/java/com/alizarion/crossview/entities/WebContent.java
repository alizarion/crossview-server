package com.alizarion.crossview.entities;

import com.alizarion.reference.filemanagement.entities.ImageManagedFile;
import com.alizarion.reference.location.entities.WebAddress;
import com.google.common.net.InternetDomainName;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author selim@openlinux.fr.
 */

@Entity
@DiscriminatorValue(value = WebContent.TYPE )
@XmlAccessorType(XmlAccessType.NONE)
@NamedQueries({@NamedQuery(name = WebContent.FIND_BY_ID,
        query = "select wc from WebContent wc " +
                "where wc.contentId = :url"),
        @NamedQuery(name = WebContent.FIND_BY_WORD,
                query = "select wContent from WebContent " +
                        " wContent where wContent.title like :word or " +
                        " wContent.description like :word"),
        @NamedQuery(name = WebContent.FIND_PLAIN_TEXT,
                query = "select wContent from WebContent wContent where wContent.text like :word")
})

public class WebContent extends PublicationContent  {

    private static final long serialVersionUID = 4094361590141146101L;

    public static final String FIND_BY_ID = "WebContent.FIND_BY_ID";

    public static final String FIND_BY_WORD =  "WebContent.FIND_BY_WORD";

    public static final String FIND_PLAIN_TEXT=  "WebContent.FIND_PLAIN_TEXT";


    public static final String TYPE = "web" ;

    @Column(name = "title")
    @XmlAttribute(name = "title")
    private String title;

    @OneToOne(cascade = {CascadeType.MERGE,
            CascadeType.PERSIST})
    @JoinColumn(name = "original_url_id")
    private WebAddress originalUrl;

    @Column(name = "canonical_url")
    private String canonicalUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "website_id")
    @XmlElement(name = "host")
    private WebHost webHost;

    @OneToMany(fetch = FetchType.EAGER,cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Set<WebContentCache> cache = new HashSet<>();

    @Column(name = "image_url",length = 10240)
    @XmlAttribute(name = "image_url")
    private String imageURL;

    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST}
            ,optional = true)
    private ImageManagedFile image;

    @Column(name = "text",
            nullable = true,
            length = 30960)
    @XmlAttribute(name = "text")
    private String text;

    @Column(name = "description",nullable = true,length = 20480)
    @XmlAttribute(name = "description")
    private String description;

    @Column(name = "publish_date",
            nullable = true)
    private Date publishDate;




    protected WebContent(){
    }



    protected WebContent(URL url) {
        super(url);
    }

    protected WebContent(Builder webContentBuilder){
        super(webContentBuilder.getUrl());
        this.title = webContentBuilder.getTitle();
        this.canonicalUrl = webContentBuilder.getCanonicalUrl()!= null
                ? webContentBuilder.getCanonicalUrl().toString() :null;
        this.description = webContentBuilder.getDescription();
        this.text =  webContentBuilder.getText();
        this.image =  webContentBuilder.getImage();
        this.webHost =  webContentBuilder.getWebHost();

        this.webHost.setRssFeed(webContentBuilder.getRssUrl());
        this.originalUrl = webContentBuilder.getOriginalUrl();
        this.videoUrl =  webContentBuilder.getVideoUrl() != null ?
                webContentBuilder.getVideoUrl().toString() : null;
        this.publishDate = webContentBuilder.getPublishDate();
        this.imageURL = webContentBuilder.getImageURL() != null ?
                webContentBuilder.getImageURL().toString() : null;
    }


    public static class Builder{

        private URL url;

        private String title;

        private WebAddress originalUrl;

        private URL canonicalUrl;

        private URL videoUrl;

        private RSSFeed rssUrl;

        private ImageManagedFile cache;

        private ImageManagedFile image;

        private String text;

        private String imageURL;

        private String homeTitle;

        private WebHost webHost;

        private String description;

        private Date publishDate;

        public Builder(final URL url,
                       final String title
        ) throws MalformedURLException, URISyntaxException {
            this.url = url;
            this.webHost =  new WebHost(
                    InternetDomainName
                            .from(url.getHost())
                            .topPrivateDomain().toString());
            this.title = title;
        }

        public Builder setOriginalUrl(String originalUrl)
                throws MalformedURLException, URISyntaxException {
            this.originalUrl = new WebAddress(originalUrl);
            return this;
        }

        public Builder setCanonicalUrl(String canonicalUrl)
                throws MalformedURLException {
            this.canonicalUrl = StringUtils.isEmpty(canonicalUrl) ?
                    null : new URL(canonicalUrl);
            return this;
        }

        public Builder setVideoUrl(String  videoUrl)
                throws MalformedURLException {
            this.videoUrl =  StringUtils.isEmpty(videoUrl) ?
                    null : new URL(videoUrl);
            return this;
        }

        public Builder setImageUrl(final String imageURL)
                throws MalformedURLException {
            this.imageURL = imageURL;
            return this;
        }


        public Builder setRssUrl(String rssUrl) throws MalformedURLException {

            try {
                this.rssUrl = StringUtils.isEmpty(rssUrl) ?
                        null : new RSSFeed(new URL(rssUrl));
            } catch (MalformedURLException e) {
                this.rssUrl = new RSSFeed(new URL(this.url.toString() +'/'+rssUrl)) ;
            }
            return this;
        }

        public Builder setHomeTitle(String title){
            this.homeTitle = title;
            return this;
        }


        public String getImageURL() {
            return imageURL;
        }

        public Builder setCache(ImageManagedFile cache) {
            this.cache = cache;
            return this;
        }

        public Builder setImage(ImageManagedFile image) {
            this.image = image;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }



        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setPublishDate(Date publishDate) {
            this.publishDate = publishDate;
            return this;
        }

        public URL getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }

        public WebAddress getOriginalUrl() {
            return originalUrl;
        }

        public URL getCanonicalUrl() {
            return canonicalUrl;
        }

        public URL getVideoUrl() {
            return videoUrl;
        }

        public RSSFeed getRssUrl() {
            return rssUrl;
        }

        public ImageManagedFile getCache() {
            return cache;
        }

        public ImageManagedFile getImage() {
            return image;
        }

        public String getText() {
            return text;
        }

        public WebHost getWebHost() {
            if (StringUtils.isEmpty(this.homeTitle)){
                this.webHost.setTitle(this.homeTitle);
            }
            return webHost;
        }

        public String getDescription() {
            return description;
        }

        public Date getPublishDate() {
            return publishDate;
        }

        public WebContent build(){
            return new WebContent(this);
        }
    }


    public String getTitle() {
        return title;
    }

    public WebAddress getOriginalUrl() {
        return originalUrl;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }


    public ImageManagedFile getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public WebHost getWebHost() {
        return webHost;
    }

    public String getDescription() {
        return description;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setWebHost(WebHost webHost){
        this.webHost =  webHost;
    }

    public void setImage(ImageManagedFile image) {
        this.image = image;
    }

    public void addCache(WebContentCache  contentCache){
        this.cache.add(contentCache);
    }

    public Set<WebContentCache> getCache() {
        return cache;
    }

    public URL getImageURL() throws MalformedURLException {
        return imageURL != null ? new URL(imageURL):null;
    }

    public String getImageURLAsString() {
        return imageURL;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WebContent)) return false;
        return  super.equals(o);


    }


    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (title != null ? title.hashCode() : 0);

        return result;
    }
}
