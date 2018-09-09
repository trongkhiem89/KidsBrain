DROP TABLE IF EXISTS "accounts";
CREATE TABLE "accounts"(
	userId TEXT NOT NULL PRIMARY KEY,
	email TEXT DEFAULT '',
	fullName TEXT DEFAULT '',
	mobile TEXT  DEFAULT '',
	gender int  DEFAULT 0,
	birthDay TEXT  DEFAULT '',
	address TEXT DEFAULT '',
	photo TEXT DEFAULT ''
	--updatedAt TEXT DEFAULT ''
);

DROP TABLE IF EXISTS "kids";
CREATE TABLE "kids"(
	id TEXT NOT NULL PRIMARY KEY,
	ageId TEXT DEFAULT '',
	parentId TEXT DEFAULT '',
	fullName TEXT DEFAULT '',
	gender TEXT  DEFAULT '',
	birthDay TEXT  DEFAULT '',
	photo TEXT DEFAULT ''
);

-- Danh mục chức năng home
DROP TABLE IF EXISTS "items";
CREATE TABLE "items"(
	id TEXT PRIMARY KEY,
	icon TEXT DEFAULT '',
	description TEXT DEFAULT '',
	status TEXT DEFAULT 'active',
	sort int DEFAULT 0
);
INSERT INTO "items"(id, icon, description, status, sort) VALUES ('ITEM_1', '',  'Giới thiệu & Hướng dẫn', 'active', 0);
INSERT INTO "items"(id, icon, description, status, sort) VALUES ('ITEM_2', '', 'Tìm kiếm', 'active', 1);
INSERT INTO "items"(id, icon, description, status, sort) VALUES ('ITEM_3', '', 'Thông tin tài khoản', 'active', 2);
INSERT INTO "items"(id, icon, description, status, sort) VALUES ('ITEM_4', '', 'Thông tin con', 'active', 3);

-- Danh mục ngôn ngữ của chức năng
DROP TABLE IF EXISTS "itemLanguages";
CREATE TABLE "itemLanguages"(
	id INTEGER IDENTITY(1,1) PRIMARY KEY,
	itemId TEXT NOT NULL,
	langCode TEXT DEFAULT 'vi',
	name TEXT DEFAULT ''
);
INSERT INTO "itemLanguages"(itemId, langCode, name) VALUES ('ITEM_1', 'vi', 'Giới thiệu & Hướng dẫn');
INSERT INTO "itemLanguages"(itemId, langCode, name) VALUES ('ITEM_2', 'vi', 'Tìm kiếm');
INSERT INTO "itemLanguages"(itemId, langCode, name) VALUES ('ITEM_3', 'vi', 'Thông tin tài khoản');
INSERT INTO "itemLanguages"(itemId, langCode, name) VALUES ('ITEM_4', 'vi', 'Thông tin con');
INSERT INTO "itemLanguages"(itemId, langCode, name) VALUES ('ITEM_1', 'en', 'Tutorial & Information');
INSERT INTO "itemLanguages"(itemId, langCode, name) VALUES ('ITEM_2', 'en', 'Search');
INSERT INTO "itemLanguages"(itemId, langCode, name) VALUES ('ITEM_3', 'en', 'Profile');
INSERT INTO "itemLanguages"(itemId, langCode, name) VALUES ('ITEM_4', 'en', 'Children');


-- Danh mục độ tuổi
DROP TABLE IF EXISTS "levels";
CREATE TABLE "levels"(
	id TEXT NOT NULL PRIMARY KEY,
	name TEXT DEFAULT '',
	icon TEXT DEFAULT '',
	description TEXT DEFAULT '',
	status TEXT  DEFAULT '',
	createdAt TEXT DEFAULT '',
	updatedAt TEXT DEFAULT ''
);

-- Danh mục chức năng theo độ tuổi
DROP TABLE IF EXISTS "categories";
CREATE TABLE "categories"(
	id INTEGER IDENTITY(1,1) PRIMARY KEY,
	cateId TEXT NOT NULL,
	levelId TEXT NOT NULL,
	name TEXT DEFAULT '',
	icon TEXT DEFAULT '',
	description TEXT DEFAULT '',
	status TEXT DEFAULT '',
	sort int DEFAULT 0,
	createdAt TEXT DEFAULT '',
	updatedAt TEXT DEFAULT ''
);

-- Danh mục câu hỏi theo chức năng của độ tuổi
DROP TABLE IF EXISTS "questions";
CREATE TABLE "questions"(
	id INTEGER IDENTITY(1,1) PRIMARY KEY,
	questionId TEXT NOT NULL,
	cateId TEXT NOT NULL,
	levelId TEXT NOT NULL,
	name TEXT DEFAULT '',
	description TEXT DEFAULT '',
	status TEXT DEFAULT '',
	sort int DEFAULT 0,
	createdAt TEXT DEFAULT '',
	updatedAt TEXT DEFAULT '',
	isChecked TEXT DEFAULT 'N'
);

-- Bảng nối lưu thông tin đáp án của danh mục
-- score: Số điểm đạt được, khác với score trong Rate
DROP TABLE IF EXISTS "levelCateRates";
CREATE TABLE "levelCateRates"(
	id INTEGER IDENTITY(1,1) PRIMARY KEY,
	levelId TEXT NOT NULL,
	cateId TEXT NOT NULL,
	rateId TEXT NOT NULL,
	score INTEGER DEFAULT -1
);

-- Bảng lưu tất cả các đáp án
-- scoreRange: Lưu khoảng điểm
DROP TABLE IF EXISTS "rates";
CREATE TABLE "rates"(
	id TEXT NOT NULL PRIMARY KEY,
	levelId TEXT NOT NULL,
    cateId TEXT NOT NULL,
	scoreRange TEXT DEFAULT '',
	rating TEXT DEFAULT '',
	name TEXT DEFAULT ''
);

DROP TABLE IF EXISTS "settings";
CREATE TABLE settings ( id TEXT NOT NULL PRIMARY KEY, scope TEXT NOT NULL, name TEXT NOT NULL, value TEXT,status TEXT DEFAULT 'active',last_modified TEXT NOT NULL );
INSERT INTO "settings" VALUES('3B2C589B','[app]','db_schema_version','1.0', 'active', datetime('now'));

CREATE INDEX idx_settings_name ON settings (name);
CREATE INDEX idx_settings_scope ON settings (scope);

DROP TABLE IF EXISTS keys_certificates;
CREATE TABLE keys_certificates ( id TEXT NOT NULL PRIMARY KEY,
								 name TEXT,
								 type TEXT NOT NULL,
								 key TEXT NOT NULL,
								 iv TEXT NOT NULL,
								 last_modified TEXT NOT NULL);

DROP TABLE IF EXISTS file_encryption;
CREATE TABLE file_encryption ( 	id TEXT NOT NULL PRIMARY KEY,
								keys_certificates_id TEXT NOT NULL,
								iv TEXT,
								folder_id TEXT NOT NULL,
								name TEXT NOT NULL,
								path TEXT NOT NULL,
								type TEXT,
								tag TEXT,
								status TEXT,
								is_favourite TEXT,
								object_id TEXT DEFAULT "",
								sync_status TEXT DEFAULT "",
								last_modified TEXT NOT NULL,
								FOREIGN KEY (folder_id) REFERENCES file_encryption(id),
								FOREIGN KEY (keys_certificates_id) REFERENCES keys_certificates(id) );