package com.artsemrogovenko.diplom.specification.httprequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public class MyRequest {

    public static <T> ResponseEntity<T> convertResponse(HttpRequestBase request, String serializedData, Class<T> required) throws IOException {
        T result = null;
        int statusCode = 0;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        if (request instanceof HttpPost) {
            HttpPost httpPost = (HttpPost) request;
            httpPost.setEntity(new StringEntity(serializedData, ContentType.APPLICATION_JSON));
            response = httpClient.execute(httpPost);
        }
        if (request instanceof HttpPut) {
            HttpPut httpPut = (HttpPut) request;
            httpPut.setEntity(new StringEntity(serializedData, ContentType.APPLICATION_JSON));
            response = httpClient.execute(httpPut);
        }

        try {
            statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            ObjectMapper objectMapper = new ObjectMapper();
            result = objectMapper.readValue(responseBody, required);
        } finally {
            response.close();
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf(statusCode));
    }

    public static <T> ResponseEntity<List<T>> convertResponseList(String url, Class<T> clazz) throws IOException {
        List<T> result = null;
        int statusCode = 0; // Объявляем statusCode за пределами блока try
        CloseableHttpClient httpClient = HttpClients.createDefault();   // Создаем экземпляр HttpClient
        HttpGet httpGet = new HttpGet(url); // Создаем объект HttpGet с URL, куда нужно отправить запрос
        CloseableHttpResponse response = httpClient.execute(httpGet);    // Выполняем запрос
        try {
            statusCode = response.getStatusLine().getStatusCode();// Получаем код ответа
            String responseBody = EntityUtils.toString(response.getEntity());

            ObjectMapper objectMapper = new ObjectMapper();// Создаем ObjectMapper для преобразования JSON в объект Java
            result = objectMapper.readValue(responseBody, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
            // Теперь объект result содержит данные из тела ответа в виде списка объектов заданного класса
        } finally {
            // Важно закрыть ответ после использования для освобождения ресурсов
            response.close();
        }
        return new ResponseEntity<>(result, HttpStatus.valueOf(statusCode));
    }

}
