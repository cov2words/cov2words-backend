package io.hepp.cov2words.domain.entity;

import lombok.*;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@Entity
@Table(name = "words")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class WordEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID id;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "date_created")
    private DateTime dateCreated;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "date_modified")
    private DateTime dateModified;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "date_invalidated")
    private DateTime dateInvalidated;

    @Column(name = "word")
    private String word;

    @Column(name = "language")
    private String language;

    @Column(name = "position")
    private long position;
}
