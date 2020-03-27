package io.hepp.cov2words.domain.entity;

import lombok.*;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.UUID;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@Entity
@Table(name = "answers_words_mapping")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AnswerWordMappingEntity {
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

    @Column(name = "order_id")
    private int order;

    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH})
    @JoinColumn(name = "word_id", referencedColumnName = "id")
    private WordEntity word;

    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH})
    @JoinColumn(name = "answer_id", referencedColumnName = "id")
    private AnswerEntity answerEntity;
}
