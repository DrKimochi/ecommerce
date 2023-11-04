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

insert into "category" ("id", "name", "description", "icon_url")
values ('HATS', 'Pirate Hats', 'Fashionable hats that embody adventure and the spirit of the high seas', 'http://localImageRepo/hat.ico');

insert into "product" ("name", "description", "price", "image_url", "is_active", "category_id")
values('Shusui', 'Famous katana once wielded by the legendary samurai', 3493.83, 'http://localImageRepo/shusui.jpg', true, 'SWRD');

insert into "product" ("name", "description", "price", "image_url", "is_active", "category_id")
values('The Going Merry', 'A worthy ship for anyone willing to become the next pirate king', 9998.99, 'http://localImageRepo/merry.jpg', true, 'SHIP');

insert into "product" ("name", "description", "price", "image_url", "is_active", "category_id")
values('The Flying Dutchman', 'An old ship from maritime folklore. Very much haunted.', 1440.50, 'http://localImageRepo/dutchman.jpg', false, 'SHIP');

insert into "product" ("name", "description", "price", "image_url", "is_active", "category_id")
values('Treasure chest', 'Contains the One Piece. The pirate king''s treasure of unimaginable value', 10000.01, 'http://localImageRepo/onepiece.jpg', true, 'MISC');

insert into "order" ("status", "user_id", "created_date", "updated_date")
values('SUBMITTED', '1', '2023-11-03 12:30:00', '2023-11-04 13:00:01');

insert into "order_product" ("order_id", "product_id", "quantity", "unit_price")
values(1,1,3,3493.83);

insert into "order_product" ("order_id", "product_id", "quantity", "unit_price")
values(1,2,1,9998.99);
