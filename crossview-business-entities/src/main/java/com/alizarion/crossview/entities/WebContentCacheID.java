package com.alizarion.crossview.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author selim@openlinux.fr.
 */
@Embeddable
public class WebContentCacheID  implements Serializable{

    private static final long serialVersionUID = -1892003558125997986L;


    @Enumerated(EnumType.STRING)
    @Column(name = "view_port")
    private DeviceViewPort viewPort;

    @ManyToOne(optional = false)
    @JoinColumn(name = "web_content_id")
    private WebContent webContent;

    protected WebContentCacheID() {
    }

    public WebContentCacheID(DeviceViewPort viewPort, WebContent webContent) {
        this.viewPort = viewPort;
        this.webContent = webContent;
    }

    public DeviceViewPort getViewPort() {
        return viewPort;
    }

    public void setViewPort(DeviceViewPort viewPort) {
        this.viewPort = viewPort;
    }

    public WebContent getWebContent() {
        return webContent;
    }

    public void setWebContent(WebContent webContent) {
        this.webContent = webContent;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WebContentCacheID)) return false;

        WebContentCacheID that = (WebContentCacheID) o;

        return viewPort == that.viewPort &&
                !(webContent != null ? !webContent.equals(that.webContent) :
                        that.webContent != null);

    }

    @Override
    public int hashCode() {
        int result = viewPort != null ? viewPort.hashCode() : 0;
        return result;
    }
}
