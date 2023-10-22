insert into account (email,is_active,is_admin,name,password)
values ('admin@shopamos.com',true, true,'Adam Minster','$2a$10$bOxm1buxlN.XytMy/5uwYuoBPHUpEqoqJNmj2rBOIBaR4Zf5Gponu');

insert into account (email,is_active,is_admin,name,password)
values ('customer@shopamos.com',true, false,'Gus Thomas','$2a$10$bOxm1buxlN.XytMy/5uwYuoBPHUpEqoqJNmj2rBOIBaR4Zf5Gponu');

insert into category (id, name, description, icon_url)
values ('MISC', 'Miscellaneous', 'Products that dont fit in any other category', 'http://localImageRepo/misc.ico');
