insert into account(id, email, password, name) VALUES (1, 'demo@demo.org', '$2a$10$.IPhj1KxDtlNPmIb7wyqveamlF7m4g.V.Vf4n//SCcCo56h0VtllS', 'Demo User');
alter sequence account_id_seq restart with 2;


insert into assetcategory(id, name) VALUES (1, 'workstation');
insert into assetcategory(id, name) VALUES (2, 'server');
insert into assetcategory(id, name) VALUES (3, 'audio/video');
insert into assetcategory(id, name) VALUES (4, 'other');
alter sequence assetcategory_id_seq restart with 5;


insert into asset(id, account_id, name, category, created, modified) VALUES (1, 1, 'blackbeard', 1, '2011-08-05 09:05:06.523-0800', '2011-08-06 03:01:47.298-0500');
insert into asset(id, account_id, name, category, created, modified) VALUES (2, 1, 'thor', 1, '2012-08-05 14:29:33.834-0500', '2012-10-17 21:35:23.398-0500');
alter sequence asset_id_seq restart with 3;


insert into itemtype(id, name) VALUES (1, 'case');
insert into itemtype(id, name) VALUES (2, 'motherboard');
insert into itemtype(id, name) VALUES (3, 'cpu');
insert into itemtype(id, name) VALUES (4, 'ram');
insert into itemtype(id, name) VALUES (5, 'graphics');
insert into itemtype(id, name) VALUES (6, 'power supply');
insert into itemtype(id, name) VALUES (7, 'ssd');
insert into itemtype(id, name) VALUES (8, 'hard drive');
insert into itemtype(id, name) VALUES (9, 'display');
insert into itemtype(id, name) VALUES (10, 'keyboard');
insert into itemtype(id, name) VALUES (11, 'mouse');
insert into itemtype(id, name) VALUES (12, 'optical');
insert into itemtype(id, name) VALUES (13, 'flash');
alter sequence itemtype_id_seq restart with 14;

insert into product(id, itemtype_id, name, asin, sku, upc, ean) VALUES (1, 1, 'Corsair Obsidian 650D Aluminum Mid Tower ATX Enthusiast Computer Case - Black CC650DW', '', '', '', '');
insert into product(id, itemtype_id, name, asin, sku, upc, ean) VALUES (2, 2, 'Gigabyte Intel Z77 LGA 1155 AMD CrossFireX_NVIDIA SLI Dual LAN Dual UEFI BIOS ATX', '', '', '', '');	
insert into product(id, itemtype_id, name, asin, sku, upc, ean) VALUES (3, 2, 'ASUS P8P67 DELUXE', '', '', 'P8P67 (REV 3.0)', '');
insert into product(id, itemtype_id, name, asin, sku, upc, ean) VALUES (4, 3, 'Intel Core i7-2600K', 'BX80623I72600K', '', '', '');
insert into product(id, itemtype_id, name, asin, sku, upc, ean) VALUES (5, 3, 'Intel Core i5-3570K Quad-Core Processor 3.4 GHz 4 Core LGA 1155', '', '', '', '');
insert into product(id, itemtype_id, name, asin, sku, upc, ean) VALUES (6, 4, 'Corsair Vengeance 16GB (2x8GB) DDR3 1600 MHz (PC3 12800)', '', '', '', '');
insert into product(id, itemtype_id, name, asin, sku, upc, ean) VALUES (7, 1, 'Cooler Master HAF 932', '', '', '', '');
insert into product(id, itemtype_id, name, asin, sku, upc, ean) VALUES (8, 6, 'Corsair Professional Series 650-Watt 80 Plus Certified Power Supply compatible with Intel and AMD Platforms - CMPSU-650HX', '', '', '', '');
insert into product(id, itemtype_id, name, asin, sku, upc, ean) VALUES (9, 7, 'SanDisk Extreme SSD 120 GB SATA 6.0 Gb-s2.5-Inch Solid State Drive SDSSDX-120G-G25', '', '', '', '');
insert into product(id, itemtype_id, name, asin, sku, upc, ean) VALUES (10, 8, 'Western Digital 1 TB Caviar Green SATA II 64 MB Cache Bulk_OEM Desktop Hard Drive WD10EARS', '', '', '', '');
insert into product(id, itemtype_id, name, asin, sku, upc, ean) VALUES (11, 9, 'ACHIEVA Shimian QH270-Lite Quad HD 2560x1440 DVI', '', '', '', '');
insert into product(id, itemtype_id, name, asin, sku, upc, ean) VALUES (12, 1, 'Cooler Master 690 II Advanced - Mid Tower Computer Case with USB 3.0 Ports and X-Dock (RC-692A-KKN5)', '', '', '', '');
alter sequence product_id_seq restart with 13;


insert into revision(id, asset_id, index, created) VALUES (1, 1, 1, '2011-02-07 20:25:33.594-0500');
insert into revision(id, asset_id, index, created) VALUES (2, 1, 2, '2011-08-05 11:27:06.927-0500');
insert into revision(id, asset_id, index, created) VALUES (3, 2, 1, '2012-10-17 21:35:23.234-0500');
alter sequence revision_id_seq restart with 4;


insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (1, 1, 1, 'http://www.amazon.com/Corsair-Obsidian-Black-Computer-CC650DW-1/dp/B004UE1W9K/', 1, 179.99);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (2, 1, 2, 'http://www.amazon.com/Gigabyte-CrossFireX-NVIDIA-Motherboard-GA-Z77X-UD5H/dp/B007R21JK4/', 1, 180.98);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (3, 1, 5, 'http://www.amazon.com/Intel-Core-i5-3570K-Quad-Core-Processor/dp/B007SZ0E1K/', 1, 223.79);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (4, 1, 6, 'http://www.amazon.com/Corsair-Vengeance-Desktop-Memory-CMZ16GX3M2A1600C10/dp/B006EWUO22/', 1, 89.99);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (5, 1, 8, 'http://www.amazon.com/Corsair-Professional-Certified-compatible-Platforms/dp/B002LVUPZQ/', 1, 148.46);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (6, 1, 9, 'http://www.amazon.com/SanDisk-Extreme-Gb-s2-5-Inch-Solid-SDSSDX-120G-G25/dp/B006EKJCWM/', 1, 108.70);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (7, 1, 10, 'http://www.amazon.com/Western-Digital-Intellipower-Desktop-WD10EADS/', 1, 108.49);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (8, 1, 11, 'http://www.amazon.com/Achieva-Shimian-QH270-lite-wide/dp/B009UZ78L0/', 1, 309.99);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (9, 2, 7, 'http://www.amazon.com/Cooler-Master-Advanced-SuperSpeed-RC-932-KKN5-GP/dp/B001EPUQAE/', 1, 149.99);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (10, 2, 2, 'http://www.amazon.com/Gigabyte-CrossFireX-NVIDIA-Motherboard-GA-Z77X-UD5H/dp/B007R21JK4/', 1, 180.98);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (11, 2, 4, 'http://www.amazon.com/Intel-i7-2600K-Quad-Core-Processor-Cache/dp/B004FA8NOQ/', 1, 339.60);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (12, 2, 6, 'http://www.amazon.com/Corsair-Vengeance-Desktop-Memory-CMZ16GX3M2A1600C10/dp/B006EWUO22/', 1, 89.99);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (13, 2, 8, 'http://www.amazon.com/Corsair-Professional-Certified-compatible-Platforms/dp/B002LVUPZQ/', 1, 148.46);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (14, 2, 9, 'http://www.amazon.com/SanDisk-Extreme-Gb-s2-5-Inch-Solid-SDSSDX-120G-G25/dp/B006EKJCWM/', 1, 108.70);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (15, 2, 10, 'http://www.amazon.com/Western-Digital-Intellipower-Desktop-WD10EADS/', 1, 108.49);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (16, 2, 11, 'http://www.amazon.com/Achieva-Shimian-QH270-lite-wide/dp/B009UZ78L0/', 1, 309.99);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (17, 3, 12, 'http://www.amazon.com/Cooler-Master-690-Advanced-RC-692A-KKN5/dp/B007ZOB9MM/', 1, 76.49);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (18, 3, 3, 'http://www.amazon.com/Intel-i7-2600K-Quad-Core-Processor-Cache/dp/B004FA8NOQ/', 1, 339.60);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (19, 3, 4, 'http://www.amazon.com/Corsair-Vengeance-Desktop-Memory-CMZ16GX3M2A1600C10/dp/B006EWUO22/', 1, 89.99);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (20, 3, 6, 'http://www.amazon.com/Corsair-Vengeance-Desktop-Memory-CMZ16GX3M2A1600C10/dp/B006EWUO22/', 1, 89.99);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (21, 3, 8, 'http://www.amazon.com/Corsair-Professional-Certified-compatible-Platforms/dp/B002LVUPZQ/', 1, 148.46);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (22, 3, 9, 'http://www.amazon.com/SanDisk-Extreme-Gb-s2-5-Inch-Solid-SDSSDX-120G-G25/dp/B006EKJCWM/', 1, 108.70);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (23, 3, 10, 'http://www.amazon.com/Western-Digital-Intellipower-Desktop-WD10EADS/', 1, 108.49);
insert into item(id, revision_id, product_id, reference, quantity, price) VALUES (24, 3, 11, 'http://www.amazon.com/Achieva-Shimian-QH270-lite-wide/dp/B009UZ78L0/', 1, 309.99);
alter sequence item_id_seq restart with 25;




