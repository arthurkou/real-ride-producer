package real_ride_producer.config;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.common.config.SaslConfigs.SASL_MECHANISM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class KafkaConnectionProperties {

    private static final String SASL_BASIC_AUTH_MECHANISM = "PLAIN";

    public Map<String, Object> buildConnectionProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(BOOTSTRAP_SERVERS_CONFIG, getBootstrapServer());
        props.put(SASL_MECHANISM, SASL_BASIC_AUTH_MECHANISM);
        return props;
    }

    private List<String> getBootstrapServer() {
        return List.of("localhost:9092", "localhost:9093", "localhost:9094");
    }
}
