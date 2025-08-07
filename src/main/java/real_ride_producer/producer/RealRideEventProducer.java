package real_ride_producer.producer;

import java.net.ConnectException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import real_ride.avro.messages.RealRideAvroMessage;
import real_ride_producer.domain.RealRideEvent;
import static real_ride_producer.config.RealRideProducerConfig.ATTEMPT_INTERVAL_IN_SECONDS;
import static real_ride_producer.config.RealRideProducerConfig.MAX_ATTEMPTS_FOR_RETRY;

@Slf4j
@Component
@RequiredArgsConstructor
public class RealRideEventProducer {

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    private final KafkaTemplate<Integer, RealRideAvroMessage> kafkaTemplate;

    @Retryable(
            retryFor = ConnectException.class,
            backoff = @Backoff(delay = ATTEMPT_INTERVAL_IN_SECONDS * 1000),
            maxAttempts = MAX_ATTEMPTS_FOR_RETRY)
    public void send(RealRideEvent realRideEvent) {

        RealRideAvroMessage avroMessage = new RealRideAvroMessage(realRideEvent.getRideID(),
                realRideEvent.getDriverID(),
                realRideEvent.getPassengerID(),
                realRideEvent.getPickupTime(),
                realRideEvent.getPickupLocation(),
                realRideEvent.getArrivalTime(),
                realRideEvent.getArrivalLocation(), realRideEvent.getStatus().toString());

        Integer key = realRideEvent.getRideID();
        ProducerRecord<Integer, RealRideAvroMessage> producerRecord =
                new ProducerRecord<>(topic, null, key, avroMessage);

        kafkaTemplate
                .send(producerRecord)
                .whenComplete(
                        (result, ex) -> {
                            if (ex == null) {
                                handleSuccess(key, realRideEvent, result);
                            } else {
                                handleFailure(key, realRideEvent, ex);
                            }
                        });
    }

    private void handleFailure(Integer key, RealRideEvent msg, Throwable ex) {
        log.error(
                "Error sending the message with key {} for message {} and exception: {}",
                key,
                msg,
                ex.getMessage());
    }

    private void handleSuccess(Integer key, RealRideEvent msg, SendResult<Integer, RealRideAvroMessage> result) {
        log.info(
                "Message sent successfully for the key: {} and the value: {}, partition: {}",
                key,
                msg,
                result.getRecordMetadata().partition());
    }
}
