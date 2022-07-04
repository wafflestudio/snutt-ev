ALTER TABLE lecture_evaluation
    MODIFY	COLUMN	grade_satisfaction  DOUBLE  NULL,
    MODIFY	COLUMN	gains               DOUBLE  NULL,
    MODIFY	COLUMN	teaching_skill      DOUBLE  NULL,
    MODIFY	COLUMN	life_balance        DOUBLE  NULL,
    ADD	    COLUMN  from_snuev          BIT     DEFAULT false;
