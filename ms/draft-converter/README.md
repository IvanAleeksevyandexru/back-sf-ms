# Draft Converter Service

Конвертирует некоторые данные (изначально XML данные от СМЭВ и сведения из словарей) в данные
черновика (ScenarioDto) или в указанный шаблоном json-формат.

Изначально реализован на Apache Velocity.
Управляет процессом [XmlDraftConverter](src/main/java/ru/gosuslugi/pgu/draftconverter/service/XmlDraftConverter.java).
Посредством [TemplateDataService](src/main/java/ru/gosuslugi/pgu/draftconverter/context/service/TemplateDataService.java)
формируется [контекст](src/main/java/ru/gosuslugi/pgu/draftconverter/render/data/UsageAwareContext.java) (данные для шаблона).
Они передаются в обработчик [RenderService](src/main/java/ru/gosuslugi/pgu/draftconverter/render/service/RenderService.java),
который производит загрузку шаблона посредством [TemplateService](src/main/java/ru/gosuslugi/pgu/draftconverter/template/service/TemplateService.java)
в локальное хранилище шаблонов Apache Velocity. TemplateService также следит за изменениями шаблонов и синхронизирует
их с [сервисом хранения дескриптора услуг](src/main/java/ru/gosuslugi/pgu/draftconverter/template/client/ServiceDescriptorTemplateClient.java).
После получения результирующей строки, перед возвращением в качестве ответа, она конвертируется в
`ScenarioDto`.

Публичный API находится в пакете `ru.gosuslugi.pgu.draftconverter.api`.
REST-спецификация: http://localhost:8097/api/v3/api-docs
Swagger-ui: http://localhost:8097/api/swagger-ui/index.html?configUrl=/api/v3/api-docs/swagger-config
