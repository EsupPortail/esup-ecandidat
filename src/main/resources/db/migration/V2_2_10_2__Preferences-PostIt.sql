--
-- Modification table preference_ind
--
ALTER TABLE preference_ind CHANGE COLUMN cand_col_sort_pref cand_col_sort_pref TEXT NULL DEFAULT NULL COMMENT 'préférences de la colonne de trie dans l\'ecran des candidatures';
UPDATE preference_ind SET cand_col_sort_pref = CASE WHEN (cand_col_sort_pref IS NULL OR cand_col_sort_direction_pref IS NULL) THEN NULL ELSE CONCAT(CONCAT(cand_col_sort_pref,':'), cand_col_sort_direction_pref) END;
ALTER TABLE preference_ind DROP COLUMN cand_col_sort_direction_pref;


--
-- Modification table post_it
--
ALTER TABLE post_it CHANGE COLUMN user_post_it user_post_it VARCHAR(255) NOT NULL COMMENT 'nom d\'utilisateur de la personne ayant réalisé le post-it' AFTER message_post_it;
ALTER TABLE post_it ADD COLUMN user_cre_post_it VARCHAR(50) NULL COMMENT 'login de la personne ayant réalisé le post-it' AFTER user_post_it;
UPDATE post_it p SET p.user_cre_post_it = (SELECT login_ind FROM individu i WHERE p.user_post_it = i.libelle_ind AND (SELECT count(1) FROM individu ii WHERE i.libelle_ind = ii.libelle_ind)=1 limit 1);
UPDATE post_it p SET p.user_cre_post_it = 'admin' WHERE user_cre_post_it IS NULL;
ALTER TABLE post_it CHANGE COLUMN user_cre_post_it user_cre_post_it VARCHAR(50) NOT NULL COMMENT 'login de la personne ayant réalisé le post-it';
ALTER TABLE post_it DROP COLUMN user_post_it;