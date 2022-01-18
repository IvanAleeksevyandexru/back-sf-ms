// Для 1 DC
CREATE KEYSPACE IF NOT EXISTS service_descriptor WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'dc1' : 1};

// Для 3 DC
// CREATE KEYSPACE IF NOT EXISTS service_descriptor WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'dc1' : 3, 'dc2' : 3 , 'dc3' : 3};

// Таблица сервис-дескрипторов
CREATE TABLE IF NOT EXISTS service_descriptor.service_descriptor (
   serviceid    text,
   updated       timestamp,
   body          text,
   PRIMARY KEY (serviceid)
)
WITH compaction = {'class': 'SizeTieredCompactionStrategy'};

// Таблица шаблонов по услуге
CREATE TABLE IF NOT EXISTS service_descriptor.service_template (
   service_id text,
   package_file blob,
   updated timestamp,
   checksum text,
   PRIMARY KEY (service_id)
)
WITH compaction = {'class': 'SizeTieredCompactionStrategy'};
