create sequence hibernate_sequence start 1 increment 1;
create table ConfirmationToken (id int8 not null, expiryDate date, token varchar(255), user_id int8 not null, primary key (id));
create table users (id bigserial not null, email varchar(255), enabled boolean, password varchar(255), login varchar(255), primary key (id));
alter table users add constraint UK_6dotkott2kjsp8vw4d0m25fb7 unique (email);
alter table users add constraint UK_ow0gan20590jrb00upg3va2fn unique (login);
alter table ConfirmationToken add constraint FKarrepjqnsgej3429c3715doe8 foreign key (user_id) references users;