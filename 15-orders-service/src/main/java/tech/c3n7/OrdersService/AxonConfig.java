package tech.c3n7.OrdersService;

import com.thoughtworks.xstream.XStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// https://discuss.axoniq.io/t/getting-xstream-dependency-exception/3634/6
@Configuration
public class AxonConfig {

    @Bean
    public XStream xStream() {
        XStream xStream = new XStream();

        xStream.allowTypesByWildcard(new String[] { "tech.c3n7.**" });
        return xStream;
    }
}