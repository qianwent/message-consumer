package com.qwt.message.consumer.service;

import com.qwt.message.consumer.exception.KnownServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(DBServiceImpl.class);

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
            ResponseEntity responseEntity = restOperations.exchange(createRequest(value), new ParameterizedTypeReference<Map<String, String>>() {
            });
            LOGGER.info("response: " + responseEntity);
            return value;
        } catch (Exception e) {
            LOGGER.error("RestOperations exception: ", e);
            if (e instanceof HttpStatusCodeException && ((HttpStatusCodeException) e).getStatusCode().is4xxClientError()) {
                throw new KnownServiceException(e);
            }
            throw e;
        }
    }

    private RequestEntity createRequest(String value) {
        try {
            LOGGER.info("--------------createRequest");
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
