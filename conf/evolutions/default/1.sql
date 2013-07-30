# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table alert (
  primary_key               bigint auto_increment not null,
  alert_type                integer,
  timestamp                 bigint,
  duration                  bigint,
  alert_value               double,
  constraint ck_alert_alert_type check (alert_type in (0,1,2)),
  constraint pk_alert primary key (primary_key))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table alert;

SET FOREIGN_KEY_CHECKS=1;

