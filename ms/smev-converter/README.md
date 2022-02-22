## smev-converter - преобразователь ответов барбарбока в json

### Описание
См. https://jira.egovdev.ru/browse/EPGUCORE-81867

Микросервис отправляет xml-запрос в СМЭВ (барбарбок) и преобразовывает полученный xml-ответ в формат json по указанному vm-шаблону через draft-converter.

Доступность на тестовых окружениях:
- http://pgu-uat-fednlb.test.gosuslugi.ru/smev-converter
- https://dev01.pgu2-pub.test.gosuslugi.ru/smev-converter
- https://dev02.pgu2-pub.test.gosuslugi.ru/smev-converter
- https://dev-l11.pgu2-pub.test.gosuslugi.ru/smev-converter

### Swagger-ui 
http://localhost:8092/api/swagger-ui.html

### Actuator 
http://pgu-uat-fednlb.test.gosuslugi.ru/smev-converter/actuator
