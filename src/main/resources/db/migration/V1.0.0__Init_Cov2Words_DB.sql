-- TODO Add database generation script for all tables
CREATE TABLE word_index
(
  id                BINARY(16)  NOT NULL,
  date_modified     DATETIME(3) NOT NULL,
  date_created      DATETIME(3) NOT NULL,
  date_invalidated  DATETIME(3),
  language          VARCHAR(4) NOT NULL,
  position          BIGINT,
  total_items       BIGINT,
  PRIMARY KEY (id)
);

-- TODO CREATE WORD table
CREATE TABLE words
(
  id                BINARY(16)  NOT NULL,
  date_modified     DATETIME(3) NOT NULL,
  date_created      DATETIME(3) NOT NULL,
  date_invalidated  DATETIME(3),
  language          VARCHAR(4) NOT NULL,
  word              VARCHAR(32) NOT NULL,
  position          BIGINT,
  PRIMARY KEY (id)
);
-- TODO Create answer table
-- TODO create answer-word table

-- TODO Create indexes for all columns in WHERE, ORDER BY, GROUP BY conditions
CREATE INDEX idx_word_index_language ON word_index (language);
CREATE INDEX idx_word_index_date_invalidated ON word_index (date_invalidated);

CREATE INDEX idx_words_language ON words (language);
CREATE INDEX idx_words_position ON words (position);
CREATE INDEX idx_words_date_invalidated ON words (date_invalidated);
