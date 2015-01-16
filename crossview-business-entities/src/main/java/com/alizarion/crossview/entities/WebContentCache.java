package com.alizarion.crossview.entities;

import com.alizarion.reference.filemanagement.entities.ImageManagedFile;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author selim@openlinux.fr.
 */
@Entity
@Table(name = Account.TABLE_PREFIX+"_web_content_cache")
public class WebContentCache implements Serializable {

    private static final long serialVersionUID = -6588209871388011698L;


    @Id
    @TableGenerator(name=Account.TABLE_PREFIX+"_web_content_cache_SEQ", table="sequence",
            pkColumnName="SEQ_NAME", valueColumnName="SEQ_COUNT")
    @GeneratedValue(strategy=GenerationType.TABLE,
            generator=Account.TABLE_PREFIX+"_web_content_cache_SEQ")
    @Column(name = "cache_id")
    private Long id;


    @Embedded
    private WebContentCacheID webContentCacheId;

    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private ImageManagedFile cacheImage;


    protected WebContentCache() {
    }

    public WebContentCache(WebContentCacheID webContentCacheId) {
        this.webContentCacheId =  webContentCacheId;
    }


    public WebContentCacheID getWebContentCacheId() {
        return webContentCacheId;
    }

    public void setWebContentCacheId(WebContentCacheID webContentCacheId) {
        this.webContentCacheId = webContentCacheId;
    }

    public ImageManagedFile getCacheImage() {
        return cacheImage;
    }

    public void setCacheImage(ImageManagedFile cacheImage) {
        this.cacheImage = cacheImage;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WebContentCache)) return false;

        WebContentCache cache = (WebContentCache) o;

        return !(id != null ? !id.equals(cache.id) :
                cache.id != null) && !(webContentCacheId != null ?
                !webContentCacheId.equals(cache.webContentCacheId) :
                cache.webContentCacheId != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (webContentCacheId != null ? webContentCacheId.hashCode() : 0);
        return result;
    }
}
