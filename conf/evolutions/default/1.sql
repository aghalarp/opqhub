# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table alert_notification (
  primary_key               bigint auto_increment not null,
  voltage_alert_notification tinyint(1) default 0,
  frequency_alert_notification tinyint(1) default 0,
  device_alert_notification tinyint(1) default 0,
  alert_via_email           tinyint(1) default 0,
  notification_email        varchar(255),
  alert_via_sms             tinyint(1) default 0,
  sms_carrier               integer,
  sms_number                varchar(255),
  min_acceptable_voltage    double,
  max_acceptable_voltage    double,
  min_acceptable_frequency  double,
  max_acceptable_frequency  double,
  device_primary_key        bigint,
  constraint ck_alert_notification_sms_carrier check (sms_carrier in (0,1,2,3,4,5,6,7,8,9,10)),
  constraint pk_alert_notification primary key (primary_key))
;

create table event (
  primary_key               bigint auto_increment not null,
  event_type                integer,
  event_value               double,
  timestamp                 bigint,
  event_duration            bigint,
  device_primary_key        bigint,
  external_cause_primary_key bigint,
  constraint ck_event_event_type check (event_type in (0,1,2,3)),
  constraint pk_event primary key (primary_key))
;

create table external_cause (
  primary_key               bigint auto_increment not null,
  cause_type                varchar(255),
  cause_description         varchar(255),
  constraint pk_external_cause primary key (primary_key))
;

create table measurement (
  primary_key               bigint auto_increment not null,
  timestamp                 bigint,
  voltage                   double,
  frequency                 double,
  device_primary_key        bigint,
  constraint pk_measurement primary key (primary_key))
;

create table opq_device (
  primary_key               bigint auto_increment not null,
  device_id                 bigint,
  description               varchar(255),
  sharing_data              tinyint(1) default 0 not null,
  grid_id                   varchar(255),
  grid_scale                double,
  grid_row                  integer,
  grid_col                  integer,
  north_east_latitude       double,
  north_east_longitude      double,
  south_west_latitude       double,
  south_west_longitude      double,
  person_primary_key        bigint,
  constraint pk_opq_device primary key (primary_key))
;

create table person (
  primary_key               bigint auto_increment not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  password_hash             varbinary(255),
  password_salt             varbinary(255),
  state                     varchar(255),
  city                      varchar(255),
  zip                       varchar(255),
  street_name               varchar(255),
  street_number             varchar(255),
  constraint pk_person primary key (primary_key))
;

alter table alert_notification add constraint fk_alert_notification_device_1 foreign key (device_primary_key) references opq_device (primary_key) on delete restrict on update restrict;
create index ix_alert_notification_device_1 on alert_notification (device_primary_key);
alter table event add constraint fk_event_device_2 foreign key (device_primary_key) references opq_device (primary_key) on delete restrict on update restrict;
create index ix_event_device_2 on event (device_primary_key);
alter table event add constraint fk_event_externalCause_3 foreign key (external_cause_primary_key) references external_cause (primary_key) on delete restrict on update restrict;
create index ix_event_externalCause_3 on event (external_cause_primary_key);
alter table measurement add constraint fk_measurement_device_4 foreign key (device_primary_key) references opq_device (primary_key) on delete restrict on update restrict;
create index ix_measurement_device_4 on measurement (device_primary_key);
alter table opq_device add constraint fk_opq_device_person_5 foreign key (person_primary_key) references person (primary_key) on delete restrict on update restrict;
create index ix_opq_device_person_5 on opq_device (person_primary_key);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table alert_notification;

drop table event;

drop table external_cause;

drop table measurement;

drop table opq_device;

drop table person;

SET FOREIGN_KEY_CHECKS=1;

