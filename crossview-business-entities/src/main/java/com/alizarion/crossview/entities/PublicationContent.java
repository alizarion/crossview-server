package com.alizarion.crossview.entities;


import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author selim@openlinux.fr.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = Account.TABLE_PREFIX+"_publication_content")
@DiscriminatorColumn(name = "type")
@XmlRootElement(name = "webcontent")
public abstract class PublicationContent implements Serializable {


    private static final long serialVersionUID = -6200626319069752500L;


    @Id
    @Column(name = "id")
    private String contentId;

    @OneToMany(mappedBy = "content",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<Publication> publications = new HashSet<>();

    @Column(name = "creation_date",
            nullable = false)
    private Date creationDate;

    @Transient
    private transient Integer publicationsCount;

    protected PublicationContent() {
        this.creationDate =  new Date();
    }


    public void addToPublication(Publication publication){
        publication.setContent(this);
        this.publications.add(publication);
    }

    protected PublicationContent(final URL contentId) {
        this.contentId = contentId.toString();
        this.creationDate = new Date();
    }




    public Set<Publication> getPublications() {
        return publications;
    }

    public Integer getPublicationCount(){
        return this.publicationsCount;
    }

    public void setPublications(Set<Publication> publications) {
        this.publications = publications;
    }

    public String getContentId() {
        return contentId;
    }



    public URL getContentIdAsUrl() throws MalformedURLException {
        return new URL(contentId);
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PublicationContent)) return false;

        PublicationContent that = (PublicationContent) o;

        return !(contentId != null ? !contentId.equals(that.contentId) : that.contentId != null);

    }



    @PostLoad
    public void postLoad(){
        this.publicationsCount = this.publications.size();
    }

    @Override
    public int hashCode() {
        return contentId != null ? contentId.hashCode() : 0;
    }
}
