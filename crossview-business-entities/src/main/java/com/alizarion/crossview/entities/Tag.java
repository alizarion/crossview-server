package com.alizarion.crossview.entities;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author selim@openlinux.fr.
 */
@Entity
@Table(name = Account.TABLE_PREFIX + "_tag")
public class Tag implements Serializable{

    private static final long serialVersionUID = 7826229580623646132L;

    @Id
    @Column(unique = true,nullable = false,length = 60)
    private String label;

    @Column(name = "creation_date")
    private Date creationDate;



    public Tag(){

    }

    public Tag(String key) {
        key = StringUtils.stripAccents(key.replaceAll("[^\\p{L}\\p{Nd}]+", ""));
        this.label = key;
        this.creationDate = new Date();

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Date getCreationDate() {
        return creationDate;
    }



    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tag)) return false;

        Tag tag = (Tag) o;

        return !(label != null ? !label.equals(tag.label) : tag.label != null);

    }

    @Override
    public int hashCode() {
        return label != null ? label.hashCode() : 0;
    }
}
