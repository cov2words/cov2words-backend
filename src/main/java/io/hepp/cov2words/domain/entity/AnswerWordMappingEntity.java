package io.hepp.cov2words.domain.entity;

import lombok.*;
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

    @Column(name = "date_created")
    private DateTime dateCreated;

    @Column(name = "date_modified")
    private DateTime dateModified;

    @Column(name = "date_invalidated")
    private DateTime dateInvalidated;

    @Column(name = "order")
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
