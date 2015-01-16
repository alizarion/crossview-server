package com.alizarion.crossview.web.dto;


import com.alizarion.crossview.entities.WebContent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author selim@openlinux.fr.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CrossLinkDTO {

    @XmlElement(name = "linkedPublications")
    private List<LinkedPublicationDTO> linkedPublications = new ArrayList<>();

    @XmlElement(name = "content")
    private WebContentDTO content;


    public CrossLinkDTO( WebContent content,Set<LinkedPublicationDTO> linkedPublicationDTO  ) throws MalformedURLException {

        this.content = new WebContentDTO(content);
        this.linkedPublications.addAll(linkedPublicationDTO);
    }

    public WebContentDTO getContent() {
        return content;
    }

    public void setContent(WebContentDTO content) {
        this.content = content;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CrossLinkDTO)) return false;

        CrossLinkDTO that = (CrossLinkDTO) o;

        return !(content != null ? !content.equals(that.content) : that.content != null);

    }

    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }
}
