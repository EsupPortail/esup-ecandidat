--
-- Mise à jour de la structure des tables de formulaire
--

--
-- Structure de la table `formulaire_candidat`
--
CREATE TABLE `formulaire_candidat`
(
 `id_candidat`                     INT(10) NOT NULL comment 'id du candidat', 
 `id_formulaire_limesurvey`        INT(11) NOT NULL comment 'id du formulaire limesurvey', 
 `reponses_formulaire_candidat`    TEXT comment 'les réponses du formulaire', 
 `dat_reponse_formulaire_candidat` DATETIME NOT NULL comment 'date de réponse au formulaire', 
 `dat_cre_formulaire_candidat`     DATETIME NOT NULL comment 'date de création de la réponse', 
 `dat_mod_formulaire_candidat`     DATETIME NOT NULL comment 'date de modification de la réponse',
 PRIMARY KEY (`id_candidat`,`id_formulaire_limesurvey`)
)
comment='table des réponses aux formulaires limesurvey' 
engine=innodb; 

ALTER TABLE `formulaire_candidat`
	ADD INDEX `fk_candidat_formulaire_candidat_id_candidat` (`id_candidat`), 
	ADD CONSTRAINT `fk_candidat_formulaire_candidat_id_candidat` FOREIGN KEY (`id_candidat`) REFERENCES `candidat` (`id_candidat`);

--
-- Mise à jour des données et alimentation de la table formulaire_candidat
--

INSERT IGNORE INTO `formulaire_candidat`(
	select distinct candidat.id_candidat, formulaire.id_formulaire_limesurvey, formulaire_cand.reponses_formulaire_cand, 
	max(formulaire_cand.dat_reponse_formulaire_cand), max(formulaire_cand.dat_cre_formulaire_cand), max(formulaire_cand.dat_mod_formulaire_cand)
	from formulaire_cand, formulaire, candidat, candidature 
	where formulaire_cand.id_cand = candidature.id_cand
	and formulaire_cand.id_formulaire = formulaire.id_formulaire
	and candidature.id_candidat = candidat.id_candidat
	and formulaire_cand.cod_typ_statut_piece = 'TR'
	group by formulaire.id_formulaire_limesurvey, candidat.id_candidat, formulaire_cand.reponses_formulaire_cand
);

DELETE FROM `formulaire_cand` where formulaire_cand.cod_typ_statut_piece = 'TR';

--
-- Mise à jour structure de la table `formulaire_cand`
--
ALTER TABLE `formulaire_cand` 
	DROP COLUMN `cod_typ_statut_piece`, 
	DROP COLUMN `dat_reponse_formulaire_cand`, 
	DROP COLUMN `reponses_formulaire_cand`, 
	DROP INDEX `fk_formulaire_cand_typ_statut_piece_cod`, 
	DROP FOREIGN KEY `fk_formulaire_cand_typ_statut_piece_cod`; 
  
ALTER TABLE `formulaire_cand` COMMENT='table des formulaires (NC) de la candidature';

--
-- Structure de la table `pj_candidat`
--
CREATE TABLE `pj_candidat` (
  `id_candidat` int(10) NOT NULL COMMENT 'id du candidat',
  `cod_anu_pj_candidat` varchar(4) NOT NULL COMMENT 'code de l''année',
  `cod_tpj_pj_candidat` varchar(5) NOT NULL COMMENT 'code de la pièce du siScol',
  `nom_fic_pj_candidat` varchar(30) NOT NULL COMMENT 'nom du fichier',
  `dat_exp_pj_candidat` DATETIME NULL DEFAULT NULL COMMENT 'date d''expiration de la pièce',
  `dat_cre_pj_candidat` DATETIME NOT NULL comment 'date de creation de la pièce',
  PRIMARY KEY (`id_candidat`,`cod_anu_pj_candidat`,`cod_tpj_pj_candidat`)
) 
comment='table des pièces d''un candidat provenant du SiScol' 
engine=innodb;

ALTER TABLE `pj_candidat`
	ADD INDEX `fk_candidat_pj_candidat_id_candidat` (`id_candidat`), 
	ADD CONSTRAINT `fk_candidat_pj_candidat_id_candidat` FOREIGN KEY (`id_candidat`) REFERENCES `candidat` (`id_candidat`);