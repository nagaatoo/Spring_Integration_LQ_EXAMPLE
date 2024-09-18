# Пример реализации системы менеджмента сообщений с использованием Spring Integration

### Запуск
Сервис использует два профиля - prod (с интеграциями) и default (dev mode без интеграций)

Для default:
```
mvn spring-boot:run
```


Для prod:

```
docker-compose up
```
```
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```
