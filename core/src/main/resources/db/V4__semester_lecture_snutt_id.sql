ALTER TABLE semester_lecture ADD COLUMN snutt_id CHAR(24) DEFAULT NULL;
ALTER TABLE semester_lecture ADD INDEX semester_lecture_snutt_id_index(snutt_id);
