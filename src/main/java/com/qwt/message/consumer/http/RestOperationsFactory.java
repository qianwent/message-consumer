package com.qwt.message.consumer.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Component
public class RestOperationsFactory {

    private final Environment env;
    private final ObjectMapper objectMapper;

    @Autowired
    public RestOperationsFactory(Environment env, ObjectMapper objectMapper) {
        this.env = env;
        this.objectMapper = objectMapper;
    }

    public RestOperations createRestOperations(String prefixKey) {
        return newRestOperations(prefixKey);
    }

    private RestOperations newRestOperations(String key) {
        System.out.println("--------------newRestOperations");
        final int readTimeout = getConfiguredReadTimeout(key);
        final int connectTimeout = getConfiguredConnectTimeout(key);
        HttpClient httpClient = buildHttpClient(key);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        final RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setMessageConverters(asList(new MappingJackson2HttpMessageConverter(objectMapper)));
        return restTemplate;
    }

    private int getConfiguredReadTimeout(String key) {
        return env.getProperty(key + ".readTimeout", Integer.class, 10);
    }

    private int getConfiguredConnectTimeout(String key) {
        return env.getProperty(key + ".connectTimeout", Integer.class, 10);
    }

    private HttpClient buildHttpClient(String key) {
        final List<Header> headers = new ArrayList<>();
//        addBasicAuthHeaderTo(headers, key);
        final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setDefaultHeaders(ImmutableList.copyOf(headers));
        httpClientBuilder.setConnectionManager(newConnectionManager(key));
        return httpClientBuilder.build();
    }

    private PoolingHttpClientConnectionManager newConnectionManager(String key) {
        final int defaultMaxPerRoute = env.getProperty(
                key + ".clientConnectionManager.defaultMaxPerRoute", Integer.class, 10);
        final int maxTotal = env.getProperty(key + ".clientConnectionManager.maxTotal",
                Integer.class, 20);
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        connectionManager.setMaxTotal(maxTotal);
        return connectionManager;
    }
}
