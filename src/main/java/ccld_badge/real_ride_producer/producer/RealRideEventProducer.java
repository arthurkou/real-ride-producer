package ccld_badge.real_ride_producer.producer;

import static ccld_badge.real_ride_producer.config.RealRideProducerConfig.ATTEMPT_INTERVAL_IN_SECONDS;
import static ccld_badge.real_ride_producer.config.RealRideProducerConfig.MAX_ATTEMPTS_FOR_RETRY;

import ccld_badge.real_ride_producer.domain.RealRideEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.ConnectException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RealRideEventProducer {

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public RealRideEventProducer(
            KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Retryable(
            retryFor = ConnectException.class,
            backoff = @Backoff(delay = ATTEMPT_INTERVAL_IN_SECONDS * 1000),
            maxAttempts = MAX_ATTEMPTS_FOR_RETRY)
    public void send(RealRideEvent realRideEvent) throws JsonProcessingException {
        Integer key = realRideEvent.getRideID();
        String msg = objectMapper.writeValueAsString(realRideEvent);

        ProducerRecord<Integer, String> producerRecord =
                new ProducerRecord<>(topic, null, key, msg);

        kafkaTemplate
                .send(producerRecord)
                .whenComplete(
                        (result, ex) -> {
                            if (ex == null) {
                                handleSuccess(key, msg, result);
                            } else {
                                handleFailure(key, msg, ex);
                            }
                        });
    }

    private void handleFailure(Integer key, String msg, Throwable ex) {
        log.error(
                "Error sending the message with key {} for message {} and exception: {}",
                key,
                msg,
                ex.getMessage());
    }

    private void handleSuccess(Integer key, String msg, SendResult<Integer, String> result) {
        log.info(
                "Message sent successfully for the key: {} and the value: {}, partition: {}",
                key,
                msg,
                result.getRecordMetadata().partition());
    }
}
