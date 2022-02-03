# epgu2sf-ms

## Описание микросервисов

### sp-adapter-service
Сервис отправки заявления в service-processing (вне нашей системы).
При подготовке к отправкеформирует XML и PDF с помощью сервисов pdf-generator-ms и xml-generator-ms

### service-descriptor-storage
Сервис для загрузки и получения сервис дескрипторов (описания услуги в формате XML) и шаблонов для формирования XML и PDF.
Файлы хранятся в DB Cassandra

### voskhod-adapter-service
Микросервис для подписания заявления ЭЦП. Работает через EsepService (Восход).

### identification-ms
Микросервис для работы с платформой идентификации личности

### draft-converter
Конвертирует некоторые данные (изначально XML данные от СМЭВ и сведения из словарей) в данные
черновика (ScenarioDto) или в указанный шаблоном json-формат.

### smev-converter
Микросервис для отправки запросов в СМЭВ-адаптер (барбарбок) и конвертации ответа по указанному vm-шаблону в сервисе draft-converter

### smev-jms-receiver
Микросервис для отправки запросов в СМЭВ-адаптер по JMS и получения ответов по ним

### pgu-scenario-generator-service
Сервис генерации дескриптора услуги на лету. Используется для услуги Обжалования штрафов.
От ведомства приходит xml с описанием экранов пользователя, и для каждого orderId создается уникальный serviceDescriptor.

### lk-jms-notifier
Микросервис отправки уведомлений в ЛК по JMS. На стороне ЛК сообщения попадают в очередь "jms.queue.scFormDataRequest", после чего записываются в базу в таблицу 'LK.SC_DATA'.

### service-publisher
Микросервис для публикации дескриптора, шаблонов и параметров услуг на среды

### pdf-generator-ms
Микросервис для генерации PDF файла из шаблонов. Шаблоны хранятся в git репозиториях услуг и создаются аналитиками при разработке услуги.

### xml-generator-ms
Микросервис для генерации XML-файлов на основе шаблонов vm, используя данные из черновика и описания услуги.

## Локальный запуск
В корне репозитория лежaт файлы:
- `docker-compose-dev01.yaml` для запуска form-service и sp-adapter-service локально, интеграция с остальными сервисами с dev окружения.
- `docker-compose-local.yaml` для запуска всех сервисов этого репозитория локально.
  В них описаны сервисы которые нужны для прохождения полного процесса от начала заполнения заявления до появления его в ЛК.

### Запуск сервисов из docker-compose-local
Из корня репозитория выполнить команды:
```
mvn clean install
docker-compose -f ./docker-compose-local.yaml build --no-cache
docker-compose -f ./docker-compose-local.yaml up -d
```
Если какой-то из сервисов, например form-service, планируется запускать в дебаге, запустить остальные исключив из списка form-service:
```
docker-compose -f ./docker-compose-local.yaml up sp-adapter-service service-descriptor-storage cassandra cassandra-load-service-descriptor-keyspace
```
либо просто закомментировав описание form-service в `docker-compose-local.yaml`.

#### sp-adapter-service
Для запуска с профилем local нужно поднять service-descriptor-storage (см. ниже).
Для запуска с подключением к service-descriptor-storage на дев-стенде нужно запустить sp-adapter-service с профилем `dev01`.
Для смены профиля нужно поменять команду запуска service-descriptor-storage в `docker-compose-local.yaml`:
```
command: java -jar /app.jar --spring.profiles.active=dev01
```

#### service-descriptor-storage
Для запуска с локальным хранилищем, нужно запустить service-descriptor-storage с профилем `local` и поднять контейнеры:
- cassandra;
- cassandra-load-service-descriptor-keyspace.
  Для запуска с подключением хранилища дев-стенда нужно запустить service-descriptor-storage с профилем `dev01`.

#### Загрузка шаблонов и сервис дескрипторов в локальное хранилище
Шаблоны и дескрипторы лежат в репозитории `epgu2-services-json`, там в README описано как их загрузить используя скрипт.
Пример загрузки дескриптора услуги 10000000103-судимость:
```
curl -X PUT -H "Content-Type: application/json" -d @./sudimost.json http://localhost:8096/v1/scenario/10000000103
```
Пример загрузки шаблонов услуги 10000000103-судимость:
```
curl -X PUT -H "Content-Type: multipart/form-data" --form 'template=@'./10000000103.zip http://localhost:8096/v1/templates/10000000103
```
Предварительно шаблоны нужно запаковать в архив, только не папку 10000000103 целиком, а именно шаблоны из этой папки.

### Остановка сервисов
```
docker-compose -f ./docker-compose-local.yaml down --volume
```

# Полезно
* [Формирование PDF](docs/service_pdf.md)