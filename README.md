# Дипломная работа “Облачное хранилище”

## Описание проекта

Приложение - REST-сервис, предоставляет REST интерфейс для возможности загрузки файлов и вывода списка уже загруженных файлов пользователя. 
Все запросы к сервису авторизованы. Веб-приложение (FRONT) подключается к сервису, и предоставляет пользователю функционал FRONT для авторизации, загрузки и вывода списка файлов пользователя.

## Требования к приложению:

- Сервис предоставляет REST интерфейс для интеграции с FRONT
- Сервис реализовывает все методы описанные [yaml файле](./CloudServiceSpecification.yaml):
  1. Вывод списка файлов
  2. Добавление файла
  3. Удаление файла
  4. Авторизация
- Все настройки вычитываться из файла настроек (yml)
- Информация о пользователях сервиса (логины для авторизации) и данных хранятся в базе данных postgresql

## Требования в реализации

- Приложение разработано с использованием Spring Boot
- Использован сборщик пакетов maven
- Для запуска используется docker, docker-compose
- Код размещен на github
- Код покрыт unit тестами с использованием mockito
- Добавлены интеграционные тесты с использованием testcontainers

## Описание и запуск приложения:

- Приложение развертывается в 4 контейнерах: BACKEND, база данных POSTGRES, FRONTEND и прокси сервер NGINX
- После запуска контейнеров сервис становится доступным по адресу http://localhost:5500
- Взаимодейтвие с FRONT происходит через прокси по адресу http://localhost:5500/
- Взаимодействие с BACK через прокси по адресу http://localhost:5500/login, http://localhost:5500/logout, http://localhost:5500/file, http://localhost:5500/list

## Авторизация приложения:

FRONT приложение использует header `auth-token` в котором отправляет токен (ключ-строка) для идентификации пользователя на BACKEND.
Для получения токена нужно пройти авторизацию на BACKEND, отправив на метод /login пару логин и пароль (список в файле authorization_data.txt), в случае успешной проверки в ответ BACKEND возвращает json объект
с полем `auth-token` и значением токена. Все дальнейшие запросы с FRONTEND, кроме метода /login отправляются с этим header.
Для выхода из приложения нужно вызвать метод BACKEND /logout, который добавляет токен в blacklist, до момента истечения его срока действия (5 часов с момента его выдачи) и последующие запросы с этим токеном будут не авторизованы и возвращать код 401.
