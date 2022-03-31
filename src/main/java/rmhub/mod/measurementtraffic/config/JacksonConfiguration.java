package rmhub.mod.measurementtraffic.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfiguration {

  @Primary
  @Bean
  public ObjectMapper jsonMapper() {
    final ObjectMapper jsonMapper = new ObjectMapper();
    jsonMapper.enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
    //  Setting should be set to true!
    jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    return jsonMapper;
  }

}