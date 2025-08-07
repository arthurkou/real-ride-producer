package real_ride_producer.config;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.util.Map;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import real_ride.avro.messages.RealRideAvroMessage;

@Configuration
public class RealRideProducerConfig {

    private static final String SERIALIZER_CLASS_CONFIG_KEY = IntegerSerializer.class.getName();
    private static final String AVRO_SERIALIZER_CLASS_CONFIG = KafkaAvroSerializer.class.getName();
    public static final int MAX_ATTEMPTS_FOR_RETRY = 5;
    public static final int ATTEMPT_INTERVAL_IN_SECONDS = 2;

    @Bean
    public ProducerFactory<Integer, RealRideAvroMessage> kafkaProducerFactory(
            KafkaConnectionProperties connProperties) {
        return new DefaultKafkaProducerFactory<>(buildProducerProperties(connProperties));
    }

    @Bean
    public KafkaTemplate<Integer, RealRideAvroMessage> kafkaTemplate(
            ProducerFactory<Integer, RealRideAvroMessage> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    private Map<String, Object> buildProducerProperties(KafkaConnectionProperties connProperties) {
        var props = connProperties.buildConnectionProperties();
        props.put(KEY_SERIALIZER_CLASS_CONFIG, SERIALIZER_CLASS_CONFIG_KEY);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, AVRO_SERIALIZER_CLASS_CONFIG);
        props.put("schema.registry.url", "http://localhost:8081");
        return props;
    }
}
