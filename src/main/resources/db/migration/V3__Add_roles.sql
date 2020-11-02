alter table users add column roles varchar(255);
update users set roles = '["SuperAdmin"]' where id = 1;