package com.alizarion.crossview.web.dto;

import com.alizarion.crossview.entities.WebContentCache;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author selim@openlinux.fr.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class WebContentCacheDTO {

    @XmlAttribute(name = "userAgent")
    private String userAgent;

    @XmlAttribute(name = "width")
    private String width;

    @XmlAttribute(name = "image")
    private ImageManagedFileDTO image;

    @XmlAttribute(name = "viewportType")
    private String viewportType;


    public WebContentCacheDTO() {
    }

    public WebContentCacheDTO(WebContentCache cache) {
        this.userAgent = cache.getWebContentCacheId()
                .getViewPort().getUserAgent().getUserAgent();

        this.width = cache.getWebContentCacheId()
                .getViewPort().getViewPort().toString();
        this.image =  new ImageManagedFileDTO(cache.getCacheImage());

        this.viewportType =cache.getWebContentCacheId()
                .getViewPort().name();
    }
}
