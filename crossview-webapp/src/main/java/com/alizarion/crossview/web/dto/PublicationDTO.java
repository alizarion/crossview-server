package com.alizarion.crossview.web.dto;

import com.alizarion.crossview.entities.Account;
import com.alizarion.crossview.entities.Publication;
import com.alizarion.crossview.entities.Tag;
import com.alizarion.crossview.entities.WebContent;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author selim@openlinux.fr.
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "publication")
public class PublicationDTO extends SubjectDTO implements Serializable {

    private static final long serialVersionUID = -9218294907818275796L;

    @XmlAttribute(name = "id")
    private String id;

    @XmlAttribute(name = "tags")
    private Set<TagDTO> tags = new HashSet<>();

    @XmlElement(name = "content")
    private WebContentDTO content;

    @XmlElement(name = "publisher")
    private UserDTO publisher;

    @XmlElement(name = "publicationTime")
    private Long publicationTime;


    @XmlElement(name = "lastUpdateTime")
    private Long lastUpdateTime;

    @XmlAttribute(name = "observers")
    private Set<String> observers =  new HashSet<>();

    @XmlAttribute(name = "linksCount")
    private Integer linksCount;

    public PublicationDTO() {
    }

    public PublicationDTO(Publication publication) throws MalformedURLException {
        if(publication!= null){
            for (Tag tag : publication.getTags()){
                this.tags.add(new TagDTO(tag));
            }
            this.publicationTime = publication.getCreationDate().getTime()/1000;
            this.lastUpdateTime = publication.getLastUpdate().getTime()/1000;

            this.content = new WebContentDTO((WebContent)publication.getContent());
            this.publisher =  new UserDTO(publication.getPublisher());
          //  this.linksCount = publication.getLinksCount();

            this.id =  publication.getId().toString();
            for (Account account :  publication.getObservers()){
                if(!account.getCredential().getUsername().equals(this.publisher.getUsername())) {
                    observers.add(account.getCredential().getUsername());
                }
            }
        }
    }

    public Publication getVO(EntityManager entityManager){
        Publication  publication;
        if (!StringUtils.isEmpty(this.id)){
            publication = entityManager.find(Publication.class,Long.parseLong(this.id));
            publication.setContent(content.getVO(entityManager));

        }  else {
            publication = new Publication(content.getVO(entityManager));
        }

        for (TagDTO tagDTO : this.tags){
            publication.addTag(tagDTO.getVO(entityManager));
        }

        return publication;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<TagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags;
    }

    public WebContentDTO getContent() {
        return content;
    }

    public void setContent(WebContentDTO content) {
        this.content = content;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublicationDTO)) return false;

        PublicationDTO that = (PublicationDTO) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
