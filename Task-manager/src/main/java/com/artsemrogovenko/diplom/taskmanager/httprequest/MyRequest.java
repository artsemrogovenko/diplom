package com.artsemrogovenko.diplom.taskmanager.httprequest;

import com.artsemrogovenko.diplom.taskmanager.dto.ComponentRequest;
import com.artsemrogovenko.diplom.taskmanager.dto.ComponentResponse;
import com.artsemrogovenko.diplom.taskmanager.dto.TaskForUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

public class MyRequest {


    /**
     * @param request        метод Http запроса
     * @param serializedData тело запроса json
     * @param required       требуемый класс на выходе
     * @return ResponseEntity
     * @throws IOException
     */
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

    /**
     * получить список обьектов Get запросом
     *
     * @param url   путь запроса
     * @param clazz Требуемый класс обьектов на выходе
     * @throws IOException
     */
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

    public static ResponseEntity<List<ComponentResponse>> postRequest(String url, List<ComponentRequest> componentRequests, String user, String number) {
        RestTemplate restTemplate = new RestTemplate();
        // Создаем объект HttpHeaders для указания типа контента
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Создаем тело запроса с использованием списка ComponentRequest
        HttpEntity<List<ComponentRequest>> requestEntity = new HttpEntity<>(componentRequests, headers);
        // Создаем строку запроса с параметрами user и number
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("user", user)
                .queryParam("number", number);
        String request = builder.toUriString();
        // Отправляем POST запрос
        ResponseEntity<List<ComponentResponse>> responseEntity = restTemplate.exchange(
                request,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<ComponentResponse>>() {
                }
        );
        return responseEntity;
    }

    public static ResponseEntity<String> assignTask(TaskForUser task, String userId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Создаем тело запроса с использованием списка ComponentRequest
        HttpEntity<TaskForUser> requestEntity = new HttpEntity<>(task, headers);
        // Создаем строку запроса с параметрами user и number
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://account-service:8084/task/assignTask/")
                .queryParam("userId", userId);
        String request = builder.toUriString();
        // Отправляем POST запрос
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                request,
                HttpMethod.POST,
                requestEntity,
                String.class); // Ожидаемый тип ответа - String
        return responseEntity;
    }

    public static ResponseEntity<?> rollbackComponents(List<ComponentRequest> components) {
        // Создаем экземпляр RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Устанавливаем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создаем тело запроса
        HttpEntity<List<ComponentRequest>> requestEntity = new HttpEntity<>(components, headers);

        // Указываем URL эндпоинта, куда отправляется запрос
        String url = "http://storage-server:8081/component/megaImport";

        // Отправляем POST-запрос
        ResponseEntity<?> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                Object.class); // Ожидаемый тип ответа

        return responseEntity;
    }



}
