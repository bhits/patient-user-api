create table scope (id bigint not null auto_increment, scope varchar(255), primary key (id)) ENGINE=InnoDB;
create table user_type (id bigint not null auto_increment, type varchar(255), primary key (id)) ENGINE=InnoDB;
create table user_type_scopes (user_type bigint not null, scopes bigint not null) ENGINE=InnoDB;
alter table scope add constraint UK_5pa317tgcvklphqob5d5s4jjg unique (scope);
alter table user_type_scopes add constraint UK_1139xwtbp3pljyj5ym4ssb8yn unique (scopes);
alter table user_type_scopes add constraint FK_1139xwtbp3pljyj5ym4ssb8yn foreign key (scopes) references scope (id);
alter table user_type_scopes add constraint FK_3mr67pecnjd3oe94yoqi49xvl foreign key (user_type) references user_type (id);