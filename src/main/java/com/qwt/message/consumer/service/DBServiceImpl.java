package com.qwt.message.consumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Component
public class DBServiceImpl implements DBService {

    private RestOperations restOperations;
    private String url;

    @Autowired
    public DBServiceImpl(
            @Qualifier("addValueAPIRestOperations")
                    RestOperations restOperations,
            @Value("${addValue.url}")
                    String url) {
        this.restOperations = restOperations;
        this.url = url;
    }

    @Override
    public String addValue(String value) {
        try {
            System.out.println("----------------test call");
            ResponseEntity responseEntity = restOperations.exchange(createRequest(value), new ParameterizedTypeReference<Map<String, String>>() {
            });
            System.out.println("response: " + responseEntity);
            return value;
        } catch (Exception e) {
            throw e;
        }
    }

    private RequestEntity createRequest(String value) {
        try {
            System.out.println("--------------createRequest");
            RequestEntity request = RequestEntity.post(new URI(url))
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Accept-Encoding", "gzip")
                    .body(value);
            return request;
        } catch (URISyntaxException e) {
            throw new RuntimeException("Exception in adding value", e);
        }
    }
}
