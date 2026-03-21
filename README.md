# Email микросервис

## Сборка и запуск
- `./gradlew build`
- `./gradlew bootRun`

## Сообщение в RabbitMQ
Принимает сообщение от RabbitMQ в формате JSON со следующими полями: *type, toEmail, link*

## Шаблоны для писем
Шаблоны для писем:
- Для подтверждения: [verification.html]()
- Для восстановления [password-reset.html]()
