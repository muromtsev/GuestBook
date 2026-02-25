# 📖 Гостевая книга (Guestbook)

Веб-приложение для сбора отзывов и сообщений от посетителей. Написано на Spring Boot с использованием PostgreSQL.

## 🚀 Функциональность

- ✍️ Добавление записей с именем, email и сообщением
- ✅ Валидация введенных данных (имя, email, длина сообщения)
- 🖼️ Автоматическая генерация аватарок через Gravatar или DiceBear
- 📋 Отображение всех записей в обратном хронологическом порядке
- 📱 Адаптивный дизайн (Thymeleaf + Bootstrap)

## 🛠 Технологии

- **Java 21**
- **Spring Boot 3.5.0**
- **Spring MVC** + **Thymeleaf**
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL**
- **Lombok**
- **Maven**
- **Docker** / **Docker Compose**
- **JUnit 5** + **Mockito** (тесты)

## 📋 Требования

- Java 21+
- Maven 3.8+
- PostgreSQL 16+ (или Docker)
- Docker (опционально)

### Запуск через Docker Compose

1. #### Соберите JAR #### 
    `mvn clean package -DskipTests` 
2. #### Поднимите контейнеры #### 
    `docker-compose up --build`
3. #### Откройте в браузере ####
    `http://localhost:8080`

## 📄 Лицензия
MIT License - свободное использование, модификация и распространение.

