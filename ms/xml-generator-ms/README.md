## Микросервис создания XML

### Описание

Формирует XML-файлы на основе шаблонов vm, используя данные из черновика и описания услуги.

Изначально реализован на Apache Velocity. Управляет
процессом [XmlService](src/main/java/ru/gosuslugi/pgu/xmlservice/service/XmlService.java).
Посредством [TemplateDataService](src/main/java/ru/gosuslugi/pgu/xmlservice/context/service/TemplateDataService.java)
формируется
[контекст](src/main/java/ru/gosuslugi/pgu/xmlservice/render/data/UsageAwareContext.java)
(данные для шаблона). Они передаются в обработчик
[RenderService](src/main/java/ru/gosuslugi/pgu/xmlservice/render/service/RenderService.java),
который производит загрузку шаблона посредством
[TemplateService](src/main/java/ru/gosuslugi/pgu/xmlservice/template/service/TemplateService.java)
в локальное хранилище шаблонов Apache Velocity. TemplateService также следит за изменениями шаблонов
и синхронизирует их
с [сервисом хранения дескриптора услуг](src/main/java/ru/gosuslugi/pgu/xmlservice/template/client/ServiceDescriptorTemplateClient.java)
. После получения результирующей строки, перед возвращением в качестве ответа, она валидируется.

### Внешние интеграции

Сервис обращается к

1) descriptor-storage-service,
2) draft-service,
3) terrabyte.

Сервис черновиков (draft-service) используется для загрузки черновика только для случаев, когда во
входном запросе отсутствует тело черновика (см. `GenerateXmlRequest#draft`).

Terrabyte вызывается при получении digest-values вложений в черновике и при обработке запроса на
конечную точку `/generateAndStore` для сохранения полученного файла.

Дескриптор услуг (descriptor-storage-service) вызывается всегда для наполнения контекста параметрами
из описания услуги (атрибут `parameters`) и для получения флага о необходимости обработки digest
values.

### Контракты

* Если в результате рендеринга получился пустой документ, исключение не выбрасывается. Такое
  содержимое не валидируется.
* Если в результате рендеринга остались placeholder-ы, выбрасывается исключение: 400 Bad Request.

### API и проверка

* [Rest-спецификация](http://localhost:8097/api/v3/api-docs)
* [Swagger-UI](http://localhost:8097/api/swagger-ui/index.html?configUrl=/api/v3/api-docs/swagger-config)
* В IntelliJ IDEA есть HTTP Client, для которого подходят примеры запросов в
  http-client/http-requests.http. В http-client.env.json можно задать код услуги, id пользователя,
  адрес сервиса для определенного окружения. Запросы можно конвертировать в curl.
