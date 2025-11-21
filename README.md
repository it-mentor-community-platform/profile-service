Profile Service
===============================================================

### Основная информация
Сервис реализует работу с профилем пользователя, который включает в себя `username`, `telegram account`,
`github account`, `labels` для роли пользователя(студент, ментор и т.д.) и его ачивок, ссылки на сданные проекты роадмапа,
ссылки на сделанные ревью проектов других пользователей и т.д.

Сервис имеет свою
[базу данных](https://github.com/it-mentor-community-platform/meta/blob/main/system-analytics/services/profile-service/index.md#%D1%81%D1%85%D0%B5%D0%BC%D0%B0-%D0%B1%D0%B4)
для хранения деталей профиля пользователя.

Данные о `id` текущего пользователя и его роли сервис получает из
[кастомных заголовков](https://github.com/it-mentor-community-platform/meta/blob/main/system-analytics/services/gateway/index.md#%D0%BF%D1%80%D0%B0%D0%B2%D0%B8%D0%BB%D0%B0-security)
входящего запроса.

### Используемый стек
- Spring Boot 3
- Spring Data JDBC
- Spring Kafka
- Liquibase

### Сборка и запуск Docker-образа
Перейдите в корень проекта, на один уровень с `Dockerfile`, и используйте следующие команды:
1. Сборка образа
    ```bash
    docker build -t profile-service:local .
    ```
2. Запуск контейнера
    ```bash
    docker run \
      -e POSTGRES_URL=your_postgres_server_url \
      -e POSTGRES_USERNAME=root \
      -e POSTGRES_PASSWORD=password \
      -p 8080:8080 \ 
      profile-service:local
    ```
   **!** `your_postgres_server_url` - замените на адрес вашего postgres сервера. Например `192.168.1.50:5432`
   или `postgres:5432` или `localhost:5432`, в зависимости от способа запуска БД и её настроек. <br>
   В случае подключения к локально запущенной базе postgres может помочь использование адреса 
   `host.docker.internal:5432` и ключа запуска `--add-host=host.docker.internal:host-gateway` для `docker run`.
   Но так же должен быть настроен postgres для работы с запросами с других ip, помимо `localhost`.(по-умолчанию
   настроен на `localhost`)

### Ссылки на репозиторий документации
- [Системная аналитика Profile Service](https://github.com/it-mentor-community-platform/meta/blob/main/system-analytics/services/profile-service/index.md)