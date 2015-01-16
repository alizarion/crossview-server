package com.alizarion.crossview.web.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.io.Serializable;

/**
 * @author selim@openlinux.fr.
 */
@XmlRootElement
@XmlSeeAlso({PublicationDTO.class, UserDTO.class})
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PublicationDTO.class),
        @JsonSubTypes.Type(value = UserDTO.class)
})
public abstract class SubjectDTO implements Serializable {
    private static final long serialVersionUID = -4025182825009143410L;

    protected SubjectDTO() {
    }
}
