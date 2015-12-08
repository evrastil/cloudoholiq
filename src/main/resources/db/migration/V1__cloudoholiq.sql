CREATE SEQUENCE timestamp_seq;

CREATE TABLE category (
  id uuid PRIMARY KEY,
  orderIndex int,
  entity jsonb,
  CONSTRAINT item_json_quid_equalto_table_guid CHECK (CAST(id AS varchar(50)) = entity ->> 'id')
);

CREATE TABLE filtergroup (
  id uuid PRIMARY KEY,
  entity jsonb,
  CONSTRAINT item_json_quid_equalto_table_guid CHECK (CAST(id AS varchar(50)) = entity ->> 'id')
);

CREATE TABLE filter (
  id uuid PRIMARY KEY,
  entity jsonb,
  CONSTRAINT item_json_quid_equalto_table_guid CHECK (CAST(id AS varchar(50)) = entity ->> 'id')
);

CREATE TABLE sorting (
  id uuid PRIMARY KEY,
  entity jsonb,
  CONSTRAINT item_json_quid_equalto_table_guid CHECK (CAST(id AS varchar(50)) = entity ->> 'id')
);

CREATE TABLE categoryfilter (
  categoryId uuid,
  filterGroupId uuid
);

CREATE TABLE blob (
  id uuid PRIMARY KEY,
  data BYTEA,
  entity jsonb,
  CONSTRAINT item_json_quid_equalto_table_guid CHECK (CAST(id AS varchar(50)) = entity ->> 'id')
);

CREATE TABLE serviceoffering (
  id uuid PRIMARY KEY,
  entity jsonb,
  CONSTRAINT item_json_quid_equalto_table_guid CHECK (CAST(id AS varchar(50)) = entity ->> 'id')
);

CREATE TABLE vendor (
  id uuid PRIMARY KEY,
  entity jsonb,
  CONSTRAINT item_json_quid_equalto_table_guid CHECK (CAST(id AS varchar(50)) = entity ->> 'id')
);

CREATE TABLE visitentrylog (
  id uuid PRIMARY KEY,
  entity jsonb,
  CONSTRAINT item_json_quid_equalto_table_guid CHECK (CAST(id AS varchar(50)) = entity ->> 'id')
);

--seed filters
INSERT INTO public.filter (id, entity) VALUES ('71355d0e-701f-4686-b184-d84bd67e2679', '{"id": "71355d0e-701f-4686-b184-d84bd67e2679", "key": "name-is", "path": "name", "type": "STRING", "label": "name is", "expression": "EQ"}');
INSERT INTO public.filter (id, entity) VALUES ('81355d0e-701f-4686-b184-d84bd67e2698', '{"id": "81355d0e-701f-4686-b184-d84bd67e2698", "key": "name-start-with", "path": "name", "type": "STRING", "label": "name start with", "expression": "START_WITH"}');
INSERT INTO public.filter (id, entity) VALUES ('51355d0e-701f-4686-b184-d84bd67e2648', '{"id": "51355d0e-701f-4686-b184-d84bd67e2648", "key": "name-contains", "path": "name", "type": "STRING", "label": "name contains", "expression": "CONTAINS"}');
INSERT INTO public.filter (id, entity) VALUES ('a51e7447-f6f6-46b8-8acb-1f9cd27dfbec', '{"id": "a51e7447-f6f6-46b8-8acb-1f9cd27dfbec", "key": "key-is", "path": "key", "type": "STRING", "label": "key is", "expression": "EQ"}');
INSERT INTO public.filter (id, entity) VALUES ('c51e7447-f6f6-46b8-8acb-1f9cd27dfdec', '{"id": "c51e7447-f6f6-46b8-8acb-1f9cd27dfdec", "key": "vendor", "path": "vendor", "type": "STRING", "label": "vendor", "expression": "EQ"}');
INSERT INTO public.filter (id, entity) VALUES ('f51e7447-f6f6-46b8-8acb-1f9cd27dfeec', '{"id": "f51e7447-f6f6-46b8-8acb-1f9cd27dfeec", "key": "category", "path": "category", "type": "STRING", "label": "category", "expression": "EQ"}');

