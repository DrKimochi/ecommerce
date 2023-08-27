insert into "user" ("email","is_active","is_admin","name","password", "id")
values ('luffy@mugiwara.com',true, true,'Monkey D Luffy','meat123', nextval('user_seq'));

insert into "user" ("email","is_active","is_admin","name","password", "id")
values ('zoro@mugiwara.com',false, false,'roronoa zoro','meat123', nextval('user_seq'));
