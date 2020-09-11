package com.datastax.astra.client;

import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

@Component
public class AstraStargateApiClient {

    /** Logger for the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraStargateApiClient.class);
   
    /** Header for authToken. */
    public static final String HEADER_CASSANDRA = "X-Cassandra-Token";
    
    @Value("${astra.stargate.db-id}")
    private String dbId;
    
    @Value("${astra.stargate.region-id}")
    private String regionId;
    
    @Value("${astra.stargate.username}")
    private String username;
    
    @Value("${astra.stargate.password}")
    private String password;
    
    @Value("${astra.stargate.keyspace}")
    private String keyspace;
    
    private HttpClient httpClient;
    
    private ObjectMapper objectMapper;
    
    public AstraStargateApiClient() {
        // Setup HTTP client
        httpClient = HttpClient.newBuilder()
                .version(Version.HTTP_2)
                .followRedirects(Redirect.NORMAL)
                .build();
        LOGGER.info("Http Client has been initialized");
        
        // Setup Jackson
        objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
        objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
        LOGGER.info("Jackson serializer has been initialized");
    }
    
    public AstraStargateApiClient(String dbId, String regionId, 
            String username, String password, String keyspace) {
        this();
        this.dbId = dbId;
        this.regionId = regionId;
        this.username = username;
        this.password = password;
        this.keyspace = keyspace;
    }
    
    @SuppressWarnings("unchecked")
    public Optional<String> authentiticate() {
        try {
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getAstratUrlAuth()))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString("{ "
                            + "\"username\": \""+ username +"\", "
                            + "\"password\": \""+ password + "\"}")).build();
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (null !=response && response.statusCode() == HttpStatus.OK.value()) {
                Map<String, String> repo = objectMapper.readValue(response.body(), Map.class);
                return Optional.ofNullable(repo.get("authToken"));
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
    }
    
    public <D extends Serializable> String saveDocument(D doc, String authToken, String collectionName) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getAstratUrlAuth()))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(objectMapper.writeValueAsString(doc))).build();
            
            // Todo implement the call
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occured", e);
        }
        
        // TODO parsing respond 
        
        return null;
    }
    
    public <D extends Serializable> Optional<D> readObject(String authToken, String collectioName, String docId) {
        return null;
    }
    
    private String getAstraApiUrlCore() {
        StringBuilder sb = new StringBuilder();
        sb.append("https://").append(dbId).append("-").append(regionId);
        sb.append(".apps.astra.datastax.com/api/rest");
        return sb.toString();
    }
    
    private String getAstratUrlAuth() {
        return getAstraApiUrlCore() + "/v1/auth/";
    }
    
    private String getAstraUrlCreateNewObject(String collectionName) {
        return getAstraApiUrlCore() + "/v2/namespaces/" + keyspace + "/collections/" + collectionName + "/";
    }

    /**
     * Getter accessor for attribute 'objectMapper'.
     *
     * @return
     *       current value of 'objectMapper'
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}