ALTER TABLE lecture_evaluation MODIFY	COLUMN	grade_satisfaction  DOUBLE  NULL;
ALTER TABLE lecture_evaluation MODIFY	COLUMN	gains               DOUBLE  NULL;
ALTER TABLE lecture_evaluation MODIFY	COLUMN	teaching_skill      DOUBLE  NULL;
ALTER TABLE lecture_evaluation MODIFY	COLUMN	life_balance        DOUBLE  NULL;
ALTER TABLE lecture_evaluation ADD	    COLUMN  from_snuev          BIT     DEFAULT false;
