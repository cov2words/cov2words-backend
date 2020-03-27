package io.hepp.cov2words.domain.entity;

import lombok.*;
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

    @Column(name = "date_created")
    private DateTime dateCreated;

    @Column(name = "date_modified")
    private DateTime dateModified;

    @Column(name = "date_invalidated")
    private DateTime dateInvalidated;

    @Column(name = "position")
    private BigInteger position;

    @Column(name = "total_items")
    private long totalItems;

    @Column(name = "language")
    private String language;
}
