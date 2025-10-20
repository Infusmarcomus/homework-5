# homework-5
В первом комите у меня был pom общий - агрегатор чтобы проще запускать было
вы сказали убрать


User-serivce - логика для пользователя

Notification-service - для логики оповещений.

DTO вынесен в отдельный модуль common-dto (библиотека общая)

Инфраструктура (Kafka, Zookeeper, PostgreSQL) поднимается через docker-compose.

**Как запускать:**

Поднять инфраструктуру:
1. docker compose up -d
2. cd user-service
   mvn spring-boot:run
3. cd notification-service
   mvn spring-boot:run


- user-service → порт: 8080
- notification-service → порт: 8081
- kafka: 9093

Через postman
JSON для теста чтобы не лезть в контролер и не искать end point


http://localhost:8080/api/users/register

{
"name": "Artur",
"lastName": "Marchenko",
"email": "artur.marchenko@mail.ru",
"age": 25,
"password": "qwerty123"
}