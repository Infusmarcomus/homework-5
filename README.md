# homework-6
Я понял что у меня неправильная бизнес логика у меня контроллер в предыдущей
версии делал обработку через маппер делал преобразования сам
и сервис возвращал сущность

1. в этой версии я вынес всю логику в service(преобразования маппер и логика)
контроллер стал чистым а сервис все делает сам и возвращает dto

стало:

Клиент → Controller (чистый) → Service (вся логика) → DB

было:

Клиент → Controller(mapper) → Service (не вся логика) → Repository → DB

короче оптимизировал
2. из DTO я не отдаю ID и createdAt uptatedAt не знаю нужно ли знать
клиенту об этом подумал что нет
3. переписал тесты
4. сделал swagger для api написал схемы
5. сделал eventType enum потому что string к регистру чувствителен
6. сделал кастомные исключения 
7. сделал hateous


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

