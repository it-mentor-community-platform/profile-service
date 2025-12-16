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

### Локальный запуск и тестирование   
- Через консоль
```bash
    ./gradlew bootRun --args='--spring.profiles.active=ide'
```

- В IntelliJ IDEA
  * Run -> Edit Configurations....
  * В поле Active profiles введите имя профиля: `ide`

### Ссылки на репозиторий документации
- [Системная аналитика Profile Service](https://github.com/it-mentor-community-platform/meta/blob/main/system-analytics/services/profile-service/index.md)
