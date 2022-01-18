## Микросервис для отправки запросов в СМЭВ-адаптер по JMS и получения ответов по ним

### Описание
Сервис получения данных из СМЭВ через вызов СМЭВ-адаптера в синхронном режиме или асинхронном режиме.
Позволяет синхронно получить данные для детсадов по orderId и userSelectedRegion.
Может использоваться для отправки любых сообщений в СМЭВ-адаптер.
На стороне сервиса сообщения попадают в очереди "spring.jms.template.send-queue-name" и "spring.jms.template.reply-queue-name". 

Rest-спецификация: http://localhost:8099/api/v3/api-docs
Swagger-ui: http://localhost:8099/api/swagger-ui/index.html?configUrl=/api/v3/api-docs/swagger-config
