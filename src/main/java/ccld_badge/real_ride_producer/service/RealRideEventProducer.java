package ccld_badge.real_ride_producer.service;

import ccld_badge.real_ride_producer.config.AutoCreateConfig;
import ccld_badge.real_ride_producer.domain.RealRideEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RealRideEventProducer {

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "real-ride-events";

    @Autowired
    public RealRideEventProducer(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void send(RealRideEvent realRideEvent) throws JsonProcessingException {
        Integer key = realRideEvent.getRideID();
        String msg = objectMapper.writeValueAsString(realRideEvent);

        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(TOPIC, null, key, msg);

        kafkaTemplate.send(producerRecord).whenComplete((result, ex) -> {
            if (ex == null) {
                handleSuccess(key, msg, result);
            } else {
                handleFailure(key, msg, ex);
            }
        });
    }

    private void handleFailure(Integer key, String msg, Throwable ex) {
        log.error("Error sending the message with key {} for message {} and exception: {}",
                key, msg, ex.getMessage());
    }

    private void handleSuccess(Integer key, String msg, SendResult<Integer, String> result) {
        log.info("Message sent successfully for the key: {} and the value: {}, partition: {}",
                key, msg, result.getRecordMetadata().partition());
    }
}

