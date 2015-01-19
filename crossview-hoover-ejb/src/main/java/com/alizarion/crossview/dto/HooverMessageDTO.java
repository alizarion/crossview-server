package com.alizarion.crossview.dto;

import com.alizarion.crossview.entities.WebContent;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author selim@openlinux.fr.
 */
public class HooverMessageDTO implements Serializable {

    private static final long serialVersionUID = 7375526507700270183L;

    private URL contentId;

    private URL contentImageUrl;

    private URL faviconImageUrl;


    public HooverMessageDTO(WebContent webContent) throws MalformedURLException {
        this.contentId = new URL(webContent.getContentId());
        this.contentImageUrl = webContent.getImageURL();
        this.faviconImageUrl = new URL(webContent.getWebHost().
                getTempFaviconUrl());


    }


    public URL getContentId() {
        return contentId;
    }

    public void setContentId(URL contentId) {
        this.contentId = contentId;
    }

    public URL getContentImageUrl() {
        return contentImageUrl;
    }

    public void setContentImageUrl(URL contentImageUrl) {
        this.contentImageUrl = contentImageUrl;
    }

    public URL getFaviconImageUrl() {
        return faviconImageUrl;
    }

    public void setFaviconImageUrl(URL faviconImageUrl) {
        this.faviconImageUrl = faviconImageUrl;
    }
}
