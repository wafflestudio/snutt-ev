ALTER TABLE lecture_evaluation ADD INDEX idx_lecture_evaluation_recommended (semester_lecture_id, is_hidden, like_count, id);
