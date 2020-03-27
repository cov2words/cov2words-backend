package io.hepp.cov2words.domain.entity;

import lombok.*;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.UUID;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@Entity
@Table(name = "word_index")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class IndexEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID id;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "date_created")
    private DateTime dateCreated;

    @Column(name = "date_modified")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime dateModified;

    @Column(name = "date_invalidated")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime dateInvalidated;

    @Column(name = "position")
    private BigInteger position;

    @Column(name = "total_items")
    private long totalItems;

    @Column(name = "language")
    private String language;
}
