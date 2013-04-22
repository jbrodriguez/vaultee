CREATE TABLE account
(
    id 						bigserial PRIMARY KEY,
    email					varchar(255) not null,
    password				varchar(255) not null,
    name   					varchar(255) not null
);

CREATE TABLE assetcategory (
	id 						serial PRIMARY KEY,
	name					varchar(255) not null
);

CREATE TABLE asset
(
	id						bigserial PRIMARY KEY,
	account_id				bigint REFERENCES account (id),
	name					varchar(32) not null,
	category				integer not null,
	created					timestamptz not null,
	modified				timestamptz not null
);


CREATE TABLE itemtype
(
	id 						bigserial PRIMARY KEY,
	name					varchar(255) not null
);


CREATE TABLE product
(
	id						bigserial PRIMARY KEY,
	itemtype_id				bigint REFERENCES itemtype (id),
	name 					varchar(255) not null,
	asin					varchar(255),
	sku						varchar(255),
	upc						varchar(255),
	ean						varchar(255)
);


CREATE TABLE revision
(
	id 						bigserial PRIMARY KEY,
	asset_id				bigint REFERENCES asset (id),
	index					bigint not null,
	created					timestamptz not null
);

CREATE TABLE item
(
	id 						bigserial PRIMARY KEY,
	revision_id				bigint REFERENCES revision (id),
	product_id				bigint REFERENCES product (id),
	reference				varchar(255),
	quantity				bigint,
	price					numeric(19,2)
);


