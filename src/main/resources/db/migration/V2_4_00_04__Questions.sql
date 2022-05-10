insert into type_traduction (cod_typ_trad, lib_typ_trad, length_typ_trad) values ('QUESTION_LIB', 'Libellé', 500);

alter table question_cand add user_cre_question_cand varchar(50) not null;

alter table question_cand add user_mod_question_cand varchar(50) not null;

alter table question_cand add user_mod_statut_question_cand varchar(50) null;

alter table question_cand add dat_mod_statut_question_cand datetime null;

