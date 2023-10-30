insert into "account" ("email", "is_active", "is_admin", "name", "password")
values ('luffy@mugiwara.com', true, true, 'Monkey D Luffy', 'Meat1234');

insert into "account" ("email", "is_active", "is_admin", "name", "password")
values ('zoro@mugiwara.com', false, false, 'roronoa zoro', 'meat123');

insert into "category" ("id", "name", "description", "icon_url")
values ('MISC', 'Miscellaneous', 'Products that dont fit in any other category', 'http://localImageRepo/misc.ico');

insert into "category" ("id", "name", "description", "icon_url")
values ('SHIP', 'Ships', 'Ships of all sizes for venturing into the sea ', 'http://localImageRepo/ship.ico');

insert into "category" ("id", "name", "description", "icon_url")
values ('SWRD', 'Swords', 'Swords of different grades for an edge in battle', 'http://localImageRepo/sword.ico');

insert into "product" ("name", "description", "price", "image_url", "is_active", "category_id")
values('The Going Merry', 'A worthy ship for anyone willing to become the next pirate king', 9998.99, 'http://localImageRepo/merry.jpg', true, 'SHIP');

insert into "product" ("name", "description", "price", "image_url", "is_active", "category_id")
values('Treasure chest', 'Contains the One Piece. The pirate king''s treasure of unimaginable value', 10000.01, 'http://localImageRepo/onepiece.jpg', false, 'MISC')
