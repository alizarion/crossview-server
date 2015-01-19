package com.alizarion.crossview.entities;

import com.alizarion.crossview.entities.notifications.LinkType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author selim@openlinux.fr.
 */
@Entity
@Table(name = Account.TABLE_PREFIX+"_publication_link",uniqueConstraints={@UniqueConstraint(columnNames={"linked_content_id","linked_with_content_id","linker_id","type"})})
public class CrossLink  implements Serializable {


    private static final long serialVersionUID = 4423604633229546121L;

    @Id
    @TableGenerator(name=Account.TABLE_PREFIX+"_publication_link_SEQ", table="sequence",
            pkColumnName="SEQ_NAME", valueColumnName="SEQ_COUNT")
    @GeneratedValue(strategy=GenerationType.TABLE,
            generator=Account.TABLE_PREFIX+"_publication_link_SEQ")
    @Column(name = "link_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private LinkType type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "linker_id")
    private User linker;

    @ManyToOne(optional = false)
    @JoinColumn(name = "publisher_id")
    private User publisher;

    @Column(name = "creation_date")
    private Date creationDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "linked_content_id")
    private Publication content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "linked_with_content_id")
    private Publication linkedWith;

    protected CrossLink() {
    }


    public CrossLink(final LinkType type,
                     final Publication publication,
                     final Publication linkedWith,
                     final User linker) {
        this.type = type;
        this.content = publication;
        this.linkedWith = linkedWith;
        this.linker = linker;
        this.publisher = publication.getPublisher();
        this.creationDate = new Date();
    }

    public Long getId() {
        return id;
    }

    public LinkType getType() {
        return type;
    }

    public User getLinker() {
        return linker;
    }

    public void setLinker(User linker) {
        this.linker = linker;
    }



    public User getPublisher() {
        return publisher;
    }

    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

    public Publication getContent() {
        return content;
    }

    public void setContent(Publication content) {
        this.content = content;
    }

    public Publication getLinkedWith() {
        return linkedWith;
    }

    public void setLinkedWith(Publication linkedWith) {
        this.linkedWith = linkedWith;
    }

    @PreRemove
    public void preRemove(){
        this.linkedWith.removeFromLink(this);
        this.linkedWith.removeFromLinkedWith(this);
        this.content.removeFromLink(this);
        this.content.removeFromLinkedWith(this);

    }

    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CrossLink)) return false;

        CrossLink crossLink = (CrossLink) o;

        return !(content != null ?
                !content.equals(crossLink.content) :
                crossLink.content != null) &&
                !(linkedWith != null ? !linkedWith.equals(crossLink.linkedWith) :
                        crossLink.linkedWith != null) &&
                !(linker != null ? !linker.equals(crossLink.linker) :
                        crossLink.linker != null) &&
                type == crossLink.type;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (linker != null ? linker.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (linkedWith != null ? linkedWith.hashCode() : 0);
        return result;
    }
}
