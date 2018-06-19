--
-- Modification table preference_ind
--
ALTER TABLE preference_ind
	CHANGE COLUMN cand_col_sort_pref cand_col_sort_pref TEXT NULL DEFAULT NULL COMMENT 'préférences de la colonne de trie dans l\'ecran des candidatures';
	
UPDATE preference_ind SET cand_col_sort_pref = CASE WHEN cand_col_sort_pref IS NULL THEN NULL ELSE CONCAT(CONCAT(cand_col_sort_pref,':'), cand_col_sort_direction_pref) END;

ALTER TABLE preference_ind DROP COLUMN cand_col_sort_direction_pref;