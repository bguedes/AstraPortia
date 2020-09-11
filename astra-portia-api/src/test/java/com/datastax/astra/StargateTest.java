package com.datastax.astra;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.datastax.astra.client.AstraPortiaServices;
import com.datastax.astra.client.AstraStargateApiClient;

@RunWith(JUnitPlatform.class)
@SpringJUnitConfig
@ContextConfiguration(classes = {AstraStargateApiClient.class, AstraPortiaServices.class})
@TestPropertySource(locations = "/application-test.properties")
public class StargateTest {
  
    @Autowired
    private AstraStargateApiClient stargateClient;
    
    @Autowired
    private AstraPortiaServices services;
    
    @Test
    public void shoud_return_an_authToken_() {
        Optional<String> authToken = stargateClient.authentiticate();
        Assertions.assertThat(authToken.isPresent());
    }
    
    @Test
    public void should_file_be_found() {
        services.importNearEarthObjects("src/main/resources/2020_09_10_near_earth_asteroids_and_comets.json");
    }

}