# Tasks schema
 
# --- !Ups

CREATE SEQUENCE taskUser_id_seq;
CREATE TABLE taskUser (
   id integer NOT NULL DEFAULT nextVal('taskUser_id_seq'),
   name varchar(255) UNIQUE NOT NULL
);

ALTER TABLE task add usuario varchar(255) NOT NULL;
ALTER TABLE task add constraint fk_task_userTask_1 foreign key (usuario) references taskUser (name) on delete restrict on update restrict;

INSERT INTO taskUser (id,name) values (1, 'anonimo');
INSERT INTO taskUser (id,name) values (2, 'josvi');
INSERT INTO taskUser (id,name) values (3, 'joaqui');
 
# --- !Downs

DROP TABLE taskUser;
DROP SEQUENCE taskUser_id_seq;