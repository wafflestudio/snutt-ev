CREATE TABLE IF NOT EXISTS lecture
(
    id             BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    created_at     DATETIME(6)  NOT NULL,
    updated_at     DATETIME(6)  NOT NULL,
    academic_year  VARCHAR(255) NULL,
    category       VARCHAR(255) NULL,
    classification VARCHAR(255) NULL,
    course_number  VARCHAR(255) NULL,
    credit         INT          NOT NULL,
    department     VARCHAR(255) NULL,
    instructor     VARCHAR(255) NULL,
    title          VARCHAR(255) NULL,
    CONSTRAINT lecture__unique__course__instructor
        UNIQUE (course_number, instructor)
);

CREATE TABLE IF NOT EXISTS semester_lecture
(
    id             BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    created_at     DATETIME(6)  NOT NULL,
    updated_at     DATETIME(6)  NOT NULL,
    academic_year  VARCHAR(255) NULL,
    category       VARCHAR(255) NULL,
    classification VARCHAR(255) NULL,
    credit         INT          NOT NULL,
    extra_info     LONGTEXT     NULL,
    semester       INT          NOT NULL,
    `year`           INT          NOT NULL,
    lecture_id     BIGINT       NOT NULL,
    CONSTRAINT semester_lecture__unique__lecture__year__semester
        UNIQUE (lecture_id, `year`, semester),
    CONSTRAINT semester_lecture__fk__lecture_id
        FOREIGN KEY (lecture_id) REFERENCES lecture (id)
);

CREATE TABLE IF NOT EXISTS lecture_evaluation
(
    id                  BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    created_at          DATETIME(6)  NOT NULL,
    updated_at          DATETIME(6)  NOT NULL,
    content             LONGTEXT     NOT NULL,
    dislike_count       BIGINT       NOT NULL,
    gains               DOUBLE       NOT NULL,
    grade_satisfaction  DOUBLE       NOT NULL,
    is_hidden           BIT          NOT NULL,
    is_reported         BIT          NOT NULL,
    life_balance        DOUBLE       NOT NULL,
    like_count          BIGINT       NOT NULL,
    rating              DOUBLE       NOT NULL,
    teaching_skill      DOUBLE       NOT NULL,
    user_id             VARCHAR(255) NOT NULL,
    semester_lecture_id BIGINT       NOT NULL,
    CONSTRAINT lecture_evaluation__fk__semester_lecture_id
        FOREIGN KEY (semester_lecture_id) REFERENCES semester_lecture (id)
);

CREATE TABLE IF NOT EXISTS evaluation_comment
(
    id                    BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    created_at            DATETIME(6)  NOT NULL,
    updated_at            DATETIME(6)  NOT NULL,
    content               LONGTEXT     NULL,
    dislike_count         BIGINT       NOT NULL,
    is_hidden             BIT          NOT NULL,
    is_reported           BIT          NOT NULL,
    like_count            BIGINT       NOT NULL,
    user_id               VARCHAR(255) NOT NULL,
    lecture_evaluation_id BIGINT       NOT NULL,
    CONSTRAINT evaluation_comment__fk__lecture_evaluation_id
        FOREIGN KEY (lecture_evaluation_id) REFERENCES lecture_evaluation (id)
);

CREATE TABLE IF NOT EXISTS evaluation_report
(
    id                    BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    created_at            DATETIME(6)  NOT NULL,
    updated_at            DATETIME(6)  NOT NULL,
    content               LONGTEXT     NOT NULL,
    is_hidden             BIT          NOT NULL,
    user_id               VARCHAR(255) NOT NULL,
    lecture_evaluation_id BIGINT       NOT NULL,
    CONSTRAINT evaluation_report__unique__lecture_evaluation__user_id
        UNIQUE (lecture_evaluation_id, user_id),
    CONSTRAINT evaluation_report__fk__lecture_evaluation_id
        FOREIGN KEY (lecture_evaluation_id) REFERENCES lecture_evaluation (id)
);

CREATE TABLE IF NOT EXISTS tag_group
(
    id         BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6)  NOT NULL,
    color      VARCHAR(255) NULL,
    name       VARCHAR(255) NOT NULL,
    ordering   INT          NOT NULL,
    value_type VARCHAR(255) NOT NULL,
    CONSTRAINT tag_group__unique__ordering
        UNIQUE (ordering),
    CONSTRAINT tag_group__unique__name
        UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS tag
(
    id           BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    created_at   DATETIME(6)  NOT NULL,
    updated_at   DATETIME(6)  NOT NULL,
    description  VARCHAR(255) NULL,
    int_value    INT          NULL,
    name         VARCHAR(255) NOT NULL,
    ordering     INT          NOT NULL,
    string_value VARCHAR(255) NULL,
    tag_group_id BIGINT       NOT NULL,
    CONSTRAINT tag__unique__tag_group_id__ordering
        UNIQUE (tag_group_id, ordering),
    CONSTRAINT tag__fk__tag_group_id
        FOREIGN KEY (tag_group_id) REFERENCES tag_group (id)
);
