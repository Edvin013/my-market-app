# My Market App

Простое приложение интернет‑магазина: список товаров, их карточки, корзина и оформление заказов. Сделано на Spring Boot.

## Что тут можно делать
- Открыть каталог и найти нужный товар
- Заглянуть в карточку и решить купить
- Сложить товары в корзину, менять количество или убрать лишнее
- Нажать «Купить» и получить заказ
- Посмотреть историю своих заказов

## Как запустить
```bash
./mvnw clean package
java -jar target/my-market-app-0.0.1-SNAPSHOT.jar
```
```bash
docker compose up -d --build
```
Приложение будет на: http://localhost:8080

Тесты (в памяти на H2):
```bash
./mvnw test
```

## Коротко про API
Главное без деталей: товары `/items`, корзина `/cart/items`, оформить `/buy`, заказы `/orders`.

## База
В работе — PostgreSQL. В тестах — лёгкая H2 (без сохранения данных).

## Технологии 
Java 21 · Spring Boot · Spring MVC · JPA/Hibernate · PostgreSQL · H2 (tests) · Thymeleaf · Lombok · JUnit 5 · Mockito · Maven · Docker

