-- Database generation script for all tables.
CREATE TABLE timestamps
(
  id                BINARY(16)  NOT NULL,
  date_modified     DATETIME(3) NOT NULL,
  date_created      DATETIME(3) NOT NULL,
  date_invalidated  DATETIME(3),
  hash              VARCHAR(64) NOT NULL,
  root_hash         VARCHAR(64) NOT NULL,
  transaction       VARCHAR(64) NOT NULL,
  currency          INTEGER NOT NULL,
  timestamp_status  INTEGER NOT NULL,
  blockchain_timestamp  DATETIME(3),
  certificate       BLOB,
  PRIMARY KEY (id)
);

-- Create answer-timestamp table
CREATE TABLE answers_timestamps_mapping
(
  id                BINARY(16)  NOT NULL,
  date_modified     DATETIME(3) NOT NULL,
  date_created      DATETIME(3) NOT NULL,
  date_invalidated  DATETIME(3),
  timestamp_id      BINARY(16) NOT NULL,
  answer_id         BINARY(16) NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_mapping_timestamp_timestamp (timestamp_id),
  INDEX idx_mapping_timestamp_answer (answer_id),
  CONSTRAINT fk_mapping_answer_timestamp
    FOREIGN KEY (timestamp_id)
    REFERENCES timestamps (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_mapping_timestamp_answer
    FOREIGN KEY (answer_id)
    REFERENCES answers (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- Create indexes for all columns in WHERE, ORDER BY, GROUP BY conditions (performance)
CREATE INDEX idx_answers_timestamps_mapping_date_invalidated ON answers_timestamps_mapping (date_invalidated);
CREATE INDEX idx_timestamps_date_invalidated ON timestamps (date_invalidated);
CREATE INDEX idx_timestamps_hash ON timestamps (hash);
CREATE INDEX idx_timestamps_timestamp_status ON timestamps (timestamp_status);
