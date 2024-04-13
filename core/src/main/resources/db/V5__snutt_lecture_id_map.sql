ALTER TABLE semester_lecture DROP COLUMN snutt_id;
CREATE TABLE IF NOT EXISTS snutt_lecture_id_map
(
    id                    BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    created_at            DATETIME(6)  NOT NULL,
    updated_at            DATETIME(6)  NOT NULL,
    snutt_id              CHAR(24) DEFAULT NULL,
    semester_lecture_id   BIGINT       NOT NULL,
    CONSTRAINT snutt_lecture_id_map__unique__snutt_id
        UNIQUE (snutt_id),
    CONSTRAINT snutt_lecture_id_map__fk__semester_lecture_id
        FOREIGN KEY (semester_lecture_id) REFERENCES semester_lecture (id)
);
