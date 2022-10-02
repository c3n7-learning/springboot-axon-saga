package tech.c3n7.PaymentsService;

import com.thoughtworks.xstream.XStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

// https://discuss.axoniq.io/t/getting-xstream-dependency-exception/3634/6
@Configuration
@Import({ AxonConfig.class })
public class AxonConfig {

    @Bean
    public XStream xStream() {
        XStream xStream = new XStream();

        xStream.allowTypesByWildcard(new String[] { "tech.c3n7.**" });
        return xStream;
    }
}