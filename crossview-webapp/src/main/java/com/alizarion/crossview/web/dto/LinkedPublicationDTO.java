package com.alizarion.crossview.web.dto;

import com.alizarion.crossview.entities.Publication;
import com.alizarion.crossview.entities.User;

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
public class LinkedPublicationDTO {

    @XmlElement(name = "linkedBy")
    private List<UserDTO> linkedBy = new ArrayList<>();

    @XmlElement(name = "linkedWith")
    private PublicationDTO linkedWith;


    public LinkedPublicationDTO() {

    }

    public LinkedPublicationDTO( Publication linkedWith,Set<User> linkedBy) throws MalformedURLException {
        this.linkedWith = new PublicationDTO(linkedWith);

        for (User user :  linkedBy){
            this.linkedBy.add(new UserDTO(user));
        }
    }
}
