package com.example.cloudstorage.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

public enum LoggerMessages {

    AUTH_BY_TOKEN ("Попытка авторизации по токену пользователем %s"),
    AUTH_BY_TOKEN_SUCCESS ("Авторизация по токену пользователем %s успешна"),
    AUTH_BY_TOKEN_FAILED ("Авторизация по токену завершилась неудачно"),
    AUTH_BY_TOKEN_ERROR ("Произошла ошибка во время проверки токена: %s"),
    EDIT_FILE_NAME ("Принят запрос на изменение имени файла %s"),
    EDIT_FILE_NAME_SUCCESS("Имя файла %s успешно изменено на $s"),
    FILE_LOAD ("Принят запрос на загрузку файла %s"),
    FILE_LOAD_SUCCESS ("Файл %s загружен успешно"),
    FILE_DELETE ("Принят запрос на удаление файла %s"),
    FILE_DELETE_SUCCESS("Файл %s удален успешно"),
    FILE_DOWNLOAD ("Принят запрос на скачивание файла %s"),
    FILE_DOWNLOAD_SUCCESS ("Файл %s отправлен успешно"),
    GET_FILES_LIST ("Принят запрос на получение списка файлов"),
    GET_FILES_LIST_SUCCESS ("Список файлов отправлен успешно"),
    JWT_TOKEN_IN_BLACKLIST ("Jwt токен %s в черном списке"),
    JWT_TOKEN_INVALID ("Jwt токет %s не валидный"),
    LOGOUT ("Пользователь %s разлогинился, токен %s добавлен в черный список"),
    SUCCESS_AUTHENTICATION ("Аудентификация пользователя %s прошла успешно, пользователю выдан токен с датой экспирации - %s"),
    START ("Файловый сервис запущен"),
    TRY_AUTHENTICATION ("Попытка аудентификации пользователя %s"),
    TRY_AUTHENTICATION_BY_TOKEN ("Попытка аудентификации пользователя по токену");

    private final String message;

    LoggerMessages(String message) {
        this.message = message;
    }

    public String getMessage(String... strings) {
        switch (strings.length) {
            case 1:
                return String.format(this.message, strings[0]);
            case 2:
                return String.format(this.message, strings[0], strings[1]);
            default:
                return this.message;
        }
    }
}
