package com.alizarion.crossview.web.dto;

import com.alizarion.crossview.entities.Tag;

import javax.persistence.EntityManager;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author selim@openlinux.fr.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class TagDTO {

    @XmlAttribute(name = "text")
    private String value;

    public TagDTO(Tag tag) {
        this.value = tag.getLabel();
    }


    public TagDTO() {
    }

    public Tag getVO(EntityManager entityManager){
        Tag tag =  entityManager.find(Tag.class,value);
        if (tag == null){
            tag = new Tag(value);
        }
        return tag;
    }
}
