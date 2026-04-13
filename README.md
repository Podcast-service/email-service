# Email микросервис

## Сборка и запуск
- `docker compose up --build`

## Сообщение в RabbitMQ
Принимает сообщение от RabbitMQ в формате JSON со следующими полями: *type, toEmail, code*

## Шаблоны для писем
Шаблоны для писем:
- Для подтверждения: [verification.html](./src/main/resources/templates/verification.html)
- Для восстановления [password-reset.html](./src/main/resources/templates/password-reset.html)
