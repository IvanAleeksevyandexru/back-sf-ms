## Микросервис отправки уведомлений в ЛК по JMS

### Описание
Сервис отправки уведомлений в ЛК по JMS для сохранения кода региона пользователя в асинхронном режиме.
Может использоваться для отправки любых сообщений. 
На стороне ЛК сообщения попадают в очередь "jms.queue.scFormDataRequest", после чего записываются в базу. 

Rest-спецификация: http://localhost:8097/api/v3/api-docs
Swagger-ui: http://localhost:8097/api/swagger-ui/index.html?configUrl=/api/v3/api-docs/swagger-config
