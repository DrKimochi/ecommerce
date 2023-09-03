insert into "account" ("email","is_active","is_admin","name","password", "id")
values ('luffy@mugiwara.com',true, true,'Monkey D Luffy','meat123', nextval('account_seq'));

insert into "account" ("email","is_active","is_admin","name","password", "id")
values ('zoro@mugiwara.com',false, false,'roronoa zoro','meat123', nextval('account_seq'));
