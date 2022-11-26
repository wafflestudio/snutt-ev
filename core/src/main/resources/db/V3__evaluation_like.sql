DROP TABLE evaluation_comment;

ALTER TABLE lecture_evaluation DROP dislike_count;

CREATE TABLE IF NOT EXISTS evaluation_like
(
    id                    BIGINT AUTO_INCREMENT
    PRIMARY KEY,
    created_at            DATETIME(6)  NOT NULL,
    updated_at            DATETIME(6)  NOT NULL,
    user_id               VARCHAR(255) NOT NULL,
    lecture_evaluation_id BIGINT       NOT NULL,
    CONSTRAINT evaluation_like__unique__lecture_evaluation__user_id
    UNIQUE (lecture_evaluation_id, user_id),
    CONSTRAINT evaluation_like__fk__lecture_evaluation_id
    FOREIGN KEY (lecture_evaluation_id) REFERENCES lecture_evaluation (id)
);
