package io.hepp.cov2words.domain.entity;

import lombok.*;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@Entity
@Table(name = "timestamps")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TimestampEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "hash")
    private String hash;

    @Column(name = "root_hash")
    private String rootHash;

    @Column(name = "transaction")
    private String transaction;

    @Column(name = "certificate")
    private byte[] certificate;

    @Column(name = "currency_id")
    private int currency;

    @Column(name = "status")
    private long status;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "date_created")
    private DateTime dateCreated;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "date_modified")
    private DateTime dateModified;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "date_invalidated")
    private DateTime dateInvalidated;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "blockchain_timestamp")
    private DateTime blockchainTimestamp;
}
