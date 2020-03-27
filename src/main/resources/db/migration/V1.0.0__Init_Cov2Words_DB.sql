-- Database generation script for all tables.
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

-- Create table that contains the signal words for combination.
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

-- Create answer table that contains the raw data answers from the questionnaire.
CREATE TABLE answers
(
  id                BINARY(16)  NOT NULL,
  date_modified     DATETIME(3) NOT NULL,
  date_created      DATETIME(3) NOT NULL,
  date_invalidated  DATETIME(3),
  answer            TEXT NOT NULL,
  source            VARCHAR(32) NOT NULL,
  PRIMARY KEY (id)
);
-- TODO create answer-word table (Foreign keys necessary)

-- Create indexes for all columns in WHERE, ORDER BY, GROUP BY conditions (performance)
CREATE INDEX idx_word_index_language ON word_index (language);
CREATE INDEX idx_word_index_date_invalidated ON word_index (date_invalidated);

CREATE INDEX idx_words_language ON words (language);
CREATE INDEX idx_words_position ON words (position);
CREATE INDEX idx_words_date_invalidated ON words (date_invalidated);

CREATE INDEX idx_answers_answer ON answers (answer);
CREATE INDEX idx_answers_date_invalidated ON answers (date_invalidated);
