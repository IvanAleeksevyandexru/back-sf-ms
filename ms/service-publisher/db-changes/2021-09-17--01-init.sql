--liquibase formatted sql

--changeset tirdyneev@it-ne.ru:2021-09-17--01-01
CREATE SCHEMA IF NOT EXISTS pgu_publisher;

--changeset tirdyneev:2021-09-17--01-02
CREATE TABLE pgu_publisher.publish_task (
	id serial PRIMARY KEY,
	parent_id integer,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp,
	status varchar(255),
	service_id varchar(255),
	service_version varchar(255),
    target_environment varchar(255) NOT NULL,
    is_send_descriptor boolean NOT NULL DEFAULT false,
    descriptor oid,
    is_send_templates boolean NOT NULL DEFAULT false,
    templates oid,
    is_send_config boolean NOT NULL DEFAULT false,
    config oid,
    is_revert_on_fail boolean NOT NULL DEFAULT false,
    revert_to_version varchar(255)
);

--changeset tirdyneev:2021-09-17--01-03
COMMENT ON TABLE pgu_publisher.publish_task IS 'Задания на публикацию услуг: дескриптор, шаблоны, настройки каталога';
COMMENT ON COLUMN pgu_publisher.publish_task.descriptor IS 'Файл json, дескриптор услуги';
COMMENT ON COLUMN pgu_publisher.publish_task.templates IS 'Файл zip, vm-шаблоны услуги';
COMMENT ON COLUMN pgu_publisher.publish_task.config IS 'Файл json, параметры для каталога услуг';
