package com.alizarion.crossview.web.dto;

import com.alizarion.reference.filemanagement.entities.ImageManagedFile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author selim@openlinux.fr.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ImageManagedFileDTO {

    @XmlAttribute(name = "imageId")
    private String imageId;

    @XmlAttribute(name = "width")
    private String width;

    @XmlAttribute(name = "height")
    private String height;

    @XmlAttribute(name = "weight")
    private String  weight;


    public ImageManagedFileDTO() {
    }

    public ImageManagedFileDTO(ImageManagedFile imageFile) {
        this.imageId = imageFile.getUUID();
        this.width = imageFile.getImageMetaData().getWidth().toString();
        this.height = imageFile.getImageMetaData().getHeight().toString();
        this.weight = imageFile.getWeight().toString();


    }
}
